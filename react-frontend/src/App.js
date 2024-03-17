import TabNavItems from "./components/tabs/TabNavItems";
import { BrowserRouter as Router, Route, Routes} from 'react-router-dom';

import './components/tabs/tabs.css';
function App() {
  return (
      <Router>
          <Routes>
              <Route path="/" element={<TabNavItems/>}/>
          </Routes>

      </Router>

  );
}

export default App;
