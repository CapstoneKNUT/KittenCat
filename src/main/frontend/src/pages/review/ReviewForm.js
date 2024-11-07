import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';

function ReviewForm() {
    const { rno } = useParams();
    const [content, setContent] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        if (rno) {
            const fetchReview = async () => {
                const response = await axios.get(`http://localhost:8080/api/board/read?rno=${rno}`);
                setContent(response.data.content);
            };
            fetchReview();
        }
    }, [rno]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const userId = localStorage.getItem("userId");
        const reviewData = { content, writer: userId };

        try {
            if (rno) {
                await axios.put(`http://localhost:8080/api/board/modify?userId=${userId}`, reviewData);
                alert("Review modified successfully");
            } else {
                await axios.post(`http://localhost:8080/api/board/register?userId=${userId}`, reviewData);
                alert("Review registered successfully");
            }
            navigate("/reviews");
        } catch (error) {
            console.error("Failed to submit review:", error);
        }
    };

    return (
        <div>
            <h2>{rno ? "Edit Review" : "New Review"}</h2>
            <form onSubmit={handleSubmit}>
        <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            required
        />
                <button type="submit">{rno ? "Update" : "Submit"}</button>
            </form>
        </div>
    );
}

export default ReviewForm;
