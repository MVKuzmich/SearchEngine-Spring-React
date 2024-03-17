import {useState, useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";


const DashboardTab = () => {
    const [siteCount, setSiteCount] = useState(0);
    const [pageCount, setPageCount] = useState(0);
    const [lemmaCount, setLemmaCount] = useState(0);

    const {getStatistics} = useSearchEngineService();

    useEffect(() => {
        setInterval(() => getData(), 1000);
    }, []);

    const getData = () => {
        getStatistics().then(onDataLoaded);
    }
    const onDataLoaded = (data) => {
        setSiteCount(data.siteCount);
        setPageCount(data.pageCount);
        setLemmaCount(data.lemmaCount);
    }
    return (
        <div className="TabContent">
            <div className="Section-header">
                <h2 className="Section-title">Dashboard</h2>
            </div>
            <div className="Statistics">
                <div className="Statistics-info">
                    <div className="Statistics-block">
                        <span className="Statistics-title">sites</span>
                    </div>
                    <div className="Statistics-block">
                        <span className="Statistics-title">pages</span>
                    </div>
                    <div className="Statistics-block">
                        <span className="Statistics-title">lemmas</span>
                    </div>
                </div>
                <div className="Statistics-info">
                    <div className="Statistics-block">
                        <span className="Statistics-amount" id="totalSites">{siteCount}</span>
                    </div>
                    <div className="Statistics-block">
                        <span className="Statistics-amount" id="totalPages">{pageCount}</span>
                    </div>
                    <div className="Statistics-block">
                        <span className="Statistics-amount" id="totalLemmas">{lemmaCount}</span>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default DashboardTab;