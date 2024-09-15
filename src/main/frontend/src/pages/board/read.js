import React, {useState, useEffect, useRef} from 'react';
import {useNavigate} from 'react-router-dom';
import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

const BoardRead = () => {
    const navigate = useNavigate();
    const [currentUser, setCurrentUser] = useState('');
    const [bno, setBno] = useState('');
    const [replyList, setReplyList] = useState([]);
    const [replyPaging, setReplyPaging] = useState([]);
    const [dto, setDto] = useState({});
    const [user, setUser] = useState('');
    const [modalType, setModalType] = useState('');
    const [replyText, setReplyText] = useState('');
    const [replyer, setReplyer] = useState('');
    const [modifyText, setModifyText] = useState('');
    const [hasAuth, setHasAuth] = useState(false);
    const [replyObj, setReplyObj] = useState(false);
    const [rno, setRno] = useState(false);

    const registerModalRef = useRef(null);
    const addReplyBtnRef = useRef(null);
    const closeRegisterBtnRef = useRef(null);
    const [pageRequestDTO, setPageRequestDTO] = useState({
        page: 1,
        size: 10,
        type: '',
        keyword: '',
        link: '',
        getLink() {
            let link = '';

            const { page, size, type, keyword } = this;

            link += `page=${page}&size=${size}`;

            if (type && type.length > 0) {
                link += `&type=${type}`;
            }

            if (keyword) {
                link += `&keyword=${encodeURIComponent(keyword)}`;
            }

            return link;
        }
    });
    const { page, size, type} = pageRequestDTO;
    const link = pageRequestDTO ? pageRequestDTO.getLink() : '';
    const [goLast, setGoLast] = useState(false);

    useEffect(() => {
        fetchBoardData();
        fetchUserData();
        fetchModal();
    }, []);

    useEffect(() => {
        getList({bno,page,size,goLast,type});
    }, [bno,page,size,goLast,type]);

    useEffect(() => {
        addReply(replyObj);
        modifyReply(replyObj);
    }, [replyObj]);

    useEffect(() => {
        getReply(rno);
        removeReply(rno);
    }, [rno]);

    const fetchBoardData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/board/read${bno}`);
            setBno(response.data.bno);
            // printReplies(1, 10, true);
        } catch (error) {
            if (error.response && error.response.status === 400) {
                // 서버에서 400 응답을 받은 경우, 리다이렉트
                navigate('/member/login?error=ACCESS_DENIED');
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

    const fetchModal = () => {
        try {
            const registerModal = new window.bootstrap.Modal(registerModalRef.current);

            // 댓글 추가 버튼 클릭 시 모달 표시
            const handleAddReplyClick = () => {
                registerModal.show();
            };

            // 닫기 버튼 클릭 시 모달 숨기기
            const handleCloseRegisterClick = () => {
                registerModal.hide();
            };

            // 이벤트 리스너 설정
            const addReplyBtn = addReplyBtnRef.current;
            const closeRegisterBtn = closeRegisterBtnRef.current;

            if (addReplyBtn) {
                addReplyBtn.addEventListener('click', handleAddReplyClick);
            }

            if (closeRegisterBtn) {
                closeRegisterBtn.addEventListener('click', handleCloseRegisterClick);
            }

            return () => {
                if (addReplyBtn) {
                    addReplyBtn.removeEventListener('click', () => registerModal.show());
                }
                if (closeRegisterBtnRef.current) {
                    closeRegisterBtnRef.current.removeEventListener('click', () => registerModal.hide());
                }
            };
        } catch (error) {
            console.error(error);
        }
    };

    const getList = async ({bno, page, size, goLast, type}) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/replies/list/${bno}`,{
                params: {page, size, type}
            });

            if (goLast) {
                const total = response.data.total;
                const lastPage = parseInt(Math.ceil(total / size), 10);

                // 마지막 페이지를 요청하여 재귀 호출
                return getList({bno: bno, page: lastPage, size: size, type: type});
            }
            return response.data
        } catch (error) {
            console.error(error);
            throw error;
        }
    };

    const addReply = async (replyObj) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/replies/`,replyObj)
            return response.data
        } catch (error) {
            console.error(error);
            throw error;
        }
    };

    const getReply = async (rno) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/replies/${rno}`)
            return response.data
        } catch (error) {
            console.error(error);
            throw error;
        }
    };

    const modifyReply = async (replyObj) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/replies/${replyObj.rno}`, replyObj)
            return response.data
        } catch (error) {
            console.error(error);
            throw error;
        }
    };

    const removeReply = async (rno) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/replies/${rno}`)
            return response.data
        } catch (error) {
            console.error(error);
            throw error;
        }
    };

    const printList = (dtoList) => {

        if (dtoList && dtoList.length > 0) {
            const listHtml = dtoList.map(dto => (
                <li key={dto.rno} className="list-group-item d-flex replyItem">
                    <span className="col-2">{dto.rno}</span>
                    <span className="col-6" data-rno={dto.rno}>{dto.replyText}</span>
                    <span className="col-2">{dto.replyer}</span>
                    <span className="col-2">{dto.regDate}</span>
                </li>
            ));
            setReplyList(listHtml);
        } else {
            setReplyList([]);
        }
    };

    const printPages = (data) => {
        let pageStr = '';

        if (data.prev) {
            pageStr += `<li class="page-item"><a class="page-link" data-page="${data.start - 1}">이전</a></li>`;
        }

        for (let i = data.start; i <= data.end; i++) {
            pageStr += `<li class="page-item ${i === data.page ? 'active' : ''}"><a class="page-link" data-page="${i}">${i}</a></li>`;
        }

        if (data.next) {
            pageStr += `<li class="page-item"><a class="page-link" data-page="${data.end + 1}">다음</a></li>`;
        }

        setReplyPaging(pageStr);
    };

    const printReplies = async (page, size, goLast, type = 'board') => {
        try {
            getList({bno, page, size, goLast, type}).then(
                data => {
                    printList(data.dtoList); //목록 처리
                    printPages(data);
                }
            )
        } catch (error) {
            console.error(error);
        }
    };

    printReplies(1,10, true)

    const handleRegister = async () => {
        try {
            const replyObj = { bno, replyText, replyer };
            await axios.post('http://localhost:8080/api/replies/', replyObj);
            alert(replyObj.bno + '댓글이 등록되었습니다.');
            setReplyText('');
            setReplyer('');
            printReplies(1, 10, true);
        } catch (error) {
            alert('댓글 등록 중 오류 발생');
        }
    };

    const handleModifyReply = async () => {
        if (!hasAuth) {
            alert('댓글 작성자만 수정이 가능합니다.');
            return;
        }

        try {
            const replyObj = { bno, rno: dto.rno, replyText: modifyText };
            await axios.put(`http://localhost:8080/api/replies/${dto.rno}`, replyObj);
            alert(replyObj.rno +'댓글이 수정되었습니다.');
            setModifyText('');
            printReplies(page,size);
        } catch (error) {
            console.error(error);
        }
    };

    const handleRemoveReply = async () => {
        if (!hasAuth) {
            alert('댓글 작성자만 삭제가 가능합니다.');
            return;
        }

        try {
            await axios.delete(`http://localhost:8080/api/replies/${dto.rno}`);
            alert('댓글이 삭제되었습니다.');
            setModifyText('');
            setPageRequestDTO(prevState => ({
                ...prevState,  // 이전 상태를 복사
                page: 1        // page 값을 1로 업데이트
            }));
            printReplies(page, size);
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div class="row mt-3">
            <div class="col">
            <div className="card">
                <div className="card-header">게시글 읽기</div>
                <div className="card-body">
                    <div className="input-group mb-3">
                        <span className="input-group-text">게시물 번호</span>
                        <input type="text" className="form-control" value={dto.bno || ''} readOnly />
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">제목</span>
                        <input type="text" className="form-control" value={dto.title || ''} readOnly />
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">내용</span>
                        <textarea className="form-control col-sm-5" rows="5" readOnly>{dto.content || ''}</textarea>
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">작성자</span>
                        <input type="text" className="form-control" value={dto.writer || ''} readOnly />
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">게시한 날짜</span>
                        <input type="text" className="form-control" value={dto.regDate || ''} readOnly />
                    </div>
                    <div className="input-group mb-3">
                        <span className="input-group-text">수정한 날짜</span>
                        <input type="text" className="form-control" value={dto.modDate || ''} readOnly />
                    </div>
                    <div className="my-4">
                        <div className="float-end">
                            <a href={`/board/list?${link}`} className="text-decoration-none">
                                <button type="button" className="btn btn-primary">목록</button>
                            </a>
                            {user.username === dto.writer && (
                                <a href={`/board/modify?bno=${dto.bno}&${link}`} className="text-decoration-none">
                                    <button type="button" className="btn btn-secondary">수정</button>
                                </a>
                            )}
                        </div>
                    </div>
                </div>
                {dto.fileNames && dto.fileNames.length > 0 && (
                    <div className="card">
                        {dto.fileNames.map(fileName => (
                            <img key={fileName} className="card-img-top" src={`http://localhost:8080/api/view/${fileName}`} alt="파일" />
                        ))}
                    </div>
                )}
            </div>
            <div className="row mt-3">
                <div className="col-md-12">
                    <div className="my-4">
                        <button className="btn btn-info" onClick={() => setModalType('register')}>댓글 작성</button>
                    </div>
                    <ul className="list-group replyList">
                        {replyList}
                    </ul>
                </div>
            </div>
            <div className="row mt-3">
                <div className="col">
                    <ul className="pagination replyPaging" dangerouslySetInnerHTML={{ __html: replyPaging }} />
                </div>
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
                                <div className="input-group mb-3">
                                    <span className="input-group-text">댓글 내용</span>
                                    <input type="text" className="form-control" value={replyText} onChange={e => setReplyText(e.target.value)} />
                                </div>
                                <div className="input-group mb-3">
                                    <span className="input-group-text">댓글 작성자</span>
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

            {/* Modify Modal */}
            {modalType === 'modify' && (
                <div className="modal modifyModal" tabIndex="-1">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">댓글 수정</h5>
                                <button type="button" className="btn-close" onClick={() => setModalType('')}></button>
                            </div>
                            <div className="modal-body">
                                <div className="input-group mb-3">
                                    <span className="input-group-text">댓글 내용</span>
                                    <input type="text" className="form-control" value={modifyText} onChange={e => setModifyText(e.target.value)} />
                                </div>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" onClick={() => setModalType('')}>취소</button>
                                <button type="button" className="btn btn-primary" onClick={handleModifyReply}>수정</button>
                                <button type="button" className="btn btn-danger" onClick={handleRemoveReply}>삭제</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            </div>
        </div>/* layout fragment end */
    );
};

export default BoardRead;