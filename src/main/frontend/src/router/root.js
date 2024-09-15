import {lazy, Suspense} from "react";
import loginRedirect from "../components/redirect/LoginRedirect";
const {createBrowserRouter} = require("react-router-dom");

const Loading = <div>Loading....</div>

const App = lazy(() => import("../App"))
const Main = lazy(() => import("../pages/main"))
const BoardList = lazy(() => import("../pages/board/list"))
const BoardRead = lazy(() => import("../pages/board/read"))
const BoardRegister = lazy(() => import("../pages/board/register"))
const BoardModify = lazy(() => import("../pages/board/modify"))
const PlaceList = lazy(() => import("../pages/place/list"))
const PlaceRead = lazy(() => import("../pages/place/read"))
const MemberJoin = lazy(() => import("../pages/member/join"))
const MemberLogin = lazy(() => import("../pages/member/login"))

const root = createBrowserRouter([
    {
        path: "",
        element: <Suspense fallback={Loading}><Main><loginRedirect/></Main></Suspense>,
    },
    {
        path: "main",
        element: <Suspense fallback={Loading}><Main/></Suspense>,
    },
    {
        path: "board",
        children: [
            {
                path:'list',
                element:<Suspense fallback={Loading}><BoardList/></Suspense>
            },
            {
                path:'read',
                element:<Suspense fallback={Loading}><BoardRead/></Suspense>
            },
            {
                path:'register',
                element:<Suspense fallback={Loading}><BoardRegister/></Suspense>
            },
            {
                path:'modify',
                element:<Suspense fallback={Loading}><BoardModify/></Suspense>
            },
        ]
    },
    {
        path: "place",
        children: [
            {
                path:'list',
                element:<Suspense fallback={Loading}><PlaceList/></Suspense>
            },
            {
                path:'read',
                element:<Suspense fallback={Loading}><PlaceRead/></Suspense>
            },
        ]
    },
    {
        path: "member",
        children: [
            {
                path:'login',
                element:<Suspense fallback={Loading}><MemberLogin/></Suspense>
            },
            {
                path:'join',
                element:<Suspense fallback={Loading}><MemberJoin/></Suspense>
            },
        ]
    },
]);

export default root