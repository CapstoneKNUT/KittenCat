import React, { useState, useRef } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import BasicLayout from "../../layout/BasicLayout";
import { uploadToServer, removeFileToServer } from '../../upload'; // upload.js의 함수 임포트

const BoardRegister = () => {
    const [errors, setErrors] = useState([]);
    const [filePreviews, setFilePreviews] = useState([]);
    const [showUploadModal, setShowUploadModal] = useState(false);
    const titleRef = useRef();
    const contentRef = useRef();
    const writerRef = useRef();
    const fileInputRef = useRef();
    const navigate = useNavigate(); // useNavigate 훅을 사용하여 페이지 이동

    const handleUploadFiles = async () => {
        const formData = new FormData();
        for (let file of fileInputRef.current.files) {
            formData.append('files', file);
        }

        try {
            const data = await uploadToServer(formData);
            setFilePreviews(data.map(file => ({
                ...file,
                url: `http://localhost:8080/api/view/${file.uuid}_${file.fileName}`
            })));
            setShowUploadModal(false);
        } catch (error) {
            console.error('Upload failed:', error);
            setErrors(['파일 업로드 중 문제가 발생했습니다.']);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData();
        formData.append('title', titleRef.current.value);
        formData.append('content', contentRef.current.value);
        formData.append('writer', writerRef.current.value);

        filePreviews.forEach(file => {
            formData.append('fileNames', `${file.uuid}_${file.fileName}`);
        });

        try {
            await axios.post('http://localhost:8080/api/board/register', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });
            // 폼 제출 후 리셋 및 리디렉션 처리
            navigate('/board/list'); // 게시판 목록 페이지로 이동
        } catch (error) {
            console.error('Form submission failed:', error);
        }
    };

    const handleRemoveFile = async (uuid, fileName) => {
        try {
            await removeFileToServer(uuid, fileName); // upload.js의 함수를 호출;
            setFilePreviews(prevFiles => prevFiles.filter(file => file.uuid !== uuid));
        } catch (error) {
            console.error('File removal failed:', error);
        }
    };

    return (
        <BasicLayout>
        <div className="container mt-3">
            <div className="card">
                <div className="card-header">Board Register</div>
                <div className="card-body">
                    <form onSubmit={handleSubmit}>
                        <div className="input-group mb-3">
                            <span className="input-group-text">제목</span>
                            <input type="text" ref={titleRef} className="form-control" placeholder="Title" />
                        </div>

                        <div className="input-group mb-3">
                            <span className="input-group-text">내용</span>
                            <textarea ref={contentRef} className="form-control col-sm-5" rows="5" />
                        </div>

                        <div className="input-group mb-3">
                            <span className="input-group-text">글쓴이</span>
                            <input type="text" ref={writerRef} className="form-control" placeholder="Writer" readOnly />
                        </div>

                        <div className="input-group mb-3">
                            <span className="input-group-text">이미지</span>
                            <div className="float-end uploadHidden">
                                <button type="button" className="btn btn-primary" onClick={() => setShowUploadModal(true)}>
                                    ADD Files
                                </button>
                            </div>
                        </div>

                        <div className="my-4">
                            <div className="float-end">
                                <button type="submit" className="btn btn-primary">등록</button>
                                <button type="reset" className="btn btn-secondary">리셋</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            {/* 첨부 파일 섬네일을 보여줄 부분 */}
            <div className="row mt-3">
                <div className="col">
                    <div className="container-fluid d-flex uploadResult" style={{ flexWrap: 'wrap' }}>
                        {filePreviews.map(file => (
                            <div className="card col-4" key={file.uuid}>
                                <div className="card-header d-flex justify-content-center">
                                    {file.fileName}
                                    <button className="btn-sm btn-danger ms-2" onClick={() => handleRemoveFile(file.uuid, file.fileName)}>X</button>
                                </div>
                                <div className="card-body">
                                    <img src={file.url} alt={file.fileName} />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* 첨부파일 추가를 위한 모달창 */}
            <div className={`modal ${showUploadModal ? 'show d-block' : ''}`} tabIndex="-1" role="dialog">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title">업로드 파일</h5>
                            <button type="button" className="btn-close" onClick={() => setShowUploadModal(false)}></button>
                        </div>
                        <div className="modal-body">
                            <div className="input-group mb-3">
                                <input type="file" ref={fileInputRef} className="form-control" multiple />
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-primary" onClick={handleUploadFiles}>업로드</button>
                            <button type="button" className="btn btn-outline-dark" onClick={() => setShowUploadModal(false)}>닫기</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </BasicLayout>
    );
};

export default BoardRegister;
