import {useState, useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import useInterval from "../../hooks/setInterval.hook";
import ListItems from "../listItems/ListItems";
import SiteItem from "../siteItem/SiteItem";
import { Button, Badge } from 'react-bootstrap'
import './management.css';


const ManagementPage = ({isIndexing, setIsIndexing, onDataLoaded}) => {

    const [name, setName] = useState("");
    const [url, setUrl] = useState("");
    const [submitMessage, setSubmitMessage] = useState("");
    const [messageVisible, setMessageVisible] = useState(false);
    const [siteList, setSiteList] = useState([]);

    const { getStatistics, startIndexing, stopIndexing, addSite, getSites } = useSearchEngineService();

    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }

    const {startInterval, stopInterval} = useInterval(() => {
        console.log('useInterval');
        getData();
    }, 10000, isIndexing);

    useEffect(() => {
        handleSiteGetting()  
    }, []);


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

    const handleSiteGetting = () => {
        getSites().then((res) => setSiteList(res));
    };   

    const handleSubmit = (event) => {
        event.preventDefault();
        const res = addSite(JSON.stringify(site)).then(
            () => {
                if(res) {
                setName("");
                setUrl("");
                setSubmitMessage("Your site has been added succesfully!");
                handleSiteGetting();
            } else {
                setSubmitMessage(res.error);
            }
        handleMessageVisibility();
        });  
      }
        
    
      const handleUrlChange = (event) => {
        setUrl(event.target.value);
      }

      const handleNameChange = (event) => {
        setName(event.target.value);
      }


    return (
        <div>
            <p>
                <Badge bg="secondary">Indexation Management</Badge>
            </p>
            <span>Add site: </span>
            <form className="site-form" onSubmit={handleSubmit}>
                <div>
                    <label className="label" htmlFor="fname">Site Name: {' '}
                        <input type="text" id="siteName" placeholder="Enter site name" name="name" value={name} onChange={handleNameChange}/>
                    </label><br/>
                    <label className="label" htmlFor="fname">Site Domain: {' '}
                        <input type="text" id="siteUrl" placeholder="Enter site domain url" name="url" value={url} onChange={handleUrlChange}/>
                    </label><br/>
                    {messageVisible && <p className="msg">{submitMessage}</p>}
                </div>
                <Button type="submit" onSubmit={handleSubmit}>Save site!</Button>
            </form>
            {siteList.length > 0 ? <ListItems listItems={siteList} Component = {SiteItem} /> : <p>Any site was not added</p>}
            <Button type="button" onClick={() => toggleIndexing(isIndexing)}>{isIndexing ? 'Stop' : 'Start'} Indexation</Button>
            
        </div>
    );
}

export default ManagementPage;