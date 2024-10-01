import {lazy, Suspense} from "react";
import loginRedirect from "../components/redirect/LoginRedirect";
import {Plan, PlanProvider} from "../pages/plan/plan";
import PlanRegister from "../pages/plan/register";
const {createBrowserRouter} = require("react-router-dom");

const Loading = <div>Loading....</div>

const App = lazy(() => import("../App"))
const Main = lazy(() => import("../pages/main"))
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
        path: "plan",
        element:<Suspense fallback={Loading}><PlanProvider><Plan /></PlanProvider></Suspense>,
        children: [
            {
                path:'register',
                element:<Suspense fallback={Loading}><PlanProvider><PlanRegister/></PlanProvider></Suspense>
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