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
                <p className="category">{store.p_category}</p>
                {store.p_content && store.p_content.length > 0 ? (
                    <div>
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
                    </div>
                ) : (
                    <div>내용이 없습니다.</div>
                )}
                <hr className="content-separator"/>
                {/* 구분선 추가 */}
            </div>
            <div className="content">
                <img className="store-image" src={store.p_image} alt={store.p_name} />
                <div className="store-info">
                    <div className="contact-info">
                        <div>
                            <p><strong>주소:</strong>
                                <pre>{store.p_address}</pre>
                            </p>
                        </div>
                        <div>
                            <p><strong>연락처:</strong>
                                <pre>{store.p_call}</pre>
                            </p>
                        </div>
                        <div>
                            <p><strong>홈페이지:</strong> <a href={store.p_site} target="_blank"
                                                         rel="noopener noreferrer">{store.p_site}</a></p>
                        </div>
                    </div>
                </div>
            </div>
            <div className="info-section">
                <div className="operating-hours">
                    <strong>영업시간:</strong>
                    <pre>{store.p_opentime}</pre>
                </div>
                <div className="parking-info">
                    <strong>주차 안내:</strong>
                    <pre>{store.p_park}</pre>
                </div>
            </div>
        </div>
    );
}

export default StoreRead;
