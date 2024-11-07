import React from "react";
import BasicMenu from '../components/menus/basic';

function BasicLayout({children}){
    return(
        <header>
          <BasicMenu/>
        <div>
            <main>
                {children}
            </main>
        </div>
        </header>
    )
}

export default BasicLayout;