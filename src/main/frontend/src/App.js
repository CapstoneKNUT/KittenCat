import {RouterProvider} from "react-router-dom"
import root from "./router/root";
import './pages/css/styles.css';

function App() {
    return (
            <RouterProvider router={root}/>
    );
}

export default App;