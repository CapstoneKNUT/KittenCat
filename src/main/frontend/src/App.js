import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import HeaderPage from '../src/components/menus/Header.js';
import Lists from './pages/place/Lists.js';
import PlanLists from './pages/plan/Lists.js';
import PlanRead from './pages/plan/PlanRead.js';
import Read from './pages/place/Read.js';
import StoreLists from './pages/Store/StoreLists.js';
import StoreRead from './pages/Store/StoreRead.js';
import LoginForm from './pages/member/LoginForm.js';
import UserProfile from './pages/member/UserProfile.js';
import { UserProvider } from './pages/member/UserContext.js';
import JoinForm from './pages/member/JoinForm.js';
import Modify from './pages/member/Modify.js';
import BoardList from './pages/board/list.js'; // BoardList 컴포넌트
import BoardRegister from "./pages/board/BoardRegister.js";
import BoardModify  from "./pages/board/BoardModify.js";
import BoardRead from "./pages/board/BoardRead";
import Plan from "./pages/plan/PlanInit";
import Main from './pages/Main/main.js';

function App() {
    return (
        <UserProvider>
            <Router>
                <HeaderPage />
                <Routes>
                    <Route path="/member/login" element={<LoginForm />} />
                    <Route path="/member/join" element={<JoinForm />} />
                    <Route path="/member/profile" element={<UserProfile />} />
                    <Route path="/member/modify" element={<Modify />} />

                    <Route path="/main" element={<Main />} />
                    <Route path="/" element={<Main />} />

                    <Route path="/place/list" element={<Lists />} />
                    <Route path="/place/read/:pord" element={<Read />} />

                    <Route path="/store/list" element={<StoreLists />} />
                    <Route path="/store/read/:sno" element={<StoreRead />} />

                    <Route path="/board/list" element={<BoardList />} />
                    <Route path="/board/register" element={<BoardRegister />} />
                    <Route path="/board/modify/:bno" element={<BoardModify />} />
                    <Route path="/board/read/:bno" element={<BoardRead />} />

                    <Route path="/plan" element={<Plan />} />
                    <Route path="/plan/list" element={<PlanLists />} />
                    <Route path="/plan/read/:planNo" element={<PlanRead />} />

                </Routes>
            </Router>
        </UserProvider>
    );
}

export default App;
