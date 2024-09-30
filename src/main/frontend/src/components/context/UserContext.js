import React, { createContext, useState, useEffect } from 'react';
import Cookies from 'js-cookie';

// Context 생성
export const UserContext = createContext();

// Provider 컴포넌트
export const UserProvider = ({ children }) => {
    const [username, setUsername] = useState('');

    useEffect(() => {
        // 애플리케이션 로드 시 쿠키에서 사용자 정보 읽기
        const storedUsername = Cookies.get('username');
        if (storedUsername) {
            setUsername(storedUsername);
        }
    }, []);

    return (
        <UserContext.Provider value={{ username, setUsername }}>
            {children}
        </UserContext.Provider>
    );
};
