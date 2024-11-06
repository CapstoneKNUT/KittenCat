import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './StoreRead.css'; // 스타일 파일 추가

function StoreRead() {
    const { sno } = useParams();
    const [store, setStore] = useState(null);
    const [favorites, setFavorites] = useState(() => {
        const savedFavorites = localStorage.getItem('bookmarks');
        return savedFavorites ? JSON.parse(savedFavorites) : [];
    });
    const [isContentExpanded, setIsContentExpanded] = useState(false); // 내용 확장 상태 추가

    useEffect(() => {
        const fetchData = async (username, sno) => {
            try {
                const response = await axios.get('http://localhost:8080/api/store/read', {
                    params: {
                        username,
                        sno,
                    },
                });
                setStore(response.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        const user = JSON.parse(localStorage.getItem('user'));
        if (user && user.mid) {
            fetchData(user.mid, sno);
        }
    }, [sno]);

    if (!store) {
        return <p>Loading...</p>;
    }

    const toggleContent = () => {
        setIsContentExpanded(!isContentExpanded);
    };

    const maxLength = 300; // 최대 글자 수 설정

    return (
        <div className="detail-page">
            <div className="header">
                <h1>{store.p_name}</h1>
                <p>
                    {isContentExpanded || store.p_content.length <= maxLength
                        ? store.p_content
                        : `${store.p_content.substring(0, maxLength)}...`}
                    {!isContentExpanded && store.p_content.length > maxLength && (
                        <span onClick={toggleContent} className="read-more">
                            더보기
                        </span>
                    )}
                    {isContentExpanded && (
                        <span onClick={toggleContent} className="read-less">
                            간략히
                        </span>
                    )}
                </p>
                <hr className="content-separator" /> {/* 구분선 추가 */}
            </div>
            <div className="content">
                <img src={store.p_image} alt={store.p_name} className="store-image" />
                <div className="store-info">
                    <p><strong>주소 : </strong> {store.p_address}</p>
                    <p><strong>연락처 : </strong> {store.p_call}</p>
                    <p><strong>홈페이지 : </strong> <a href={store.p_site} target="_blank" rel="noopener noreferrer">{store.p_site}</a></p>
                    <p><strong>영업시간 : </strong>{store.p_opentime}</p>
                    <p><strong>주차 안내 : </strong> {store.p_park}</p>
                </div>
            </div>
        </div>
    );
}

export default StoreRead;
