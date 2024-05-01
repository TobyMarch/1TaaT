import React, { useState, useEffect } from "react";
import axios from "axios";
import "./Style.css";
import logo from "./img/logo.svg";
import { ReactComponent as SVGarchive } from "./img/history.svg";
import { ReactComponent as SVGSingle } from "./img/single.svg";
import { ReactComponent as SVGMulti } from "./img/multi.svg";
import { ReactComponent as SVGAdd } from "./img/add.svg";
import { ReactComponent as SVGshare } from "./img/share.svg";
import { ReactComponent as SVGflag } from "./img/flag.svg";
import { ReactComponent as SVGimport } from "./img/Google_Calendar_icon_(2020).svg";
import { useCookies } from "react-cookie";
import { useNavigate } from 'react-router-dom';

import {
  TASK_API_URL,
  TOP_TASK_API_URL,
  PAGINATED_TASKS_API_URL,
  ARCHIVED_API_URL,
  LOGOUT_ROUTE,
} from "./URLConstants";

function Home() {
  const [owner, setOwner] = useState("");
  const [createdDate, setCreatedDate] = useState("");
  const [isListView, setIsListView] = useState(false);
  const [items, setItems] = useState([]);
  const [editItemId, setEditItemId] = useState(null);
  const [editableTitle, setEditableTitle] = useState("");
  const [editableDescription, setEditableDescription] = useState("");
  const [menuVisible, setMenuVisible] = useState(false);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [priority, setPriority] = useState(1);
  const [duration, setDuration] = useState("S");
  const [selectedOption, setSelectedOption] = useState("");
  const [cookies] = useCookies(["XSRF-TOKEN"]);
  const [charTitleCount, setTitleCharCount] = useState(0);
  const [charDescriptionCount, setDescriptionCharCount] = useState(0);
  const [archivedItems, setArchivedItems] = useState([]);
  const [showArchived, setShowArchived] = useState(false);
  const [isdelayable, setIsdelayable] = useState(false);
   const [formErrors, setFormErrors] = useState({});
    const [showSubtasks, setShowSubtasks] = useState(false);
  const navigate = useNavigate();
  const toggleSubtasksVisibility = () => {
  setShowSubtasks(!showSubtasks);
};

    const [subTasks, setSubtasks] = useState([{
  title: "",
  description: "",
  startDate: "",
  dueDate: "",
  priority: 1,
  duration: "S",
  completed: false
}]);

  const archivedStyle = {
  background: 'linear-gradient(11deg, #c0c0c0 0%, #f0f0f0 100%)',
  };

    const priorityGradientStyles = [
    {
      background: "linear-gradient(11deg, #7673AC 0%, #b3d4ff 100%)", // Lowest
    },
    {
      background: "linear-gradient(11deg, #7bd5b7 0%, #7ccf9f 100%)", // lower
    },
    {
      background: "linear-gradient(11deg, #e1c97a 0%, #ffea9e 100%)", // Medium
    },
    {
      background: "linear-gradient(11deg, #dfa661 0%, #ffc18b 100%)", // Higher
    },
    {
      background: "linear-gradient(11deg, #d46666 0%, #f37d8f 100%)", // Highest
    },
  ];

  const redirectToCalendar = () => {
    navigate('/calendar');
  };

  const toggleMenu = () => {
    setMenuVisible(!menuVisible);
  };


  const isOverdue = (dueDateString) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const dueDate = new Date(dueDateString);
    return dueDate < today;
  };


  const validateForm = () => {
    const newTaskFail = {};
    if (new Date(startDate) >= new Date(dueDate)) {
      newTaskFail.date = "Due date must be after the start date.";
    }
    return newTaskFail;
  };

  const handleSubtaskTitleChange = (index, value) => {
  const updatedSubtasks = subTasks.map((subTask, i) => {
    if (i === index) {
      return { ...subTask, title: value };
    }
    return subTask;
  });
  setSubtasks(updatedSubtasks);
};



  const addSubtask = () => {
    setSubtasks([
      ...subTasks,
      { title: "",
        description: "",
        startDate: new Date().toISOString().slice(0, 10),
        dueDate: new Date().toISOString().slice(0, 10),
        priority: 1,
        duration: "S",
       }
    ]);
  };

// const addSubtask = () => {
//   const newSubtask = {
//     title: "",
//     description: "",
//     startDate: new Date().toISOString().slice(0, 10),
//     dueDate: new Date().toISOString().slice(0, 10),
//     priority: 1,
//     duration: "S",
//     completed: false
//   };
//   setSubtasks([...subTasks, newSubtask]);
// };

  const removeSubtask = (index) => {
    const filteredSubtasks = subTasks.filter((_, i) => i !== index);
    setSubtasks(filteredSubtasks);
  };


