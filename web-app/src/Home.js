import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Style.css";
import logo from "./img/logo.svg";
import { ReactComponent as SVGarchive } from "./img/history.svg";
import { ReactComponent as SVGSingle } from "./img/single.svg";
import { ReactComponent as SVGMulti } from "./img/multi.svg";
import { ReactComponent as SVGAdd } from "./img/add.svg";
import { ReactComponent as SVGshare } from "./img/share.svg";
import {
  TASK_API_URL,
  TOP_TASK_API_URL,
  PAGINATED_TASKS_API_URL,
} from "./URLConstants";
import { useCookies } from "react-cookie";

function Home() {
  const [owner, setOwner] = useState("");
  const [createdDate, setCreatedDate] = useState("");
  const [isThreeColumns, setIsThreeColumns] = useState(false);
  const [items, setItems] = useState([]);
  const [menuVisible, setMenuVisible] = useState(false);
  const [title, setTitle] = useState("");
  const [startDate, setStartDate] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [priority, setPriority] = useState(5);
  const [selectedOption, setSelectedOption] = useState("option");
  const [cookies] = useCookies(["XSRF-TOKEN"]);

  const handleOptionChange = (event) => {
    setSelectedOption(event.target.value);
  };
  const removeTask = (taskId) => {
    setItems((items) => items.filter((item) => item.id !== taskId));
  };
  const toggleMenu = () => {
    setMenuVisible(!menuVisible);
  };

const prioritySolidColorsHex = [
  "#F40752", // High priority, red
  "#eb235d", // OrangeRed
  "#e23f69", // DarkOrange
  "#da5b74", // Gold
  "#d17880", // Yellow
  "#c8948c", // GreenYellow
  "#c0b097", // Slightly lighter than LimeGreen, custom
  "#b7cca3", // MediumSeaGreen, custom approximation
  "#afe9af"  // MediumSpringGreen
];


useEffect(() => {
  const fetchTasks = async () => {
    try {
      const url = isThreeColumns ? PAGINATED_TASKS_API_URL : TOP_TASK_API_URL;
      const response = await axios.get(url, { withCredentials: true });
      if (Array.isArray(response.data.content)) {
        setItems(response.data.content);
      } else if (response.data && !Array.isArray(response.data.content)) {

        setItems([response.data]);
      } else {
        console.error('Expected an array or an object for content, received:', response.data);
        setItems([]);
      }
    } catch (error) {
      console.error("Error fetching tasks:", error);
      setItems([]);
    }
  };

  fetchTasks();
  const currentDate = new Date().toISOString().split("T")[0];
  setStartDate(`${currentDate}T12:00`);
  setDueDate(`${currentDate}T12:00`);
}, [isThreeColumns]);



  const toggleColumns = () => {
    setIsThreeColumns(!isThreeColumns);
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
        priority,
        color: prioritySolidColorsHex[priority - 1],
      };

      await axios.post(TASK_API_URL, [data], {
        withCredentials: true,
        headers: {
          "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
        },
      });

      setOwner("");
      setTitle("");
      setStartDate("");
      setDueDate("");
      setPriority(5);
      alert("Task added successfully");
    } catch (error) {
      console.error("Error submitting data:", error);
      alert("Failed to add task");
    }
  };

const fetchTopTask = async () => {
  try {
    const response = await axios.get(TOP_TASK_API_URL, { withCredentials: true });
    console.log(response.data);
    setItems(response.data.content);
  } catch (error) {
    console.error("Error fetching top task:", error);
    alert("Failed to fetch top task");
  }
};


  return (
 <div className="App">
  {/* Top Bar Nav */}
  <div className="topBar">
    <div className="leftItems">
     <img src={logo} alt="Logo" className="logo" />

    </div>

        <div className="filterDropdown">
          <select onChange={handleOptionChange} value={selectedOption}>
            <option value="Highest">Highest Priority</option>
            <option value="option2">Lowest Priority</option>
            <option value="Newest">Newest</option>
            <option value="Oldest">Oldest</option>
          </select>
        </div>

      <button onClick={toggleColumns}>
        {isThreeColumns ? (
          <>
            <SVGSingle />
            <p>Top Task</p>
          </>
        ) : (
          <>
            <SVGMulti />
            <p>Task List</p>
          </>
        )}
      </button>

      </div>


      {/* Task List */}
      {!menuVisible && (
  <div className={`List ${isThreeColumns ? "threeColumns" : ""}`}>
    {items && items.map((item, index) => (
            <div
              className="item"
              key={index}
              style={{
                background: prioritySolidColorsHex[item.priority - 1],
              }}
            >
              <h2>{item.title}</h2><p className="dueDate">Due: {item.dueDate.split("T")[0]}</p>
              <p>This paragraph serves as placeholder text, designed to fill the space within a document or a part of a website temporarily. Its purpose is to help visualize the overall layout and typography, ensuring that the design accommodates text effectively without the need for finalized content. Placeholder text allows designers and developers to maintain the momentum in the creative process, providing a glimpse of what the final product might look like once all elements are in place.</p>
              <button
                className="archiveButton"
                onClick={() => removeTask(item.id)}
              >
                Archive
              </button>
              <button className="doneButton">Done</button>
            </div>
          ))}
        </div>
      )}

        {/* Settings button to toggle new task form */}
      <button className="trashButton" onClick={toggleMenu}>
        <SVGarchive />
      </button>
      <button className="shareButton" onClick={toggleMenu}>
        <SVGshare />
      </button>
      <button className="addButton" onClick={toggleMenu}>
        <SVGAdd />
      </button>

      {/* Conditional rendering of the new task form */}
      {menuVisible && (
        <div className="add-task-form">
          <h2>Add New Task</h2>
          <form onSubmit={handleSubmit}>

            {/* Add New Task Form */}
            <div>
              <label htmlFor="title">Task:</label>
              <input
                type="text"
                id="title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor="startDate">Start Date:</label>
              <input
                type="datetime-local"
                id="startDate"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor="dueDate">Due Date:</label>
              <input
                type="datetime-local"
                id="dueDate"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
                required
              />
            </div>
            <div>
              <label htmlFor="priority">Priority:</label>
              <input
                type="range"
                id="priority"
                value={priority}
                min="1"
                max="9"
                onChange={(e) => setPriority(parseInt(e.target.value, 10))}
              />
              <span>{priority}</span>
            </div>
           <div class="button-container">
  <button class="back" onClick={toggleMenu}>Back</button>
  <button class="submit" type="submit">Submit</button>
</div>

          </form>
        </div>
      )}
    </div>
  );
}

export default Home;
