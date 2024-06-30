import {useState, useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import {Container, Stack, Badge, Spinner, Button} from 'react-bootstrap';
import './dashboard.css';
import ErrorMessage from "../errorMessage/ErrorMessage";
import ListItems from "../listItems/ListItems";
import SiteItem from "../siteItem/SiteItem";
import SiteStatusComponent from "../siteStatusComponent/SiteStatusComponent";

const DashboardPage = ({isIndexing, data, onDataLoaded}) => {

    const [indexingSites, setIndexingSites] = useState([]);
    const [selectedItems, setSelectedItems] = useState([]);
    
    const {getStatistics, deleteIndexedSites, loading, error} = useSearchEngineService();

    const {siteCount, pageCount, lemmaCount} = data;
    
    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }

    const getSites = () => {
        getStatistics().then((res) => {setIndexingSites(res.sites)});
    }

    const handleDeleteSites = () => {
        deleteIndexedSites(JSON.stringify(selectedItems)).then(getData);
        setSelectedItems([]);
        
    }

    useEffect(() => {
        console.log('first useEffect Dashboard');
        getData();
    }, []);

    useEffect(() => {
        getSites();
    }, [siteCount, isIndexing]);


    const content = () => {
        return ( 
            (indexingSites.length > 0) 
            ? <ListItems 
                listItems={indexingSites}
                selectedItems={selectedItems}
                setSelectedItems={setSelectedItems}
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
            <div>{content()}</div>
            <div className="dashboard-btn-wrapper">
                <Button className="save-btn" type="button" onClick={handleDeleteSites} disabled={selectedItems.length === 0}>Delete sites</Button>
            </div>
            
        </Container>
        );
}

export default DashboardPage;