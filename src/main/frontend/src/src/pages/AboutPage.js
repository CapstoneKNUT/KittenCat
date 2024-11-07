import React, {useContext} from "react";
import BasicLayout from '../layout/BasicLayout';
import {UserContext} from "../components/context/UserContext";

function AboutPage(props){
    const { username } = useContext(UserContext); // UserContext에서 username 가져오기

    return(
        <BasicLayout>
            <div>About Page</div>
        </BasicLayout>
    );
}
export default AboutPage;