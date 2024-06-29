import {useState, useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import {Container, Stack, Badge, Spinner} from 'react-bootstrap';
import './dashboard.css';
import ErrorMessage from "../errorMessage/ErrorMessage";
import ListItems from "../listItems/ListItems";
import SiteItem from "../siteItem/SiteItem";
import SiteStatusComponent from "../siteStatusComponent/SiteStatusComponent";

const DashboardPage = ({isIndexing, data, onDataLoaded}) => {

    const [indexingSites, setIndexingSites] = useState([]);
    
    const {getStatistics, loading, error} = useSearchEngineService();

    const {siteCount, pageCount, lemmaCount} = data;
    
    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }
    
    useEffect(() => {
        console.log('first useEffect Dashboard');
        getData();
    }, []);

    useEffect(() => {
        getStatistics().then((res) => {setIndexingSites(res.sites)});
    }, [siteCount]);


    const content = () => {
        return ( 
            (indexingSites.length > 0) 
            ? <ListItems 
                listItems={indexingSites} 
                Component={SiteItem} 
                StatusComponent={SiteStatusComponent}                
            />
            : <p className="no-site-msg">Any site was not indexed</p>
            );       
        }
    return (error) ? (<ErrorMessage/>) : 
        (<Container>
            <p>
                <Badge bg="secondary">Site Indexation Dashboard</Badge>
            </p>
            {loading ? <Spinner/> : <Stack direction="horizontal" gap={3} >
                <Stack className="stat"  direction="vertical" gap={0}>
                    <div>
                        <Badge>Sites</Badge>
                    </div>
                    <div>{siteCount}</div>
                </Stack>
                <Stack className="stat" direction="vertical" gap={0}>
                    <div>
                        <Badge>Pages</Badge>
                    </div>
                    <div>{pageCount}</div>
                </Stack>
                <Stack className="stat" direction="vertical" gap={0}>
                    <div>
                        <Badge>Lemmas</Badge>
                    </div>
                    <div>{lemmaCount}</div>
                </Stack>
            </Stack>
            }
            {content()}
        </Container>
        );
}

export default DashboardPage;