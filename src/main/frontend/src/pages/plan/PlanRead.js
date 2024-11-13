import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {Link, useNavigate, useParams} from 'react-router-dom';
import { useUser } from '../member/UserContext.js'; // UserContext에서 유저 정보를 가져오기
import carAvif from './components/car.avif';
import subwayPng from './components/subway.png';
import './PlanInit.css';

function PlanRead() {
    const { planNo } = useParams();
    const navigate = useNavigate();
    const { user } = useUser(); // 로그인한 유저 정보 가져오기
    const [isCar, setIsCar] = useState(true); // 차량 이용 여부 (true: 차, false: 대중교통)
    const [ps_startDate, setPsStartDate] = useState(''); // 출발 날짜
    const [readOnly, setReadOnly] = useState(false); // 출발 날짜
    const [duration, setDuration] = useState(null); //몇일 차인가?
    const [planData, setPlanData] = useState([]); //일정표의 장소
    const [planPlaces, setPlanPlaces] = useState([]); //일정표의 장소
    const [planPlaceAlls, setPlanPlaceAlls] = useState([]); //일정표의 장소
    const [LatestDate, setLatestDate] = useState(''); //장소 중 제일 마지막 일차
    const [transportParents, setTransportParents] = useState({}); //일정표의 장소
    const [transportChilds, setTransportChilds] = useState([]); //일정표의 장소
    const [dayTT, setDayTT] = useState(null);

    const ps_startDatePart = ps_startDate.split('T')[0];

    //Axios------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    // 저장된 일정 정보 가져오기(일정표 번호)
    useEffect(() => {
        const fetchPSData = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/api/plan/${planNo}`);
                setPlanData(response.data); // 단일 맵 형식의 데이터 설정
                console.log(response.data);
                setPsStartDate(formatDate(planData.ps_startDate))
                console.log(ps_startDate);
            } catch (err) {
                console.error('Error fetching plan data:', err);
            }
        };

        fetchPSData();
    }, []);

    //로그인 확인
    useEffect(() => {
        if (user) {

        } else {
            alert("로그인 후 이용해주세요.");
            navigate('/member/login');
        }
    }, [user, navigate]);

    //내 일정 여행장소 모두 가져오기
    const fetchPPDataAll = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planNo}/planplaceAll`);
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
            const response = await axios.get(`http://localhost:8080/api/plan/${planNo}/planplace`, {
                params: {
                    day,
                },
            });
            if (response.data) {
                console.log(response.data);
                const data = Array.isArray(response.data) ? response.data : [response.data];
                setPlanPlaces(data); // 상태 업데이트
                return data;
            } else {
                console.error('Invalid data structure received from API.');
            }
        } catch (error) {
            console.log(error);
        }
    };

    //내 일정 교통정보 가져오기
    const fetchTPData = async (ppOrd, day) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planNo}/TransportParent/${ppOrd}`, {
                params: {
                    day,
                },
            });
            if (response.data) {
                const data = Array.isArray(response.data) ? response.data : [response.data];
                setTransportParents(prev => ({
                    ...prev,
                    [ppOrd]: data
                }));
            }
        } catch (error) {
            console.log(error);
        }
    };
/*
    //내 일정 자식 교통정보 가져오기
    const fetchTCData = async (planNo) => {
        try {
            const response = await axios.get(`http://localhost:8080/api/plan/${planNo}/TransportParent/${tno}`);
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
        console.log("날짜"+dayDiff);
        // 날짜 차이 + 1 반환 (0일 차이 -> 1, 1일 차이 -> 2, 2일 차이 -> 3, ...)
        return dayDiff + 1;
    }

    function formatDate(dateArray) {
        if (!Array.isArray(dateArray) || dateArray.length < 3) return '';

        // 날짜 부분만 추출
        const [year, month, day] = dateArray.slice(0, 3);

        // 두 자리 형식으로 맞추기 (예: 1 -> "01")
        const formattedMonth = String(month).padStart(2, '0');
        const formattedDay = String(day).padStart(2, '0');
        console.log(`시작 날짜${year}-${formattedMonth}-${formattedDay}`)

        // "YYYY-MM-DD" 형식으로 반환
        return `${year}-${formattedMonth}-${formattedDay}`;
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

        console.log(`최신날짜:${year}-${month}-${day}`);
        return `${year}-${month}-${day}`; // "YYYY-MM-DD" 형식으로 반환
    }

    //리액트 기능------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    const handleTabClick = (tabNumber) => {
        setActiveTab(tabNumber); // 클릭된 탭으로 상태 업데이트
    };

    //css기능
    const [activeTab, setActiveTab] = useState(1); // 기본 활성화된 탭
    //mock 데이터------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //동적 데이터 호출 ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //html------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    return (
        <div className="container">
            <div className="plan-init">
                <h3>일정표 정보</h3>
                <div className="input-card">
                    <div className="input-field">
                        <label>일정표 제목:</label>
                        <input
                            type="text"
                            value={planData.title}
                            placeholder="일정표 제목을 입력하세요"
                            readOnly={!readOnly}
                        />
                    </div>
                    <div className="input-field">
                        <label>교통수단:</label>
                        <div className="transportation-options">
                            <label className={`transport-option ${planData.isCar ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    value={true}
                                    checked={isCar === true}
                                    disabled={!readOnly}
                                />
                                <img src={carAvif} alt="차" style={{width: 'auto', height: "43px"}}/>
                                <span>차</span>
                            </label>
                            <label className={`transport-option ${isCar === false ? 'selected' : ''}`}>
                                <input
                                    type="radio"
                                    value={false}
                                    checked={isCar === false}
                                    disabled={!readOnly}
                                />
                                <img src={subwayPng} alt="대중교통" style={{width: 'auto', height: "50px"}}/>
                                <span>대중교통</span>
                            </label>
                        </div>
                    </div>
                    <div className="input-field">
                        <label>출발 날짜:</label>
                        <input
                            type="text"
                            value={ps_startDate}
                            placeholder={`${ps_startDate}`}
                            disabled={!readOnly}
                        />
                    </div>
                </div>

                <button onClick={async () => {
                    const Ok = await fetchPPDataAll(); // savePlan이 완료될 때까지 대기
                    console.log(Ok);
                    console.log(planData.ps_startDate)
                    const latest = getLatestDate(planPlaceAlls);
                    console.log(latest);
                    setLatestDate(latest);

                    const gapDate = compareDates(ps_startDatePart, LatestDate)
                    console.log(gapDate);
                    setDuration(gapDate);
                }} className="save-button">일정표 보기
                </button>
            </div>
            <div className="plan-details">
                <h3>일정표</h3>
                <div className="tab-buttons">
                    {Array.from({length: duration}).map((_, index) => (
                        <div
                            key={index + 1} // key는 1부터 시작하는 값을 사용
                            className={`tab-button ${activeTab === index + 1 ? 'active' : ''}`}
                            onClick={() => {
                                handleTabClick(index + 1);
                                fetchPPData(index+1)
                                setDayTT(index +1)
                            }} // 클릭 시 해당 일차로 설정
                        >
                            {index + 1}일차
                        </div>
                    ))}
                </div>
                <div className="schedule-content">
                    {planPlaces.map((item, index) => (
                        <div className="schedule-item" key={index}>
                            {/* 교통 정보를 먼저 표시 */}
                            <div className="transport-info">
                                <h4>교통 정보</h4>
                                <button onClick={() => fetchTPData(item.ppOrd, dayTT)}>교통 정보 가져오기</button>
                                {transportParents[item.ppOrd]?.map((transport) => (
                                    <div key={transport.tno}>
                                        <p>교통 수단: {transport.t_method}</p>
                                        <p>출발 시간:
                                        {transport.t_startDateTime && (
                                            transport.t_startDateTime.length === 6
                                                ? `${transport.t_startDateTime[0]}-${(transport.t_startDateTime[1]).toString().padStart(2, '0')}-${transport.t_startDateTime[2].toString().padStart(2, '0')} 
                                               ${transport.t_startDateTime[3].toString().padStart(2, '0')}:${transport.t_startDateTime[4].toString().padStart(2, '0')}:${transport.t_startDateTime[5].toString().padStart(2, '0')}`
                                                : `${transport.t_startDateTime[0]}-${(transport.t_startDateTime[1]).toString().padStart(2, '0')}-${transport.t_startDateTime[2].toString().padStart(2, '0')} 
                                               ${transport.t_startDateTime[3].toString().padStart(2, '0')}:${transport.t_startDateTime[4].toString().padStart(2, '0')}:00`
                                        )}</p>
                                        <p>이동 시간: {transport.t_takeTime && (
                                            transport.t_takeTime.length === 3
                                                ? `${transport.t_takeTime[0]}:${(transport.t_takeTime[1]).toString().padStart(2, '0')}:${transport.t_takeTime[2].toString().padStart(2, '0')}`
                                                : `${transport.t_takeTime[0]}-${(transport.t_takeTime[1]).toString().padStart(2, '0')}-${transport.t_takeTime[2].toString().padStart(2, '0')}:00`
                                        )}</p>
                                        <p>도착 시간: {transport.t_goalDateTime && (
                                            transport.t_goalDateTime.length === 6
                                                ? `${transport.t_goalDateTime[0]}-${(transport.t_goalDateTime[1]).toString().padStart(2, '0')}-${transport.t_goalDateTime[2].toString().padStart(2, '0')} 
                                               ${transport.t_goalDateTime[3].toString().padStart(2, '0')}:${transport.t_goalDateTime[4].toString().padStart(2, '0')}:${transport.t_goalDateTime[5].toString().padStart(2, '0')}`
                                                : `${transport.t_goalDateTime[0]}-${(transport.t_goalDateTime[1]).toString().padStart(2, '0')}-${transport.t_goalDateTime[2].toString().padStart(2, '0')} 
                                               ${transport.t_goalDateTime[3].toString().padStart(2, '0')}:${transport.t_goalDateTime[4].toString().padStart(2, '0')}:00`
                                        )}</p>
                                    </div>
                                ))}
                            </div>

                            {/* 장소 정보 표시 */}
                            <span className="schedule-time">
                                {item.pp_startDate && (
                                    item.pp_startDate.length === 6
                                        ? `${item.pp_startDate[0]}-${(item.pp_startDate[1]).toString().padStart(2, '0')}-${item.pp_startDate[2].toString().padStart(2, '0')} 
                                           ${item.pp_startDate[3].toString().padStart(2, '0')}:${item.pp_startDate[4].toString().padStart(2, '0')}:${item.pp_startDate[5].toString().padStart(2, '0')}`
                                        : `${item.pp_startDate[0]}-${(item.pp_startDate[1]).toString().padStart(2, '0')}-${item.pp_startDate[2].toString().padStart(2, '0')} 
                                           ${item.pp_startDate[3].toString().padStart(2, '0')}:${item.pp_startDate[4].toString().padStart(2, '0')}:00`
                                )}
                            </span>
                            <span>{item.pp_title}</span>
                            <span>{item.pp_takeDate && (
                                `${item.pp_takeDate[0].toString().padStart(2, '0')}:${item.pp_takeDate[1].toString().padStart(2, '0')}`
                            )}</span>
                        </div>
                    ))}
                </div>

                {/*<div className="schedule-content">
                    {planPlaceAlls[activeTab] && Array.isArray(planPlaceAlls[activeTab]) && planPlaceAlls[activeTab].map((item, index) => (
                        <div className="schedule-item" key={item.ppOrd}>
            <span className="schedule-time">
                 pp_startDate는 배열이므로 첫 번째 요소로 날짜 추출
                {item.pp_startDate && new Date(item.pp_startDate[0], item.pp_startDate[1] - 1, item.pp_startDate[2], item.pp_startDate[3], item.pp_startDate[4]).toLocaleString()}
            </span>
                            <span>{item.pp_title}</span>
                            <span>
                 pp_takeDate도 배열이라서 첫 번째 값만 추출
                                {item.pp_takeDate && `${item.pp_takeDate[0]}분 ${item.pp_takeDate[1]}초`}
            </span>
                            <span>{item.pp_startAddress}</span>
                            <div className="schedule-actions">
                                <button>수정</button>
                                <button>삭제</button>
                            </div>
                        </div>
                    ))}
                </div>*/}
                <div>
                    <Link to={`/plan/list`}>
                    <button>목록으로 이동</button>
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default PlanRead;
