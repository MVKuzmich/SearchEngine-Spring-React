import './searchresult.css';
const SearchResult = ({ item }) => {
    const { site, siteName, uri, title, snippet } = item;
    const url = `${site}${uri}`;
    return (
        <div className="search-result">
            <div className="result-header">
                <div className="domain">{siteName}</div>
                <a href={url} className="url" target="_blank" rel="noopener noreferrer">
                    {url}
                </a>
            </div>
            <h3 className="title">
                <a href={url} target="_blank" rel="noopener noreferrer">{title}</a>
            </h3>
            <p className="snippet">{snippet}</p>
        </div>
    );
}

export default SearchResult;