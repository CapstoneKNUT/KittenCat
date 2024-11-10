import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link, useNavigate } from 'react-router-dom';
import './BoardList.css';
import {useUser} from "../member/UserContext";




const BoardList = () => {
    const [pageRequest, setPageRequest] = useState({
        size: 10,
        type: '',
        location: '',
        keyword: '',
        page: 1,
    });
    const [board, setBoard] = useState([]);
    const [responseData, setResponseData] = useState({
        prev: false,
        next: false,
        start: 0,
        end: 0,
        page: 1,
    });
    const navigate = useNavigate();
    const { user } = useUser();
    const username = user ? user.mid : null;

    const fetchBoards = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/board/list', {
                params: pageRequest,
            });

            console.log('API 응답:', response.data);

            if (response.data && response.data.dtoList) {
                setBoard(response.data.dtoList);
                setResponseData(response.data);
            } else {
                console.error('Unexpected response structure:', response.data);
            }
        } catch (error) {
            console.error('Error fetching boards:', error);
        }
    };

    useEffect(() => {
        fetchBoards();
    }, [pageRequest]);

    const handlePageChange = (pageNum) => {
        setPageRequest((prev) => ({
            ...prev,
            page: pageNum,
        }));
    };

    // 날짜 배열을 문자열로 변환하는 함수
    const formatDate = (dateArray) => {
        if (!dateArray || dateArray.length < 3) return '';
        const [year, month, day] = dateArray;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    };


    const handleRegister = (e) => {
        e.preventDefault(); // 기본 폼 제출 방지
        if (!username) {
            alert('로그인이 필요합니다.');
            return;
        }
        // 등록 페이지로 이동
        navigate('/board/register');
    };

    return (
        <div className="board-list">
            <div className="board-row mt-3">
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        fetchBoards(); // 검색 후 데이터 다시 가져오기
                    }}
                >
                    <div className="col">
                        <input type="hidden" name="size" value={pageRequest.size} />
                        <div className="input-group">
                            <select
                                className="form-select"
                                name="type"
                                value={pageRequest.type}
                                onChange={(e) => setPageRequest({ ...pageRequest, type: e.target.value })}
                            >
                                <option value="">---</option>
                                <option value="t">제목</option>
                                <option value="c">내용</option>
                                <option value="w">작성자</option>
                                <option value="tc">제목 내용</option>
                                <option value="tcw">제목 내용 작성자</option>
                            </select>

                            {/*<select
                                className="form-select"
                                name="location"
                                value={pageRequest.location}
                                onChange={(e) => setPageRequest({ ...pageRequest, location: e.target.value })}
                            >
                                <option value="">---</option>
                                <option value="seoul">서울</option>
                                <option value="gg">경기도</option>
                                <option value="gwd">강원도</option>
                            </select>
*/}
                            <input
                                type="text"
                                className="form-control"
                                name="keyword"
                                value={pageRequest.keyword}
                                onChange={(e) => setPageRequest({ ...pageRequest, keyword: e.target.value })}
                            />

                            <div className="input-group-append">
                                <button className="btn btn-outline-secondary" type="submit">
                                    Search
                                </button>
                                <button
                                    className="btn btn-outline-secondary"
                                    type="button"
                                    onClick={() => setPageRequest({ size: 10, type: '', location: '', keyword: '', page: 1 })}
                                >
                                    Clear
                                </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

            <div className="board-row mt-3">
                <div className="board-col">
                    <div className="board-card">
                        <div className="board-card-body">
                            <h5 className="board-card-title">리뷰 목록</h5>
                            <table className="table">
                                <thead>
                                <tr>
                                    <th scope="col">게시물 번호</th>
                                    <th scope="col">제목</th>
                                    <th scope="col">게시자</th>
                                    <th scope="col">등록날짜</th>
                                </tr>
                                </thead>
                                <tbody>
                                {board.map((dto) => (
                                    <tr key={dto.bno}>
                                        <td>{dto.bno}</td>
                                        <td>
                                            <Link to={`/board/read/${dto.bno}`} className="text-decoration-none">
                                                {dto.title}
                                            </Link>
                                            <span className="badge" style={{ backgroundColor: '#0a53be' }}>
                                                    {dto.replyCount}
                                                </span>
                                        </td>
                                        <td>{dto.writer}</td>
                                        <td>{formatDate(dto.regDate)}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>

                            <div className="float-end">
                                <ul className="pagination flex-wrap">
                                    {responseData.prev && (
                                        <li className="page-item">
                                            <button
                                                className="page-link"
                                                onClick={() => handlePageChange(responseData.start - 1)}
                                            >
                                                Previous
                                            </button>
                                        </li>
                                    )}
                                    {Array.from({ length: responseData.end - responseData.start + 1 }).map(
                                        (_, index) => (
                                            <li
                                                className={`page-item ${
                                                    responseData.page === responseData.start + index ? 'active' : ''
                                                }`}
                                                key={index}
                                            >
                                                <button
                                                    className="page-link"
                                                    onClick={() => handlePageChange(responseData.start + index)}
                                                >
                                                    {responseData.start + index}
                                                </button>
                                            </li>
                                        )
                                    )}
                                    {responseData.next && (
                                        <li className="page-item">
                                            <button
                                                className="page-link"
                                                onClick={() => handlePageChange(responseData.end + 1)}
                                            >
                                                Next
                                            </button>
                                        </li>
                                    )}
                                </ul>
                            </div>

                            <div className="mt-3">
                                <button className="btn btn-primary" onClick={handleRegister}>
                                    리뷰 등록
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default BoardList;
