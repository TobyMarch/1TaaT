import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Style.css';

function New() {
  const [owner, setOwner] = useState('');
  const [task, setTask] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [sliderValue, setSliderValue] = useState(5);

  useEffect(() => {
    const currentDate = new Date().toISOString().split('T')[0];
    setDueDate(currentDate);
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const data = {
        owner,
        task,
        dueDate,
        rating: sliderValue
      };
      await axios.post('http://127.0.0.1:8080/api/tasks', [data]);

      setOwner('');
      setTask('');
      setDueDate('');
      setSliderValue(5);
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
            <label htmlFor="task">Task:</label>
            <input type="text" id="task" value={task} onChange={(e) => setTask(e.target.value)} />
          </div>
          <div>
            <label htmlFor="dueDate">Due Date:</label>
            <input type="date" id="dueDate" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
          </div>
          <div>
            <label htmlFor="rating">Rating:</label>
            <input type="range" id="rating" value={sliderValue} min="1" max="9" onChange={(e) => setSliderValue(parseInt(e.target.value, 10))} />
            <span>{sliderValue}</span>
          </div>
          <button type="submit">Submit</button>
        </form>
      </div>
    </div>
  );
}

export default New;
