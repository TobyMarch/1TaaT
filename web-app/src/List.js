import React from 'react';
import './Style.css';

function List({ isThreeColumns, toggleColumns, menuVisible, toggleMenu }) {
  return (
    <div className={`List ${isThreeColumns ? 'threeColumns' : ''}`}>
      <div>Item 1</div>
      <div>Item 2</div>
      <div>Item 3</div>
      <div>Item 4</div>
      <div>Item 5</div>


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
  );
}

export default List;
