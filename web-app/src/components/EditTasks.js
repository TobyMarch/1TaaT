import React, { useState, useEffect } from "react";
import axios from "axios";
import { useCookies } from "react-cookie";
import { useParams, useNavigate } from "react-router-dom";
import { useLocation } from 'react-router-dom';

import {
  TASK_API_URL,
  TOP_TASK_API_URL,
  PAGINATED_TASKS_API_URL,
  ARCHIVED_API_URL,
  LOGOUT_ROUTE,
} from "../URLConstants";

function EditTask() {
  const location = useLocation();
  const [item, setItem] = useState(location.state);
   // const { item } = location.state || {};
  const [task, setTask] = useState(null);
  const [owner, setOwner] = useState("");
  const [createdDate, setCreatedDate] = useState("");
  const [isListView, setIsListView] = useState(false);
  const [items, setItems] = useState([]);
  const [editItemId, setEditItemId] = useState(null);
  const [editableTitle, setEditableTitle] = useState("");
  const [editableDescription, setEditableDescription] = useState("");
  const [menuVisible, setMenuVisible] = useState(false);
  const [title, setTitle] = useState(item.title);
  const [description, setDescription] = useState("");
  const [startDate, setStartDate] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [priority, setPriority] = useState(1);
  const [duration, setDuration] = useState("S");
  const [pageNumber, setPageNumber] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
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

  const [subTasks, setSubtasks] = useState([
      // {
      //   title: "",
      //   description: "",
      //   startDate: "",
      //   dueDate: "",
      //   priority: 1,
      //   duration: "S",
      //   completed: false,
      // },
  ]);

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
    navigate("/calendar");
  };

  const redirectToNewTask = () => {
    navigate("/NewTask");

  };

   const redirectToEditTask = () => {
    navigate("/editTask");
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
  axios.post(
    `${TASK_API_URL}/${item.id}`,
    {
      title: editableTitle,
      description: editableDescription,
      startDate: item.startDate,
      dueDate: item.dueDate,
      priority: item.priority,
      duration: item.duration,
      subTasks: item.subTasks.map(subTask => ({
          title: subTask.title,
          description: subTask.description,
          startDate: subTask.startDate,
          dueDate: subTask.dueDate,
          priority: subTask.priority,
          duration: subTask.duration,
          completed: subTask.completed
      })),
      isdelayable: item.isdelayable
    },
    {
      headers: {
        "Content-Type": "application/json",
        "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
      },
      withCredentials: true,
    }
  )
  .then(response => {
    console.log("Update successful:", response.data);
    fetchTasks(); // Refresh the tasks list to reflect the update
  })
  .catch(error => {
    console.error("Failed to save changes:", error);
  });
};
  const handleArchiveClick = async () => {
    setSelectedOption("");
    setPageNumber(0);
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
    fetchTasks(newSortOrder);
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



    //  useEffect(() => {
    //     const fetchTask = async () => {
    //         try {
    //             const response = await axios.get(`${TASK_API_URL}/${taskId}`, {
    //                 headers: {
    //                     'X-XSRF-TOKEN': cookies['XSRF-TOKEN'],
    //                     'Content-Type': 'application/json'
    //                 },
    //                 withCredentials: true
    //             });
    //             setTask(response.data);
    //         } catch (error) {
    //             console.error('Error fetching task:', error);
    //         }
    //     };
    //
    //     fetchTask();
    // }, [taskId, cookies, axios]);



  // useEffect(() => {
  //   fetchTasks();
  //   const currentDate = new Date().toISOString().split("T")[0];
  //   setStartDate(`${currentDate}T12:00`);
  //   setDueDate(`${currentDate}T12:00`);
  // }, [isListView]);



  const toggleColumns = () => {
    setIsListView(!isListView);
  };

  // Task retrieval and submission

  const fetchTasks = async () => {
    try {
      let paginatedWithparams =
        PAGINATED_TASKS_API_URL + `?size=10&page=${pageNumber}` + selectedOption;
      const response = await axios.get(
        isListView ? paginatedWithparams : TOP_TASK_API_URL,
        { withCredentials: true }
      );
      if (Array.isArray(response.data.content)) {
        setItems(response.data.content);
        setTotalPages(response.data.totalPages);
      } else if (response.data && !Array.isArray(response.data.content)) {
        setItems([response.data]);
      } else {
        console.error(
          "Expected an array or an object for content, received:",
          response.data
        );
        setItems([]);
      }
    } catch (error) {
      console.error("Error fetching tasks:", error);
      setItems([]);
    }
  };

  const fetchArchivedTasks = async () => {
    try {
        let paginatedWithParameters = ARCHIVED_API_URL + `?size=10&page=${pageNumber}` + selectedOption;
        const response = await axios.get(paginatedWithParameters, {
        withCredentials: true,
        headers: {
          "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
        },
      });
      if (response.data && Array.isArray(response.data.content)) {
        setTotalPages(response.data.totalPages);
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
        startDate: startDate ? new Date(startDate).toISOString() : null,
        dueDate: dueDate ? new Date(dueDate).toISOString() : null,
        priority,
        duration,
        isdelayable,
        subTasks: subTasks.map((subTask) => ({
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
      setSubtasks([{ title: "" }]);
      toggleMenu();
      alert("Task added successfully");
      fetchTasks();
    } catch (error) {
      console.error("Error submitting data:", error);
      alert("Failed to add task" + error.message);
    }
  };


console.log( item );
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
