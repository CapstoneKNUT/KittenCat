import React, { useEffect, useState } from 'react';
import { useLocation, Link, useNavigate } from 'react-router-dom';
import axios from "axios";
import { useUser } from '../member/UserContext.js';
import './StoreList.css';

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
                params.p_name = keyword;
            } else if (searchType === 'address') {
                params.p_address = keyword;
            }

            const response = await axios.get('http://localhost:8080/api/store/list', { params });
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
                return item.p_name.includes(keywordInput);
            } else if (searchType === 'address') {
                return item.p_address.includes(keywordInput);
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

    const toggleBookmark = async (sno) => {
        if (!user || !user.mid) {
            alert('로그인이 필요합니다.');
            return;
        }

        const isBookmarked = bookmarks.some(bookmark => bookmark.sno === sno);

        try {
            const response = await axios.post('http://localhost:8080/api/store/remove', {
                sno,
                username: user.mid
            });

            if (response.status === 200) {
                alert('북마크가 해제되었습니다.');
                const updatedBookmarks = [...bookmarks, { sno }];
                setBookmarks(updatedBookmarks);
                localStorage.setItem('bookmarks', JSON.stringify(updatedBookmarks));
                // 페이지 새로고침
                window.location.reload();
            }
        } catch (error) {
            console.error('Error toggling bookmark:', error);
            alert('처리 중 오류가 발생했습니다.');
        }
    };

    return (
        <div className="results-page">
            <h2>{user ? `${user.m_name}님의 찜 목록` : "찜 목록"}</h2>

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
                        <li key={storeItem.sno}>
                            <Link to={`/store/read/${storeItem.sno}`}>
                                <div>{storeItem.p_name}</div>
                                <div>{storeItem.p_category}</div>
                                <div>{storeItem.p_address}</div>
                                <img src={storeItem.p_image} alt={storeItem.p_name} style={{width: '200px', hight: '200px'}}/>
                                <div>⭐ {storeItem.p_star}</div>
                            </Link>
                            <button onClick={() => toggleBookmark(storeItem.sno)}
                                    style={{background: 'none', border: 'none', cursor: 'pointer'}}>
                                {bookmarks.some((bookmark) => bookmark.sno === store.sno) ? (
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="pink"
                                         stroke="black" width="24px" height="24px">
                                        <path
                                            d="M12 21.7l-1.6-1.4C5.1 16.1 2 12.7 2 8.8 2 5.6 4.6 3 7.8 3c1.9 0 3.7 0.9 4.7 2.3C13.5 3.9 15.3 3 17.2 3 20.4 3 23 5.6 23 8.8c0 3.9-3.1 7.3-8.4 11.5L12 21.7z"/>
                                    </svg>
                                ) : (
                                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none"
                                         stroke="black" width="24px" height="24px">
                                        <path
                                            d="M12 21.7l-1.6-1.4C5.1 16.1 2 12.7 2 8.8 2 5.6 4.6 3 7.8 3c1.9 0 3.7 0.9 4.7 2.3C13.5 3.9 15.3 3 17.2 3 20.4 3 23 5.6 23 8.8c0 3.9-3.1 7.3-8.4 11.5L12 21.7z"/>
                                    </svg>
                                )}
                            </button>
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
