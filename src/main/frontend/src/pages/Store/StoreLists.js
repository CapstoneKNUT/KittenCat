import React, { useEffect, useState } from 'react';
import { useLocation, Link, useNavigate } from 'react-router-dom';
import axios from "axios";
import { useUser } from '../member/UserContext.js';
import { area } from "../place/Area";

function StoreLists() {
    const { user } = useUser();
    const [store, setStore] = useState([]);
    const [selectedArea, setSelectedArea] = useState('');
    const [selectedSubArea, setSelectedSubArea] = useState('');
    const [keywordInput, setKeywordInput] = useState('');
    const [p_area, setP_area] = useState('');
    const [p_subArea, setP_subArea] = useState('');
    const [p_keyword, setP_keyword] = useState('');
    const [places, setPlaces] = useState([]);
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

    const fetchData = async (username) => {
        try {
            const response = await axios.get('http://localhost:8080/api/store/list', {
                params: {
                    username,
                },
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

    useEffect(() => {
        if (user) {
            fetchData(user.mid);
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

    const filteredSubArea = selectedArea
        ? area.find((a) => a.name === selectedArea)?.subArea || []
        : [];

    const handleSearch = async (e) => {
        e.preventDefault();

        const p_area = selectedArea;
        const p_subArea = selectedSubArea !== '지역 전체' ? selectedSubArea : '';
        const p_keyword = keywordInput;

        try {
            const response = await axios.post('http://localhost:8080/api/store/list', {
                p_area,
                p_subArea,
                p_keyword,
            });
            setResponseData(response.data);
        } catch (error) {
            console.error('에러 발생:', error);
        }
    };

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const locationParam = searchParams.get('location') || '';
        const districtParam = searchParams.get('district') || '';
        const keywordParam = searchParams.get('keyword') || '';

        const filteredStores = responseData.dtoList.filter((item) => {
            const matchesLocation = locationParam ? item.p_address.includes(locationParam) : true;
            const matchesDistrict = districtParam ? item.p_address.includes(districtParam) : true;
            const matchesKeyword = keywordParam ? item.p_name.includes(keywordParam) : true;

            return matchesLocation && matchesDistrict && matchesKeyword;
        });

        if (filteredStores.length > 0) {
            setStore(filteredStores);
        } else {
            setStore(responseData.dtoList);
        }
    }, [location, responseData.dtoList]);

    const toggleBookmark = async (sno) => {
        if (!user || !user.mid) {
            alert('로그인이 필요합니다.');
            return;
        }

        const isBookmarked = bookmarks.some(bookmark => bookmark.sno === sno);

        try {
            if (isBookmarked) {
                // 북마크 제거
                const response = await axios.delete('http://localhost:8080/api/store/remove', {
                    params: {
                        username: user.mid.mid,
                        sno
                    }
                });

                if (response.status === 200) {
                    alert('북마크가 제거되었습니다.');
                    const updatedBookmarks = [...bookmarks, {sno}];
                    setBookmarks(updatedBookmarks);
                    localStorage.setItem('bookmarks', JSON.stringify(updatedBookmarks));
                }
            }else{

            }
        } catch (error) {
            console.error('Error toggling bookmark:', error);
            alert('처리 중 오류가 발생했습니다.' + error);
        }
    };

    return (
        <div className="results-page">
            <h2>{user ? `${user.m_name}님의 찜 목록` : "찜 목록"}</h2>
            <form className="search-form" onSubmit={handleSearch}>
                <select value={selectedArea} onChange={(e) => { setSelectedArea(e.target.value); setP_area(e.target.value); }}>
                    <option value="">지역 선택</option>
                    {area.map((a) => (
                        <option key={a.name} value={a.name}>
                            {a.name}
                        </option>
                    ))}
                </select>

                <select value={selectedSubArea} onChange={(e) => { setSelectedSubArea(e.target.value); setP_subArea(e.target.value); }} disabled={!selectedArea}>
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
                    onChange={(e) => { setKeywordInput(e.target.value); setP_keyword(e.target.value); }}
                    placeholder="이름 검색"
                />
                <button type="submit">검색</button>
            </form>

            {store.length > 0 ? (
                <ul className="results-list">
                    {store.map((storeItem) => (
                        <li key={storeItem.sno}>
                            <Link to={`/store/read/${storeItem.sno}`}>
                                <div>{storeItem.p_name}</div>
                                <div>{storeItem.p_category}</div>
                                <div>{storeItem.p_address}</div>
                                <img src={storeItem.p_image} alt={storeItem.p_name} style={{width: '100px'}}/>
                                <div>⭐ {storeItem.p_star}</div>
                            </Link>
                            <button onClick={() => toggleBookmark(store.sno)}
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
                    {Array.from({length: responseData.end - responseData.start + 1}).map((_, index) => (
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

export default StoreLists;
