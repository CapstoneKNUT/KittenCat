import React from 'react';

function Head({ setSearchQuery }) {
  return (
    <div className="header">
      <h1>어디로 여행을 떠나시나요?</h1>
      <input
        type="text"
        placeholder="국가명이나 도시명으로 검색해보세요..."
        onChange={(e) => setSearchQuery(e.target.value.toLowerCase())}
      />
      <div className="tabs">
        <span className="tab active">국내</span>
      </div>
    </div>
  );
}

export default Head;
