import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {Link, useNavigate} from 'react-router-dom';
import { useUser } from '../member/UserContext.js'; // UserContext에서 유저 정보를 가져오기
import carAvif from './components/car.avif';
import subwayPng from './components/subway.png';
import './PlanInit.css';

function PlanInit() {
    const navigate = useNavigate();
    const { user } = useUser(); // 로그인한 유저 정보 가져오기
    const [store, setStore] = useState([]);
    const [title, setTitle] = useState(''); // 여행지 제목
    const [isCar, setIsCar] = useState(true); // 차량 이용 여부 (true: 차, false: 대중교통)
    const [ps_startDate, setPsStartDate] = useState(''); // 출발 날짜
    const [readOnly, setReadOnly] = useState(false); // 출발 날짜
    const [planSet, setPlanSet] = useState({planNo: null});
    const [takeTime, setTakeTime] = useState({ hours: '00', minutes: '00' }); // 시간과 분을 각각 관리



    const handleTimeChange = (e) => {
        const { name, value } = e.target;
        setTakeTime((prev) => ({
            ...prev,
            [name]: value, // 시간 또는 분 값을 업데이트
        }));
    };

    const [pageRequest, setPageRequest] = useState({
        size: 12,
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

    // 일정 저장
    const savePlan = async () => {
        // 필수 입력값 확인
        if (!title) {
            alert("일정표 제목을 입력하세요."); // 제목이 비어있을 때 경고
            return;
        }

        if (!ps_startDate) {
            alert("출발 날짜를 선택하세요."); // 출발 날짜가 비어있을 때 경고
            return;
        }

        try {
            const planData = {
                title,
                isCar,
                writer: user.mid,
                ps_startDate,
            };

            await axios.post('http://localhost:8080/api/plan/register/init', planData);

            setReadOnly(true);
        } catch (error) {
            console.error('Error saving plan:', error);
        }
    };


    // 저장된 일정 정보 가져오기(일정표 번호)
    const fetchPSData = async () => {
        try {
            const planSetRes = await axios.get('http://localhost:8080/api/plan/write', {
                params : {writer: user.mid},
            });
            console.log("Received data:", planSetRes.data);
            if (planSetRes.data) {
                const planSetPlanNo = parseInt(planSetRes.data.planNo, 10)
                console.log(planSetPlanNo)
                setPlanSet({planNo : planSetPlanNo})
            } else {
                console.error('Invalid data structure received from API.');
            }

        } catch (error) {
            console.error('Error saving plan:', error);
        }
    };

    //내 찜목록 가져오기
    const fetchData = async (username) => {
        try {
            const response = await axios.get('http://localhost:8080/api/store/list', {
                params: {
                    username,
                },
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

    //로그인 확인
    useEffect(() => {
        if (user) {
            fetchData(user.mid);
        } else {
            alert("로그인 후 이용해주세요.");
            navigate('/member/login');
        }
    }, [user, navigate]);

    // 장소 추가 시 교통 정보 계산
    const addPlace = async (sno) => {
        if (takeTime) {
            const formattedTakeTime = `${takeTime.hours}:${takeTime.minutes}`; // "HH:mm" 형식으로 변환
            try {
                await axios.post(`http://localhost:8080/api/plan/${planSet.planNo}/add`, {
                    sno,
                    takeTime: formattedTakeTime // 문자열 형식으로 전송
                });
            } catch (error) {
                console.log(error);
            }
        } else {
            alert("머물 시간을 입력하세요");
        }
    };

    //mock 데이터
    useEffect(() => {
        // API에서 받은 데이터가 없을 경우 확인용 기본 데이터 설정
        if (responseData.dtoList && responseData.dtoList.length > 0) {
            setStore(responseData.dtoList); // API에서 받아온 데이터 설정
        } else {
            // 임시 데이터 설정
            const tempData = [
                {
                    sno: 1,
                    p_name: "예시 장소 1",
                    p_category: "음식점",
                    p_address: "서울시 강남구 임시 주소 1",
                    p_image: "https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fvideo-phinf.pstatic.net%2F20240920_40%2F1726792574994KnzQC_JPEG%2F46o3j90jLU_03.jpg",
                    p_star: 4.5,
                },
                {
                    sno: 2,
                    p_name: "예시 장소 2",
                    p_category: "카페",
                    p_address: "서울시 강남구 임시 주소 2",
                    p_image: "https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20240220_241%2F1708398564214Rqc5U_JPEG%2F%25C6%25BC%25BA%25BB.jpg",
                    p_star: 4.0,
                },
            ];
            setStore(tempData); // 임시 데이터를 설정
        }
    }, [responseData]);


    const generateHourOptions = () => {
        const options = [];
        for (let i = 0; i < 24; i++) {
            const hours = i < 10 ? `0${i}` : i; // 2자리 형식으로 시간 표시
            options.push(
                <option key={hours} value={hours}>
                    {hours}
                </option>
            );
        }
        return options;
    };

    // 0분부터 59분까지의 분 옵션 생성 함수
    const generateMinuteOptions = () => {
        const options = [];
        for (let i = 0; i < 60; i++) {
            const minutes = i < 10 ? `0${i}` : i; // 2자리 형식으로 분 표시
            options.push(
                <option key={minutes} value={minutes}>
                    {minutes}
                </option>
            );
        }
        return options;
    };


    // useEffect(() => {
    //     if (responseData.dtoList) {
    //         setStore(responseData.dtoList); // API에서 받아온 데이터 설정
    //     }
    // }, [responseData.dtoList]);


    return (
        <div className="container">
            <div className="plan-init">
                <h3>일정표 정보 입력</h3>
                <div className="input-card">
                    <div className="input-field">
                        <label>일정표 제목:</label>
                        <input
                            type="text"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="일정표 제목을 입력하세요"
                            readOnly={readOnly}
                        />
                    </div>
                    <div className="input-field">
                        <label>교통수단:</label>
                        <div className="transportation-options">
                            <label className={`transport-option ${isCar ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    value={true}
                                    checked={isCar === true}
                                    onChange={() => setIsCar(true)}
                                    disabled={readOnly}
                                />
                                <img src={carAvif} alt="차" style={{width: 'auto', height: "43px"}}/>
                                <span>차</span>
                            </label>
                            <label className={`transport-option ${isCar === false ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    value={false}
                                    checked={isCar === false}
                                    onChange={() => setIsCar(false)}
                                    disabled={readOnly}
                                />
                                <img src={subwayPng} alt="대중교통" style={{width: 'auto', height: "50px"}}/>
                                <span>대중교통</span>
                            </label>
                        </div>
                    </div>
                    <div className="input-field">
                        <label>출발 날짜:</label>
                        <input
                            type="datetime-local"
                            value={ps_startDate}
                            onChange={(e) => setPsStartDate(e.target.value)}
                            disabled={readOnly}
                        />
                    </div>
                </div>

                <button onClick={() => {
                    savePlan();
                    fetchPSData();
                }} className="save-button" disabled={readOnly}>저장하기
                </button>

                {/* 시간 입력 필드 추가 */}
                <div className="input-field">
                    <label>머물 시간:</label>
                    <div style={{ display: 'inline-block', marginRight: '10px' }}>
                        <select
                            name="hours"
                            value={takeTime.hours} // 상태에 저장된 시간 값
                            onChange={handleTimeChange} // 시간 선택 시 상태 업데이트
                            disabled={!readOnly} // readOnly일 경우 비활성화
                        >
                            {generateHourOptions()} {/* 시간 옵션 렌더링 */}
                        </select>
                    </div>
                    <div style={{ display: 'inline-block', marginRight: '10px' }}>
                        <span>:</span>
                    </div>
                    <div style={{ display: 'inline-block' }}>
                        <select
                            name="minutes"
                            value={takeTime.minutes} // 상태에 저장된 분 값
                            onChange={handleTimeChange} // 분 선택 시 상태 업데이트
                            disabled={!readOnly} // readOnly일 경우 비활성화
                        >
                            {generateMinuteOptions()} {/* 분 옵션 렌더링 */}
                        </select>
                    </div>
                </div>

                {/* 카드 형태로 저장된 store 정보 표시 */}
                <div className="store-list">
                    {store.length > 0 ? (
                        <div className="results-grid">
                            {store.map((storeItem) => (
                                <div key={storeItem.sno}
                                     onClick={() => {
                                         if (readOnly) {
                                             addPlace(storeItem.sno);
                                         }
                                     }}
                                     className="place-item" className="store-card"
                                     style={{display: 'flex', alignItems: 'center'}}>
                                    <img src={storeItem.p_image} alt={storeItem.p_name} className="store-image"
                                         style={{width: "100px", height: '80px', marginRight: '10px'}}/>
                                    <div className="store-info">
                                        <h4>{storeItem.p_name}</h4>
                                        <p>{storeItem.p_category}</p>
                                        <div>⭐ {storeItem.p_star}</div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="no-results">저장된 항목이 없습니다.</div>
                    )}
                </div>
            </div>
            <div className="plan-details">
                <h3>일정표</h3>
                <div className="details-content">
                    {/* 일정표 세부 내용을 여기에 추가 */}
                    <p>여기에 일정표를 작성하세요.</p>
                </div>
            </div>
        </div>
    );
}

export default PlanInit;
