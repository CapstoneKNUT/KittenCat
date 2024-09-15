import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import BasicLayout from "../../layout/BasicLayout";

const MemberJoin = () => {
    const [formData, setFormData] = useState({
        mid: '',
        mpw: '',
        mpwc: '',
        name: '',
        email: '',
        phone: '',
        address: '',
        birth: '',
        gender: 'man',
        mbti: ''
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

    const handleSubmit = (e) => {
        e.preventDefault();

        const { mpw, mpwc } = formData;

        // 비밀번호 일치 확인
        if (mpw !== mpwc) {
            setError('비밀번호가 서로 다릅니다.');
            return;
        }

        // 비밀번호 패턴 확인
        const passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
        if (!passwordPattern.test(mpw)) {
            setError('비밀번호는 8자 이상의 문자와 숫자의 조합이어야 합니다.');
            return;
        }

        // 추가적인 폼 검증 (전화번호, 주소, 생년월일 등)
        // 예시: 전화번호는 숫자만 포함하고 특정 길이인지 검증
        const phonePattern = /^\d{10,15}$/;
        if (!phonePattern.test(formData.phone)) {
            setError('전화번호는 10자에서 15자 사이의 숫자여야 합니다.');
            return;
        }

        // 날짜 형식 검증
        if (!formData.birth) {
            setError('생년월일을 입력해 주세요.');
            return;
        }

        // 모든 검증을 통과한 경우 폼 제출
        // 실제 폼 제출을 원할 경우, API 호출을 수행합니다.
        // 예: axios.post('/member/join', formData).then(response => { ... });

        // 테스트용으로 페이지 이동
        navigate('/member/login');
    };

    return (
        <BasicLayout>
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
                            <label htmlFor="mpw">비밀번호</label>
                            <input
                                className="form-control"
                                id="mpw"
                                name="mpw"
                                type="password"
                                value={formData.mpw}
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
                            <label htmlFor="name">이름</label>
                            <input
                                className="form-control"
                                id="name"
                                name="name"
                                type="text"
                                value={formData.name}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="email">이메일</label>
                            <input
                                className="form-control"
                                id="email"
                                name="email"
                                type="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="phone">전화번호</label>
                            <input
                                className="form-control"
                                id="phone"
                                name="phone"
                                type="text"
                                value={formData.phone}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="address">집주소</label>
                            <input
                                className="form-control"
                                id="address"
                                name="address"
                                type="text"
                                value={formData.address}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="birth">생년월일</label>
                            <input
                                className="form-control"
                                id="birth"
                                name="birth"
                                type="date"
                                value={formData.birth}
                                onChange={handleChange}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>성별</label>
                            <fieldset>
                                <legend>성별</legend>
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
                                value={formData.mbti}
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
        </BasicLayout>
    );
};

export default MemberJoin;
