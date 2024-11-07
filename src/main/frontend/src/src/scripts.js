/*!
* Start Bootstrap - Simple Sidebar v6.0.3 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2021 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
//
// Scripts
//

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }

});

// document.getElementById('registerForm').addEventListener('submit', function(event) {
//     event.preventDefault(); // 폼의 기본 제출 동작을 막음
//
//     var password1 = document.getElementById('mpw').value;
//     var password2 = document.getElementById('mpwc').value;
//     var errorMessage = document.getElementById('errorMessage');
//
//     // 에러 메시지 초기화
//     errorMessage.textContent = '';
//
//     // 비밀번호 일치 확인
//     if (password1 !== password2) {
//         errorMessage.textContent = "비밀번호가 서로 다릅니다.";
//         alert('비밀번호가 서로 다릅니다.');
//         return;
//     }
//
//     // 비밀번호가 문자와 숫자의 조합인지 확인
//     var passwordPattern = /^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d]{8,}$/;
//     if (!passwordPattern.test(password1)) {
//         errorMessage.textContent = '비밀번호는 8장 이상의 문자와 숫자의 조합이 돼야합니다.';
//         alert('비밀번호는 8장 이상의 문자와 숫자의 조합이 돼야합니다.');
//         return;
//     }
//
//     // 모든 검증을 통과한 경우 폼을 제출하거나 다음 작업 수행
//     this.submit(); // 실제 폼 제출
// });
//
// //권태완이 추가한 코드, main 부분
// document.addEventListener('DOMContentLoaded', () => {
//     let slideIndex = 0;
//     const slides = document.querySelectorAll('.carousel-item');
//     const totalSlides = slides.length;
//     const prevButton = document.querySelector('.prev-button');
//     const nextButton = document.querySelector('.next-button');
//
//     function showSlide(index) {
//         slides.forEach((slide, i) => {
//             slide.style.transform = `translateX(${(i - index) * 100}%)`;
//         });
//     }
//
//     prevButton.addEventListener('click', () => {
//         slideIndex = (slideIndex > 0) ? slideIndex - 1 : totalSlides - 1;
//         showSlide(slideIndex);
//     });
//
//     nextButton.addEventListener('click', () => {
//         slideIndex = (slideIndex < totalSlides - 1) ? slideIndex + 1 : 0;
//         showSlide(slideIndex);
//     });
//
//     showSlide(slideIndex);
// });
