import { lazy, Suspense } from "react";
import { createBrowserRouter } from "react-router-dom";
import Layout from "../layouts/Layout";

// 로딩 컴포넌트 
const Loading = () => <div className="loading-spinner">Loading...</div>;

// 컴포넌트 lazy 로딩
const Main = lazy(() => import("../pages/main"));
const PlaceList = lazy(() => import("../pages/place/Lists"));
const PlaceRead = lazy(() => import("../pages/place/Read"));
const StoreList = lazy(() => import("../Store/StoreLists"));
const StoreRead = lazy(() => import("../Store/StoreRead")); 
const Plan = lazy(() => import("../pages/plan/plan"));
const PlanRegister = lazy(() => import("../pages/plan/register"));

// 컴포넌트 래퍼
const withSuspense = (Component) => (
<Suspense fallback={<Loading />}>
    <Component />
</Suspense>
);

const router = createBrowserRouter([
{
    path: "/",
    element: <Layout />,
    children: [
        {
            index: true,
            element: withSuspense(Main)
        },
        {
            path: "place",
            children: [
                {
                    path: "list",
                    element: withSuspense(PlaceList)
                },
                {
                    path: "read/:pord",
                    element: withSuspense(PlaceRead)
                }
            ]
        },
        {
            path: "store",
            children: [
                {
                    path: "list",
                    element: withSuspense(StoreList)
                },
                {
                    path: "read/:sno",
                    element: withSuspense(StoreRead)
                }
            ]
        },
        {
            path: "plan",
            children: [
                {
                    index: true, 
                    element: withSuspense(Plan)
                },
                {
                    path: "register",
                    element: withSuspense(PlanRegister)
                }
            ]
        }
    ]
},
{
    path: "*",
    element: <div>404 Not Found</div>
}
]);

export default router;