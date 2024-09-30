import {RouterProvider} from "react-router-dom"
import {Provider} from "react-redux"
import root from "./router/root";
import './pages/css/styles.css';
import store from './components/slice/store'
import {UserProvider} from "./components/context/UserContext";
import axios from "axios";
import {useEffect} from "react";

function App() {
    useEffect(() => {
        // Spring Boot 서버에서 쿠키를 설정하는 요청
        const setCookie = async () => {
            try {
                await axios.get('http://localhost:8080/set-cookie', {
                    withCredentials: true, // 쿠키 포함
                });
            } catch (error) {
                console.error('Error setting cookie:', error);
            }
        };

        setCookie();
    }, []);
    return (
        <UserProvider>
            <Provider store={store}>
                <RouterProvider router={root}/>
            </Provider>
        </UserProvider>
    );
}

export default App;