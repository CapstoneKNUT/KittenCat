import React, { useState } from 'react';
import axios from 'axios';
import { useUser } from './UserContext';
import { useNavigate } from 'react-router-dom';

const Modify = () => {
    const { user } = useUser();
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        mid: user.mid || '',
        m_pw: user.m_pw || '',
        mpwc: '', // Password confirmation
        m_email: user.m_email || '',
        m_name: user.m_name || '',
        m_phone: user.m_phone || '',
        m_address: user.m_address || '',
        m_birth: user.m_birth || '',
        m_gender: user.m_gender || 'man',
        m_mbti: user.m_mbti || '',
        m_del: user.m_del || false,
        m_social: user.m_social || false
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({ ...formData, [name]: type === 'checkbox' ? checked : value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const { m_pw, mpwc } = formData;

        // Password confirmation check
        if (m_pw !== mpwc) {
            setError('비밀번호가 서로 다릅니다.');
            return;
        }

        try {
            const response = await axios.put('http://localhost:8080/api/member/modify', { ...formData, mid: user.mid });
            if (response.data.result === 'success') {
                alert('회원 정보가 수정되었습니다.');
                navigate('/member/profile');
            }
        } catch (error) {
            setError('회원 정보 수정에 실패하였습니다. 다시 시도해 주세요.');
        }
    };

    return (
        <div className="container mt-3">
            <div className="card">
                <div className="card-header">Modify Your Profile</div>
                <div className="card-body">
                    {error && <div className="text-danger">{error}</div>}
                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="mid">아이디</label>
                            <input className="form-control" id="mid" name="mid" type="text" value={formData.mid} readOnly />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_pw">비밀번호</label>
                            <input className="form-control" id="m_pw" name="m_pw" type="password" value={formData.m_pw} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="mpwc">비밀번호 확인</label>
                            <input className="form-control" id="mpwc" name="mpwc" type="password" value={formData.mpwc} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_name">이름</label>
                            <input className="form-control" id="m_name" name="m_name" type="text" value={formData.m_name} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_email">이메일</label>
                            <input className="form-control" id="m_email" name="m_email" type="email" value={formData.m_email} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_phone">전화번호</label>
                            <input className="form-control" id="m_phone" name="m_phone" type="text" value={formData.m_phone} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_address">집주소</label>
                            <input className="form-control" id="m_address" name="m_address" type="text" value={formData.m_address} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_birth">생년월일</label>
                            <input className="form-control" id="m_birth" name="m_birth" type="date" value={formData.m_birth} onChange={handleChange} required />
                        </div>
                        <div className="form-group">
                            <label>성별</label>
                            <fieldset>
                                <label>
                                    <input className="form-check-input" value="man" name="m_gender" type="radio" checked={formData.m_gender === 'man'} onChange={handleChange} />
                                    남성
                                </label>
                                <label>
                                    <input className="form-check-input" value="woman" name="m_gender" type="radio" checked={formData.m_gender === 'woman'} onChange={handleChange} />
                                    여성
                                </label>
                            </fieldset>
                        </div>
                        <div className="form-group">
                            <label htmlFor="m_mbti">MBTI(선택)</label>
                            <input className="form-control" id="m_mbti" name="m_mbti" type="text" value={formData.m_mbti} onChange={handleChange} />
                        </div>
                        <div className="my-4">
                            <div className="float-end">
                                <button type="submit" className="btn btn-primary">수정하기</button>
                                <button type="button" className="btn btn-secondary" onClick={() => navigate('/profile')}>취소</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Modify;
