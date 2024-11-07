//BoardDelete.js
import React from 'react';
import axios from 'axios';

const BoardDelete = ({ bno, onDeleted }) => {
    const handleDelete = async () => {
        if (window.confirm('정말 삭제하시겠습니까?')) {
            try {
                await axios.post('http://localhost:8080/api/board/remove', { bno });
                onDeleted(); // 삭제 후 게시글 목록 갱신
            } catch (err) {
                console.error(err);
            }
        }
    };

    return (
        <button onClick={handleDelete}>삭제</button>
    );
};

export default BoardDelete;
