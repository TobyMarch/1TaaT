import React from "react";
import "./Style.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ProtectedRoute } from "./ProtectedRoute.js";
import Login from "./Login.js";
import Home from "./Home.js";
import Calendar from "./components/calendar";
import NewTask from "./components/NewTask";
import EditTask from "./components/EditTasks";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<ProtectedRoute />}>
          <Route path="/" element={<Home />} />
          <Route path="/calendar" element={<Calendar />} />
          <Route path="/NewTask" element={<NewTask />} />
          <Route path="/EditTask/:taskId" element={<EditTask />} />
        </Route>
        <Route element={<ProtectedRoute loginPage={true} redirectPath="/" />}>
          <Route path="/login" element={<Login />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
