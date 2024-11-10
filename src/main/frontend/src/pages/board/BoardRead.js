import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import './BoardRead.css';
import { useUser } from '../member/UserContext'; // 사용자 정보를 가져오기 위한 훅

const BoardRead = () => {
    const { bno } = useParams(); // URL에서 bno를 가져옵니다
    const navigate = useNavigate(); // navigate 함수 사용
    const [board, setBoard] = useState(null);
    const { user } = useUser(); // 현재 로그인한 사용자 정보 가져오기
    const username = user ? user.mid : null; // 사용자 이름 추출

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

    const handleModify = async () => {
        // 수정 페이지로 이동
        navigate(`/board/modify/${bno}`);
    };

    const handleDelete = async () => {
        const confirmDelete = window.confirm('정말 게시물을 삭제하시겠습니까?');
        if (confirmDelete) {
            try {
                await axios.post('http://localhost:8080/api/board/remove', { bno: board.bno });
                alert('게시물이 삭제되었습니다.');
                navigate('/board/list'); // 목록 페이지로 이동
            } catch (error) {
                console.error('게시물 삭제 중 오류 발생:', error);
                alert('게시물 삭제에 실패했습니다.');
            }
        }
    };

    const handleBackToList = () => {
        navigate('/board/list'); // 목록 페이지로 이동
    };

    return (
        <div className="board-detail-page">
            <h1>{board.title}</h1> {/* 게시글 제목 표시 */}
            <hr className="content-separator"/>
            <div className="board-info">
                <p><strong>제목:</strong> {board.title}</p>
                <p><strong>작성자:</strong> {board.writer}</p>
                <p><strong>등록일:</strong> {formatDate(board.regDate)}</p>
                <p><strong>수정일:</strong> {formatDate(board.modDate)}</p>
            </div>
            <hr className="content-separator"/>
            <div className="board-content">
                <p>{board.content}</p>
            </div>
            <div className="button-group">
                <button className="btn btn-secondary" onClick={handleBackToList}>목록으로 돌아가기</button> {/* 목록으로 돌아가는 버튼 */}
                {board.writer === username && ( // 작성자가 현재 사용자와 같을 경우 버튼을 보여줌
                    <>
                        <button className="btn btn-warning" onClick={handleModify}>수정</button>
                        <button className="btn btn-danger" onClick={handleDelete}>삭제</button>
                    </>
                )}
            </div>
        </div>
    );
};

export default BoardRead;
