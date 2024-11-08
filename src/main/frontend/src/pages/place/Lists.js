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
  const username = user ? user.mid : null;

  const [pageRequest, setPageRequest] = useState({
    page: 1,
    size: 12,
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

    setPageRequest({ page: 1, size: 12 });

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
    if (!user || !user.mid) {
        alert('로그인이 필요합니다.');
        return;
    }

    const isBookmarked = bookmarks.some(bookmark => bookmark.pord === pord);

    try {
        if (isBookmarked) {
            alert('북마크 해제는 찜목록에서 하실 수 있습니다.');
        } else {
            // 북마크 추가
            const response = await axios.post('http://localhost:8080/api/place/register', {
                pord,
                username: user.mid
            });

            if (response.status === 200) {
                alert('북마크가 등록되었습니다.');
                const updatedBookmarks = [...bookmarks, { pord }];
                setBookmarks(updatedBookmarks);
                localStorage.setItem('bookmarks', JSON.stringify(updatedBookmarks));
            }
        }
    } catch (error) {
        console.error('Error toggling bookmark:', error);
        alert('처리 중 오류가 발생했습니다.');
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
                        <path d="M12 21.7l-1.6-1.4C5.1 16.1 2 12.7 2 8.8 2 5.6 4.6 3 7.8 3c1.9 0 3.7 0.9 4.7 2.3C13.5 3.9 15.3 3 17.2 3 20.4 3 23 5.6 23 8.8c0 3.9-3.1 7.3-8.4 11.5L12 21.7z" />
                      </svg>
                  ) : (
                      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="black" width="24px" height="24px">
                        <path d="M12 21.7l-1.6-1.4C5.1 16.1 2 12.7 2 8.8 2 5.6 4.6 3 7.8 3c1.9 0 3.7 0.9 4.7 2.3C13.5 3.9 15.3 3 17.2 3 20.4 3 23 5.6 23 8.8c0 3.9-3.1 7.3-8.4 11.5L12 21.7z" />
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
