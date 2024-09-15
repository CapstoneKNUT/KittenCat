module.exports = {
    plugins: {
        autoprefixer: {
            overrideBrowserslist: ['>0.2%', 'not dead', 'not op_mini all'],
            flexbox: 'no-2009',
            // autoprefixer 설정에 대한 추가 옵션이 필요한 경우 여기에 추가
        },
        // 다른 PostCSS 플러그인들이 필요하다면 여기에 추가
    }
};
