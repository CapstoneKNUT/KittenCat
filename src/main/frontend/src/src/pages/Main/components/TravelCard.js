import React from 'react';

function TravelCard({ region }) {
  return (
    <div className="travel-card">
      <img src={region.image} alt={region.name} />
      <h3>{region.name}</h3>
    </div>
  );
}

export default TravelCard;
