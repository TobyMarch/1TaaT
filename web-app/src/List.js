/* 
List.js handles all data received from the Java backend. 
It will take the data and display it as a specially formatted list. 
*/

import './Style.css';

function List() {
  return (
    <div className="List">
        <ul>
            <li>task 1</li>
            <li>task 2</li>
            <li>task 3</li>
        </ul>
    </div>
  );
}

export default List;