import React, { useContext } from 'react';
import { Plan } from './plan'; // 경로에 맞게 수정

const PlanRegister = () => {
    const { isCar, setIsCar } = useContext(Plan); // Plan 컨텍스트에서 값 가져오기

    return (
        <div>
            <h1>Plan Register</h1>
            <p>Is Car: {isCar ? 'Yes' : 'No'}</p>
            {/* 추가적인 로직 */}
        </div>
    );
};

export default PlanRegister;
