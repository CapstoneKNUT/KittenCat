// components/SearchPage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

// 지역과 시/군 데이터
const cities = {
  서울: ["종로구", "강남구", "서초구"],
  부산: ["해운대구", "수영구", "부산진구"],
  강원도: ["강릉시", "속초시", "원주시"],
  // 더 많은 지역 추가
};

function SearchPage() {
  const [location, setLocation] = useState('');
  const [district, setDistrict] = useState('');
  const [keyword, setKeyword] = useState('');
  const navigate = useNavigate();  // useHistory 대신 useNavigate 사용

  const handleSearch = (e) => {
    e.preventDefault();
    // location, district, keyword를 URL 쿼리 파라미터로 추가
    navigate(`/list?location=${location}&district=${district}&keyword=${keyword}`);
  };

  const handleLocationChange = (e) => {
    setLocation(e.target.value);
    setDistrict('');  // 지역 변경 시 시/군 초기화
  };

  return (
    <div className="search-page">
      <h1>여행지 검색</h1>
      <form onSubmit={handleSearch}>
        {/* 지역 선택 */}
        <select value={location} onChange={handleLocationChange}>
          <option value="">지역 선택</option>
          {Object.keys(cities).map((city) => (
            <option key={city} value={city}>{city}</option>
          ))}
        </select>

        {/* 시/군 선택 */}
        {location && (
          <select value={district} onChange={(e) => setDistrict(e.target.value)}>
            <option value="">시/군 선택</option>
            {cities[location].map((district) => (
              <option key={district} value={district}>{district}</option>
            ))}
          </select>
        )}

        {/* 키워드 검색 */}
        <input
          type="text"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="키워드 검색"
        />
        <button type="submit">검색하기</button>
      </form>
    </div>
  );
}

export default SearchPage;
