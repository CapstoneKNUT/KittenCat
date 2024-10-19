import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import './Lists.css';  // CSS 파일을 임포트

function Lists() {
  const [results, setResults] = useState([]);
  const [locationInput, setLocationInput] = useState('');
  const [districtInput, setDistrictInput] = useState('');
  const [keywordInput, setKeywordInput] = useState('');
  const location = useLocation();
  const navigate = useNavigate(); // useHistory 대신 useNavigate 사용

  // "여행지"와 관련된 단어 리스트
  const relatedTravelWords = ['attraction', 'beach', 'park', 'mountain', 'museum', 'cafe', 'landmark'];

  // 검색 기능
  const handleSearch = (e) => {
    e.preventDefault();

    const searchParams = new URLSearchParams();
    if (locationInput) searchParams.append('location', locationInput);
    if (districtInput) searchParams.append('district', districtInput);
    if (keywordInput) searchParams.append('keyword', keywordInput);
    
    navigate(`?${searchParams.toString()}`); // useNavigate로 페이지 이동
  };

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const locationParam = searchParams.get('location') || '';
    const districtParam = searchParams.get('district') || '';
    const keywordParam = searchParams.get('keyword') || '';

    const allResults = [
      { id: 1, name: '서울 남산타워', category: 'attraction', location: '서울', district: '중구' },
      { id: 2, name: '서울 대공원', category: 'attraction', location: '서울', district: '과천시' },
      { id: 3, name: '부산 해운대', category: 'beach', location: '부산', district: '해운대구' },
      { id: 4, name: '서울 타워 카페', category: 'cafe', location: '서울', district: '종로구' },
      { id: 5, name: '강릉 커피숍', category: 'cafe', location: '강원도', district: '강릉시' },
    ];

    // 검색 조건이 모두 비어 있으면 모든 항목을 보여줌
    if (!locationParam && !districtParam && !keywordParam) {
      setResults(allResults);
      return;
    }

    // 검색 조건이 있을 경우 필터링
    const filteredResults = allResults.filter((item) => {
      const matchesLocation = locationParam ? item.location.includes(locationParam) : true;
      const matchesDistrict = districtParam ? item.district.includes(districtParam) : true;
      const matchesKeyword = keywordParam ? item.name.includes(keywordParam) : true;

      // 카테고리 또는 이름에 여행지 관련 단어가 있는지 확인
      const isTravelRelated = relatedTravelWords.some((word) =>
        item.category.includes(word) || item.name.includes(word)
      );

      return matchesDistrict && matchesLocation && matchesKeyword && isTravelRelated;
    });

    setResults(filteredResults);
  }, [location, relatedTravelWords]);

  return (
    <div className="results-page">
      <h2>검색 결과</h2>
      
      {/* 검색창 */}
      <form className="search-form" onSubmit={handleSearch}>
        <input
          type="text"
          value={districtInput}
          onChange={(e) => setDistrictInput(e.target.value)}
          placeholder="위치 검색" // 위치 검색 (구/군)
        />
        <input
          type="text"
          value={locationInput}
          onChange={(e) => setLocationInput(e.target.value)}
          placeholder="지역 검색" // 지역 검색 (시/도)
        />
        <input
          type="text"
          value={keywordInput}
          onChange={(e) => setKeywordInput(e.target.value)}
          placeholder="키워드 검색"
        />
        <button type="submit">검색</button>
      </form>
      
      <ul className="results-list">
        {results.map((item) => (
          <li key={item.id}>
            <Link to={`/detail/${item.id}`}>
              <div>{item.name}</div>
              <div>{item.category}</div>
              <div>{item.location} - {item.district}</div>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Lists;
