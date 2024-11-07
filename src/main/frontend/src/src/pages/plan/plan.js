import React, { createContext, useState } from 'react';

// Context 생성
export const Plan = createContext();

// Provider 컴포넌트
export const PlanProvider = ({ children }) => {
    const [isCar, setIsCar] = useState(true);
    const [isBike, setIsBike] = useState(true);

    return (
        <Plan.Provider value={{ isCar, setIsCar, isBike, setIsBike }}>
            {children}
        </Plan.Provider>
    );
};
