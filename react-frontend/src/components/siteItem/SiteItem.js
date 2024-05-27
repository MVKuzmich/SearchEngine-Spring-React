import React from "react";

const SiteItem = (site) => {
  const { url, name } = site.item;

  return (
    <div>
      <p>{`Name: ${name} - URL: ${url}`}</p>
    </div>
  );
};

export default SiteItem;
