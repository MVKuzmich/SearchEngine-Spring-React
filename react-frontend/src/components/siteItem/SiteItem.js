import React from "react";
import './siteItem.css';

const SiteItem = ({item}) => {
  const { url, name} = item;

  return (
    <>
      <span>{`Name: ${name} - URL: ${url}`}</span> {' '}
    </>
  );
};

export default SiteItem;
