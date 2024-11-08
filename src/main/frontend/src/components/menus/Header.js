import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useUser } from '../../pages/member/UserContext.js';
import logoimages from './로고.jpg';
import './Header.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';

function Header() {
    const { user, logout } = useUser();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/main');
    };

    return (
        <div className='menubar fixed-header d-flex justify-content-between align-items-center'>
            <Link to="/main">
                <img src={logoimages} alt='Home' className='home' />
            </Link>
            <div className='nav-links d-flex align-items-center'>
                <div className='travel-info'><Link to="/place/list">여행지 정보</Link></div>
                <div className='travel-info'><Link to="/plan">일정 짜기</Link></div>
                <div className='travel-info'><Link to="/board/list">리뷰 목록</Link></div>
                <div className='travel-info'><Link to="/store/list">찜 목록</Link></div>
                <div className='travel-info'><Link to="/member/profile">마이 페이지</Link></div>
                <div className='menu-icons'>
                    {user ? (
                        <button onClick={handleLogout} className="btn btn-outline-primary">로그아웃</button>
                    ) : (
                        <Link to="/member/login" className="btn btn-outline-primary">로그인</Link>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Header;
