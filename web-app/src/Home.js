import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Style.css";
import logo from "./img/logo.svg";
import { ReactComponent as SVGarchive } from "./img/history.svg";
import { ReactComponent as SVGSingle } from "./img/single.svg";
import { ReactComponent as SVGMulti } from "./img/multi.svg";
import { ReactComponent as SVGAdd } from "./img/add.svg";
import { ReactComponent as SVGshare } from "./img/share.svg";
import { ReactComponent as SVGremove} from "./img/remove.svg";
import { ReactComponent as SVGdone} from "./img/done.svg";
import { ReactComponent as SVGflag} from "./img/flag.svg";

import {
  TASK_API_URL,
  TOP_TASK_API_URL,
  PAGINATED_TASKS_API_URL,
  ARCHIVED_API_URL,
} from "./URLConstants";
import { useCookies } from "react-cookie";

function Home() {
  const [username, setUsername] = useState('');
  const [owner, setOwner] = useState("");
  const [createdDate, setCreatedDate] = useState("");
  const [isThreeColumns, setIsThreeColumns] = useState(false);
  const [items, setItems] = useState([]);
  const [menuVisible, setMenuVisible] = useState(false);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [priority, setPriority] = useState(1);
  const [duration, setDuration] = useState(1);
  const [archivedItems, setArchivedItems] = useState([]);
    const [showArchived, setShowArchived] = useState(false);
  const [selectedOption, setSelectedOption] = useState("option");
  const [cookies] = useCookies(["XSRF-TOKEN"]);

const handleOptionChange = (event) => {
  setSelectedOption(event.target.value);
  sortItems(event.target.value);
};

const sortItems = (filter) => {
  let sortedItems = [...items];
  switch (filter) {
    case 'Highest':
      sortedItems.sort((a, b) => b.priority - a.priority);
      break;
    case 'Lowest':
      sortedItems.sort((a, b) => a.priority - b.priority);
      break;
    case 'Newest':
      sortedItems.sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate));
      break;
    case 'Oldest':
      sortedItems.sort((a, b) => new Date(a.createdDate) - new Date(b.createdDate));
      break;
    default:
      break;
  }
  setItems(sortedItems);
};

const doneTask = async (taskId) => {
    try {
        await axios.put(`${TASK_API_URL}/${taskId}/archive`, {
            withCredentials: true,
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
            },
        });

        setItems((items) => items.filter((item) => item.id !== taskId));

        alert("Task done successfully");
    } catch (error) {
        console.error("Error finishng the task:", error);
        alert("Failed to finish task");
    }
};


const removeTask = async (taskId) => {
    try {
        await axios.delete(`${TASK_API_URL}/${taskId}`, {
            withCredentials: true,
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
            },
        });

        setItems((items) => items.filter((item) => item.id !== taskId));

        alert("Task removed successfully");
    } catch (error) {
        console.error("Error removing the task:", error);
        alert("Failed to remove task");
    }
};

  const toggleMenu = () => {
    setMenuVisible(!menuVisible);
  };

const isOverdue = (dueDateString) => {
  const today = new Date();
  today.setHours(0, 0, 0, 0); // Normalize today's date to midnight for accurate comparison

  const dueDate = new Date(dueDateString);
  return dueDate < today;
};

const priorityGradientStyles = [
  {
    background: 'linear-gradient(11deg, #b8b8b8 0%, #ffffff 100%)', // Low
  },
  {
    background: 'linear-gradient(11deg, #80ee8d 0%, #81fffb 100%)', // Slightly higher priority
  },
  {
    background: 'linear-gradient(11deg, #d1ee80 0%, #e0ff97 100%)', // Medium priority
  },
  {
    background: 'linear-gradient(11deg, #eeaf71 0%, #ffd979 100%)', // Higher priority
  },
  {
    background: 'linear-gradient(11deg, #ee5c5c 0%, #ff6996 100%)'  // High priority
  }
];

  const validateForm = () => {
    const newTaskFail = {};
    if (new Date(startDate) >= new Date(dueDate)) {
      newTaskFail.date = 'Due date must be after the start date.';
    }
    return newTaskFail;
  };

const fetchArchivedTasks = async () => {
    try {
        const response = await axios.get(ARCHIVED_API_URL, {
            withCredentials: true,
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
            },
        });
        if (response.data) {
            console.log('Archived tasks fetched successfully:', response.data);
            return response.data;
        }
    } catch (error) {
        console.error('Failed to fetch archived tasks:', error);
        alert('Failed to fetch archived tasks');
        return [];
    }
}

  useEffect(() => {
    const storedUsername = localStorage.getItem('username'); // Retrieve username from local storage
    if (storedUsername) {
      setUsername(storedUsername);
    }
  }, []);

