// src/pages/board/BoardRegister.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const BoardRegister = () => {
    const [board, setBoard] = useState({ title: '', content: '' });
    const navigate = useNavigate();

    const handleRegister = async () => {
        try {
            await axios.post('http://localhost:8080/api/board/register', board);
            navigate('/board');
        } catch (error) {
            console.error('Error registering board:', error);
        }
    };

    return (
        <div>
            <h1>게시물 등록</h1>
            <input
                type="text"
                value={board.title}
                onChange={(e) => setBoard({ ...board, title: e.target.value })}
                placeholder="제목"
            />
            <textarea
                value={board.content}
                onChange={(e) => setBoard({ ...board, content: e.target.value })}
                placeholder="내용"
            />
            <button onClick={handleRegister}>등록하기</button>
        </div>
    );
};

export default BoardRegister;
