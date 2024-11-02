import HeaderPage from './components/menus/Header.js';
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Lists from './pages/place/Lists.js';
import Main from './pages/Main/Main.js';
import Read from './pages/place/Read.js';
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