// TravelDatePicker.js
import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import './TravelDatePicker.css';

function TravelDatePicker({ onNext }) {  // onNext prop을 받음
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const handleDateChange = (dates) => {
    const [start, end] = dates;
    setStartDate(start);
    setEndDate(end);
  };

  const handleNextStep = () => {
    if (!startDate || !endDate) {
      alert("여행 날짜를 모두 선택해 주세요.");
      return;
    }

    // 부모 컴포넌트로 선택된 날짜 전달
    onNext({ startDate, endDate });
  };

  return (
    <div className="travel-datepicker">
      <h2>여행 기간이 어떻게 되시나요?</h2>
      <p>* 여행 일자는 최대 10일까지 설정 가능합니다.</p>
      <DatePicker
        selected={startDate}
        onChange={handleDateChange}
        startDate={startDate}
        endDate={endDate}
        selectsRange
        inline
        minDate={new Date()}
        maxDate={new Date().setDate(new Date().getDate() + 365)}
        monthsShown={2}
        dateFormat="yyyy년 MM월 dd일"
      />
      <button onClick={handleNextStep}>선택</button>
    </div>
  );
}

export default TravelDatePicker;
