import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './Read.css';

function Read() {
    const { pord } = useParams();
    const [place, setPlace] = useState(null);
    const [favorites, setFavorites] = useState(() => {
        const savedFavorites = localStorage.getItem('bookmarks');
        return savedFavorites ? JSON.parse(savedFavorites) : [];
    });
    const [isContentExpanded, setIsContentExpanded] = useState(false); // 내용 확장 상태 추가

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/place/read?pord=${pord}`);
                setPlace(response.data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData();
    }, [pord]);

    useEffect(() => {
        const handleStorageChange = () => {
            const savedFavorites = localStorage.getItem('bookmarks');
            if (savedFavorites) {
                setFavorites(JSON.parse(savedFavorites));
            }
        };

        window.addEventListener('storage', handleStorageChange);
        return () => {
            window.removeEventListener('storage', handleStorageChange);
        };
    }, []);

    if (!place) {
        return <div>로딩 중...</div>;
    }

    const isFavorite = favorites.some(fav => fav && fav.pord === parseInt(pord));

    const toggleFavorite = () => {
        let updatedFavorites;

        if (isFavorite) {
            updatedFavorites = favorites.filter(fav => fav && fav.pord !== parseInt(pord));
        } else {
            const newFavorite = {
                pord: parseInt(pord),
                p_name: place.p_name,
                p_address: place.p_address,
                p_call: place.p_call,
                p_site: place.p_site,
                p_opentime: place.p_opentime,
                p_category: place.p_category,
                p_park: place.p_park,
            };
            updatedFavorites = [...favorites, newFavorite];
        }

        setFavorites(updatedFavorites);
        localStorage.setItem('bookmarks', JSON.stringify(updatedFavorites));
        window.dispatchEvent(new Event('storage'));
    };

    const maxLength = 300; // 최대 글자 수 설정

    const toggleContent = () => {
        setIsContentExpanded(!isContentExpanded);
    };

    return (
        <div className="detail-page">
            <div className="header">
                <h1>{place.p_name}</h1>
                <p className="category">{place.p_category}</p>
                {place.p_content && place.p_content.length > 0 ? (
                    <div>
                        <p>
                            {isContentExpanded || place.p_content.length <= maxLength
                                ? place.p_content
                                : `${place.p_content.substring(0, maxLength)}...`}
                            {!isContentExpanded && place.p_content.length > maxLength && (
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
                <img className="store-image" src={place.p_image} alt={place.p_name} />
                <div className="store-info">
                    <div className="contact-info">
                        <div>
                            <p><strong>주소:</strong>
                                <pre>{place.p_address}</pre>
                            </p>
                        </div>
                        <div>
                            <p><strong>연락처:</strong>
                                <pre>{place.p_call}</pre>
                            </p>
                        </div>
                        <div>
                            <p><strong>홈페이지:</strong> <a href={place.p_site} target="_blank"
                                                         rel="noopener noreferrer">{place.p_site}</a></p>
                        </div>
                    </div>
                </div>
            </div>
            <div className="info-section">
                <div className="operating-hours">
                    <strong>영업시간:</strong>
                    <pre>{place.p_opentime}</pre>
                </div>
                <div className="parking-info">
                    <strong>주차 안내:</strong>
                    <pre>{place.p_park}</pre>
                </div>
            </div>
        </div>
    );
}

export default Read;
