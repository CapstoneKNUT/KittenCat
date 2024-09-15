import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import BasicLayout from "../../layout/BasicLayout"; // Link 컴포넌트 추가

function PlaceList() {
    const [pageRequest, setPageRequest] = useState({
        size: 10,
        type: '',
        location: '',
        keyword: '',
    });
    const [responseData, setResponseData] = useState({
        dtoList: [],
        prev: false,
        next: false,
        start: 0,
        end: 0,
        page: 1,
    });

    useEffect(() => {
        // 데이터를 받아오는 함수
        const fetchData = async () => {
            try {
                const response = await axios.get('http://localhost:8080/api/place/list', {
                    params: pageRequest,
                });
                setResponseData(response.data);
            } catch (error) {
                console.log(error);
            }
        };

        fetchData();
    }, [pageRequest]);

    // 페이지 변경 함수
    const handlePageChange = (pageNum) => {
        setPageRequest((prev) => ({
            ...prev,
            page: pageNum,
        }));
    };

    return (
        <BasicLayout>
            <div>
                <div className="row mt-3">
                    <form
                        onSubmit={(e) => {
                            e.preventDefault();
                            // 데이터 요청을 위해 페이지 요청 정보를 업데이트
                            // (현재 form submit은 별도의 동작을 하지 않음, 필요에 따라 추가 구현 가능)
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

                                <select
                                    className="form-select"
                                    name="location"
                                    value={pageRequest.location}
                                    onChange={(e) => setPageRequest({ ...pageRequest, location: e.target.value })}
                                >
                                    <option value="">---</option>
                                    <option value="seoul">서울</option>
                                    <option value="gg">경기도</option>
                                    <option value="gwd">강원도</option>
                                    {/* 다른 지역 선택 */}
                                </select>

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
                                        onClick={() => setPageRequest({ size: 10, type: '', location: '', keyword: '' })}
                                    >
                                        Clear
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>

                <div className="row mt-3">
                    <div className="col">
                        <div className="card">
                            <div className="card-header">리뷰 목록</div>
                            <div className="card-body">
                                <h5 className="card-title">리뷰 목록</h5>
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
                                    {responseData.dtoList.map((dto) => (
                                        <tr key={dto.bno}>
                                            <td>{dto.bno}</td>
                                            <td>
                                                {/* Link 컴포넌트 사용 */}
                                                <Link to={`/place/read?bno=${dto.bno}`} className="text-decoration-none">
                                                    {dto.title}
                                                </Link>
                                                <span className="badge" style={{ backgroundColor: '#0a53be' }}>
                                                {dto.replyCount}
                                            </span>
                                            </td>
                                            <td>{dto.writer}</td>
                                            <td>{new Date(dto.regDate).toLocaleDateString()}</td>
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
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </BasicLayout>
    );
}

export default PlaceList;
