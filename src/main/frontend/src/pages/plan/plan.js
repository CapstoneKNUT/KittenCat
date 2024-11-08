import React, {useState, useEffect, useContext, createContext} from 'react';
import axios from 'axios';
import './plan.css';
import { useUser } from '../member/UserContext.js';
import {Link, useLocation, useNavigate} from 'react-router-dom';

function PlanRegister() {
    const location = useLocation();
    const { user } = useUser();
    const [store, setStore] = useState([]);
    const navigate = useNavigate();
    const [selectedPlaces, setSelectedPlaces] = useState([]);
    const [transportation, setTransportation] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [responseData, setResponseData] = useState({
        dtoList: [],
        prev: false,
        next: false,
        start: 0,
        end: 0,
        page: 1,
    });

    const [pageRequest, setPageRequest] = useState({
        size: 10,
        page: 1,
    });
    const [bookmarkedPlaces, setBookmarkedPlaces] = useState({
        dtoList: [],
        prev: false,
        next: false,
        start: 0,
        end: 0,
        page: 1,
    });
    const [savedPlanId, setSavedPlanId] = useState(null);

    useEffect(() => {
        if (user) {
            loadBookmarkedPlaces(user.mid);
        } else {
            alert("로그인 후 이용해주세요.");
            navigate('/member/login');
        }
    }, [user,pageRequest, navigate]);


    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const locationParam = searchParams.get('location') || '';
        const districtParam = searchParams.get('district') || '';
        const keywordParam = searchParams.get('keyword') || '';

        const filteredStores = responseData.dtoList.filter((item) => {
            const matchesLocation = locationParam ? item.p_address.includes(locationParam) : true;
            const matchesDistrict = districtParam ? item.p_address.includes(districtParam) : true;
            const matchesKeyword = keywordParam ? item.p_name.includes(keywordParam) : true;

            return matchesLocation && matchesDistrict && matchesKeyword;
        });

        if (filteredStores.length > 0) {
            setStore(filteredStores);
        } else {
            setStore(responseData.dtoList);
        }
    }, [location, responseData.dtoList]);

// 찜 목록 불러오기
const loadBookmarkedPlaces = async (username) => {
    try {
    const response = await axios.get('http://localhost:8080/api/store/list', {                params: {
            username,
        },});
        if (response.data?.dtoList) {
            setBookmarkedPlaces(response.data);
        } else {
            console.error('Invalid data structure received from API.');
        }
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
        {bookmarkedPlaces.dtoList.map(store => (
        <div 
            key={store.sno}
            onClick={() => addPlace(store)}
            className="store-item"
        >
            {store.p_name}
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