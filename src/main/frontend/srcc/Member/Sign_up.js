import './Sign_up.css';
import React from 'react';

function Sign_up() {
  return (
    <div className="Sign_up">
      <div className='container'>
        <div className='sign-box'>
          <label htmlFor='email'>Email </label>
          <input type='email' id='email' placeholder='Value' /><br></br>
          <label htmlFor='password'>Password</label>
          <input type='password' id='password' placeholder='Value' /><br></br>
          <label htmlFor='recheck'>recheck</label>
          <input type='password' id='password' placeholder='Value' /><br></br>
        </div>
      </div>
    </div>
  );
}

export default Sign_up;
