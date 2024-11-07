import React, { useEffect } from 'react';
import { useUser } from './UserContext';
import { useNavigate } from 'react-router-dom';
import './UserProfile.css';

const UserProfile = () => {
    const { user, logout } = useUser();
    const navigate = useNavigate();

    useEffect(() => {
        if (user === null) {
            navigate('/member/login');
        }
    }, [user, navigate]);

    const handleLogout = () => {
        logout();
        navigate('/member/login');
    };

    const handleModify = () => {
        navigate('/member/modify');
    };

    return (
        <div className="profile-container">
            {user && (
                <>
                    <div className="greeting-card">
                        <h2>반가워요!</h2>
                        <h1>{user.m_name}님</h1>
                    </div>
                    <div className="profile-info-card">
                        <h2>프로필 정보</h2>
                        <p><strong>Email:</strong> {user.m_email}</p>
                        <p><strong>연락처:</strong> {user.m_phone}</p>
                        <p><strong>주소:</strong> {user.m_address}</p>
                        <p><strong>생일:</strong> {user.m_birth}</p>
                        <p><strong>성별:</strong> {user.m_gender === 'man' ? 'Male' : 'Female'}</p>
                        <p><strong>MBTI:</strong> {user.m_mbti || 'Not specified'}</p>
                        <div className="buttons">
                            <button className="modify-button" onClick={handleModify}>회원정보 수정</button>
                            <button className="logout-button" onClick={handleLogout}>로그아웃</button>
                        </div>
                    </div>
                </>
            )}
        </div>
    );
};

export default UserProfile;