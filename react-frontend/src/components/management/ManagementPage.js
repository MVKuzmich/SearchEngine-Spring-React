import { useCallback, useEffect, useState } from "react";
import { Badge, Button, Spinner } from 'react-bootstrap';
import useInterval from "../../hooks/setInterval.hook";
import useSearchEngineService from "../../services/SearchEngineService";
import ErrorMessage from '../errorMessage/ErrorMessage';
import ListItems from "../listItems/ListItems";
import SiteItem from "../siteItem/SiteItem";
import './management.css';


const ManagementPage = ({isIndexing, setIsIndexing, onDataLoaded}) => {

    const [name, setName] = useState("");
    const [url, setUrl] = useState("");
    const [submitMessage, setSubmitMessage] = useState("");
    const [messageVisible, setMessageVisible] = useState(false);
    const [newSiteList, setNewSiteList] = useState([]);
    const [urlError, setUrlError] = useState(false);
    const [prevUrl, setPrevUrl] = useState("");
    const [isValidUrl, setIsValidUrl] = useState(false);
    const [selectedItems, setSelectedItems] = useState([]);


    const { getStatistics, startIndexing, stopIndexing, addSite, getNewSites, deleteSite, loading, error} = useSearchEngineService();

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
                startIndexing(JSON.stringify(selectedItems)).then((res) => setIsIndexing((prevIsIndexing) => res.result));
                setNewSiteList([]);
                console.log(isIndexing);
            } else {
                stopIndexing().then((res) => setIsIndexing((prevIsIndexing) => res.result));
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

    const handleSitesGetting = useCallback(() => {
        getNewSites().then((res) => setNewSiteList(res));
    }, [getNewSites]);   

    const handleSubmit = (event) => {
        event.preventDefault();
        addSite(JSON.stringify(site)).then(
            (res) => {
                if(res.result) {
                setName("");
                setUrl("");
                setSubmitMessage("Your site has been added succesfully!");
                setIsValidUrl(false);
                handleSitesGetting();
            } else {
                setSubmitMessage(res.error);
            }
        handleMessageVisibility();
        });  
      }
      const pattern = /^(https?:\/\/)?(www\.)?([a-z0-9]+(?:[\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?)$/; 
    
      const handleUrlChange = (event) => {
        const value = event.target.value;

        const isValid = pattern.test(value);

        setUrl(value);
        setPrevUrl(value);
        setUrlError(!isValid);
        setIsValidUrl(isValid);
      }

      const handleUrlBlur = () => {
        setPrevUrl(url);
        setUrlError(false);
      };

      const handleUrlFocus = () => {
        const isDifferent = url === prevUrl;
        const isValid = pattern.test(url);
    
        if (isDifferent) {
          setUrlError(!isValid);
        }
      };

      const handleNameChange = (event) => {
        setName(event.target.value);
      }

      const content = () => {
        return (newSiteList.length > 0) 
            ? <ListItems 
                listItems={newSiteList} 
                Component = {SiteItem}
                deleteItem={deleteSite}
                handleItemsGetting={handleSitesGetting}
                selectedItems={selectedItems}
                setSelectedItems={setSelectedItems}
            />
            : <p className="no-site-msg">Any site was not added</p>;       
        }

    return (
        <div className="container">
            <div className="content">
                <p>
                <Badge bg="secondary">Indexation Management</Badge>
                </p>
                <span>Add site: </span>
                <form className="site-form" onSubmit={handleSubmit}>
                    <div>
                        <label className="label" htmlFor="siteName">Site Name: {' '}
                            <input type="text" id="siteName" placeholder="Enter site name" name="name" value={name} onChange={handleNameChange}/>
                        </label><br/>
                        <label className="label" htmlFor="siteUrl">Site Domain: {' '}
                            <input type="text" id="siteUrl" 
                                    placeholder="Enter site domain url" 
                                    name="url" value={url}
                                    onChange={handleUrlChange}
                                    onFocus={handleUrlFocus}
                                    onBlur={handleUrlBlur}/>
                            {urlError && (<p className="error-message">Please enter a valid URL</p>)}
                            {messageVisible && <p className="error-message">{submitMessage}</p>}
                        </label><br/>
                    </div>
                    <div className="save-btn-wrapper">
                        <Button className="save-btn" type="submit" onSubmit={handleSubmit} disabled={!isValidUrl}>Save site!</Button>
                    </div>
                </form>
                {loading ? <Spinner/> : content()}
                {error ? <ErrorMessage/> : null}  
            </div>
            <Button className="indexation-btn" type="button" onClick={() => toggleIndexing(isIndexing)} disabled={selectedItems.length === 0}>{isIndexing ? 'Stop' : 'Start'} Indexation</Button>    
        </div>
    );
}

export default ManagementPage;