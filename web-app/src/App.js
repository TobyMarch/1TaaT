/* 
App.js handles the input of the data 
owner, task, dueDate, addedDate, rating: sliderValue
then sends it to the Java backend. 

App.js also handles the Web-app layout for now. 
*/

import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Style.css';

function App() {
  const [owner, Owner] = useState('');
  const [task, setTask] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [addedDate, setAddedDate] = useState('');
  const [sliderValue, setSliderValue] = useState(5);

  useEffect(() => {
    const currentDate = new Date().toISOString().split('T')[0];
    setAddedDate(currentDate);
  }, []); 

 // send data too Java backend, change URL? 
  const handleSubmit = async () => {
    try {
      await axios.post('http://54.209.180.136:8080/api/data', { 
        owner, 
        task, 
        dueDate, 
        addedDate, 
        rating: sliderValue 
      });

      Owner('');
      setTask('');
      setDueDate('');
      setSliderValue(5);
      const currentDate = new Date().toISOString().split('T')[0];
      setAddedDate(currentDate);
    } catch (error) {
      console.error('Error submitting data:', error);
    }
  };

  return (
    <div classowner="App">
      <form onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
        <div>
          <label htmlFor="owner">owner:</label>
          <input type="text" id="owner" value={owner} onChange={(e) => Owner(e.target.value)} />
        </div>
        <div>
          <label htmlFor="task">Task:</label>
          <input type="text" id="text" value={task} onChange={(e) => setTask(e.target.value)} />
        </div>
        <div>
          <label htmlFor="dueDate">Due Date:</label>
          <input type="date" id="dueDate" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
        </div>
        <div>
          <label htmlFor="rating">Rating:</label>
          <input
            type="range"
            id="rating"
            value={sliderValue}
            min="1"
            max="9"
            onChange={(e) => setSliderValue(parseInt(e.target.value))}
          />
          <span>{sliderValue}</span> {}
        </div>
        <button type="submit">Submit</button>
      </form>
    </div>
  );
}

export default App;
