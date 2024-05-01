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
import { useNavigate } from "react-router-dom";



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


const redirectToEditTask = (item) => {
  navigate(`/EditTask/${item.id}`, { state: { task:item } });

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

  const archivedStyle = {
    background: "linear-gradient(11deg, #c0c0c0 0%, #f0f0f0 100%)",
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
    navigate("/calendar");
  };

  const redirectToNewTask = () => {
    navigate("/NewTask");

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
  axios.put(
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

  const previousPage = async () => {
    setPageNumber(pageNumber > 0 ? pageNumber - 1 : pageNumber);
    showArchived ? fetchArchivedTasks() : fetchTasks();
  }

  const nextPage = async () => {
    setPageNumber(pageNumber < (totalPages - 1) ? pageNumber + 1 : pageNumber);
    showArchived ? fetchArchivedTasks() : fetchTasks();
  }

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

  return (
    <div className="App">
      {/* Top Bar Nav */}
      <div className="topBar">
        <div className="leftItems">
          <img src={logo} alt="Logo" className="logo" />
          <button onClick={handleLogout}>
            <div className="logout">
              <p>Logout</p>
            </div>
          </button>
        </div>

        <div className="filterDropdown">
          <select onChange={handleOptionChange} value={selectedOption}>
            <option value=""></option>
            <option value="&sort=priority,DESC">Highest Priority</option>
            <option value="&sort=priority,ASC">Lowest Priority</option>
            <option value="&sort=createdDate,ASC">Newest</option>
            <option value="&sort=createdDate,DESC">Oldest</option>
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



{!menuVisible && (
  <div className={`List ${isListView ? "listView" : ""}`}>
    <button onClick={() => previousPage()}>previous</button>
    <button onClick={() => nextPage()}>next</button>
    {showArchived ? archivedItems.map((item, index) => (
      <div
        className="item"
        key={index}
        style={showArchived ? archivedStyle : priorityGradientStyles[item.priority - 1]}
      >
        <h2 className="title">{item.title}</h2>
        <p className="description">{item.description || "Not set"}</p>

        <p className="duration">Duration: {item.duration || "Not set"}</p>
        <p className="dueDate">Start: {item.startDate ? new Date(Date.parse(item.startDate)).toLocaleString() : "Not set"}</p>
        <p className="dueDate">Due: {item.dueDate ? new Date(Date.parse(item.dueDate)).toLocaleString() : "Not set"}</p>
        <div className="subTasks-list">
          {item.subTasks && item.subTasks.map((subTask, subIndex) => (
            <div key={subIndex} className="subTask-text">
              <p>Title: {subTask.title}</p>
              <p>Description: {subTask.description}</p>
              <p>Start Date: {subTask.startDate}</p>
              <p>Due Date: {subTask.dueDate}</p>
              <p>Priority: {subTask.priority}</p>
              <p>Duration: {subTask.duration}</p>
            </div>
          ))}
        </div>
      </div>
    )) : items.map((item, index) => (
      <div
        className="item"
        key={index}
        style={priorityGradientStyles[item.priority - 1]}
      >
        <h2 className="title">{item.title}</h2>
        <p className="duration">Duration: {item.duration || "Not set"}</p>
         <div className="buttonGroup">

                              <button
                                className="skipButton"
                                onClick={() => skipTask(item.id)}
                              >
                                Do Tomorrow
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
        <div className="info-container">
          <p className="dueDate">Start: {item.startDate ? new Date(Date.parse(item.startDate)).toLocaleString() : "Not set"}</p>
          <p className="dueDate">Due: {item.dueDate ? new Date(Date.parse(item.dueDate)).toLocaleString() : "Not set"}</p>
          {isOverdue(item.dueDate) && <p><SVGflag /> Overdue</p>}
        </div>
        <label>Description:</label>
        <p className="description">{item.description || "Not set"}</p>
        <div className="subTasks-list">
          {item.subTasks && item.subTasks.map((subTask, subIndex) => (
            <div key={subIndex} className="subTask-text">
              <p>Title: {subTask.title}</p>
              <p>Description: {subTask.description}</p>
              <p>Start Date: {subTask.startDate}</p>
              <p>Due Date: {subTask.dueDate}</p>
              <p>Priority: {subTask.priority}</p>
              <p>Duration: {subTask.duration}</p>
            </div>
          ))}
        </div>
        {isListView && (
          <div className="buttonGroup">

            <button
              className="editButton"
              onClick={() => redirectToEditTask(item)}
            >
              Edit
            </button>
          </div>
        )}
      </div>
    ))}
  </div>
)}




      <div className="sideBar">
        <p htmlFor="historyButton">History</p>
        <button className="historyButton" onClick={handleArchiveClick}>
          <SVGarchive />
        </button>
        <p htmlFor="ImportButton">
          Google <br />
          Import
        </p>
        <button className="importButton" onClick={redirectToCalendar}>
          <SVGimport />
        </button>

        <p htmlFor="addButton">New</p>
        <button className="addButton" onClick={redirectToNewTask}>
          <SVGAdd />
        </button>
      </div>
    </div>
  );
}

export default Home;
