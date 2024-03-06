import React, { useState } from 'react';
import './Style.css';
import App from './App';
import logo from './img/logo.svg';


function Login() {
  const [loggedIn, setLoggedIn] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = () => {

    if (username && password) {
      setLoggedIn(true);
    } else {
      alert('Invalid username or password');
    }
  };

  const handleLogout = () => {
    setLoggedIn(false);
    setUsername('');
    setPassword('');
  };

  return (
    <div className="login-container">
      {!loggedIn ? (
        <div className="login-form">
          <img src={logo} alt="Logo" className="logo" />
          <h2>1TaaT Login</h2>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button onClick={handleLogin}>Login</button>

        </div>
      ) : (
        <App onLogout={handleLogout} />
      )}
    </div>
  );
}

export default Login;
