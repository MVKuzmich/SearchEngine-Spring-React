import {useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import {Container, Stack, Badge, Spinner} from 'react-bootstrap';
import './dashboard.css';
import ErrorMessage from "../errorMessage/ErrorMessage";

const DashboardPage = ({data, onDataLoaded}) => {
    
    const {getStatistics, loading, error} = useSearchEngineService();
    
    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }
    

    useEffect(() => {
        console.log('first useEffect Dashboard');
        getData();
    }, []);

    const {siteCount, pageCount, lemmaCount} = data;
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
        </Container>
        );
}

export default DashboardPage;