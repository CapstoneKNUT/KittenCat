import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import BasicLayout from "../../layout/BasicLayout";

const PlaceRead = () => {
    const { bno } = useParams();
    const navigate = useNavigate();
    const [place, setPlace] = useState(null);
    const [replies, setReplies] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);
    const [showRegisterModal, setShowRegisterModal] = useState(false);
    const [showModifyModal, setShowModifyModal] = useState(false);
    const [replyText, setReplyText] = useState('');
    const [replyToModify, setReplyToModify] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const { data } = await axios.get(`http://localhost:8080/api/place/${bno}`);
                setPlace(data.place);
                setReplies(data.replies);
                setCurrentUser(data.currentUser);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        fetchData();
    }, [bno]);

    const handleAddReply = async () => {
        try {
            await axios.post('/api/reply/add', { bno, replyText, replyer: currentUser.username });
            setReplyText('');
            setShowRegisterModal(false);
            const { data } = await axios.get(`http://localhost:8080/api/replies?bno=${bno}`);
            setReplies(data.replies);
        } catch (error) {
            console.error('Error adding reply:', error);
        }
    };

    const handleModifyReply = async () => {
        try {
            await axios.put('/api/reply/modify', { bno, rno: replyToModify.rno, replyText });
            setShowModifyModal(false);
            setReplyToModify(null);
            const { data } = await axios.get(`http://localhost:8080/api/replies?bno=${bno}`);
            setReplies(data.replies);
        } catch (error) {
            console.error('Error modifying reply:', error);
        }
    };

    const handleRemoveReply = async () => {
        try {
            await axios.delete(`/api/reply/remove`, { data: { bno, rno: replyToModify.rno } });
            setShowModifyModal(false);
            setReplyToModify(null);
            const { data } = await axios.get(`http://localhost:8080/api/replies?bno=${bno}`);
            setReplies(data.replies);
        } catch (error) {
            console.error('Error removing reply:', error);
        }
    };

    return (
        <BasicLayout>
        <div className="container mt-3">
            {place && (
                <div className="card">
                    <div className="card-header">게시글 읽기</div>
                    <div className="card-body">
                        <div className="mb-3">
                            <label className="form-label">게시물 번호</label>
                            <input type="text" className="form-control" value={place.bno} readOnly />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">제목</label>
                            <input type="text" className="form-control" value={place.title} readOnly />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">내용</label>
                            <textarea className="form-control" rows="5" value={place.content} readOnly />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">작성자</label>
                            <input type="text" className="form-control" value={place.writer} readOnly />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">게시한 날짜</label>
                            <input type="text" className="form-control" value={new Date(place.regDate).toLocaleString()} readOnly />
                        </div>
                        <div className="mb-3">
                            <label className="form-label">수정한 날짜</label>
                            <input type="text" className="form-control" value={new Date(place.modDate).toLocaleString()} readOnly />
                        </div>
                        <div className="d-flex justify-content-end">
                            <button className="btn btn-primary me-2" onClick={() => navigate(`/place/list`)}>목록</button>
                            {currentUser && currentUser.username === place.writer && (
                                <button className="btn btn-secondary" onClick={() => navigate(`/place/modify/${bno}`)}>수정</button>
                            )}
                        </div>
                        <div className="my-4">
                            <button className="btn btn-info" onClick={() => setShowRegisterModal(true)}>댓글 작성</button>
                        </div>
                        <ul className="list-group">
                            {replies.map(reply => (
                                <li key={reply.rno} className="list-group-item d-flex">
                                    <span className="col-2">{reply.rno}</span>
                                    <span className="col-6" onClick={() => {
                                        setReplyToModify(reply);
                                        setReplyText(reply.replyText);
                                        setShowModifyModal(true);
                                    }}>{reply.replyText}</span>
                                    <span className="col-2">{reply.replyer}</span>
                                    <span className="col-2">{new Date(reply.regDate).toLocaleString()}</span>
                                </li>
                            ))}
                        </ul>
                        <div className="my-4">
                            {/* 페이지 네비게이션 컴포넌트를 추가할 수 있습니다. */}
                        </div>
                    </div>
                </div>
            )}

            {/* 댓글 등록 모달 */}
            <div className={`modal fade ${showRegisterModal ? 'show d-block' : ''}`} tabIndex="-1">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">댓글 등록</h5>
                            <button type="button" className="btn-close" onClick={() => setShowRegisterModal(false)}></button>
                        </div>
                        <div className="modal-body">
                            <div className="mb-3">
                                <label className="form-label">댓글 내용</label>
                                <input type="text" className="form-control" value={replyText} onChange={(e) => setReplyText(e.target.value)} />
                            </div>
                            <div className="mb-3">
                                <label className="form-label">댓글 작성자</label>
                                <input type="text" className="form-control" value={currentUser ? currentUser.username : ''} readOnly />
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-primary" onClick={handleAddReply}>댓글 달기</button>
                            <button type="button" className="btn btn-outline-dark" onClick={() => setShowRegisterModal(false)}>닫기</button>
                        </div>
                    </div>
                </div>
            </div>

            {/* 댓글 수정/삭제 모달 */}
            {replyToModify && (
                <div className={`modal fade ${showModifyModal ? 'show d-block' : ''}`} tabIndex="-1">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">댓글 수정</h5>
                                <button type="button" className="btn-close" onClick={() => setShowModifyModal(false)}></button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-3">
                                    <label className="form-label">댓글 내용</label>
                                    <input type="text" className="form-control" value={replyText} onChange={(e) => setReplyText(e.target.value)} />
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-info" onClick={handleModifyReply}>수정</button>
                                <button type="button" className="btn btn-danger" onClick={handleRemoveReply}>삭제</button>
                                <button type="button" className="btn btn-outline-dark" onClick={() => setShowModifyModal(false)}>닫기</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
        </BasicLayout>
    );
};

export default PlaceRead;