const handleSubtaskChange = (index, field, value) => {
  const updatedSubtasks = subTasks.map((subTask, i) => {
    if (i === index) {
      return { ...subTask, [field]: value };
    }
    return subTask;
  });
  setSubtasks(updatedSubtasks);
};

// const handleSubtaskChange = (index, field, value) => {
//     const updatedSubtasks = subTasks.map((subTask, i) => (
//       i === index ? { ...subTask, [field]: value } : subTask
//     ));
//     setSubtasks(updatedSubtasks);
//   };

  const handleEdit = (item) => {
    setEditItemId(item.id);
    setEditableTitle(item.title);
    setEditableDescription(item.description);
  };

  const handleTitleEdit = (e) => {
    setEditableTitle(e.target.value);
  };

  const handleDescriptionEdit = (e) => {
    setEditableDescription(e.target.value);
  };

const saveChanges = (item) => {
  axios.put(`${TASK_API_URL}/${item.id}`, {
    title: editableTitle,
    description: editableDescription,
  }, {
    headers: {
      "Content-Type": "application/json",
      "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
    },
    withCredentials: true
  }).then(response => {
    console.log("Update successful:", response.data);
    fetchTasks();
  }).catch(error => {
    console.error("Failed to save changes:", error);
  });
};


  const handleArchiveClick = async () => {
    setShowArchived((prevShowArchived) => {
      if (!prevShowArchived) {
        fetchArchivedTasks()
          .then((archivedTasks) => {
            setArchivedItems(archivedTasks);
          })
          .catch((error) => {
            console.error("Failed to fetch archived tasks:", error);
            setArchivedItems([]);
          });
      }
      return !prevShowArchived;
    });
  };

  const handleLogout = () => {
    fetch(LOGOUT_ROUTE, {
      method: "post",
      credentials: "include",
      headers: {
        "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
      },
    }).then((res) => {
      if (res.status === 200) {
        window.location.href = window.location.origin;
      }
    });
  };

  const handleTitleChange = (event) => {
    const newTitle = event.target.value.slice(0, 50);
    setTitle(newTitle);
    setTitleCharCount(newTitle.length);
  };

  const handleDescriptionChange = (event) => {
    const newDescription = event.target.value.slice(0, 350);
    setDescription(newDescription);
    setDescriptionCharCount(newDescription.length);
  };

  const handleOptionChange = (event) => {
    const newSortOrder = event.target.value;
    setSelectedOption(newSortOrder);
    sortItems(newSortOrder);
    fetchTasks(newSortOrder);
  };

  const sortItems = (filter) => {
    let sortedItems = [...items];
    switch (filter) {
      case "Highest":
        sortedItems.sort((a, b) => b.priority - a.priority);
        break;
      case "Lowest":
        sortedItems.sort((a, b) => a.priority - b.priority);
        break;
      case "Newest":
        sortedItems.sort(
          (a, b) => new Date(b.createdDate) - new Date(a.createdDate)
        );
        break;
      case "Oldest":
        sortedItems.sort(
          (a, b) => new Date(a.createdDate) - new Date(b.createdDate)
        );
        break;
      default:
        break;
    }
    setItems(sortedItems);
  };


   const handledelayableChange = (event) => {
  setIsdelayable(event.target.checked);
};

  const skipTask = async (taskId) => {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    try {
      const response = await axios.put(
        `${TASK_API_URL}/${taskId}/skip`,
        { dueDate: tomorrow.toISOString().split("T")[0] },
        {
          withCredentials: true,
          headers: {
            "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
          },
        }
      );
      if (response.status === 200) {
        console.log("Task skipped to next day:", response.data);
        fetchTasks();
      }
    } catch (error) {
      console.error("Error skipping the task:", error);
    }
  };


 const doneTask = async (taskId) => {
  try {
    const response = await axios.put(
      `${TASK_API_URL}/${taskId}/archive`,
      {},
      {
        withCredentials: true,
        headers: {
          "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
        },
      }
    );
    if (response.status === 200) {
      console.log("Task marked as done:", response.data);
      fetchTasks();
    }
    setItems((items) => items.filter((item) => item.id !== taskId));
  } catch (error) {
    console.error("Error finishing the task:", error);
  }
};

 const removeTask = async (taskId) => {
  try {
    const response = await axios.delete(`${TASK_API_URL}/${taskId}`, {
      withCredentials: true,
      headers: {
        "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
      },
    });
    if (response.status === 200) {
      console.log("Task removed successfully:", response.data);
      fetchTasks();
    }
    setItems((items) => items.filter((item) => item.id !== taskId));
  } catch (error) {
    console.error("Error removing the task:", error);
  }
};


  useEffect(() => {
    fetchTasks();
    const currentDate = new Date().toISOString().split("T")[0];
    setStartDate(`${currentDate}T12:00`);
    setDueDate(`${currentDate}T12:00`);
  }, [isListView]);

  const toggleColumns = () => {
    setIsListView(!isListView);
  };


