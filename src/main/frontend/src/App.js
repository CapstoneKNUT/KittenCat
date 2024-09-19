import {RouterProvider} from "react-router-dom"
import {Provider} from "react-redux"
import root from "./router/root";
import './pages/css/styles.css';
import store from './components/slice/store'

function App() {
    return (
        <Provider store={store}>
            <RouterProvider router={root}/>
        </Provider>

    );
}

export default App;