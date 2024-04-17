import {useEffect} from "react";
import useSearchEngineService from "../services/SearchEngineService";


const DashboardPage = ({data, onDataLoaded}) => {
    
    const {getStatistics} = useSearchEngineService();
    
    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }
    

    useEffect(() => {
        console.log('first useEffect Dashboard');
        getData();
    }, []);

    const {siteCount, pageCount, lemmaCount} = data;
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

export default DashboardPage;