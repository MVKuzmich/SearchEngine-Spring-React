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
        clearError();
        const res = await request("http://localhost:8080/startIndexing");
        
        return res;
    }

    const stopIndexing = async () => {
        clearError();
        const res = await request("http://localhost:8080/stopIndexing");
        
        return res;
    }

    const addSite = async (data) => {
        clearError();
        const res = await request("http://localhost:8080/addSite", 'POST', data);
        return res;
    }

    const getSites = async () => {
        clearError();
        const res = await request("http://localhost:8080/sites");
        return res;
    }
    const deleteSite = async (data) => {
        clearError();
        const res = await request("http://localhost:8080/deleteSite", 'POST', data);
        return res;
    }

    return {getStatistics, startIndexing, stopIndexing, addSite, getSites, deleteSite, loading, error};
    
}

export default useSearchEngineService;