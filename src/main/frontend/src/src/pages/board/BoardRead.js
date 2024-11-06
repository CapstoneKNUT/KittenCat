import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import './BoardRead.css';

const BoardRead = () => {
    const { bno } = useParams(); // URL에서 bno를 가져옵니다
    const [board, setBoard] = useState(null);

    // 게시물 데이터를 가져오는 함수
    const fetchBoardData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/board/read`, {
                params: { bno: bno }
            });
            setBoard(response.data);
        } catch (error) {
            console.error('게시물 데이터를 가져오는 중 오류 발생:', error);
        }
    };

    useEffect(() => {
        fetchBoardData();
    }, [bno]);

    if (!board) {
        return <div>로딩 중...</div>;
    }

    // 날짜 배열을 문자열로 변환하는 함수
    const formatDate = (dateArray) => {
        if (!dateArray || dateArray.length < 3) return '';
        const [year, month, day] = dateArray;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    };

    return (
        <div className="board-detail-page">
            <h1>{board.title}</h1>
            <hr className="content-separator"/>
            <div className="board-info">
                <p><strong>작성자:</strong> {board.writer}</p>
                <p><strong>등록일 : </strong> {formatDate(board.regDate)}</p>
                <p><strong>수정일 : </strong> {formatDate(board.modDate)}</p>
            </div>
            <hr className="content-separator"/>
            <div className="board-content">
                <p>{board.content}</p>
            </div>
        </div>
    );
};

export default BoardRead;
