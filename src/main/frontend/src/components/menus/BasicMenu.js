import React from "react";
import {Link} from "react-router-dom";

const BasicMenu = () => {
    return(
        <nav>
            <div>
                <ul>
                    <li>
                        <Link to ={'/'}>Main</Link>
                    </li>
                    <li>
                        <Link to ={'/about'}>About</Link>
                    </li>
                    <li>
                        <Link to ={'/todo/'}>Todo</Link>
                    </li>
                </ul>
            </div>
            <div>
                <div>
                    Login
                </div>
            </div>
        </nav>
    )
}

export default BasicMenu;