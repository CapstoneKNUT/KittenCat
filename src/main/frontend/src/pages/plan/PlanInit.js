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
    const [day, setDay] = useState(null);
    const [store, setStore] = useState([]);
    const [title, setTitle] = useState(''); // 여행지 제목
    const [isCar, setIsCar] = useState(true); // 차량 이용 여부 (true: 차, false: 대중교통)
    const [ps_startDate, setPsStartDate] = useState(''); // 출발 날짜
    const [readOnly, setReadOnly] = useState(false); // 출발 날짜
    const [planSet, setPlanSet] = useState({planNo: null});
    const [takeTime, setTakeTime] = useState(''); // 시간과 분을 각각 관리
    const [takeHourTime, setTakeHourTime] = useState(''); // 시간과 분을 각각 관리
    const [takeMinuteTime, setTakeMinuteTime] = useState(''); // 시간과 분을 각각 관리
    const [duration, setDuration] = useState(null); //몇일 차인가?
    const [planPlaces, setPlanPlaces] = useState([]); //일정표의 장소
    const [planPlaceAlls, setPlanPlaceAlls] = useState([]); //일정표의 장소
    const [LatestDate, setLatestDate] = useState(''); //장소 중 제일 마지막 일차
    const [transportParents, setTransportParents] = useState([]); //일정표의 장소
    const [transportChilds, setTransportChilds] = useState([]); //일정표의 장소

    const ps_startDatePart = ps_startDate.split('T')[0];

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

    //Axios------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //로그인 확인
    useEffect(() => {
        if (user) {

        } else {
            alert("로그인 후 이용해주세요.");
            navigate('/member/login');
        }
    }, [user, navigate]);

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

    // 장소를 데이터베이스에 추가 + 장소 추가 시 교통 정보 계산
    const addPlace = async (sno) => {
        if(takeTime){
            try {
                await axios.post(`http://localhost:8080/api/plan/${planSet.planNo}/add`, {
                    sno,
                    takeTime: takeTime
                });

                const updatedPlanPlaces = await fetchPPDataAll();
                console.log("버그 찾기1", updatedPlanPlaces)
                const latest = getLatestDate(planPlaceAlls);
                console.log("버그 찾기2",latest)
                setLatestDate(latest);

                const gapDate = compareDates(ps_startDatePart, LatestDate)
                console.log("버그 찾기3", gapDate)
                setDuration(gapDate);
            } catch (error) {
                console.log("버그 찾기4"+error);
            }
        }else{
            alert("머물 시간을 입력하세요")
        }
    };

    //내 일정 여행장소 모두  가져오기
    const fetchPPDataAll = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planSet.planNo}/planplaceAll`);
            if (response.data) {
                console.log(response.data);
                const data = Array.isArray(response.data) ? response.data : [response.data];
                setPlanPlaceAlls(data); // 상태 업데이트
                return data;
            } else {
                console.error('Invalid data structure received from API.');
            }
        } catch (error) {
            console.log(error);
        }
    };

    //내 일정 여행장소 가져오기
    const fetchPPData = async (day) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planSet.planNo}/planplace`, {
                params: {
                    day,
                },
            });
            if (response.data) {
                console.log(response.data);
                const data = Array.isArray(response.data) ? response.data : [response.data];
                setPlanPlace(data); // 상태 업데이트
                return data;
            } else {
                console.error('Invalid data structure received from API.');
            }
        } catch (error) {
            console.log(error);
        }
    };

