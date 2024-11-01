import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

function Store() {
  const [bookmarks, setBookmarks] = useState(() => {
    const savedBookmarks = localStorage.getItem('bookmarks');
    return savedBookmarks ? JSON.parse(savedBookmarks) : [];
  });

  useEffect(() => {
    const handleStorageChange = () => {
      const updatedBookmarks = localStorage.getItem('bookmarks');
      setBookmarks(updatedBookmarks ? JSON.parse(updatedBookmarks) : []);
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const removeBookmark = (p_ord) => {
    // p_ord를 기준으로 특정 항목만 필터링하여 삭제
    const updatedBookmarks = bookmarks.filter(bookmark => bookmark.p_ord !== p_ord);
    setBookmarks(updatedBookmarks);
    localStorage.setItem('bookmarks', JSON.stringify(updatedBookmarks));
    window.dispatchEvent(new Event('storage'));
  };

  return (
    <div className="bookmarks-page">
      <h2>찜한 여행지 목록</h2>
      {bookmarks.length === 0 ? (
        <p>찜한 여행지가 없습니다.</p>
      ) : (
        <ul className="bookmarks-list">
          {bookmarks.map((bookmark) => (
            <li key={bookmark.p_ord}>
              <Link to={`/detail/${bookmark.p_ord}`}>
                <div>{bookmark.p_name}</div>
                <div>{bookmark.p_category}</div>
                <div>{bookmark.p_location}</div>
                <div>별점: ⭐ {bookmark.p_star}</div>
                {bookmark.p_image && <img src={bookmark.p_image} alt={bookmark.p_name} />}
              </Link>
              <button onClick={() => removeBookmark(bookmark.p_ord)}>삭제</button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default Store;
