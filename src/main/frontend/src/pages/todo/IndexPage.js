import React from "react";
import {Outlet} from "react-router-dom";
import BasicLayout from '../../layout/BasicLayout';

function IndexPage(prop){
    return(
        <BasicLayout>
            <div>
                <div>LIST</div>
                <div>ADD</div>
            </div>
            <div>
                <Outlet/>
            </div>
        </BasicLayout>
    )
}

export default IndexPage;