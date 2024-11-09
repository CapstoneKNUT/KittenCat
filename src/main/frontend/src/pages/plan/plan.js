import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

function PlanRegister() {
const [selectedPlaces, setSelectedPlaces] = useState([]);
const [transportation, setTransportation] = useState('');
const [startDate, setStartDate] = useState('');
const [endDate, setEndDate] = useState('');
const [bookmarkedPlaces, setBookmarkedPlaces] = useState([]);
const [savedPlanId, setSavedPlanId] = useState(null);

// 찜 목록 불러오기
const loadBookmarkedPlaces = async () => {
    try {
    const response = await axios.get('http://localhost:8080/api/store/list');
    setBookmarkedPlaces(response.data);
    } catch (error) {
    console.error('Error loading bookmarks:', error);
    }
};

// 장소 추가 시 교통 정보 계산
const addPlace = (place) => {
    setSelectedPlaces(prev => {
    const newPlaces = [...prev, place];
    if (newPlaces.length > 1) {
        calculateTransportation(newPlaces[newPlaces.length - 2], place);
    }
    return newPlaces;
    });
};

const calculateTransportation = (from, to) => {
    return "30분"; // 예시 값
};

// 일정 저장
const savePlan = async () => {
    try {
    const planData = {
        title: "새로운 여행",
        startDate,
        places: selectedPlaces.map((place, index) => ({
        pord: place.pord,
        visitOrder: index + 1,
        startTime: place.startTime,
        duration: place.duration
        }))
    };

    const response = await axios.post('http://localhost:8080/api/plan/register', planData);
    setSavedPlanId(response.data.pid); // 저장된 plan의 ID를 상태에 저장
    } catch (error) {
    console.error('Error saving plan:', error);
    }
};

return (
    <div className="plan-register">
    <div className="header">
        <Link to="/" className="home-button">메인으로</Link>
        <Link to="/plan/list" className="list-button">일정 목록</Link>
    </div>

    <div className="bookmarked-places">
        <h3>찜한 장소</h3>
        {bookmarkedPlaces.map(place => (
        <div
            key={place.pord}
            onClick={() => addPlace(place)}
            className="place-item"
        >
            {place.p_name}
        </div>
        ))}
    </div>

    <div className="schedule-timeline">
        <div className="date-selector">
        <input
            type="datetime-local"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
        />
        </div>

        <div className="selected-places">
        {selectedPlaces.map((place, index) => (
            <div key={index} className="timeline-item">
            <div className="time-input">
                <input
                type="time"
                value={place.startTime || ''}
                onChange={(e) => {
                    const newPlaces = [...selectedPlaces];
                    newPlaces[index].startTime = e.target.value;
                    setSelectedPlaces(newPlaces);
                }}
                />
            </div>
            <div className="place-info">
                {place.p_name}
                {index < selectedPlaces.length - 1 && (
                <div className="transportation-info">
                    {transportation}
                </div>
                )}
            </div>
            </div>
        ))}
        </div>
    </div>

    {savedPlanId ? (
        <div className="save-success">
        <p>일정이 저장되었습니다!</p>
        <Link to={`/plan/detail/${savedPlanId}`} className="view-plan-button">
            저장된 일정 보기
        </Link>
        </div>
    ) : (
        <button onClick={savePlan} className="save-button">저장하기</button>
    )}
    </div>
);
}

// 추가된 CSS
const additionalStyles = `
.header {
    display: flex;
    justify-content: space-between;
    padding: 20px;
    background-color: #f5f5f5;
    margin-bottom: 20px;
}

.home-button,
.list-button,
.view-plan-button {
    padding: 10px 20px;
    border-radius: 5px;
    text-decoration: none;
    color: white;
    background-color: #007bff;
}

.save-success {
    text-align: center;
    margin: 20px 0;
    padding: 20px;
    background-color: #d4edda;
    border-radius: 5px;
}

.save-button {
    display: block;
    width: 200px;
    margin: 20px auto;
    padding: 10px;
    border: none;
    border-radius: 5px;
    background-color: #007bff;
    color: white;
    cursor: pointer;
}

.save-button:hover {
    background-color: #0056b3;
}
`;

export default PlanRegister;