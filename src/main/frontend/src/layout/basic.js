import React from 'react';
import { Link } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';

const Header = () => (
    <div className="header">
        <Link className="header-item" to="/main">🏠 Home</Link>
        <Link className="header-item" to="/place/list">여행지 정보</Link>
        <Link className="header-item" to="/board/list">리뷰 목록</Link>
        <Link className="header-item" to="/mypage">마이 페이지</Link>
        <div className="header-icons">
            <Link to="/profile">
                <img src="/assets/user.jpg" alt="Profile" className="icon" />
            </Link>
            <Link to="/settings">
                <img src="/assets/setting.jpg" alt="Settings" className="icon" />
            </Link>
        </div>
    </div>
);

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

export default Layout;
