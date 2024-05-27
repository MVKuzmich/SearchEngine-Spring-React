import React from "react";
import PropTypes from "prop-types";

const ListItems = ({ listItems, Component }) => {
  return listItems.map((item, index) => {
    return (
      <div key={index}>
        <Component item = {item} />
      </div>
    );
  });
};

ListItems.propTypes = {
  listItems: PropTypes.arrayOf(PropTypes.any).isRequired, // Adjust this type as per your requirement
  Component: PropTypes.oneOfType([PropTypes.func, PropTypes.node]).isRequired, // Ensures Component is a valid component
};

export default ListItems;