import {useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import {Container, Stack, Badge} from 'react-bootstrap';
import './dashboard.css';

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
        <Container>
            <p>
                <Badge bg="secondary">Site Indexation Dashboard</Badge>
            </p>
            <Stack direction="horizontal" gap={3} >
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
        </Container>
                        
    );
}

export default DashboardPage;