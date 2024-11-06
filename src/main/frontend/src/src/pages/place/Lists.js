import React, { useEffect, useState, useCallback } from 'react';
import axios from 'axios';
import { useLocation, Link } from 'react-router-dom';
import './Lists.css';
import { area } from './Area';
import { useUser } from '../member/UserContext';

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
  const { user } = useUser();
  const username = user ? user.username : null;

  const [pageRequest, setPageRequest] = useState({
    page: 1,
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

  const fetchData = useCallback(async () => {
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
      console.error('Error fetching data:', error);
    }
  }, [pageRequest]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handlePageChange = (pageNum) => {
    setPageRequest((prev) => ({
      ...prev,
      page: pageNum,
    }));
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    const p_area = selectedArea;
    const p_subArea = selectedSubArea !== '지역 전체' ? selectedSubArea : '';
    const p_keyword = keywordInput;

    setPageRequest({ page: 1, size: 10 });

    try {
      const response = await axios.post('http://localhost:8080/api/place/list', {
        p_area,
        p_subArea,
        p_category: '',
        p_count: 20,
        p_keyword,
      });
      setResponseData(response.data);
    } catch (error) {
      console.error('Error during search:', error);
    }
  };

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const locationParam = searchParams.get('location') || '';
    const districtParam = searchParams.get('district') || '';
    const keywordParam = searchParams.get('keyword') || '';

    const filteredPlaces = responseData.dtoList.filter((item) => {
      const matchesLocation = locationParam ? item.p_address.includes(locationParam) : true;
      const matchesDistrict = districtParam ? item.p_address.includes(districtParam) : true;
      const matchesKeyword = keywordParam ? item.p_name.includes(keywordParam) : true;

      return matchesLocation && matchesDistrict && matchesKeyword;
    });

    setPlaces(filteredPlaces.length > 0 ? filteredPlaces : responseData.dtoList);
  }, [location, responseData.dtoList]);

  const toggleBookmark = async (pord) => {
    /*if (!user || !user.username) {
      alert('로그인이 필요합니다.');
      return;
    }*/

    const username = user.username;

    try {
      const response = await axios.post('http://localhost:8080/api/place/register', { pord, username });
      if (response.status === 200) {
        alert('북마크가 등록되었습니다.');
        setBookmarks((prev) => [...prev, { pord }]);
        localStorage.setItem('bookmarks', JSON.stringify([...bookmarks, { pord }]));
      } else {
        alert('북마크 등록에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error registering bookmark:', error);
    }
  };

  const filteredSubArea = selectedArea
      ? area.find((a) => a.name === selectedArea)?.subArea || []
      : [];

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

          <select
              value={selectedSubArea}
              onChange={(e) => setSelectedSubArea(e.target.value)}
              disabled={!selectedArea}
          >
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
          {places.map((place) => (
              <li key={place.pord}>
                <Link to={`/place/read/${place.pord}`}>
                  <div>{place.p_name}</div>
                  <div>{place.p_category}</div>
                  <div>{place.p_address}</div>
                  <img src={place.p_image} alt={place.p_name} style={{ width: '100px' }} />
                  <div>⭐ {place.p_star}</div>
                </Link>
                <button onClick={() => toggleBookmark(place.pord)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                  {bookmarks.some((bookmark) => bookmark.pord === place.pord) ? (
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

        <div className="pagination">
          {responseData.prev && <button onClick={() => handlePageChange(responseData.page - 1)}>이전</button>}
          {Array.from({ length: responseData.end - responseData.start + 1 }, (_, index) => (
              <button key={index + responseData.start} onClick={() => handlePageChange(index + responseData.start)}>
                {index + responseData.start}
              </button>
          ))}
          {responseData.next && <button onClick={() => handlePageChange(responseData.page + 1)}>다음</button>}
        </div>
      </div>
  );
}

export default Lists;
