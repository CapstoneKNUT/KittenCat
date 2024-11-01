import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './Read.css';


function Read() {
  const { pord } = useParams();
  const [place, setPlace] = useState(null);  // null로 초기화
  const [favorites, setFavorites] = useState(() => {
    const savedFavorites = localStorage.getItem('bookmarks');
    return savedFavorites ? JSON.parse(savedFavorites) : [];
  });

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
  }, [pord]);  // pord를 의존성 배열에 추가

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

  // place가 로드되지 않았을 때 로딩 메시지 표시
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

  return (
    <div className="detail-page">
      <h1>{place.p_name}</h1>
      <p>{place.p_content}</p>
      <img src={place.p_image} alt={place.p_name} />
      <div className="store-info">
        <p><strong>주소:</strong> {place.p_address}</p>
        <p><strong>연락처:</strong> {place.p_call}</p>
        <p><strong>홈페이지:</strong> <a href={place.p_site} target="_blank" rel="noopener noreferrer">{place.p_site}</a></p>
        <p><strong>영업시간:</strong> {place.p_opentime}</p>
        <p><strong>주차 안내:</strong> {place.p_park}</p>
      </div>

    
      <button className="favorite-button" onClick={toggleFavorite} style={{ position: 'relative', background: 'none', border: 'none', cursor: 'pointer' }}>
        {isFavorite ? (
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="pink" stroke="black" width="24px" height="24px">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
          </svg>
        ) : (
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="black" width="24px" height="24px">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" />
          </svg>
        )}
      </button>
    </div>
  );
}

export default Read;
