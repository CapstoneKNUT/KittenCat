// src/pages/board/BoardModify.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

const BoardModify = () => {
    const { bno } = useParams();
    const navigate = useNavigate();
    const [board, setBoard] = useState({ title: '', content: '' });

    useEffect(() => {
        const fetchBoard = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/board/read?bno=${bno}`);
                setBoard(response.data);
            } catch (error) {
                console.error('Error fetching board:', error);
            }
        };

        fetchBoard();
    }, [bno]);

    const handleModify = async () => {
        try {
            await axios.post('http://localhost:8080/api/board/modify', board);
            navigate('/board');
        } catch (error) {
            console.error('Error modifying board:', error);
        }
    };

    const handleDelete = async () => {
        try {
            await axios.post('http://localhost:8080/api/board/remove', { bno });
            navigate('/board');
        } catch (error) {
            console.error('Error deleting board:', error);
        }
    };

    return (
        <div>
            <h1>게시물 수정</h1>
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
            <button onClick={handleModify}>수정하기</button>
            <button onClick={handleDelete}>삭제하기</button>
        </div>
    );
};

export default BoardModify;
