import React, { useState, useEffect } from "react";
import axios from "axios";
import { useCookies } from "react-cookie";
import { useParams, useNavigate } from "react-router-dom";
import { useLocation } from 'react-router-dom';

import {
  TASK_API_URL,
  LOGOUT_ROUTE,
} from "../URLConstants";

function EditTask() {
  const handleISODate = (isoDateString) => {
    let offset = new Date().getTimezoneOffset()
    let offsetInMillis = offset * 60000;
    let timestamp = Date.parse(isoDateString) - offsetInMillis;
    let localDateTime = new Date(timestamp).toISOString().slice(0, -1);
    return localDateTime;
  }
  const location = useLocation();
  const [item, setItem] = useState(location.state.task);
  const [owner, setOwner] = useState(item.owner);
  const [createdDate, setCreatedDate] = useState(item.createdDate);
  const [isListView, setIsListView] = useState(false);
  const [items, setItems] = useState([]);
  const [menuVisible, setMenuVisible] = useState(false);
  const [title, setTitle] = useState(item.title);
  const [description, setDescription] = useState(item.description);
  const [startDate, setStartDate] = useState(handleISODate(item.startDate));
  const [dueDate, setDueDate] = useState(handleISODate(item.dueDate));
  const [priority, setPriority] = useState(item.priority);
  const [duration, setDuration] = useState(item.duration);
  const [cookies] = useCookies(["XSRF-TOKEN"]);
  const [charTitleCount, setTitleCharCount] = useState(0);
  const [charDescriptionCount, setDescriptionCharCount] = useState(0);
  const [delayable, setDelayable] = useState(item.delayable);
  const [formErrors, setFormErrors] = useState({});
  const [subTasks, setSubtasks] = useState(item.subTasks);
  const [showSubtasks, setShowSubtasks] = useState(false);
  const navigate = useNavigate();
  const toggleSubtasksVisibility = () => {
    setShowSubtasks(!showSubtasks);
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


    const addSubtask = (item) => {
        if (item.subTasks) {
            console.log(item.subTasks.length);
            item.subTasks.push({
                title: "",
                description: "",
                startDate: new Date().toISOString().slice(0, 10),
                dueDate: new Date().toISOString().slice(0, 10),
                priority: 1,
                duration: "S",
              });
        }
  };

  const removeSubtask = (item, index) => {
    item.subTasks.filter((_, i) => {
       return i !== index;
    });
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

  const handledelayableChange = (event) => {
    setDelayable(event.target.checked);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errors = validateForm();
    try {
      const data = {
        id: item.id,
        externalId: item.externalId,
        owner,
        title,
        description,
        createdDate: item.createdDate,
        startDate: startDate ? new Date(startDate).toISOString() : null,
        dueDate: dueDate ? new Date(dueDate).toISOString() : null,
        priority,
        duration,
        recurrence: item.recurrence,
        delayable: delayable,
        archived: item.archived,
        subTasks: subTasks.map((subTask) => ({
            id: subTask.id,
            externalId: subTask.externalId,
            owner: subTask.owner,
            title: subTask.title,
            description: subTask.description,
            createdDate: subTask.createdDate,
            startDate: subTask.startDate ? new Date(subTask.startDate).toISOString() : null,
            dueDate: subTask.dueDate ? new Date(subTask.dueDate).toISOString() : null,
            priority: subTask.priority,
            duration: subTask.duration,
            recurrence: subTask.recurrence,
            delayable: subTask.delayable,
            archived: subTask.archived,
        }))
      };

      await axios.post(TASK_API_URL, [data], {
        withCredentials: true,
        headers: {
          "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
        },
      });

      setOwner(item.owner);
      setTitle(item.title);
      setDescription(item.description);
      setStartDate(item.startDate);
      setDueDate(item.dueDate);
      setPriority(item.priority);
      setDelayable(item.delayable);
      setSubtasks(item.subTasks);
      toggleMenu();
      alert("Task added successfully");
    } catch (error) {
      console.error("Error submitting data:", error);
      alert("Failed to add task" + error.message);
    }
  };

   return (
 <div className="add-task-form">
    <div className="add-task">
      <h2>Edit Task</h2>
      <form onSubmit={handleSubmit}>
       <div className="Tasks-section">
        <div className="task-input">
          <label htmlFor="title">Title:</label><br/>
          <p>Task Title: {charTitleCount}/30 </p>
          <input
            type="text"
            id="title"
            value={item.title}
            onChange={handleTitleChange}
            maxLength={30}
            required
          />
        </div>
        <div className="task-input">
          <label htmlFor="task">Task:</label>
          <p>Characters: {charDescriptionCount}/250</p>
          <textarea
            type="text"
            id="task"
            value={item.description}
            onChange={handleDescriptionChange}
            maxLength={250}
          />
        </div>
</div>

<div className="data-section">
        <div className="task-input">
          <label htmlFor="startDate">Start Date:</label>
          <input
            type="datetime-local"
            id="startDate"
            defaultValue={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
        </div>
        <div className="task-input">
          <label htmlFor="dueDate">Due Date:</label>
          <input
            type="datetime-local"
            id="dueDate"
            defaultValue={dueDate}
            onChange={(e) => setDueDate(e.target.value)}
          />
        </div>
        <div className="task-input">
          <input
            type="checkbox"
            id="delayable"
            name="delayable"
            checked={delayable}
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
</div>
  <button className="addSubTasks" type="button" onClick={toggleSubtasksVisibility}>
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
          <button className="addSubTasks" type="button" onClick={addSubtask}>
            Add Subtask
          </button>
        </div>}

        <div className="new-button-container">
         <button className="newBack" onClick={() => navigate('/')}>
  Back
</button>
          <button className="newAdd" type="submit">
            Submit
          </button>
          {formErrors.date && <p className="error">{formErrors.date}</p>}
        </div>
      </form>
    </div>
  </div>
)}

export default EditTask;
