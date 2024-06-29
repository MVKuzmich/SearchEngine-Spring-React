import React from "react";
import "./listItems.css";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrash } from '@fortawesome/free-solid-svg-icons';
import Button from 'react-bootstrap/Button';

const ListItems = ({ listItems, 
                      Component,
                      deleteItem = () => {},
                      handleItemsGetting = () => {}, 
                      selectedItems = [],
                      setSelectedItems = () => {},
                      StatusComponent }
                  ) => {

    const handleSelectItem = (item) => {
      if (selectedItems.includes(item)) {
        setSelectedItems(selectedItems.filter((selectedItem) => selectedItem !== item));
      } else {
        setSelectedItems([...selectedItems, item]);
      }
    };

    const handleDeleteItem = (item) => {
      deleteItem(JSON.stringify(item)).then((res) => {
        if(res) {
          handleItemsGetting();
        }
      });
    }
  return listItems.map((item) => {

    const renderedComponent = (typeof Component === "function" || React.isValidElement(Component)) ? <Component item={item} /> : null;
    
    const renderedStatusComponent = (typeof StatusComponent === "function" || React.isValidElement(StatusComponent)) ? <StatusComponent item = {item}/> : null;
    return (
      <div className="list-items" key={item.hash}>
        {listItems.length > 0 && (<input
              type="checkbox"
              checked={selectedItems.includes(item)}
              onChange={() => handleSelectItem(item)}
            />)}
        <span className="list-items">
          {renderedComponent} {' '}
          {deleteItem !== ListItems.defaultProps.deleteItem && (<Button className="trash-btn" onClick={() => handleDeleteItem (item)}>
            <FontAwesomeIcon className="trash-icon" icon={faTrash} />
          </Button>)}
          {renderedStatusComponent}
        </span>
      </div>    
    );
  });
};
ListItems.defaultProps = {
  deleteItem: () => {},
};

export default ListItems;