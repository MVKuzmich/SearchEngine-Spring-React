import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import './siteItem.css';
import Button from 'react-bootstrap/Button';

const SiteItem = (site) => {
  const { url, name } = site.item;

  return (
    <>
      <span>{`Name: ${name} - URL: ${url}`}</span> {' '}
      <Button className="trash-btn">
        <FontAwesomeIcon className="trash-icon" icon={faTrash} />
      </Button>
    </>
  );
};

export default SiteItem;
