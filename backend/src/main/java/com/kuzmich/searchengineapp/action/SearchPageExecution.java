package com.kuzmich.searchengineapp.action;

import com.kuzmich.searchengineapp.dto.searchDto.SearchPageData;
import com.kuzmich.searchengineapp.entity.Index;
import com.kuzmich.searchengineapp.entity.Lemma;
import com.kuzmich.searchengineapp.entity.Page;
import com.kuzmich.searchengineapp.repository.LemmaRepository;
import com.kuzmich.searchengineapp.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.morphology.Morphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class SearchPageExecution {

    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    private final LuceneMorphology luceneMorphology;

    public List<SearchPageData> getSearchResultListFromUserQuery(String userQuery, String site) {
        try {
            List<Lemma> lemmaObjectListFromUserQuery = findLemmasByWordsFromUserQuery(userQuery);
            List<String> lemmaWordsFromUserQuery = lemmaObjectListFromUserQuery.stream()
                    .map(Lemma::getLemma)
                    .distinct()
                    .collect(Collectors.toList());
            List<Index> totalIndexList = lemmaObjectListFromUserQuery.stream()
                    .flatMap(lemma -> lemma.getIndexList().stream())
                    .collect(Collectors.toList());
            if (lemmaWordsFromUserQuery.size() != 1) {
                Set<Integer> ids = new HashSet<>();
                List<Integer> commonPageIds = totalIndexList.stream().map(Index::getPage).map(Page::getId).filter(id -> !ids.add(id)).collect(Collectors.toList());
                totalIndexList.removeIf(index -> !commonPageIds.contains(index.getPage().getId()));
            }
            if (site != null) {
                totalIndexList.removeIf(index -> index.getPage().getSite().getId().intValue() != siteRepository.findSiteByUrl(site).get().getId().intValue());
            }
            return (!totalIndexList.isEmpty())
                    ? getSearchPageDataFromIndexes(totalIndexList, lemmaWordsFromUserQuery) : new ArrayList<>();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Lemma> findLemmasByWordsFromUserQuery(String userQuery) throws IOException {
        long start = System.currentTimeMillis();
        List<Lemma> lemmaList = new ArrayList<>();
        String[] queryWords = userQuery.toLowerCase().trim().split(" ");

        for (String word : queryWords) {
            if (luceneMorphology.getMorphInfo(word).stream().anyMatch(i -> i.contains("ПРЕДЛ") || i.contains("СОЮЗ")
                    || i.contains("ЧАСТ") || i.contains("МЕЖД"))) {
                continue;
            }
            List<Lemma> formWords = luceneMorphology.getNormalForms(word.toLowerCase(Locale.ROOT)).get(0).lines()
                    .map(lemmaRepository::findLemmaObjectByLemmaName)
                    .filter(Optional::isPresent)
                    .flatMap(lemmaListOpt -> lemmaListOpt.get().stream())
                    .collect(Collectors.toList());
            lemmaList.addAll(formWords);
        }
        lemmaList.sort(Comparator.comparing(Lemma::getFrequency).thenComparing(Lemma::getLemma));
        long end = System.currentTimeMillis();
        long duration = end - start;
        log.info("Перевод СЛОВ из запроса В ЛЕММУ - {} миллисекунд", duration);
        return lemmaList;
    }

    private List<SearchPageData> getSearchPageDataFromIndexes(List<Index> indexList, List<String> lemmaWordsFromUserQuery) throws IOException {
        long startMethod = System.currentTimeMillis() / 1_000;
        List<SearchPageData> resultList = new ArrayList<>();
        float maxRank = Float.MIN_VALUE;
        List<Integer> pageIds = indexList.stream().map(Index::getPage).map(Page::getId).distinct().collect(Collectors.toList());
        for (Integer id : pageIds) {
            long startIndex = System.currentTimeMillis() / 1000;
            Index indexForSearchPageData = indexList.stream().filter(index -> index.getPage().getId().intValue() == id.intValue()).findFirst()
                    .orElseThrow(() -> new RuntimeException("Something wrong"));
            Document document = Jsoup.parse(indexForSearchPageData.getPage().getContent());
            float sumRank = (float) indexList.stream().filter(ind -> ind.getPage().getId().intValue() == id.intValue())
                    .mapToDouble(Index::getRank).sum();
            if (sumRank > maxRank) {
                maxRank = sumRank;
            }
            SearchPageData searchPageData = SearchPageData
                    .builder()
                    .site(indexForSearchPageData.getPage().getSite().getUrl())
                    .siteName(indexForSearchPageData.getPage().getSite().getName())
                    .uri(indexForSearchPageData.getPage().getPath())
                    .title(Jsoup.parse(indexForSearchPageData.getPage().getContent()).title())
                    .snippet(createSnippetFromPage(lemmaWordsFromUserQuery, document))
                    .relevance(sumRank)
                    .build();
            log.info("сниппет: {}, релевантность: {}", searchPageData.getSnippet(), searchPageData.getRelevance());
            resultList.add(searchPageData);
            long duration = System.currentTimeMillis() / 1000 - startIndex;
            log.info("search for: pageID {}, path {}; SEARCH RESULT duration: {}", id, searchPageData.getUri(), duration);
        }
        long duration = System.currentTimeMillis() / 1000 - startMethod;
        log.info("длительность ПОИСКА: {}", duration);
        return updateSearchPageDataRelevance(maxRank, resultList).stream()
                .sorted(Comparator.comparing(SearchPageData::getRelevance).reversed())
                .collect(Collectors.toList());
    }

    private String createSnippetFromPage(List<String> lemmaWordsFromUserQuery, Document document) throws IOException {
        List<String> partsForSnippet = findPartsForSnippet(lemmaWordsFromUserQuery, document);

        return "...".concat(String.join("...", partsForSnippet));
    }

    private List<SearchPageData> updateSearchPageDataRelevance(float maxRank, List<SearchPageData> resultList) {
        return resultList.stream().peek(result -> result.setRelevance(result.getRelevance() / maxRank))
                .collect(Collectors.toList());
    }

    private List<String> findPartsForSnippet(List<String> lemmaWordsFromUserQuery, Document document) throws IOException {
        long start = System.currentTimeMillis();
        List<String> partsForSnippet = new ArrayList<>();
        List<String> lemmaWords = new ArrayList<>(lemmaWordsFromUserQuery);
        Elements elements = document.getElementsMatchingText(createRegex(lemmaWords));
        for (Element element : elements) {
            String sentence = "";
            String parseText = element.text();
            String[] arr = parseText.split("[\\s.,!:]+\\s*");
            for (String word : arr) {
                if (word.matches("[А-яЁё-]+")) {
                    String normalWordForm = luceneMorphology.getNormalForms(word).get(0);
                    if (lemmaWords.contains(normalWordForm)) {
                        log.info("лемма: {}, слово: {}", normalWordForm, word.toUpperCase());
                        partsForSnippet.add(getSentenceFromTextElement(word, parseText));
                        lemmaWords.remove(normalWordForm);
                    }
                    if (lemmaWords.isEmpty()) {
                        break;
                    }
                }
            }
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        log.info("Поиск слов и их индексов в тексте по леммам - {} миллисекунд", duration);
        return partsForSnippet;
    }

    private String createRegex(List<String> lemmaWords) {
        String regexFirstLetter = lemmaWords.stream()
                .filter(word -> word.length() > 1)
                .map(word -> word.substring(0, 1))
                .collect(Collectors.joining("|"));
        return "(?i)\\b".concat("(").concat(regexFirstLetter).concat(")").concat("([А-яЁё]+)(-\1)?");
    }


    private String getSentenceFromTextElement(String word, String text) {
        String partForSnippet = "";
        int maxPointFromWord = 100;
        int indexWordFromSentenceStart = text.indexOf(word);
        int lengthFromWordToSentenceEnd = (indexWordFromSentenceStart + word.length() == text.length()) ? 0 : text.substring(indexWordFromSentenceStart + word.length()).length();

        if (indexWordFromSentenceStart > maxPointFromWord && lengthFromWordToSentenceEnd > maxPointFromWord) {
            partForSnippet = text.substring(indexWordFromSentenceStart - maxPointFromWord, indexWordFromSentenceStart + maxPointFromWord);
        } else if (indexWordFromSentenceStart < maxPointFromWord && lengthFromWordToSentenceEnd > maxPointFromWord) {
            partForSnippet = text.substring(0, indexWordFromSentenceStart + maxPointFromWord);
        } else if (indexWordFromSentenceStart > maxPointFromWord && lengthFromWordToSentenceEnd < maxPointFromWord) {
            partForSnippet = text.substring(indexWordFromSentenceStart - maxPointFromWord);
        } else {
            partForSnippet = text;
        }

        return partForSnippet.replaceAll("\\b".concat(word), "<b>".concat(word).concat("</b>"));
    }
}



