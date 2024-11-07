import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const JoinForm = () => {
    const [formData, setFormData] = useState({
        mid: '',
        m_pw: '', // Changed to match MemberDTO field names
        mpwc: '',
        m_name: '', // Changed to match MemberDTO field names
        m_email: '',
        m_phone: '',
        m_address: '',
        m_birth: '',
        m_gender: 'man',
        m_mbti: ''
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

        const { m_pw, mpwc } = formData;

        // 비밀번호 일치 확인
        if (m_pw !== mpwc) {
            setError('비밀번호가 서로 다릅니다.');
            return;
        }

        // 비밀번호 패턴 확인
        const passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
        if (!passwordPattern.test(m_pw)) {
            setError('비밀번호는 8자 이상의 문자와 숫자의 조합이어야 합니다.');
            return;
        }

        // 추가적인 폼 검증 (전화번호, 주소, 생년월일 등)
        const phonePattern = /^\d{10,15}$/;
        if (!phonePattern.test(formData.m_phone)) {
            setError('전화번호는 10자에서 15자 사이의 숫자여야 합니다.');
            return;
        }

        if (!formData.m_birth) {
            setError('생년월일을 입력해 주세요.');
            return;
        }

        try {
            // API 호출
            const response = await axios.post('http://localhost:8080/api/member/join', formData);
            console.log('Join Response:', response.data);
            alert('회원가입 성공! 로그인 페이지로 이동합니다.');
            navigate('/member');
        } catch (err) {
            if (err.response) {
                // 서버에서 반환한 에러 메시지
                setError(err.response.data.error === "mid" ? "이미 사용 중인 ID입니다." : "회원가입 실패. 다시 시도해 주세요.");
            } else {
                setError('알 수 없는 오류가 발생했습니다.');
            }
        }
    };

    return (
        <div className="container mt-3">
            <div className="card">
                <div className="card-header">
                    JOIN
                </div>
                <div className="card-body">
                    <form id="registerForm" onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="mid">아이디</label>
                            <input
                                className="form-control"
                                id="mid"
                                name="mid"
                                type="text"
                                value={formData.mid}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_pw">비밀번호</label>
                            <input
                                className="form-control"
                                id="m_pw"
                                name="m_pw"
                                type="password"
                                value={formData.m_pw}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="mpwc">비밀번호 확인</label>
                            <input
                                className="form-control"
                                id="mpwc"
                                name="mpwc"
                                type="password"
                                value={formData.mpwc}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        {error && <div id="errorMessage" className="text-danger">{error}</div>}
                        <div className="form-group">
                            <label htmlFor="m_name">이름</label>
                            <input
                                className="form-control"
                                id="m_name"
                                name="m_name"
                                type="text"
                                value={formData.m_name}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_email">이메일</label>
                            <input
                                className="form-control"
                                id="m_email"
                                name="m_email"
                                type="email"
                                value={formData.m_email}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_phone">전화번호</label>
                            <input
                                className="form-control"
                                id="m_phone"
                                name="m_phone"
                                type="text"
                                value={formData.m_phone}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_address">집주소</label>
                            <input
                                className="form-control"
                                id="m_address"
                                name="m_address"
                                type="text"
                                value={formData.m_address}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_birth">생년월일</label>
                            <input
                                className="form-control"
                                id="m_birth"
                                name="m_birth"
                                type="date"
                                value={formData.m_birth}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>성별</label>
                            <fieldset>
                                <label>
                                    <input
                                        className="form-check-input"
                                        value="man"
                                        name="gender"
                                        type="radio"
                                        checked={formData.gender === 'man'}
                                        onChange={handleChange}
                                    />
                                    남성
                                </label>
                                <label>
                                    <input
                                        className="form-check-input"
                                        value="woman"
                                        name="gender"
                                        type="radio"
                                        checked={formData.gender === 'woman'}
                                        onChange={handleChange}
                                    />
                                    여성
                                </label>
                            </fieldset>
                        </div>
                        <div className="form-group">
                            <label>MBTI(선택)</label>
                            <select
                                className="form-select"
                                name="mbti"
                                value={formData.mbi}
                                onChange={handleChange}
                            >
                                <option value="">---</option>
                                <option value="ISTJ">ISTJ</option>
                                <option value="ISTP">ISTP</option>
                                <option value="ISFJ">ISFJ</option>
                                <option value="ISFP">ISFP</option>
                                <option value="INTJ">INTJ</option>
                                <option value="INTP">INTP</option>
                                <option value="INFJ">INFJ</option>
                                <option value="INFP">INFP</option>
                                <option value="ESTJ">ESTJ</option>
                                <option value="ESTP">ESTP</option>
                                <option value="ESFJ">ESFJ</option>
                                <option value="ESFP">ESFP</option>
                                <option value="ENTJ">ENTJ</option>
                                <option value="ENTP">ENTP</option>
                                <option value="ENFJ">ENFJ</option>
                                <option value="ENFP">ENFP</option>
                            </select>
                        </div>
                        <div className="my-4">
                            <div className="float-end">
                                <button type="submit" className="btn btn-primary">등록하기</button>
                                <button type="button" className="btn btn-secondary" onClick={() => navigate('/member/login')}>
                                    이전
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default JoinForm;
