import React, { useState } from 'react';
import Home_Header from './components/Head';
import TravelCard from './components/TravelCard';
import { regions } from './data/regions';
import './Main.css';

function Main() {
  const [searchQuery, setSearchQuery] = useState('');

  // 검색된 결과 필터링
  const filteredRegions = regions.filter(region =>
    region.name.toLowerCase().includes(searchQuery)
  );

  return (
    <div className="App">
      <Home_Header setSearchQuery={setSearchQuery} />
      <div className="travel-container">
        {filteredRegions.map((region, index) => (
          <TravelCard key={index} region={region} />
        ))}
      </div>
    </div>
  );
}

export default Main;
