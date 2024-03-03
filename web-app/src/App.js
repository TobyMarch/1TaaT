import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './Style.css';
import { ReactComponent as SVGSingle } from './img/single.svg';
import { ReactComponent as SVGMulti } from './img/multi.svg';
import { ReactComponent as SVGSettings } from './img/settings.svg'; // Import the settings SVG

function App() {
  const [selectedOption, setSelectedOption] = useState('');
  const [owner, setOwner] = useState('');
  const [task, setTask] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [addedDate, setAddedDate] = useState('');
  const [sliderValue, setSliderValue] = useState(5);
  const [menuVisible, setMenuVisible] = useState(false);
  const [isThreeColumns, setIsThreeColumns] = useState(false); // State for toggling columns

  useEffect(() => {
    const currentDate = new Date().toISOString().split('T')[0];
    setAddedDate(currentDate);
  }, []);

  const handleOptionChange = (e) => {
    setSelectedOption(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await axios.post('http://127.0.0.1:8080/api/data', {
        owner,
        task,
        dueDate,
        addedDate,
        rating: sliderValue
      });

      setOwner('');
      setTask('');
      setDueDate('');
      setSliderValue(5);
      const currentDate = new Date().toISOString().split('T')[0];
      setAddedDate(currentDate);
    } catch (error) {
      console.error('Error submitting data:', error);
    }
  };

  const toggleMenu = () => {
    setMenuVisible(!menuVisible);
  };

  const toggleColumns = () => {
    setIsThreeColumns(!isThreeColumns);
  };

  return (
    <div className="App">
      <div className="buttons">
        {isThreeColumns ? (
          <SVGSingle onClick={toggleColumns} />
        ) : (
          <SVGMulti onClick={toggleColumns} />
        )}
      </div>
      <div className="dropdown">
        <select value={selectedOption} onChange={handleOptionChange}>
          <option value="">Select an option</option>
          <option value="option1">Option 1</option>
          <option value="option2">Option 2</option>
          <option value="option3">Option 3</option>
        </select>
      </div>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="owner">Owner:</label>
          <input type="text" id="owner" value={owner} onChange={(e) => setOwner(e.target.value)} />
        </div>
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
          <input
            type="range"
            id="rating"
            value={sliderValue}
            min="1"
            max="9"
            onChange={(e) => setSliderValue(parseInt(e.target.value))}
          />
          <span>{sliderValue}</span>
        </div>
        <button type="submit">Submit</button>
      </form>
      {/* Place the settings SVG inside the bottom right button */}
      <button className="bottomRightButton" onClick={toggleMenu}>
        <SVGSettings />
      </button>
      <div className={`List ${isThreeColumns ? 'threeColumns' : ''}`}>
        {/* Sample divs */}
        <div>Item 1</div>
        <div>Item 2</div>
        <div>Item 3</div>
        <div>Item 4</div>
        <div>Item 5</div>

        {/* Menu */}
        <button className="bottomRightButton" onClick={toggleMenu}>
          Click me
        </button>
        {menuVisible && (
          <div className="menu">
            <ul>
              <li>Option 1</li>
              <li>Option 2</li>
              <li>Option 3</li>
              <li>Option 4</li>
              <li>Option 5</li>
            </ul>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