/*
    //내 일정 교통정보 가져오기
    const fetchTPData = async (planNo) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planSet.planNo}/TransportParent/${ppOrd}`, {
                params: {
                    day,
                },
            });
            if (response.data?.dtoList) {
                setTransportParents(response.data);
            } else {
                console.error('Invalid data structure received from API.');
            }
        } catch (error) {
            console.log(error);
        }
    };

    //내 일정 자식 교통정보 가져오기
    const fetchTCData = async (planNo) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planSet.planNo}/TransportParent/${tno}`);
            if (response.data?.dtoList) {
                setTransportChilds(response.data);
            } else {
                console.error('Invalid data structure received from API.');
            }
        } catch (error) {
            console.log(error);
        }
    };
*/

    //데이터 다루기------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //날짜 비교해서 며칠차인지 구하기
    function compareDates(date1, date2) {
        // Date 객체를 생성하거나 받은 값이 Date 형식인지 확인
        const d1 = new Date(date1);
        const d2 = new Date(date2);

        // 두 날짜를 비교하기 위해 시간 부분 제거 (UTC 기준으로 비교)
        d1.setHours(0, 0, 0, 0);
        d2.setHours(0, 0, 0, 0);

        // 날짜 차이 계산 (밀리초 단위 차이 계산 후 일 단위로 변환)
        const timeDiff = Math.abs(d1 - d2);
        const dayDiff = timeDiff / (1000 * 60 * 60 * 24);

        // 날짜 차이 + 1 반환 (0일 차이 -> 1, 1일 차이 -> 2, 2일 차이 -> 3, ...)
        return dayDiff + 1;
    }

    //최신날짜 구하기
    const getLatestDate = (planPlaceAlls) => {
        // planPlaceAlls 배열이 비어 있으면 빈 문자열 반환
        if (!planPlaceAlls || planPlaceAlls.length === 0) {
            return "";
        }

        // pp_startDate 값을 기준으로 최신 날짜 찾기
        const latestDate = planPlaceAlls.reduce((latest, current) => {
            // current.pp_startDate가 존재하고 Date 형식으로 변환 가능하다면
            if (current.pp_startDate) {
                const [year, month, day, hour = 0, minute = 0] = current.pp_startDate;
                const currentDate = new Date(year, month - 1, day, hour, minute); // 월은 0부터 시작하므로 -1

                // 최신 날짜를 비교하여 반환
                return currentDate > latest ? currentDate : latest;
            }
            return latest; // pp_startDate가 없는 경우, 기존 최신 날짜 반환
        }, new Date(0)); // 기본값으로 아주 오래된 날짜 설정

        // 최신 날짜의 년, 월, 일만 반환 (YYYY-MM-DD 형식)
        const year = latestDate.getFullYear();
        const month = String(latestDate.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작해서 1을 더함
        const day = String(latestDate.getDate()).padStart(2, '0'); // 일도 두 자리로 맞추기

        return `${year}-${month}-${day}`; // "YYYY-MM-DD" 형식으로 반환
    }

    //리액트 기능------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //mock 데이터------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //css기능
    const [activeTab, setActiveTab] = useState(1); // 기본 활성화된 탭
    // 각 일차별 일정 데이터
    const scheduleData = {
        1: [
            { time: '09:00', place: '서울역', duration: '09:00~10:00' },
            { time: '10:00', place: '고궁 투어', duration: '11:00~12:30' },
        ],
        2: [
            { time: '09:00', place: '남산타워', duration: '09:00~10:30' },
            { time: '11:00', place: '명동 쇼핑', duration: '11:00~12:30' },
        ],
        3: [
            { time: '10:00', place: '한강 유람선', duration: '10:00~11:30' },
            { time: '12:00', place: '인사동', duration: '12:00~13:30' },
        ],
    };

    //동적 데이터 호출 ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //찜 목록
    useEffect(() => {
        // API에서 받은 데이터가 없을 경우 확인용 기본 데이터 설정
        if (responseData.dtoList && responseData.dtoList.length > 0) {
            setStore(responseData.dtoList); // API에서 받아온 데이터 설정
        }
    }, [responseData]);

    //html------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

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
                    fetchData(user.mid);
                    savePlan();
                    fetchPSData();
                }} className="save-button" disabled={readOnly}>시작하기
                </button>

                {/* 시간 입력 필드 추가 */}
                <div className="input-field">
                    <label>머물 시간:</label>
                    <input
                        type="time" // HTML5의 time input 사용
                        value={takeTime} // 상태에 저장된 시간 값
                        onChange={handleTimeChange} // 시간 입력 시 상태 업데이트
                        disabled={!readOnly} // readOnly일 경우 비활성화
                    />
                </div>

                {/* 카드 형태로 저장된 store 정보 표시 */}
                <div className="store-list">
                    {store.length > 0 ? (
                        <div className="results-grid">
                            {store.map((storeItem) => (
                                <div key={storeItem.sno}
                                     onClick={() => {
                                         if (readOnly) {
                                             addPlace(storeItem.sno); //장소를 데이터에 넣기
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
                <div className="tab-buttons">
                    {Array.from({length: duration}).map((_, index) => (
                        <div
                            key={index + 1} // key는 1부터 시작하는 값을 사용
                            className={`tab-button ${activeTab === index+1 ? 'active' : ''}`}
                            onClick={() => {
                                handleTabClick(index+1);
                                setDay(index+1);
                                fetchPPData(day)
                            }} // 클릭 시 해당 일차로 설정
                        >
                            {index+1}일차
                        </div>
                    ))}
                </div>
                <div className="schedule-content">
                    {planPlaces.map((planplace) => (
                        <div
                            key={parseInt(planplace.ppOrd, 10)}>
                            className="schedule-item"
                            <div>
                                <span className="schedule-time">{planplace.pp_startDate}</span>
                                <span>{planplace.pp_title}</span>
                                <span>{planplace.pp_takeDate}</span>
                            </div>
                            <div className="schedule-actions">
                                <button>수정</button>
                                <button>삭제</button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default PlanInit;
