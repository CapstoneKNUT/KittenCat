import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './css/styles.css'; // 스타일 시트 import

const TravelPage = () => {
    const [currentSlide, setCurrentSlide] = useState(0);

    const slides = [
        { to: '/방콕', src: '/assets/지역/방콕.jpg', alt: '방콕' },
        { to: '/서울', src: '/assets/지역/서울.jpg', alt: '서울' },
        { to: '/청주', src: '/assets/지역/청주.jpg', alt: '청주' },
    ];

    const goToPreviousSlide = () => {
        setCurrentSlide((prevSlide) => (prevSlide === 0 ? slides.length - 1 : prevSlide - 1));
    };

    const goToNextSlide = () => {
        setCurrentSlide((prevSlide) => (prevSlide === slides.length - 1 ? 0 : prevSlide + 1));
    };

    return (
        <div id="layout-container">
            <main>
                <div className="search-bar">
                    <input type="text" placeholder="Hinted search text" />
                    <button className="search-button">🔍</button>
                </div>
                <div className="carousel">
                    <div className="slides">
                        {slides.map((slide, index) => (
                            <Link key={index} to={slide.to} style={{ display: index === currentSlide ? 'block' : 'none' }}>
                                <img src={slide.src} alt={slide.alt} className="icon" />
                            </Link>
                        ))}
                    </div>
                </div>
                <div className="button-container">
                    <button className="prev-button" onClick={goToPreviousSlide}>◀</button>
                    <button className="next-button" onClick={goToNextSlide}>▶</button>
                </div>
                <div className="actions">
                    <button className="interest-button">관심여행지(1)</button>
                    <button className="plan-button">계획짜기</button>
                </div>
            </main>
        </div>
    );
};

export default TravelPage;
