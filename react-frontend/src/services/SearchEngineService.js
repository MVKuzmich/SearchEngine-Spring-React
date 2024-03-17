import {useHttp} from '../hooks/http.hook'

const useSearchEngineService = () => {

    const {loading, error, clearError, request} = useHttp();

    const getStatistics = async () => {
        const res = await request("http://localhost:8080/statistics");
        const siteCount = res.statistics.total.sites;
        const pageCount = res.statistics.total.pages;
        const lemmaCount = res.statistics.total.lemmas;

        return {siteCount, pageCount, lemmaCount};
    }

    return {getStatistics};
    
}

export default useSearchEngineService;