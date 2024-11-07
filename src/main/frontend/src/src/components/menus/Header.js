// components/Header.js
import React from 'react';
import { Link } from 'react-router-dom';
// Menubar.js
import profileimages from './회원.jpg';
import settingimages from './설정.jpg';
import logoimages from './로고.jpg';
import './Header.css'
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min'; // 메뉴바 전용 CSS 파일 (선택 사항)

function Header() {
  return (
    <div className='menubar'>
      {/* Link 컴포넌트를 사용하여 각 메뉴 항목을 클릭할 때 이동할 경로를 지정합니다. */}
      <div>
        <Link to="/main">
            <img src={logoimages} alt='Home' className='home' />
        </Link>
      </div>
      <div className='travel-info'>
        <Link to="/place/list">여행지 정보</Link>
      </div>
      <div className='travel-info'>
        <Link to="/plan">일정 짜기</Link>
      </div>
      <div className='travel-info'>
        <Link to="/board/list">리뷰 목록</Link>
      </div>
      <div className='travel-info'>
        <Link to="/store/list">찜 목록</Link>
      </div>
      <div className='travel-info'>
        <Link to="/member/profile">마이 페이지</Link>
      </div>
      <div className='menu-icons'>
        <Link to="/member">
          <img src={profileimages} alt='Profile' className='profile' />
        </Link>
        <img src={settingimages} alt='Setting' className='setting' />
      </div>
    </div>
  );
}

const Layout = ({ children }) => (
  <div className="d-flex" id="wrapper">
      <div id="page-content-wrapper">
          <Header />
          {/* Page content */}
          <div className="container-fluid">
              {children}
          </div>
      </div>
  </div>
);

export default Header;

