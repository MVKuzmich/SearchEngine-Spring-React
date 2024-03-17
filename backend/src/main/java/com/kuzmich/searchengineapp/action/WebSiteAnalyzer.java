package com.kuzmich.searchengineapp.action;

import com.kuzmich.searchengineapp.config.SiteConfig;
import com.kuzmich.searchengineapp.entity.*;
import com.kuzmich.searchengineapp.repository.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveAction;
import java.util.stream.Collectors;

import static org.jsoup.Connection.*;

@Log4j2
@Component
@RequiredArgsConstructor
public class WebSiteAnalyzer extends RecursiveAction {
    @Getter
    private static CopyOnWriteArraySet<String> serviceSet = new CopyOnWriteArraySet<>();
    @Setter
    @Getter
    private static boolean isIndexationStopped;
    @Setter
    @Getter
    private static boolean isOnePageIndexation;
    @Setter
    @Getter
    private volatile Site site;
    @Setter
    private volatile String mainPath;

    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final FieldRepository fieldRepository;
    private final SiteRepository siteRepository;
    private final SiteConfig siteConfig;
    private final Lemmatizator lemmatizator;


    @SneakyThrows
    @Override
    protected void compute() {
        if (!isIndexationStopped) {
            log.info("выполняется поток: {}, парсинг страницы: {}", Thread.currentThread().getName(), mainPath);
            List<WebSiteAnalyzer> taskList = new ArrayList<>();
            Response response = null;
            try {
                response = Jsoup.connect(mainPath).userAgent(siteConfig.getUserAgent()).referrer(siteConfig.getReferrer()).timeout(0).execute();
                Page page = savePageToDatabase(createPage(response));
                log.info("SAVED page: {}, pageId: {}", page.getPath(), page.getId());

                executePageIndexation(page, response.parse());

                Set<String> links = findRelatedPageLinks(response.parse());
                if (!links.isEmpty()) {
                    serviceSet.add(mainPath);
                    for (String link : links) {
                        if (isIndexationStopped || isOnePageIndexation) {
                            break;
                        }
                        if (!serviceSet.contains(link)) {
                            serviceSet.add(link);
                            WebSiteAnalyzer task = createNewTask(link);
                            task.fork();
                            taskList.add(task);
                            Thread.sleep((long) (500 + Math.random() * 4500));
                        }
                    }
                    for (WebSiteAnalyzer task : taskList) {
                        task.join();
                    }
                }
            } catch (HttpStatusException hse) {
                Page page = Page.builder().path(response != null ? response.url().toURI().getPath() : hse.getUrl()).code(hse.getStatusCode()).content("no content").site(site).build();
                pageRepository.saveAndFlush(page);
            }
        }
    }

    private Page savePageToDatabase(Page page) {
        return pageRepository.saveAndFlush(page);
    }

    private Page createPage(Response response) throws URISyntaxException {
        return Page.builder()
                .path((response.url().toURI().getPath().equals("")) ? "/" : response.url().toURI().getPath())
                .code(response.statusCode())
                .content(response.body().toLowerCase())
                .site(site)
                .build();
    }

    private WebSiteAnalyzer createNewTask(String link) {
        WebSiteAnalyzer task = new WebSiteAnalyzer(pageRepository, lemmaRepository, indexRepository, fieldRepository, siteRepository, siteConfig, lemmatizator);
        task.setSite(site);
        task.setMainPath(link);
        return task;
    }

    private void executePageIndexation(Page page, Document document) throws IOException {
        new PageIndexExecutor(fieldRepository, lemmaRepository, indexRepository, siteRepository, lemmatizator).executePageIndexing(page, document);
    }

    private Set<String> findRelatedPageLinks(Document document) {
        Elements elements = document.select("a[abs:href^=" + site.getUrl() + "]");
        Set<String> links = elements.stream()
                .map(e -> e.attr("abs:href"))
                .collect(Collectors.toSet());
        links.removeIf(l -> l.contains("#") || l.contains("?") || l.contains("=")
                || l.matches("(?i).+\\.(?!html).{1,5}$"));
        return links;
    }
}






