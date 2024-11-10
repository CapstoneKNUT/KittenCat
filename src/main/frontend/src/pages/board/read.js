import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';
import './BoardRead.css'; // 스타일 파일

const BoardRead = () => {
    const navigate = useNavigate();
    const [currentUser, setCurrentUser] = useState('');
    const [bno, setBno] = useState('');
    const [replyList, setReplyList] = useState([]);
    const [dto, setDto] = useState({});
    const [user, setUser] = useState('');
    const [modalType, setModalType] = useState('');
    const [replyText, setReplyText] = useState('');
    const [hasAuth, setHasAuth] = useState(false);
    const registerModalRef = useRef(null);

    useEffect(() => {
        fetchBoardData();
        fetchUserData();
    }, []);

    const fetchBoardData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/board/read/${bno}`);
            setDto(response.data);
            setBno(response.data.bno);
        } catch (error) {
            if (error.response && error.response.status === 400) {
                // navigate('/member/login?error=ACCESS_DENIED');
            }
            console.error(error);
        }
    };

    const fetchUserData = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/user');
            setUser(response.data);
            setCurrentUser(response.data.username);
        } catch (error) {
            console.error(error);
        }
    };

    const handleRegister = async () => {
        try {
            const replyObj = { bno, replyText, replyer: currentUser };
            await axios.post('http://localhost:8080/api/replies/', replyObj);
            alert('댓글이 등록되었습니다.');
            setReplyText('');
            fetchReplies();
        } catch (error) {
            alert('댓글 등록 중 오류 발생');
        }
    };

    const fetchReplies = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/replies/list/${bno}`);
            setReplyList(response.data.dtoList || []);
        } catch (error) {
            console.error('댓글 목록 불러오기 오류:', error);
        }
    };

    return (
        <div className="board-read">
            <div className="card mt-3">
                <div className="card-header">게시글 읽기</div>
                <div className="card-body">
                    <div className="mb-3">
                        <strong>게시물 번호:</strong>
                        <input type="text" className="form-control" value={dto.bno || ''} readOnly />
                    </div>
                    <div className="mb-3">
                        <strong>제목:</strong>
                        <input type="text" className="form-control" value={dto.title || ''} readOnly />
                    </div>
                    <div className="mb-3">
                        <strong>내용:</strong>
                        <textarea className="form-control" rows="5" readOnly>{dto.content || ''}</textarea>
                    </div>
                    <div className="mb-3">
                        <strong>작성자:</strong>
                        <input type="text" className="form-control" value={dto.writer || ''} readOnly />
                    </div>
                    <div className="mb-3">
                        <strong>게시한 날짜:</strong>
                        <input type="text" className="form-control" value={dto.regDate || ''} readOnly />
                    </div>
                    <div className="mb-3">
                        <strong>수정한 날짜:</strong>
                        <input type="text" className="form-control" value={dto.modDate || ''} readOnly />
                    </div>
                    <button className="btn btn-info" onClick={() => setModalType('register')}>댓글 작성</button>
                </div>
            </div>

            <div className="mt-3">
                <h5>댓글 목록</h5>
                <ul className="list-group">
                    {replyList.map(reply => (
                        <li key={reply.rno} className="list-group-item d-flex justify-content-between align-items-start">
                            <div>
                                <strong>{reply.replyer}:</strong>
                                <div>{reply.replyText}</div>
                                <small className="text-muted">{reply.regDate}</small>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>

            {/* Register Modal */}
            {modalType === 'register' && (
                <div className="modal registerModal" tabIndex="-1">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">댓글 등록</h5>
                                <button type="button" className="btn-close" onClick={() => setModalType('')}></button>
                            </div>
                            <div className="modal-body">
                                <div className="mb-3">
                                    <strong>댓글 내용:</strong>
                                    <input type="text" className="form-control" value={replyText} onChange={e => setReplyText(e.target.value)} />
                                </div>
                                <div className="mb-3">
                                    <strong>댓글 작성자:</strong>
                                    <input type="text" className="form-control" value={user.username} readOnly />
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setModalType('')}>취소</button>
                                <button type="button" className="btn btn-primary" onClick={handleRegister}>등록</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default BoardRead;
