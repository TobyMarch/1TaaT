import React from 'react';
import './Style.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './Login.js';
import Home from './Home.js'
import { ProtectedRoute } from './ProtectedRoute.js';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<ProtectedRoute/>}>
          <Route path="/" element={<Home />} />
        </Route>
        <Route element={<ProtectedRoute loginPage={true} redirectPath="/"/>}>
          <Route path="/login" element={<Login />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App;
