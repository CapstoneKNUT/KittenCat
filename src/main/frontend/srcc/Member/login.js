import React from 'react';
import './login.css';
import { Link } from 'react-router-dom';


function login() {
  return (
    <div className="login">
      <div className='container'>
        <div className='login-form'>
          <label htmlFor="email">Email</label>
          <input type='email' id='email' placeholder='Value' />
          <label htmlFor='password'>Password</label>
          <input type='password' id='password' placeholder='Value' />
          <button type='button'>Log in</button>
          <div className='login-links'>
            <a href='#'>Forgot password?</a>
            <Link to="/member/join">
            <a href='#'>Sign up</a>
            </Link>
          </div>
        </div>
        <div className='login-image'></div>
      </div>
    </div>
  );
}

export default login;