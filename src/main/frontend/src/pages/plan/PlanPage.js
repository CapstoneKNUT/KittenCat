import React, { useState } from 'react';
import './PlanPage.css'; // CSS 파일 불러오기

function PlanPage() {
    const [currentDay, setCurrentDay] = useState(1); // 현재 선택된 일차
    const [plans, setPlans] = useState([]); // 일정 데이터
    const [time, setTime] = useState(''); // 시간 입력
    const [place, setPlace] = useState(''); // 장소 입력

    // 일정 추가 함수
    const addPlan = () => {
        if (!time || !place) {
            alert('시간과 장소를 모두 입력해주세요.');
            return;
        }

        const newPlan = { day: currentDay, time, place };
        setPlans([...plans, newPlan]);
        setTime('');
        setPlace('');
    };

    // 현재 일차 변경
    const changeDay = (day) => {
        setCurrentDay(day);
    };

    // 현재 일차의 일정만 필터링
    const currentDayPlans = plans.filter((plan) => plan.day === currentDay);

    return (
        <div className="plan-page">
            <h1>여행 일정 계획</h1>

            {/* 일차 선택 버튼 */}
            <div className="day-selector">
                {[1, 2, 3, 4, 5].map((day) => (
                    <button
                        key={day}
                        onClick={() => changeDay(day)}
                        className={day === currentDay ? 'active' : ''}
                    >
                        {day}일차
                    </button>
                ))}
            </div>

            {/* 일정 추가 폼 */}
            <div className="add-plan-form">
                <input
                    type="time"
                    value={time}
                    onChange={(e) => setTime(e.target.value)}
                />
                <input
                    type="text"
                    value={place}
                    onChange={(e) => setPlace(e.target.value)}
                    placeholder="장소 입력"
                />
                <button onClick={addPlan}>일정 추가</button>
            </div>

            {/* 일정 목록 */}
            <div className="plan-list">
                <h2>{currentDay}일차 일정</h2>
                {currentDayPlans.length === 0 ? (
                    <p>추가된 일정이 없습니다.</p>
                ) : (
                    currentDayPlans.map((plan, index) => (
                        <div key={index} className="plan-item">
                            <span>{plan.time}</span> - <span>{plan.place}</span>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}

export default PlanPage;
