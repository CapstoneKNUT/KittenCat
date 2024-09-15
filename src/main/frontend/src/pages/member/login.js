import React, {useEffect, useState} from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import BasicLayout from "../../layout/BasicLayout";

const LoginPage = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: '',
        rememberMe: false
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // 로그인 API 호출
            const response = await axios.post('http://localhost:8080/api/member/login', formData);
            if (response.data.success) {
                navigate('/main'); // 로그인 성공 시 리디렉션할 페이지
            } else {
                setError('로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.');
            }
        } catch (error) {
            console.error('로그인 실패:', error);
            setError('로그인 실패: 서버 오류입니다.');
        }
    };

    return (
    <BasicLayout>
        <div className="container mt-5">
            <div className="card">
                <div className="card-header">로그인</div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="username" className="form-label">아이디</label>
                            <input
                                type="text"
                                id="username"
                                name="username"
                                placeholder="아이디"
                                className="form-control"
                                value={formData.username}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">비밀번호</label>
                            <input
                                type="password"
                                id="password"
                                name="password"
                                placeholder="비밀번호"
                                className="form-control"
                                value={formData.password}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="mb-3 form-check">
                            <input
                                type="checkbox"
                                id="rememberMe"
                                name="rememberMe"
                                className="form-check-input"
                                checked={formData.rememberMe}
                                onChange={handleChange}
                            />
                            <label htmlFor="rememberMe" className="form-check-label">자동 로그인</label>
                        </div>
                        {error && <div className="alert alert-danger" role="alert">{error}</div>}
                        <button type="submit" className="btn btn-primary">로그인</button>
                    </form>
                    <div className="mt-3">
                        <Link to="#" className="btn btn-link" onClick={(e) => e.preventDefault()}>비밀번호를 잃어버렸나요?</Link>
                        <Link to="/member/join" className="btn btn-link">회원가입</Link>
                        <Link to="/oauth2/authorization/kakao" className="btn btn-link">KAKAO 로그인</Link>
                    </div>
                </div>
            </div>
        </div>
    </BasicLayout>
    );
};

export default LoginPage;
