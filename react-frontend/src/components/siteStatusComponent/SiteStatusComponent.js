import { Badge } from "react-bootstrap";

const SiteStatusComponent = ({item}) => {

    const {status} = item;

    return (
        <>
            <Badge>{status}</Badge>
        </>
    );

}

export default SiteStatusComponent;