import {React} from 'react';
import { Link } from 'react-router-dom';
const Navbar = () => {

    return (
    <nav className="navbar navbar-expand-lg navbar-light bg-light">
        <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <ul className="navbar-nav mr-auto">
                <li className="nav-item active">
                    <Link to="/dashboard" className="nav-link">Dashboard</Link>
                </li>
                <li className="nav-item">
                    <Link to="/management" className="nav-link">Management</Link>
                </li>
                <li className="nav-item">
                    <Link to="/search" className="nav-link">Search</Link>
                </li>
            </ul>
        </div>
    </nav>
    );
}

export default Navbar;