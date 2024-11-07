import React, { useEffect, useState } from 'react';
import axios from "axios";

function ReviewList() {
    const [reviews, setReviews] = useState([]);
    const [pageRequest, setPageRequest] = useState({
        size: 10,
        page: 1,
    });
    const [responseData, setResponseData] = useState({
        dtoList: [],
        prev: false,
        next: false,
        start: 0,
        end: 0,
        page: 1,
    });

    const fetchReviews = async () => {
        try {
            const response = await axios.get('http://localhost:8080/api/review/list', {

            });
            if (response.data?.dtoList) {
                setResponseData(response.data);
            } else {
                console.error('Invalid data structure received from API.');
            }
        } catch (error) {
            console.log(error);
        }
    };

    useEffect(() => {
        fetchReviews();
    }, [pageRequest]);

    const handlePageChange = (pageNum) => {
        setPageRequest((prev) => ({
            ...prev,
            page: pageNum,
        }));
    };

    return (
        <div>
            <h2>Review List</h2>
            <ul>
                {reviews && reviews.map((review) => (
                    <li key={review.rno}>
                        <div>{review.content}</div>
                        <div>{review.writer}</div>
                    </li>
                ))}
            </ul>
            <div className="pagination">
                {responseData.prev && (
                    <button onClick={() => handlePageChange(responseData.page - 1)}>
                        Previous
                    </button>
                )}
                {[...Array(responseData.end - responseData.start + 1)].map((_, index) => {
                    const pageNum = responseData.start + index;
                    return (
                        <button
                            key={pageNum}
                            onClick={() => handlePageChange(pageNum)}
                            className={responseData.page === pageNum ? 'active' : ''}
                        >
                            {pageNum}
                        </button>
                    );
                })}
                {responseData.next && (
                    <button onClick={() => handlePageChange(responseData.page + 1)}>
                        Next
                    </button>
                )}
            </div>
        </div>
    );
}

export default ReviewList;
