import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './Style.css';
import logo from './img/logo.svg';
import { ReactComponent as SVGSingle } from './img/single.svg';
import { ReactComponent as SVGMulti } from './img/multi.svg';
import { ReactComponent as SVGSettings } from './img/settings.svg';
import axios from "axios";
import { TASK_API_URL } from './URLConstants';

function App() {
  const [selectedOption, setSelectedOption] = useState('');
  const [menuVisible, setMenuVisible] = useState(false);
  const [isThreeColumns, setIsThreeColumns] = useState(false);
  const [items, setItems] = useState([]);

  useEffect(() => {
    try {
      axios.get(TASK_API_URL)
        .then((res) => {
          setItems(res.data);
        });
    } catch (e) {
      console.error('Error loading task data: ', e);
      alert('Failed to load tasks');
    }
  }, []);

  const handleOptionChange = (e) => {
    setSelectedOption(e.target.value);
  };

  const toggleMenu = () => {
    setMenuVisible(!menuVisible);
  };

  const toggleColumns = () => {
    setIsThreeColumns(!isThreeColumns);
  };

  return (
    <div className="App">
      <div className="topBar">
        <div className="leftItems">
          <div className="buttons">
            <button onClick={toggleColumns}>
              {isThreeColumns ? <SVGSingle /> : <SVGMulti />}
            </button>
          </div>
        </div>
        <div className="filterDropdown">
          <select onChange={handleOptionChange} value={selectedOption}>
            <option value="">Priority</option>
            <option value="option1">Most Recent </option>
            <option value="option2">Other</option>
          </select>
        </div>
        <img src={logo} alt="Logo" className="logo" />
      </div>
      <button className="bottomRightButton" onClick={toggleMenu}>
        <SVGSettings />
      </button>
      {menuVisible && (
        <div className="menu">
          <ul>
            <li>
              <Link to="/new">Add New Task</Link>
            </li>
            <li>
              <Link to="/new">Share</Link>
            </li>
            <li>
              <Link to="/new">Import From Google</Link>
            </li>

          </ul>
        </div>

      )}
      <div className={`List ${isThreeColumns ? 'threeColumns' : ''}`}>
        {isThreeColumns ? (
          <>
            <div className="item">
              <p>{items[0].title}</p>
              <p className="dueDate">Due: {items[0].dueDate}</p>
            </div>
          </>
        ) : (
          <>
            {items.map((item, index) => (
              <div className="item" key={index}>
                <p>{item.title}</p>
                <p className="dueDate">Due: {item.dueDate.split('T')[0]}</p>
              </div>
            ))}
          </>
        )}
      </div>
    </div>
  );
}

export default App;
