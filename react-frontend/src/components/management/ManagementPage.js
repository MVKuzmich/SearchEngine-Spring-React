import {useState, useEffect} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import useInterval from "../../hooks/setInterval.hook";
import ListItems from "../listItems/ListItems";
import SiteItem from "../siteItem/SiteItem";
import { Button, Badge, Spinner } from 'react-bootstrap';
import ErrorMessage from '../errorMessage/ErrorMessage';
import './management.css';


const ManagementPage = ({isIndexing, setIsIndexing, onDataLoaded}) => {

    const [name, setName] = useState("");
    const [url, setUrl] = useState("");
    const [submitMessage, setSubmitMessage] = useState("");
    const [messageVisible, setMessageVisible] = useState(false);
    const [siteList, setSiteList] = useState([]);

    const { getStatistics, startIndexing, stopIndexing, addSite, getSites, deleteSite, loading, error} = useSearchEngineService();

    const getData = () => {
        console.log('getData Dashboard');
        getStatistics().then(onDataLoaded);
    }

    const {startInterval, stopInterval} = useInterval(() => {
        console.log('useInterval');
        getData();
    }, 10000, isIndexing);

    useEffect(() => {
        handleSitesGetting()  
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
                console.log(isIndexing);
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

    const handleSitesGetting = () => {
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
                handleSitesGetting();
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

      const content = () => {
        return (siteList.length > 0) 
            ? <ListItems listItems={siteList} Component = {SiteItem} deleteItem={deleteSite} handleItemsGetting={handleSitesGetting}/>
            : <p>Any site was not added</p>;       
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
            {loading ? <Spinner/> : content()}
            {error ? <ErrorMessage/> : null}
            <Button type="button" onClick={() => toggleIndexing(isIndexing)}>{isIndexing ? 'Stop' : 'Start'} Indexation</Button>    
        </div>
    );
}

export default ManagementPage;