import { BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import { useCallback, useState } from 'react';
import './pages/tabs.css';
import DashboardPage from "./pages/DashboardPage";
import ManagementPage from './pages/ManagementPage';
import SearchPage from './pages/SearchPage';
import Navbar from './components/navbar/Navbar';

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
  }, [data]);

  
  return (
    <>ss
        <Router>
            <Navbar/>
            <Routes>
                <Route path="/" element={<><h1>my app</h1></>}/>
                <Route path="/dashboard" element={<DashboardPage  data={data} onDataLoaded={onDataLoaded}/>}/>
                <Route path="/management" element={<ManagementPage isIndexing={isIndexing} setIsIndexing={setIsIndexing} onDataLoaded={onDataLoaded}/>}/>
                <Route path="/search" element={<SearchPage/>}/>
            </Routes>
        </Router>
    
    </>   

  );
}

export default App;
