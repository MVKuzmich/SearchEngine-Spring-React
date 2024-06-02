import React from "react";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import './siteItem.css';
import Button from 'react-bootstrap/Button';

const SiteItem = ({item, deleteItem, handleItemsGetting}) => {
  const { url, name } = item;

  const handleDeleteItem = () => {
    deleteItem(JSON.stringify(item)).then((res) => {
      if(res) {
        handleItemsGetting();
      }
    });
  }


  return (
    <>
      <span>{`Name: ${name} - URL: ${url}`}</span> {' '}
      <Button className="trash-btn" onClick={handleDeleteItem}>
        <FontAwesomeIcon className="trash-icon" icon={faTrash} />
      </Button>
    </>
  );
};

export default SiteItem;
