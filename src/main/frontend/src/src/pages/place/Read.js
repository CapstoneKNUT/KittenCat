import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './Read.css';

function Read() {
  const { pord } = useParams();
  const [place, setPlace] = useState(null);
  const [bookmarks, setBookmarks] = useState(() => {
    const savedBookmark = localStorage.getItem('bookmarks');
    return savedBookmark ? JSON.parse(savedBookmark) : [];
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
  const toggleBookmark = async () => {
  const isBookmarked = bookmarks.some(bookmark => bookmark.pord === pord);

  try {
    if (isBookmarked) {
      // DELETE 요청 - pord만 사용
      const response = await axios.delete('http://localhost:8080/api/store/remove',{
        pord,
        username: user.mid
      });

      if (response.status === 200) {
        alert('북마크가 해제되었습니다.');
        const updatedBookmarks = bookmarks.filter(bookmark => bookmark.pord !== pord);
        setBookmarks(updatedBookmarks);
        localStorage.setItem('bookmarks', JSON.stringify(updatedBookmarks));
      }
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

  const maxLength = 300; // 최대 글자 수 설정

  const toggleContent = () => {
    setIsContentExpanded(!isContentExpanded);
  };

  return (
      <div className="detail-page">
        <div className="header">
          <h1>{place.p_name}</h1>
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
          <hr className="content-separator" /> {/* 구분선 추가 */}
        </div>
        <div className="content">
          <img className="store-image" src={place.p_image} alt={place.p_name} />
          <div className="store-info">
            <p><strong>주소:</strong> {place.p_address}</p>
            <p><strong>연락처:</strong> {place.p_call}</p>
            <p><strong>홈페이지:</strong> <a href={place.p_site} target="_blank" rel="noopener noreferrer">{place.p_site}</a></p>
            <p><strong>영업시간:</strong> {place.p_opentime}</p>
            <p><strong>주차 안내:</strong> {place.p_park}</p>
          </div>
        </div>
        <button className="favorite-button" onClick={() => toggleBookmark(place.pord)}>
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
      </div>
  );
}

export default Read;
