import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const LoginRedirect = () => {
    const navigate = useNavigate();

    useEffect(() => {
        // 현재 URL을 확인하여 스프링부트의 리다이렉트 URL인지 확인
        if (window.location.href.includes('http://localhost:8080/api/member/login?error=ACCESS_DENIED')) {
            // 리액트 앱의 로그인 페이지로 이동
            window.history.replaceState(null, '', '/member/login'); // URL을 변경하지만 페이지를 리로딩하지 않음
            navigate('/member/login');
        }
    }, [navigate]);

    return null; // 이 컴포넌트는 렌더링할 내용이 없으므로 null 반환
};

export default LoginRedirect;