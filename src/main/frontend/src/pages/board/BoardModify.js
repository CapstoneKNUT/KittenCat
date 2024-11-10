import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './BoardModify.css'; // 스타일 파일 추가

const BoardModify = () => {
    const { bno } = useParams(); // URL에서 bno를 가져옵니다
    const navigate = useNavigate(); // navigate 함수 사용
    const [board, setBoard] = useState({
        title: '',
        content: '',
        writer: '',
        regDate: '',
        modDate: ''
    });

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

    const handleChange = (e) => {
        const { name, value } = e.target;
        setBoard({ ...board, [name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault(); // 기본 폼 제출 방지
        try {
            await axios.post('http://localhost:8080/api/board/modify', board);
            alert('게시물이 수정되었습니다.');
            navigate(`/board/read/${bno}`); // 수정 후 게시글 보기 페이지로 이동
        } catch (error) {
            console.error('게시물 수정 중 오류 발생:', error);
            alert('게시물 수정에 실패했습니다.');
        }
    };

    if (!board) {
        return <div>로딩 중...</div>;
    }

    return (
        <div className="board-modify-page">
            <h1>게시물 수정</h1>
            <form onSubmit={handleSubmit}>
                <div className="mb-3">
                    <label htmlFor="title" className="form-label">제목</label>
                    <input
                        type="text"
                        className="form-control"
                        id="title"
                        name="title"
                        value={board.title}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div className="mb-3">
                    <label htmlFor="content" className="form-label">내용</label>
                    <textarea
                        className="form-control"
                        id="content"
                        name="content"
                        rows="5"
                        value={board.content}
                        onChange={handleChange}
                        required
                    ></textarea>
                </div>
                <button type="submit" className="btn btn-primary">수정 완료</button>
            </form>
        </div>
    );
};

export default BoardModify;