// Task retrieval and submission

const fetchTasks = async () => {
  try {
    const response = await axios.get(isListView ? PAGINATED_TASKS_API_URL : TOP_TASK_API_URL, { withCredentials: true });
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

  const fetchArchivedTasks = async () => {
    try {
      const response = await axios.get(ARCHIVED_API_URL, {
        withCredentials: true,
        headers: {
          "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
        },
      });
      if (response.data && Array.isArray(response.data.content)) {
        console.log(
          "Archived tasks fetched successfully:",
          response.data.content
        );
        return response.data.content;
      } else {
        console.warn("Received non-array:", response.data);
        return [];
      }
    } catch (error) {
      console.error("Failed to fetch archived tasks:", error);
    }
  };

const handleSubmit = async (e) => {
  e.preventDefault();
  const errors = validateForm();
  try {
    const data = {
      owner,
      title,
      description,
      // createdDate: new Date(createdDate).toISOString(),
      startDate: startDate ? new Date(startDate).toISOString() : null,
      dueDate: dueDate ? new Date(dueDate).toISOString() : null,
      priority,
       duration,
       isdelayable,
       subTasks: subTasks.map(subTask => ({
    title: subTask.title,
    // completed: subTask.completed
  })),
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
    setIsdelayable(false);
    setSubtasks([{ title: '' }]);
    toggleMenu();
    alert("Task added successfully");
    fetchTasks();
  } catch (error) {
    console.error("Error submitting data:", error);
    alert("Failed to add task" + error.message);
  }
};

  return (
    <div className="App">
      {/* Top Bar Nav */}
      <div className="topBar">
        <div className="leftItems">
          <img src={logo} alt="Logo" className="logo" />
          <button onClick={handleLogout}>
       <div className="logout">
<p>
          Logout
              </p>
            </div>
          </button>
        </div>

        <div className="filterDropdown">
          <select onChange={handleOptionChange} value={selectedOption}>
            <option value="Highest">Highest Priority</option>
            <option value="Lowest">Lowest Priority</option>
            <option value="Newest">Newest</option>
            <option value="Oldest">Oldest</option>
            <option value="createdDate">Created Date</option>
            <option value="startDate">Start Date</option>
            <option value="dueDate">Due Date</option>
          </select>
        </div>

        <button className="toggle" onClick={toggleColumns}>
          {isListView ? (
            <>
              <p>
                Task <br />
                List
                <br />
                View
              </p>
              <SVGMulti />
            </>
          ) : (
            <>
              <p>
                Top <br />
                Task
                <br />
                View
              </p>
              <SVGSingle />
            </>
          )}
        </button>
      </div>

      {/* Task List */}
      {!menuVisible && (
        <div className={`List ${isListView ? "listView" : ""}`}>
          {showArchived
            ? archivedItems.map((item, index) => (
                <div
                  className="item"
                  key={index}
                  style={showArchived ? archivedStyle : priorityGradientStyles[item.priority - 1]}
                >
                  {editItemId === item.id ? (
                    <div>
                      <input
                        value={editableTitle}
                        onChange={handleTitleEdit}
                        onBlur={() => saveChanges(item)}
                        onKeyPress={(e) =>
                          e.key === "Enter" && saveChanges(item)
                        }
                        autoFocus
                      />
                      <textarea
                        value={editableDescription}
                        onChange={handleDescriptionEdit}
                        onBlur={() => saveChanges(item)}
                      />
                    </div>
                  ) : (
                    <div onDoubleClick={() => handleEdit(item)}>
                      <h2 className="title">{item.title}</h2>
                      <p className="description">{item.description || 'Not set'}</p>
                      <ul className="subTasks-list">
                        {item.subTasks &&
                          item.subTasks.map((subTask, subindex) => (
                            <li key={subindex}>
                              {subTask.title} -{" "}
                              {subTask.completed ? "Done" : "Pending"}
                            </li>
                          ))}
                      </ul>
                      <p className="duration">Duration: {item.duration || 'Not set'}</p>
                      <p className="dueDate">
                        Start: {item.startDate ? new Date(Date.parse(item.startDate)).toLocaleString([], {year: 'numeric', month: '2-digit', day:'2-digit', hour: '2-digit', minute:'2-digit'}) : 'Not set'}
                      </p>
                      <p className="dueDate">
                        Due: {item.dueDate ? new Date(Date.parse(item.dueDate)).toLocaleString([], {year: 'numeric', month: '2-digit', day:'2-digit', hour: '2-digit', minute:'2-digit'}) : 'Not set'}
                      </p>
                      <div className="buttonGroup">
                        <button
                          className="shareTaskButton"
                          onClick={() => removeTask(item.id)}
                        >
                          Share
                        </button>

                        <button
                          className="archiveButton"
                          onClick={() => removeTask(item.id)}
                        >
                          Remove
                        </button>
                        <button
                          className="doneButton"
                          onClick={() => doneTask(item.id)}
                        >
                          Done
                        </button>
                      </div>

                    </div>
                  )}
                </div>
              ))
            : items.map((item, index) => (
                <div
                  className="item"
                  key={index}
                  style={priorityGradientStyles[item.priority - 1]}
                >
                  {editItemId === item.id ? (
                    <div>
                      <input
                        value={editableTitle}
                        onChange={handleTitleEdit}
                        onBlur={() => saveChanges(item)}
                        onKeyPress={(e) =>
                          e.key === "Enter" && saveChanges(item)
                        }
                        autoFocus
                      />
                      <textarea
                        value={editableDescription}
                        onChange={handleDescriptionEdit}
                        onBlur={() => saveChanges(item)}
                      />

                    </div>
                  ) : (
                    <>
                      <div onDoubleClick={() => handleEdit(item)}>
             <div className="title-container">
    <h2 className="title">{item.title}</h2>
     <p className="duration" >
            Duration: {item.duration || 'Not set'}
        </p>
    <div className="info-container">

        <p className="dueDate">
            {/* Include "Overdue" text with the flag */}
            Start: {item.startDate ? new Date(Date.parse(item.startDate)).toLocaleString([], {year: 'numeric', month: '2-digit', day:'2-digit', hour: '2-digit', minute:'2-digit'}) : 'Not set'}
            <br />
            {isOverdue(item.dueDate) && <><SVGflag /> Over</>}Due: {item.dueDate ? new Date(Date.parse(item.dueDate)).toLocaleString([], {year: 'numeric', month: '2-digit', day:'2-digit', hour: '2-digit', minute:'2-digit'}) : 'Not set'}
        </p>
        <div className="buttonGroup">  {/* Existing class for button styling */}
            <button className="skipButton" onClick={() => skipTask(item.id)}>
                Do Tomorrow
            </button>

            <button className="doneButton" onClick={() => doneTask(item.id)}>
                Done
            </button>
        </div>
    </div>
</div>
                        <label>Description:</label>
                        <p className="description">{item.description || 'Not set'}</p>


                      </div>
                    <div className="subTasks-section">
  {subTasks.map((subTask, index) => (
    <div key={index} className="subTask-input">
      <input
        type="text"
        value={subTask.title}
        onChange={(e) => handleSubtaskChange(index, 'title', e.target.value)}
        placeholder="Subtask title"

      />
      <textarea className="subTasks-description"
        value={subTask.description}
        onChange={(e) => handleSubtaskChange(index, 'description', e.target.value)}
        placeholder="Description"
      />
      <input
        type="datetime-local"
        value={subTask.startDate}
        onChange={(e) => handleSubtaskChange(index, 'startDate', e.target.value)}
      />
      <input
        type="datetime-local"
        value={subTask.dueDate}
        onChange={(e) => handleSubtaskChange(index, 'dueDate', e.target.value)}
      />
      <select
        onChange={(e) => handleSubtaskChange(index, 'priority', parseInt(e.target.value, 10))}
        value={subTask.priority}
      >
        <option value={1}>Lowest</option>
        <option value={2}>Lower</option>
        <option value={3}>Medium</option>
        <option value={4}>Higher</option>
        <option value={5}>Highest</option>
      </select>
      <select
        onChange={(e) => handleSubtaskChange(index, 'duration', e.target.value)}
        value={subTask.duration}
      >
        <option value="S">Small</option>
        <option value="M">Medium</option>
        <option value="L">Large</option>
        <option value="XL">XLarge</option>
      </select>
      <button type="button" onClick={() => removeSubtask(index)}>Remove</button>
    </div>
  ))}
  <button type="button" onClick={() => addSubtask()}>
    Add Subtask
  </button>
</div>
                    </>
                  )}

                  <div className="buttonGroup">
                    <button
                      className="shareTaskButton"
                      onClick={() => removeTask(item.id)}
                    >
                      Share
                    </button>
                     <button className="archiveButton" onClick={() => removeTask(item.id)}>
                Remove
            </button>


                  </div>

                </div>
              ))}
        </div>
      )}

      {/* SideBar */}
      <div className="sideBar">
        <p htmlFor="historyButton">History</p>
        <button className="historyButton" onClick={handleArchiveClick}>
          <SVGarchive />
        </button>
     <p htmlFor="ImportButton">Google <br />Import</p>
        <button className="importButton" onClick={redirectToCalendar}>
          <SVGimport />
        </button>
        <p htmlFor="shareButton">Share</p>
        <button className="shareButton" onClick={toggleMenu}>
          <SVGshare />
        </button>
        <p htmlFor="addButton">New</p>
        <button className="addButton" onClick={toggleMenu}>
          <SVGAdd />
        </button>
      </div>
     {/* Conditional rendering of the new task form */}
{menuVisible && (
  <div className="add-task-form">
    <div className="add-task">
      <h2>New Task</h2>
      <form onSubmit={handleSubmit}>
        <div className="task-input">
          <label htmlFor="title">Task Title:</label><br/>
          <p>Task Title: {charTitleCount}/50 </p>
          <input
            type="text"
            id="title"
            value={title}
            onChange={handleTitleChange}
            maxLength={50}
            required
          />
        </div>
        <div className="task-input">
          <label htmlFor="task">Task:</label>
          <p>Characters: {charDescriptionCount}/250</p>
          <textarea
            type="text"
            id="task"
            value={description}
            onChange={handleDescriptionChange}
            maxLength={200}
          />
        </div>

        <button type="button" onClick={toggleSubtasksVisibility}>
          {showSubtasks ? "Hide Subtasks" : "Add Subtasks"}
        </button>

        {/* Subtasks Input Section */}
        {showSubtasks && <div className="subTasks-section">
          <label>Subtasks:</label>
          {subTasks.map((subTask, index) => (
            <div key={index} className="subTask-input">
              <input
                type="text"
                value={subTask.title}
                onChange={(e) => handleSubtaskChange(index, 'title', e.target.value)}
                placeholder="Subtask title"
              />
              <textarea
                value={subTask.description}
                onChange={(e) => handleSubtaskChange(index, 'description', e.target.value)}
                placeholder="Description"
              />
              <input
                type="datetime-local"
                value={subTask.startDate}
                onChange={(e) => handleSubtaskChange(index, 'startDate', e.target.value)}
                placeholder="Start Date"
              />
              <input
                type="datetime-local"
                value={subTask.dueDate}
                onChange={(e) => handleSubtaskChange(index, 'dueDate', e.target.value)}
                placeholder="Due Date"
              />
              <button type="button" onClick={() => removeSubtask(index)}>Remove</button>
            </div>
          ))}
          <button type="button" onClick={addSubtask}>
            Add Subtask
          </button>
        </div>}

        <div className="task-input">
          <label htmlFor="startDate">Start Date:</label>
          <input
            type="datetime-local"
            id="startDate"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
        </div>
        <div className="task-input">
          <label htmlFor="dueDate">Due Date:</label>
          <input
            type="datetime-local"
            id="dueDate"
            value={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
          />
        </div>
        <div className="task-input">
          <input
            type="checkbox"
            id="delayable"
            name="delayable"
            checked={isdelayable}
            onChange={handledelayableChange}
          />
          <label htmlFor="delayable">Delayable</label>
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
        <label htmlFor="duration">Duration:</label>
        <div className="durationDropdown">
          <select
            onChange={(e) => setDuration(e.target.value)}
            value={duration}
          >
            <option value="S">Small</option>
            <option value="M">Medium</option>
            <option value="L">Large</option>
            <option value="XL">XLarge</option>
          </select>
        </div>
        <div className="button-container">
          <button className="back" onClick={toggleMenu}>
            Back
          </button>
          <button className="submit" type="submit">
            Submit
          </button>
          {formErrors.date && <p className="error">{formErrors.date}</p>}
        </div>
      </form>
    </div>
  </div>
)}
    </div>

  );
}

export default Home;
