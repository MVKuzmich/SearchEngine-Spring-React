import React from "react";
import PropTypes from "prop-types";
import "./listItems.css";

const ListItems = ({ listItems, Component }) => {
  return listItems.map((item, index) => {
    return (
      <div className="list-items" key={index}>
        <span className="item-index">{index + 1}{'. '}</span>{' '}
        <span className="list-items">
          <Component item = {item} />{' '}
        </span>
      </div>     
    );
  });
};

ListItems.propTypes = {
  listItems: PropTypes.arrayOf(PropTypes.any).isRequired, // Adjust this type as per your requirement
  Component: PropTypes.oneOfType([PropTypes.func, PropTypes.node]).isRequired, // Ensures Component is a valid component
};

export default ListItems;