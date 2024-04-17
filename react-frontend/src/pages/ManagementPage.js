import {useState, useEffect} from "react";
import useSearchEngineService from "../services/SearchEngineService";
import useInterval from "../hooks/setInterval.hook";

const ManagementPage = ({isIndexing, setIsIndexing, onDataLoaded}) => {

    const [url, setUrl] = useState([]);

    const { getStatistics, startIndexing, stopIndexing } = useSearchEngineService();

    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }

    const {startInterval, stopInterval} = useInterval(() => {
        console.log('useInterval');
        getData();
    }, 10000, isIndexing);

    useEffect(() => {
        if(isIndexing) {
            startInterval();
            console.log('interval is started')
        } else {
            stopInterval();
            console.log('interval is stopped or not started');
        }
    }, [isIndexing]);

    const toggleIndexing = () => {
        try {
            if(!isIndexing) {
                startIndexing().then((res) => setIsIndexing((prevIsIndexing) => res.isIndexing));
                console.log(isIndexing);
            } else {
                stopIndexing().then((res) => setIsIndexing((prevIsIndexing) => res.isIndexing));
                console.log(isIndexing); // Проверьте, вернулся ли нужный флаг для обновления состояния
            }
            
            
        } catch (error) {
            console.error('Error while stopping indexing:', error);
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        fetch('<http://localhost:8080/startIndexing>', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: url,
        })
        .then(response => response.json())
        .then(data => console.log('User created:', data))
        .catch(error => console.error('Error creating user:', error));
      };
    
      const handleChange = (event) => {
        const { url } = event.target;
        setUser(prevUrl => ({ ...prevUser, [name]: value }));
      };

    return (
        <div className="ManagementTab">
            <p>Management</p>
            
            <form>
                <label htmlFor="fname">Add Site Url: 
                    <input type="text" id="fname" name="fname" value={url}/>
                </label><br/>
                {!isIndexing && <button type="button" onClick={() => toggleIndexing(isIndexing)}>Start Indexation</button>}
                {isIndexing && <button type="button" onClick={() => toggleIndexing(isIndexing)}>Stop Indexation</button>}
            </form>
        </div>
    );
}

export default ManagementPage;