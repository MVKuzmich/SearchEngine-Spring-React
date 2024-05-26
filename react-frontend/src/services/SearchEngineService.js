import {useHttp} from '../hooks/http.hook'

const useSearchEngineService = () => {

    const {loading, error, clearError, request} = useHttp();

    const getStatistics = async () => {
        const res = await request("http://localhost:8080/statistics");
        const siteCount = res.statistics.total.sites;
        const pageCount = res.statistics.total.pages;
        const lemmaCount = res.statistics.total.lemmas;
        const isIndexingFlag = res.statistics.total.isIndexing;

        return {siteCount, pageCount, lemmaCount, isIndexingFlag};
    }

    const startIndexing = async () => {
        const res = await request("http://localhost:8080/startIndexing");
        
        return res;
    }

    const stopIndexing = async () => {
        const res = await request("http://localhost:8080/stopIndexing");
        
        return res;
    }

    const addSite = async (data) => {
        const res = await request("http://localhost:8080/addSite", 'POST', data);
        return res;
    }

    return {getStatistics, startIndexing, stopIndexing, addSite};
    
}

export default useSearchEngineService;