import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './JoinForm.css';

const JoinForm = () => {
    const [formData, setFormData] = useState({
        mid: '',
        m_pw: '',
        mpwc: '',
        m_name: '',
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

        if (m_pw !== mpwc) {
            setError('비밀번호가 서로 다릅니다.');
            return;
        }

        const passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
        if (!passwordPattern.test(m_pw)) {
            setError('비밀번호는 8자 이상의 문자와 숫자의 조합이어야 합니다.');
            return;
        }

        const phonePattern = /^010-\d{4}-\d{4}$/;
        if (!phonePattern.test(formData.m_phone)) {
            setError('전화번호는 010-1111-2222 형식으로 입력해 주세요.');
            return;
        }


        if (!formData.m_birth) {
            setError('생년월일을 입력해 주세요.');
            return;
        }

        try {
            const response = await axios.post('http://localhost:8080/api/member/join', formData);
            console.log('Join Response:', response.data);
            alert('회원가입 성공! 로그인 페이지로 이동합니다.');
            navigate('/member/login');
        } catch (err) {
            if (err.response) {
                setError(err.response.data.error === 'mid' ? '이미 사용 중인 ID입니다.' : '회원가입 실패. 다시 시도해 주세요.');
            } else {
                setError('알 수 없는 오류가 발생했습니다.');
            }
        }
    };

    return (
        <div className="join-container">
            <div className="card">
                <div className="card-header">회원가입</div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label>아이디</label>
                            <input name="mid" value={formData.mid} onChange={handleChange} className="form-control" required />
                        </div>
                        <div className="form-group">
                            <label>비밀번호</label>
                            <input name="m_pw" type="password" value={formData.m_pw} onChange={handleChange} className="form-control" required />
                        </div>
                        <div className="form-group">
                            <label>비밀번호 확인</label>
                            <input name="mpwc" type="password" value={formData.mpwc} onChange={handleChange} className="form-control" required />
                        </div>
                        {error && <div className="text-danger">{error}</div>}
                        <div className="form-group">
                            <label>이름</label>
                            <input name="m_name" value={formData.m_name} onChange={handleChange} className="form-control" required />
                        </div>
                        <div className="form-group">
                            <label>이메일</label>
                            <input name="m_email" type="email" value={formData.m_email} onChange={handleChange} className="form-control" required />
                        </div>
                        <div className="form-group">
                            <label>전화번호</label>
                            <input
                                name="m_phone"
                                value={formData.m_phone}
                                onChange={handleChange}
                                className="form-control"
                                placeholder="010-1111-2222"
                                pattern="^010-\d{4}-\d{4}$"
                                title="010-1111-2222 형식으로 입력해 주세요."
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label>집주소</label>
                            <input name="m_address" value={formData.m_address} onChange={handleChange}
                                   className="form-control" required/>
                        </div>
                        <div className="form-group">
                        <label>생년월일</label>
                            <input name="m_birth" type="date" value={formData.m_birth} onChange={handleChange} className="form-control" required />
                        </div>
                        <div className="form-group">
                            <label>성별</label>
                            <div>
                                <input type="radio" name="m_gender" value="man" checked={formData.m_gender === 'man'} onChange={handleChange} /> 남성
                                <input type="radio" name="m_gender" value="woman" checked={formData.m_gender === 'woman'} onChange={handleChange} /> 여성
                            </div>
                        </div>
                        <div className="form-group">
                            <label>MBTI</label>
                            <select name="m_mbti" value={formData.m_mbti} onChange={handleChange} className="form-select">
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
                        <button type="submit" className="btn btn-primary">가입하기</button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default JoinForm;
