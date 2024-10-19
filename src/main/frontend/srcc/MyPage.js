import React from 'react';
import './MyPage.css';  // 스타일 파일은 따로 관리

function MyPage() {
  return (
    <div className="mypage-container">
      <header className="mypage-header">
        <div className="profile">
          <div className="profile-pic">
            <img src="default-profile.png" alt="프로필 사진" />
          </div>
          <span className="profile-name">이름 없음</span>
        </div>
        <div className="actions">
          <button className="logout-btn">로그아웃</button>
          <button className="delete-account-btn">회원탈퇴</button>
        </div>
      </header>

      <nav className="mypage-nav">
        <ul>
          <li>내 리뷰</li>
          <li>내 일정</li>
          <li>내정보 수정</li>
        </ul>
      </nav>

      <main className="mypage-content">
        <h2>여행지 즐겨찾기 <span>0</span></h2>
        <div className="favorites-filter">
          <button>전체</button>
          <button>관광지</button>
          <button>음식점</button>
          <button>숙소</button>
          <button>캠핑장</button>
          <button>쇼핑</button>
          <button>호텔링</button>
        </div>
        <div className="favorites-empty">
          <img src="empty-list.png" alt="빈 목록" />
          <p>즐겨찾기한 목록이 없습니다.</p>
        </div>
      </main>
    </div>
  );
}

export default MyPage;
