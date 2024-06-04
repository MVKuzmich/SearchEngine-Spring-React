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
        const res = await request("http://localhost:8080/start-indexation");
        
        return res;
    }

    const stopIndexing = async () => {
        clearError();
        const res = await request("http://localhost:8080/stop-indexation");
        
        return res;
    }

    const addSite = async (data) => {
        clearError();
        const res = await request("http://localhost:8080/add-site", 'POST', data);
        return res;
    }

    const getSites = async () => {
        clearError();
        const res = await request("http://localhost:8080/new-sites");
        return res;
    }
    const deleteSite = async (data) => {
        clearError();
        const res = await request("http://localhost:8080/delete-site", 'POST', data);
        return res;
    }

    return {getStatistics, startIndexing, stopIndexing, addSite, getSites, deleteSite, loading, error};
    
}

export default useSearchEngineService;