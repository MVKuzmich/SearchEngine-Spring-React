import {useState} from "react";
import useSearchEngineService from "../../services/SearchEngineService";
import ListItems from "../listItems/ListItems";
import SearchResult from "../searchResult/SearchResult";
import './searchpage.css';

const SearchPage = () => {
    const [userQuery, setUserQuery] = useState("");
    
    const [limit, setLimit] = useState(10);
    const [offset, setOffset] = useState(0);
    const [searchResultList, setSearchResultList] = useState([]);

    const {sendUserQuery} = useSearchEngineService();

    const handleUserQueryChange = (event) => {
        setUserQuery(event.target.value);
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        sendUserQuery(userQuery, offset, limit).then(
            (res) => {
                if(res.result) {
               setUserQuery("");
               setSearchResultList(res.data);
            }
        });  
      }
    
    return (
        <div className="SearchTab">
            <p>Search</p>
            <form className="form-inline my-2 my-lg-0" onSubmit={handleSubmit}>
                <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" 
                        value={userQuery}
                        onChange={handleUserQueryChange}/>
                <button className="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
            </form>
            <ListItems 
                listItems = {searchResultList}
                Component={SearchResult}/>
        </div>
    );
}

export default SearchPage;