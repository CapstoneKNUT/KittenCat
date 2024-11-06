import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import './BoardList.css';

const BoardList = () => {
    const [board, setBoard] = useState([]); // 초기 상태를 빈 배열로 설정
    const navigate = useNavigate();

    const fetchBoards = async () => {
        try {
            const params = {
                page: 1, // 예시: 첫 페이지
                size: 10, // 예시: 한 페이지에 10개
            };

            const response = await axios.get('http://localhost:8080/api/board/list', {
                params: params,  // 쿼리 파라미터로 전달
            });

            console.log('API 응답:', response.data); // 응답을 로그로 출력해서 확인

            // 응답 구조에서 dtoList를 확인하고 상태를 업데이트
            if (response.data && response.data.dtoList) {
                setBoard(response.data.dtoList); // dtoList를 board 상태로 설정
            } else {
                console.error('Unexpected response structure:', response.data);
            }
        } catch (error) {
            console.error('Error fetching boards:', error);
        }
    };

    useEffect(() => {
        fetchBoards();
    }, []);

    /*const handleRead = (bno) => {
        navigate(`/board/read/${bno}`);
    };*/

    // 날짜 배열을 문자열로 변환하는 함수
    const formatDate = (dateArray) => {
        if (!dateArray || dateArray.length < 3) return '';
        const [year, month, day] = dateArray;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    };

    return (
        <div className="list-page">
            {/*<h2>리뷰 목록</h2>*/}
            {/* 데이터가 있을 때만 목록을 출력 */}
            {board.length > 0 ? (
                <ul className="results-list">
                    {board.map((item) => (
                        <li key={item.bno}>
                            <Link to={`/board/read/${item.bno}`}>
                                <p><strong>제목 : </strong> {item.title}</p>
                                <p><strong>작성자 : </strong> {item.writer}</p>
                                {/*<div>내용 : {item.content}</div>*/}
                                <p><strong>등록일 : </strong> {formatDate(item.regDate)}</p>
                                <p><strong>수정일 : </strong> {formatDate(item.modDate)}</p>
                            </Link>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>게시물이 없습니다.</p>
            )}
        </div>
    );
};

export default BoardList;
