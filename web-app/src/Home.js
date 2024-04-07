import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Style.css';
import logo from './img/logo.svg';
import { ReactComponent as SVGSingle } from './img/single.svg';
import { ReactComponent as SVGMulti } from './img/multi.svg';
import { TASK_API_URL, ALL_TASKS_API_URL } from './URLConstants';
import { ReactComponent as SVGAdd } from './img/add.svg';

function Home() {
    const [owner, setOwner] = useState('');
 const [createdDate, setCreatedDate] = useState('');

  const [isThreeColumns, setIsThreeColumns] = useState(false);
  const [items, setItems] = useState([]);
  const [menuVisible, setMenuVisible] = useState(false);
  const [title, setTitle] = useState('');
  const [startDate, setStartDate] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [priority, setPriority] = useState(5);
  const [selectedOption, setSelectedOption] = useState('option');
  const handleOptionChange = (event) => {
    setSelectedOption(event.target.value);
  };

  useEffect(() => {
    try {
      axios.get(ALL_TASKS_API_URL, {withCredentials: true}).then((res) => {
        setItems(res.data);
      });
    } catch (e) {
      console.error('Error loading task data: ', e);
      alert('Failed to load tasks');
    }

    const currentDate = new Date().toISOString().split('T')[0]; 
    setStartDate(currentDate + "T12:00");
    setDueDate(currentDate + "T12:00");
  }, []);
const toggleMenu = () => {
  setMenuVisible(!menuVisible);
};
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
      await axios.post(TASK_API_URL, [data], {withCredentials: true});

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

  const toggleColumns = () => {
    setIsThreeColumns(!isThreeColumns);
  };

  return (
    <div className="App">
      <div className="topBar">
        <div className="leftItems">
          <button onClick={toggleColumns}>
            {isThreeColumns ? <SVGSingle /> : <SVGMulti />}
          </button>
        </div>
   
        <div className="filterDropdown">
          <select onChange={handleOptionChange} value={selectedOption}>
            <option value="option">Priority</option>
            <option value="option1">Recently Added</option>
            <option value="option2">Archived</option>
	  <option value="option3">Reverse Priority</option>
          <option value="option4">Other</option>
	  <option value="option5">Other</option>
	  </select>
        </div>
        <img src={logo} alt="Logo" className="logo" />
      </div>

 {/* Settings button to toggle new task form */}
      <button className="bottomRightButton" onClick={toggleMenu}>
        <SVGAdd />
      </button>



      {/* Task List */}
      <div className={`List ${isThreeColumns ? 'threeColumns' : ''}`}>
        {isThreeColumns ? (
          <div className="item">
            {items[0] && (
              <>
                <p>{items[0].title}</p>
                <p className="dueDate">Due: {items[0].dueDate.split('T')[0]}</p>
                <button className="doneButton">Done</button>
                <button className="archiveButton">Archive</button>
              </>
            )}
          </div>
        ) : (
          items.map((item, index) => (
            <div className="item" key={index}>
              <p>{item.title}</p>
              <p className="dueDate">Due: {item.dueDate.split('T')[0]}</p>
              <button className="archiveButton">Archive</button>
              <button className="doneButton">Done</button>
            </div>
          ))
        )}
      </div>
	   {/* Conditional rendering of the new task form */}
      {menuVisible && (
        <div className="add-task-form">
          <h2>Add New Task</h2>
          <form onSubmit={handleSubmit}>
            {/* Form fields */}
            <div>
              <label htmlFor="title">Task:</label>
              <input type="text" id="title" value={title} onChange={(e) => setTitle(e.target.value)} required />
            </div>
            <div>
              <label htmlFor="startDate">Start Date:</label>
              <input type="datetime-local" id="startDate" value={startDate} onChange={(e) => setStartDate(e.target.value)} required />
            </div>
            <div>
              <label htmlFor="dueDate">Due Date:</label>
              <input type="datetime-local" id="dueDate" value={dueDate} onChange={(e) => setDueDate(e.target.value)} required />
            </div>
            <div>
              <label htmlFor="priority">Priority:</label>
              <input type="range" id="priority" value={priority} min="1" max="9" onChange={(e) => setPriority(parseInt(e.target.value, 10))} />
              <span>{priority}</span>
            </div>
            <button type="submit">Submit</button>
          </form>
        </div>
      )}

    </div>
  );
}

export default Home;
