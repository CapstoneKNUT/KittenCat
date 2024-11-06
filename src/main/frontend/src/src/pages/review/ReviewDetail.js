import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

function ReviewDetail() {
    const { rno } = useParams();
    const [review, setReview] = useState(null);
    const navigate = useNavigate();

    const fetchReview = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/board/read?rno=${rno}`);
            setReview(response.data);
        } catch (error) {
            console.error("Failed to fetch review:", error);
        }
    };

    const handleDelete = async () => {
        try {
            const userId = localStorage.getItem("userId");
            await axios.post(`http://localhost:8080/api/board/remove?rno=${rno}&userId=${userId}`);
            alert("Review removed successfully");
            navigate("/reviews");
        } catch (error) {
            console.error("Failed to delete review:", error);
        }
    };

    useEffect(() => {
        fetchReview();
    }, [rno]);

    if (!review) return <p>Loading...</p>;

    return (
        <div>
            <h2>Review Detail</h2>
            <p>Content: {review.content}</p>
            <button onClick={() => navigate(`http://localhost:8080/reviews/edit/${rno}`)}>Edit</button>
            <button onClick={handleDelete}>Delete</button>
        </div>
    );
}

export default ReviewDetail;
