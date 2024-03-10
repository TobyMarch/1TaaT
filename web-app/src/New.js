import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Style.css';
import { TASK_API_URL } from './URLConstants';

function New() {
  const [owner, setOwner] = useState('');
  const [title, setTitle] = useState('');
  const [createdDate, setCreatedDate] = useState('');
  const [startDate, setStartDate] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [priority, setPriority] = useState(5);

  useEffect(() => {
    const currentDate = new Date().toISOString().split('.')[0];
    setCreatedDate(currentDate);
    setDueDate(currentDate);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        owner,
        title,
        createdDate,
        startDate,
        dueDate,
        priority
      };
      await axios.post(TASK_API_URL, [data]);

      setOwner('');
      setTitle('');
      setStartDate('');
      setDueDate('');
      setPriority(5);
      alert('Task added successfully');
    } catch (error) {
      console.error('Error submitting data:', error);
      alert('Failed to add task');
    }
  };

  return (
    <div className="login-container">
      <div className="login-form">
        <h2>Add New Task</h2>
        <form onSubmit={handleSubmit}>

          <div>
            <label htmlFor="title">Task:</label>
            <input type="text" id="title" value={title} onChange={(e) => setTitle(e.target.value)} />
          </div>
          <div>
            <label htmlFor="dueDate">Start Date:</label>
            <input type="datetime-local" id="startDate" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
          </div>
          <div>
            <label htmlFor="dueDate">Due Date:</label>
            <input type="datetime-local" id="dueDate" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
          </div>
          <div>
            <label htmlFor="rating">Priority:</label>
            <input type="range" id="priority" value={priority} min="1" max="9" onChange={(e) => setPriority(parseInt(e.target.value, 10))} />
            <span>{priority}</span>
          </div>
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  );
}

export default New;
