import {useState, useEffect} from "react";
import useSearchEngineService from "../services/SearchEngineService";
import useInterval from "../hooks/setInterval.hook";

const ManagementPage = ({isIndexing, setIsIndexing, onDataLoaded}) => {

    const [name, setName] = useState("");
    const [url, setUrl] = useState("");
    const [submitMessage, setSubmitMessage] = useState("");
    const [messageVisible, setMessageVisible] = useState(false);

    const { getStatistics, startIndexing, stopIndexing, addSite } = useSearchEngineService();

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

    const site = {
        name, 
        url
    }

    const handleMessageVisibility = () => {
        setMessageVisible(true);
        setTimeout(() => {
          setMessageVisible(false);
        }, 5000);
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        const res = addSite(JSON.stringify(site));
        if(res) {
            setName("");
            setUrl("");
            setSubmitMessage("Your site has been added succesfully!");

        } else {
            setSubmitMessage(res.error);
        }
        handleMessageVisibility();
      }
    
      const handleUrlChange = (event) => {
        setUrl(event.target.value);
      }

      const handleNameChange = (event) => {
        setName(event.target.value);
      }


    return (
        <div className="ManagementTab">
            <p>Management</p>
            
            <form onSubmit={handleSubmit}>
                <label htmlFor="fname">Add Site Name: 
                    <input type="text" id="fname" placeholder="Enter site name" name="fname" value={name} onChange={handleNameChange}/>
                </label><br/>
                <label htmlFor="fname">Add Site Url: 
                    <input type="text" id="fname" placeholder="Enter site domain url" name="fname" value={url} onChange={handleUrlChange}/>
                </label><br/>
                {messageVisible && <p className="msg">{submitMessage}</p>}
                <button type="submit" onSubmit={handleSubmit}>Save site!</button>
            </form>
            <button type="button" onClick={() => toggleIndexing(isIndexing)}>{isIndexing ? 'Stop' : 'Start'} Indexation</button>
        </div>
    );
}

export default ManagementPage;