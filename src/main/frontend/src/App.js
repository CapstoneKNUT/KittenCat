import HeaderPage from 'C:/Users/taewan/OneDrive/바탕 화면/KittenCat-KittenCat/src/main/frontend/src/components/menus/Header.js';
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Lists from 'C:/Users/taewan/OneDrive/바탕 화면/KittenCat-KittenCat/src/main/frontend/src/pages/place/Lists.js';
import Main from 'C:/Users/taewan/OneDrive/바탕 화면/KittenCat-KittenCat/src/main/frontend/src/pages/Main/Main.js';
import Read from 'C:/Users/taewan/OneDrive/바탕 화면/KittenCat-KittenCat/src/main/frontend/src/pages/place/Read.js';
import Store from './Store/Store.js';

function App() {
    return (
        <Router>
            <HeaderPage />
            <Routes>
                <Route path="/place/list" element={<Lists />} />
                <Route path="/main" element={<Main />} />
                <Route path="/place/read/:pord" element={<Read />} />
                <Route path="/store" element={<Store />} />
            </Routes>
        </Router>
    );
}

export default App;