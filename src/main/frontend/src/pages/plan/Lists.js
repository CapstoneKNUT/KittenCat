import React, { useEffect, useState } from 'react';
import { useLocation, Link, useNavigate } from 'react-router-dom';
import axios from "axios";
import { useUser } from '../member/UserContext.js';

function StoreLists() {
  const { user } = useUser();
  const [store, setStore] = useState([]);
  const [selectedArea, setSelectedArea] = useState('');
  const [keywordInput, setKeywordInput] = useState('');
  const [searchType, setSearchType] = useState('name');
  const [bookmarks, setBookmarks] = useState(() => {
    const savedBookmarks = localStorage.getItem('bookmarks');
    return savedBookmarks ? JSON.parse(savedBookmarks) : [];
  });

  const [pageRequest, setPageRequest] = useState({
    size: 12,
    page: 1,
  });
  const [responseData, setResponseData] = useState({
    dtoList: [],
    prev: false,
    next: false,
    start: 0,
    end: 0,
    page: 1,
  });

  const location = useLocation();
  const navigate = useNavigate();

  // API 호출 함수
  const fetchData = async (username, searchType = 'name', keyword = '') => {
    try {
      const params = {
        username,
        page: pageRequest.page,
        size: pageRequest.size,
      };

      if (searchType === 'name') {
        params.title = keyword;
      } else if (searchType === 'address') {
        params.ps_startDate = keyword;
      }

      const response = await axios.get('http://localhost:8080/api/plan/list', { params });
      if (response.data?.dtoList) {
        setResponseData(response.data);
        setStore(response.data.dtoList);  // 받은 데이터로 store 상태 업데이트
      } else {
        console.error('Invalid data structure received from API.');
      }
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    if (user) {
      fetchData(user.mid);  // 사용자 정보가 있을 경우 데이터 fetch
    } else {
      alert("로그인 후 이용해주세요.");
      navigate('/member/login');
    }
  }, [user, pageRequest, navigate]);

  const handlePageChange = (pageNum) => {
    setPageRequest((prev) => ({
      ...prev,
      page: pageNum,
    }));
  };

  const handleSearch = (e) => {
    e.preventDefault();

    if (!keywordInput) {
      alert("검색어를 입력해주세요.");
      return;
    }

    // 검색을 수행한 후, 기존 store 데이터에 필터링된 결과를 적용
    const filteredStores = responseData.dtoList.filter((item) => {
      if (searchType === 'name') {
        return item.title.includes(keywordInput);
      } else if (searchType === 'address') {
        return item.ps_startDate.includes(keywordInput);
      }
      return true;
    });

    setStore(filteredStores);  // 필터링된 결과로 store 상태 업데이트
  };

  const handleResetSearch = () => {
    setKeywordInput('');
    setSearchType('name'); // 기본 검색 타입으로 초기화
    fetchData(user.mid); // 데이터를 다시 불러오기
  };

  return (
      <div className="results-page">
        <h2>{user ? `${user.m_name}님의 여행지 일정 목록` : "여행지 일정 목록"}</h2>

        {/* 검색 폼 */}
        <form className="search-form" onSubmit={handleSearch}>
          <select value={searchType} onChange={(e) => setSearchType(e.target.value)}>
            <option value="name">이름으로 검색</option>
            <option value="address">주소로 검색</option>
          </select>

          <input
              type="text"
              value={keywordInput}
              onChange={(e) => setKeywordInput(e.target.value)}
              placeholder="검색어를 입력하세요"
          />

          <button type="submit">검색</button>
        </form>

        {/* 검색 초기화 버튼 추가 */}
        <button onClick={handleResetSearch} style={{ marginTop: '10px' }}>
          검색 초기화
        </button>

        {store.length > 0 ? (
            <ul className="results-list">
              {store.map((storeItem) => (
                  <li key={storeItem.planNo}>
                    <Link to={`/plan/read/${storeItem.planNo}`}>
                      <div>{storeItem.title}</div>
                      <div>{storeItem.writer}</div>
                      <div>{storeItem.ps_startDate}</div>
                    </Link>
                  </li>
              ))}
            </ul>
        ) : (
            <div className="no-results">찜한 항목이 없습니다.</div>
        )}


        {/* 페이지네이션 */}
        <div className="pagination">
          {responseData.prev && (
              <button onClick={() => handlePageChange(responseData.page - 1)}>이전</button>
          )}
          {Array.from({ length: responseData.end - responseData.start + 1 }, (_, index) => (
              <button key={index + responseData.start} onClick={() => handlePageChange(index + responseData.start)}>
                {index + responseData.start}
              </button>
          ))}
          {responseData.next && (
              <button onClick={() => handlePageChange(responseData.page + 1)}>다음</button>
          )}
        </div>
      </div>
  );
}

export default StoreLists;
