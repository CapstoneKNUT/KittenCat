import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../member/UserContext.js'; // UserContext에서 유저 정보를 가져오기
import carAvif from './components/car.avif';
import subwayPng from './components/subway.png';
import './PlanInit.css';

function PlanInit() {
    const navigate = useNavigate();
    const { user } = useUser(); // 로그인한 유저 정보 가져오기
    const [title, setTitle] = useState(''); // 여행지 제목
    const [isCar, setIsCar] = useState(true); // 차량 이용 여부 (1: 차, 0: 대중교통)
    const [ps_startDate, setPsStartDate] = useState(''); // 출발 날짜

    useEffect(() => {
        if (!user) {
            alert("로그인 후 이용해주세요.");
            navigate('/member/login'); // 로그인 페이지로 이동
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
                writer : user.mid,
                ps_startDate,
            };

            await axios.post('http://localhost:8080/api/plan/register/init', planData);

            // 저장 후 다른 페이지로 이동
            navigate('/plan/register');
        } catch (error) {
            console.error('Error saving plan:', error);
        }
    };

    return (
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
                    />
                </div>
                <div className="input-field">
                    <label>교통수단:</label>
                    <div className="transportation-options">
                        <label className={`transport-option ${isCar === 1 ? 'selected' : ''}`}>
                            <input
                                type="radio"
                                value={true}
                                checked={isCar === true}
                                onChange={() => setIsCar(true)}
                            />
                            <img src={carAvif} alt="차" style={{ width: 'auto', height: "43px" }}/>
                            <span>
                                차
                            </span>
                        </label>
                        <label className={`transport-option ${isCar === 0 ? 'selected' : ''}`}>
                            <input
                                type="radio"
                                value={false}
                                checked={isCar === false}
                                onChange={() => setIsCar(false)}
                            />
                            <img src={subwayPng} alt="대중교통" style={{ width: 'auto', height: "50px" }}/>
                            <span>
                                대중교통
                            </span>
                        </label>
                    </div>
                </div>
                <div className="input-field">
                <label>출발 날짜:</label>
                    <input
                        type="datetime-local"
                        value={ps_startDate}
                        onChange={(e) => setPsStartDate(e.target.value)}
                    />
                </div>
            </div>
            <button onClick={savePlan} className="save-button">저장하기</button>
        </div>
    );
}

export default PlanInit;
