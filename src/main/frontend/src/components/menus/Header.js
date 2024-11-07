// components/Header.js
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useUser } from '../../pages/member/UserContext.js';
import profileimages from './회원.jpg';
import settingimages from './설정.jpg';
import logoimages from './로고.jpg';
import './Header.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';

function Header() {
    const { user, logout } = useUser(); // user와 logout 함수 가져오기
    const navigate = useNavigate();

    const handleLogout = () => {
        logout(); // 로그아웃 함수 호출
        navigate('/'); // 홈으로 리디렉션
    };

    return (
        <div className='menubar'>
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
            {/*<div className='travel-info'>
                <Link to="/member/profile">마이 페이지</Link>
            </div>*/}
            <div className='menu-icons'>
                <Link to="/member/profile">
                    <img src={profileimages} alt='Profile' className='profile' />
                </Link>
                {/*<img src={settingimages} alt='Setting' className='setting' />*/}
                {user ? (
                    <button onClick={handleLogout} className="btn btn-outline-primary ml-2">로그아웃</button>
                ) : (
                    <Link to="/member/login" className="btn btn-outline-primary ml-2">로그인</Link>
                )}
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
