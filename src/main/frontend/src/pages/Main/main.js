// import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Main.css';
import travelGif from './components/travel.gif'; // GIF 파일 경로로 수정
import travelIcon1 from './components/travel-icon1.png';
import travelIcon2 from './components/travel-icon2.png';
import travelIcon3 from './components/travel-icon3.png';

function Main() {
    const navigate = useNavigate();

    return (
        <div className="main-page">
            <section className="banner">
                <div className="banner-content">
                    <h1>즐겁고 간편한 여행 계획!</h1>
                    <p>최고로 즐거운 여행! Plan Be 와 함께하세요.</p>
                    <button className="plan-now-btn" onClick={() => navigate('/plan')}>
                        PLAN NOW
                    </button>
                </div>
                <div className="banner-images">
                    <img src={travelGif} alt="Banner GIF"/>
                </div>
            </section>

            <section className="plan-be-section">
                <div className="travel-container">
                    <div className="travel-item">
                        <img src={travelIcon1} alt="어디로 가든 즐거운 여행"/>
                        <p>어디로 가든 즐거운 여행</p>
                    </div>
                    <div className="travel-item">
                        <img src={travelIcon2} alt="효율적인 시간 관리" />
                        <p>효율적인 시간 관리</p>
                    </div>
                    <div className="travel-item">
                        <img src={travelIcon3} alt="친구와 공유하는 나의 일정" />
                        <p>친구와 공유하는 나의 일정</p>
                    </div>
                </div>
                <button className="plan-be-btn" onClick={() => navigate('/about')}>
                    Plan Be와 함께하세요!
                </button>
            </section>
        </div>
    );
}

export default Main;
