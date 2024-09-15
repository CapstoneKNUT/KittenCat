import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom'; // useNavigate 사용
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min';
import BasicLayout from "../../layout/BasicLayout";

function BoardModify() {
    const [dto, setDto] = useState({});
    const [fileNames, setFileNames] = useState([]);
    const [filesToRemove, setFilesToRemove] = useState([]);
    // eslint-disable-next-line no-unused-vars
    const [uploadFiles, setUploadFiles] = useState([]);
    // eslint-disable-next-line no-unused-vars
    const [errors, setErrors] = useState([]);
    const navigate = useNavigate(); // history를 navigate로 변경
    const { bno } = useParams();

    useEffect(() => {
        // 데이터를 가져오는 함수
        const fetchData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/board/${bno}`);
                setDto(response.data);
                setFileNames(response.data.fileNames || []);
            } catch (error) {
                console.error('Error fetching board data:', error);
            }
        };

        fetchData();
    }, [bno]);

    // 게시물 수정 함수
    const handleModify = async () => {
        try {
            const formData = new FormData();
            formData.append('bno', dto.bno);
            formData.append('title', dto.title);
            formData.append('content', dto.content);
            formData.append('writer', dto.writer);
            fileNames.forEach(fileName => formData.append('fileNames', fileName)); // 수정: fileNames는 배열이므로 여러 번 추가
            filesToRemove.forEach(file => {
                formData.append('removeFileNames', `${file.uuid}_${file.fileName}`);
            });

            await axios.post('http://localhost:8080/api/board/modify', formData);
            navigate('/board/list'); // history.push를 navigate로 변경
        } catch (error) {
            console.error('Error modifying board:', error);
        }
    };

    // 게시물 삭제 함수
    const handleRemove = async () => {
        try {
            await axios.post('http://localhost:8080/api/board/remove', { bno: dto.bno });
            navigate('/board/list'); // history.push를 navigate로 변경
        } catch (error) {
            console.error('Error removing board:', error);
        }
    };

    // 파일 업로드 함수
    const handleUploadFiles = async (event) => {
        try {
            const formData = new FormData();
            Array.from(event.target.files).forEach(file => formData.append('files', file));

            const response = await axios.post('http://localhost:8080/api/board/upload', formData);
            setUploadFiles(response.data);
        } catch (error) {
            console.error('Error uploading files:', error);
        }
    };

    // 파일 삭제 함수
    const handleFileRemove = (uuid, fileName) => {
        if (!window.confirm('파일을 삭제하시겠습니까?')) return;
        setFilesToRemove([...filesToRemove, { uuid, fileName }]);
        setFileNames(fileNames.filter(file => file !== `${uuid}_${fileName}`));
    };

    return (
        <BasicLayout>
        <div className="container mt-3">
            <div className="card">
                <div className="card-header">Board Modify</div>
                <div className="card-body">
                    <form id="f1" onSubmit={e => e.preventDefault()}>
                        <div className="input-group mb-3">
                            <span className="input-group-text">Bno</span>
                            <input type="text" className="form-control" value={dto.bno || ''} name="bno" readOnly />
                        </div>
                        <div className="input-group mb-3">
                            <span className="input-group-text">Title</span>
                            <input
                                type="text"
                                className="form-control"
                                value={dto.title || ''}
                                onChange={e => setDto({ ...dto, title: e.target.value })}
                            />
                        </div>
                        <div className="input-group mb-3">
                            <span className="input-group-text">Content</span>
                            <textarea
                                className="form-control"
                                rows="5"
                                value={dto.content || ''}
                                onChange={e => setDto({ ...dto, content: e.target.value })}
                            ></textarea>
                        </div>
                        <div className="input-group mb-3">
                            <span className="input-group-text">Writer</span>
                            <input type="text" className="form-control" value={dto.writer || ''} readOnly />
                        </div>
                        <div className="input-group mb-3">
                            <span className="input-group-text">RegDate</span>
                            <input
                                type="text"
                                className="form-control"
                                value={dto.regDate ? new Date(dto.regDate).toLocaleString() : ''}
                                readOnly
                            />
                        </div>
                        <div className="input-group mb-3">
                            <span className="input-group-text">ModDate</span>
                            <input
                                type="text"
                                className="form-control"
                                value={dto.modDate ? new Date(dto.modDate).toLocaleString() : ''}
                                readOnly
                            />
                        </div>
                        <div className="my-4">
                            <div className="float-end">
                                <button className="btn btn-primary" type="button" onClick={handleModify}>
                                    Modify
                                </button>
                                <button className="btn btn-danger" type="button" onClick={handleRemove}>
                                    Remove
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div className="row mt-3">
                <div className="col">
                    <div className="container-fluid d-flex uploadResult" style={{ flexWrap: 'wrap' }}>
                        {fileNames.map((fileName, index) => {
                            const [uuid, file] = fileName.split('_');
                            return (
                                <div className="card col-4" key={index}>
                                    <div className="card-header d-flex justify-content-center">
                                        {file}
                                        <button
                                            className="btn-sm btn-danger"
                                            onClick={() => handleFileRemove(uuid, file)}
                                        >
                                            X
                                        </button>
                                    </div>
                                    <div className="card-body">
                                        <img src={`/view/s_${fileName}`} alt={file} />
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
            <div
                className="modal fade uploadModal"
                tabIndex="-1"
                role="dialog"
                aria-labelledby="uploadModalLabel"
                aria-hidden="true"
            >
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title" id="uploadModalLabel">
                                Upload File
                            </h5>
                            <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div className="modal-body">
                            <input
                                type="file"
                                className="form-control"
                                multiple
                                onChange={handleUploadFiles}
                            />
                        </div>
                        <div className="modal-footer">
                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={handleUploadFiles} // 수정: 이 버튼은 파일 업로드를 트리거하지 않음
                            >
                                Upload
                            </button>
                            <button type="button" className="btn btn-outline-dark" data-bs-dismiss="modal">
                                Close
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </BasicLayout>
    );
}

export default BoardModify;
