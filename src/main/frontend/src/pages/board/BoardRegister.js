import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './BoardRegister.css';
import { useUser } from '../member/UserContext';

const BoardRegister = () => {
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const { user } = useUser();
    const username = user ? user.mid : null;

    const handleSubmit = async (e) => {
        e.preventDefault(); // 기본 폼 제출 방지
        if (!username) {
            alert('로그인이 필요합니다.');
            return;
        }

        try {
            const response = await axios.post('http://localhost:8080/api/board/register', {
                title,
                content,
                writer: username, // 작성자 정보를 설정
            });
            alert('리뷰가 등록되었습니다.');
            navigate('/board/list'); // 등록 후 게시글 목록 페이지로 이동
        } catch (error) {
            console.error('리뷰 등록 중 오류 발생:', error);
            alert('리뷰 등록에 실패했습니다.');
        }
    };

    return (
        <div className="board-register-page">
            <h1>리뷰 등록</h1>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label htmlFor="title" className="form-label">제목</label>
                    <input
                        type="text"
                        className="form-control"
                        id="title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="content" className="form-label">내용</label>
                    <textarea
                        className="form-control"
                        id="content"
                        rows="5"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        required
                    ></textarea>
                </div>
                <button type="submit" className="btn btn-primary">등록</button>
            </form>
        </div>
    );
};

export default BoardRegister;
