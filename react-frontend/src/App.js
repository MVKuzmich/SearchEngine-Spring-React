import { useCallback, useState } from 'react';
import DashboardPage from "./components/dashboard/DashboardPage";
import ManagementPage from './components/management/ManagementPage';
import SearchPage from './components/search/SearchPage';
import {Container, Row, Col} from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import './app.css';

function App() {

  const [isIndexing, setIsIndexing] = useState(false);
  const [siteCount, setSiteCount] = useState(0);
  const [pageCount, setPageCount] = useState(0);
  const [lemmaCount, setLemmaCount] = useState(0);

  const data= {siteCount, pageCount, lemmaCount};
  
  const onDataLoaded = useCallback((data) => {
    console.log('onDataLoaded Dashboard');
    setSiteCount(data.siteCount);
    setPageCount(data.pageCount);
    setLemmaCount(data.lemmaCount);
    if(!data.isIndexingFlag) {
      setIsIndexing(data.isIndexingFlag)
    }
  }, []);

  
  return (
    <Container>
        <Row>
          <Col>
            <DashboardPage isIndexing={isIndexing} data={data} onDataLoaded={onDataLoaded}/>
          </Col>
          <Col>
            <ManagementPage isIndexing={isIndexing} setIsIndexing={setIsIndexing} onDataLoaded={onDataLoaded}/>
          </Col>
        </Row>
        <Row>
          <Col>
            <SearchPage/>
          </Col>
        </Row>
      </Container>
  );
}

export default App;