useEffect(() => {


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
        description,
        createdDate,
        startDate,
        dueDate,
        priority,
        color: priorityGradientStyles[priority - 1],
      };

      await axios.post(TASK_API_URL, [data], {
        withCredentials: true,
        headers: {
          "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
        },
      });

      setOwner("");
      setTitle("");
      setDescription("");
      setStartDate("");
      setDueDate("");
      setPriority(1);
      toggleMenu();
      alert("Task added successfully");
      fetchTopTask();
        fetchTasks();
    } catch (error) {
      console.error("Error submitting data:", error);
      alert("Failed to add task" + error.message);
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

const fetchTasks = async () => {
  try {
    const response = await axios.get(isThreeColumns ? PAGINATED_TASKS_API_URL : TOP_TASK_API_URL, { withCredentials: true });
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

    const handleArchiveClick = async () => {
        const archivedTasks = await fetchArchivedTasks();
        setArchivedItems(archivedTasks);
        setShowArchived(true); // Show archived tasks
    };

  return (
 <div className="App">
  {/* Top Bar Nav */}
  <div className="topBar">
    <div className="leftItems">
     <img src={logo} alt="Logo" className="logo" />
 <p>{username}<br/>Logout</p>
    </div>

        <div className="filterDropdown">
          <select onChange={handleOptionChange} value={selectedOption}>
            <option value="Highest">Highest Priority</option>
            <option value="Lowest">Lowest Priority</option>
            <option value="Newest">Newest</option>
            <option value="Oldest">Oldest</option>
          </select>
        </div>

      <button className="toggle" onClick={toggleColumns}>
        {isThreeColumns ? (
          <>
          <p>Task <br/>List<br/>View</p>
            <SVGMulti />


          </>
        ) : (
          <>
           <p>Top <br/>Task<br/>View</p>
            <SVGSingle />

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
          style={priorityGradientStyles[item.priority - 1]} // Apply gradient based on priority
        >
                  <h2 className="title">{item.title}</h2>
                  <p className="duration"> duration</p>

                  <p className="dueDate">
          Start: {item.startDate.split("T")[0]}
        </p>

                     <p className="dueDate">
          Due: {item.dueDate.split("T")[0]}
          {isOverdue(item.dueDate) && (
            <span style={{ color: 'red', marginLeft: '10px' }}>
              Overdue <SVGflag />
            </span>
          )}
        </p>
                  <p className="description">{item.description}</p>

        <div className="buttonGroup">
        <button className="shareTaskButton" onClick={() => removeTask(item.id)}>Share<SVGdone/></button>

              <button
                className="archiveButton"
                onClick={() => removeTask(item.id)}
              >
                Remove<SVGremove/>
              </button>
              <button className="doneButton" onClick={() => doneTask(item.id)}>Done<SVGdone/></button>

            </div>
            <div className="taskInfo">{item.owner}{item.priority}created{item.createdDate}</div>
            </div>
          ))}
        </div>
      )}

        {/* Settings button to toggle new task form */}
        <div className="sideBar">
        <button className="trashButton" onClick={handleArchiveClick}>
                <SVGarchive />
            </button>
      <button className="shareButton" onClick={toggleMenu}>
        <SVGshare />
      </button>
      <button className="addButton" onClick={toggleMenu}>
        <SVGAdd />
      </button>
</div>
      {/* Conditional rendering of the new task form */}
      {menuVisible && (
        <div className="add-task-form">
          <h2>Add New Task</h2>
          <form onSubmit={handleSubmit}>

            {/* Add New Task Form */}
            <div>
              <label htmlFor="title">Task Title:</label>
              <input
                type="text"
                id="title"
                value={title}
                onChange={(e) => setTitle(e.target.value.slice(0, 50))}
                maxLength={50}
                required
              />
            </div>
             <div>
              <label htmlFor="">Task:</label>
              <input
  type="text"
  id="task"
  value={description}
  onChange={(e) => setDescription(e.target.value.slice(0, 350))}
  maxLength={350}
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

  <input type="checkbox" id="Reoccurring " name="Reoccurring " value="Car"></input>
  <label for="Reoccurring "> Reoccurring </label>
  <input type="checkbox" id="vehicle3" name="vehicle3" value="Boat"></input>
  <label for="vehicle3"> placeholder</label>
            </div>
            <div>
              <label htmlFor="priority">Priority:</label>
              <input
                type="range"
                id="priority"
                value={priority}
                min="1"
                max="5"
                onChange={(e) => setPriority(parseInt(e.target.value, 10))}
              />
              <span>{priority}</span>
            </div>
            <div>
              <label htmlFor="Duration">Duration:</label>
              <input
                type="range"
                id="duration"
                value={duration}
                min="1"
                max="3"
                onChange={(e) => setDuration(parseInt(e.target.value, 10))}
              />
              <span>{duration}</span>
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
