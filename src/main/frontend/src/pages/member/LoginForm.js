import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useUser } from './UserContext';
import { useNavigate, Link } from 'react-router-dom';

const LoginForm = () => {
    const { user, setUser } = useUser(); // user 정보를 가져옵니다.
    const [mid, setMid] = useState('');
    const [m_pw, setMPw] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    // 컴포넌트가 마운트될 때 사용자 정보가 있으면 프로필 페이지로 리다이렉트
    useEffect(() => {
        if (user) {
            navigate('/member/profile');
        }
    }, [user, navigate]);

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('http://localhost:8080/api/member/login', { mid, m_pw });
            console.log('Login Response:', response.data);
            setUser(response.data);
            localStorage.setItem('user', JSON.stringify(response.data));
            alert('로그인 성공!');
            navigate('/member/profile');
        } catch (err) {
            if (err.response) {
                setError(err.response.data.error);
            } else {
                setError('알 수 없는 오류가 발생했습니다.');
            }
        }
    };

    return (

        <div className="container mt-5">
            <div className="card">
                <div className="card-header">로그인</div>
                <div className="card-body">
                    <form onSubmit={handleLogin}>
                        <div className="mb-3">
                            <label htmlFor="mid" className="form-label">아이디</label>
                            <input
                                type="text"
                                id="mid"
                                placeholder="아이디"
                                className="form-control"
                                value={mid}
                                onChange={(e) => setMid(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="m_pw" className="form-label">비밀번호</label>
                            <input
                                type="password"
                                id="m_pw"
                                placeholder="비밀번호"
                                className="form-control"
                                value={m_pw}
                                onChange={(e) => setMPw(e.target.value)}
                                required
                            />
                        </div>
                        {error && <div className="alert alert-danger" role="alert">{error}</div>}
                        <button type="submit" className="btn btn-primary">로그인</button>
                    </form>
                    <div className="mt-3">
                        <Link to="/member/join" className="btn btn-link">회원가입</Link>
                        {/*                            <Link to="#" className="btn btn-link" onClick={(e) => e.preventDefault()}>비밀번호를 잃어버렸나요?</Link>*/}
                    </div>
                </div>
            </div>
        </div>

    );
};

export default LoginForm;