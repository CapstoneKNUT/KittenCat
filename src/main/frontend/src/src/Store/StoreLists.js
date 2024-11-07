import React, { useEffect, useState } from 'react';
import { useLocation, Link } from 'react-router-dom';
import axios from "axios";
import { useUser } from '../pages/member/UserContext.js';
import { area } from "../pages/place/Area";

function StoreLists() {
    const { user } = useUser();
    const [store, setStore] = useState([]);
    const [selectedArea, setSelectedArea] = useState('');
    const [selectedSubArea, setSelectedSubArea] = useState('');
    const [keywordInput, setKeywordInput] = useState('');
    const [p_area, setP_area] = useState('');
    const [p_subArea, setP_subArea] = useState('');
    const [p_keyword, setP_keyword] = useState('');

    const [pageRequest, setPageRequest] = useState({
        size: 10,
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
        }
    }, [user, pageRequest]);

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

    return (
        <div className="results-page">
            <h2>검색 결과</h2>
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

            <ul className="results-list">
                {store && store.map((store) => (
                    <li key={store.sno}>
                        <Link to={`/store/read/${store.sno}`}>
                            <div>{store.p_name}</div>
                            <div>{store.p_category}</div>
                            <div>{store.p_address}</div>
                            <img src={store.p_image} alt={store.p_name} style={{ width: '100px' }} />
                            <div>⭐ {store.p_star}</div>
                        </Link>
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

export default StoreLists;
