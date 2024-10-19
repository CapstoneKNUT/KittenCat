import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import './Lists.css';
import { area } from './Area';

function Lists() {
  const [places, setPlaces] = useState([]);
  const [bookmarks, setBookmarks] = useState(() => {
    const savedBookmarks = localStorage.getItem('bookmarks');
    return savedBookmarks ? JSON.parse(savedBookmarks) : [];
  });

  const [selectedArea, setSelectedArea] = useState('');
  const [selectedSubArea, setSelectedSubArea] = useState('');
  const [keywordInput, setKeywordInput] = useState('');
  const location = useLocation();
  const navigate = useNavigate();

  const [pageRequest, setPageRequest] = useState({
    size: 10,
  });
  const [responseData, setResponseData] = useState({
    dtoList: [],
    prev: false,
    next: false,
    start: 0,
    end: 0,
    page: 1,
  });

  useEffect(() => {
    // 데이터를 받아오는 함수
    const fetchData = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/place/list', {
          params: pageRequest,
        });
        if (response.data?.dtoList) {
          setResponseData(response.data);
        } else {
          console.error('Invalid data structure received from API.');
        }
      } catch (error) {
        console.log(error);
      }
    };

    fetchData();
  }, [pageRequest]);

  const handlePageChange = (pageNum) => {
    setPageRequest((prev) => ({
      ...prev,
      page: pageNum,
    }));
  };

  // 하위 지역 필터링 함수
  const filteredSubArea = selectedArea
    ? area.find((a) => a.name === selectedArea)?.subArea || []
    : [];

  // 검색 기능
  const handleSearch = (e) => {
    e.preventDefault();

    const searchParams = new URLSearchParams();
    if (selectedArea) searchParams.append('location', selectedArea);
    if (selectedSubArea && selectedSubArea !== '지역 전체') searchParams.append('district', selectedSubArea);
    if (keywordInput) searchParams.append('keyword', keywordInput);

    navigate(`?${searchParams.toString()}`);
  };

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const locationParam = searchParams.get('location') || '';
    const districtParam = searchParams.get('district') || '';
    const keywordParam = searchParams.get('keyword') || '';

    const filteredPlaces = responseData.dtoList.filter((item) => {
      const matchesLocation = locationParam ? item.p_add.includes(locationParam) : true;
      const matchesDistrict = districtParam ? item.p_add.includes(districtParam) : true;
      const matchesKeyword = keywordParam ? item.p_name.includes(keywordParam) : true;

      return matchesLocation && matchesDistrict && matchesKeyword;
    });

    if (filteredPlaces.length > 0) {
      setPlaces(filteredPlaces);
    } else {
      setPlaces(responseData.dtoList);
    }
  }, [location, responseData.dtoList]);

  const toggleBookmark = (pord) => {
    // places 배열이 비어있으면 함수 종료
    if (!places || places.length === 0) {
      console.error('No places available.');
      return;
    }

    const bookmarkItem = places.find(item => item.pord === pord);

    if (!bookmarkItem) {
      console.error(`Bookmark item with pord ${pord} not found.`);
      return; // 찾지 못했을 경우 함수 종료
    }

    const isBookmarked = bookmarks && bookmarks.some(bookmark => bookmark.pord === pord);
    const updatedBookmarks = isBookmarked
      ? bookmarks.filter(bookmark => bookmark && bookmark.pord !== pord)
      : [...bookmarks, bookmarkItem];

    setBookmarks(updatedBookmarks);
    localStorage.setItem('bookmarks', JSON.stringify(updatedBookmarks));
    window.dispatchEvent(new Event('storage'));
  };

  return (
    <div className="results-page">
      <h2>검색 결과</h2>
      <form className="search-form" onSubmit={handleSearch}>
        <select value={selectedArea} onChange={(e) => setSelectedArea(e.target.value)}>
          <option value="">지역 선택</option>
          {area.map((a) => (
            <option key={a.name} value={a.name}>
              {a.name}
            </option>
          ))}
        </select>

        <select value={selectedSubArea} onChange={(e) => setSelectedSubArea(e.target.value)} disabled={!selectedArea}>
          <option value="">시/구/군</option>
          <option value="지역 전체">지역 전체</option>
          {filteredSubArea.map((sub, index) => (
            <option key={index} value={sub}>
              {sub}
            </option>
          ))}
        </select>

        <input
          type="text"
          value={keywordInput}
          onChange={(e) => setKeywordInput(e.target.value)}
          placeholder="키워드 검색"
        />
        <button type="submit">검색</button>
      </form>

      <ul className="results-list">
        {places && places.map((place) => (
          <li key={place.pord}>
            <Link to={`/place/read/${place.pord}`}>
              <div>{place.p_name}</div>
              <div>{place.p_category}</div>
              <div>{place.p_address}</div>
              <img src={place.p_image} alt={place.p_name} style={{ width: '100px' }} />
              <div>⭐ {place.p_star}</div>
            </Link>
            <button onClick={() => toggleBookmark(place.pord)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
              {bookmarks && bookmarks.some(bookmark => bookmark && bookmark.pord === place.pord) ? (
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="pink" stroke="black" width="24px" height="24px">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                </svg>
              ) : (
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="black" width="24px" height="24px">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
                </svg>
              )}
            </button>
          </li>
        ))}
      </ul>
      <div className="float-end">
        <ul className="pagination flex-wrap">
          {responseData.prev && (
            <li className="page-item">
              <button
                className="page-link"
                onClick={() => handlePageChange(responseData.start - 1)}
              >
                Previous
              </button>
            </li>
          )}
          {Array.from({ length: responseData.end - responseData.start + 1 }).map((_, index) => (
            <li
              className={`page-item ${responseData.page === responseData.start + index ? 'active' : ''}`}
              key={index}
            >
              <button
                className="page-link"
                onClick={() => handlePageChange(responseData.start + index)}
              >
                {responseData.start + index}
              </button>
            </li>
          ))}
          {responseData.next && (
            <li className="page-item">
              <button
                className="page-link"
                onClick={() => handlePageChange(responseData.end + 1)}
              >
                Next
              </button>
            </li>
          )}
        </ul>
      </div>
    </div>
  );
}

export default Lists;
