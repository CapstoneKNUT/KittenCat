// TravelPlan.js
import React, { useState } from 'react';
import TravelDatePicker from './TravelDatePicker';
import { useNavigate } from 'react-router-dom';

function TravelPlan() {
  const [dates, setDates] = useState(null);
  const navigate = useNavigate();

  // onNext 함수 정의
  const handleNext = (selectedDates) => {
    setDates(selectedDates);
    // 다음 페이지로 이동
    navigate('/next-step'); 
  };

  return (
    <div>
      {!dates ? (
        // onNext prop을 TravelDatePicker에 전달
        <TravelDatePicker onNext={handleNext} />  
      ) : (
        <div>
          <h2>여행 일정이 저장되었습니다.</h2>
          <p>시작 날짜: {dates.startDate.toDateString()}</p>
          <p>종료 날짜: {dates.endDate.toDateString()}</p>
        </div>
      )}
    </div>
  );
}

export default TravelPlan;
