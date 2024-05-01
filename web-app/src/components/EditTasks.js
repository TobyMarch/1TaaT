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
    const [task, setTask] = useState(location.state.task);
    const [owner, setOwner] = useState(task.owner);
    const [menuVisible, setMenuVisible] = useState(false);
    const [title, setTitle] = useState(task.title);
    const [description, setDescription] = useState(task.description);
    const [startDate, setStartDate] = useState(handleISODate(task.startDate));
    const [dueDate, setDueDate] = useState(handleISODate(task.dueDate));
    const [priority, setPriority] = useState(task.priority);
    const [duration, setDuration] = useState(task.duration);
    const [cookies] = useCookies(["XSRF-TOKEN"]);
    const [charTitleCount, setTitleCharCount] = useState(0);
    const [charDescriptionCount, setDescriptionCharCount] = useState(0);
    const [delayable, setDelayable] = useState(task.delayable);
    const [formErrors, setFormErrors] = useState({});
    const [subTasks, setSubtasks] = useState(task.subTasks);
    const [showSubtasks, setShowSubtasks] = useState(false);
    const [idsToDelete, setIdsToDelete] = useState([]);
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

    const validateForm = () => {
        const newTaskFail = {};
        if (new Date(startDate) >= new Date(dueDate)) {
            newTaskFail.date = "Due date must be after the start date.";
        }
        return newTaskFail;
    };

    const addSubtask = () => {
        setSubtasks([
            ...subTasks,
            {
                title: "",
                description: "",
                startDate: new Date().toISOString().slice(0, -1),
                dueDate: new Date().toISOString().slice(0, -1),
                priority: 1,
                duration: "S",
            }
        ]);
    };

    const removeSubtask = (subTask, index) => {
        // prepare to delete subtask on submission
        if (subTask.id) {
            setIdsToDelete([
                ...idsToDelete,
                subTask.id
            ]);
        }
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
                id: task.id,
                externalId: task.externalId,
                owner,
                title,
                description,
                createdDate: task.createdDate,
                startDate: startDate ? new Date(startDate).toISOString() : null,
                dueDate: dueDate ? new Date(dueDate).toISOString() : null,
                priority,
                duration,
                recurrence: task.recurrence,
                delayable: delayable,
                archived: task.archived,
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
            }).then(() => {
                idsToDelete.map((taskId) => {
                    axios.delete(`${TASK_API_URL}/${taskId}`, {
                        withCredentials: true,
                        headers: {
                            "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
                        },
                    });
                });
            });

            setOwner(task.owner);
            setTitle(task.title);
            setDescription(task.description);
            setStartDate(task.startDate);
            setDueDate(task.dueDate);
            setPriority(task.priority);
            setDelayable(task.delayable);
            setSubtasks(task.subTasks);
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
                            <label htmlFor="title">Title:</label><br />
                            <p>Task Title: {charTitleCount}/30 </p>
                            <input
                                type="text"
                                id="title"
                                value={task.title}
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
                                value={task.description}
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
                        {showSubtasks ? "Hide Subtasks" : "Show Subtasks"}
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
                                    value={handleISODate(subTask.startDate)}
                                    onChange={(e) => handleSubtaskChange(index, 'startDate', e.target.value)}
                                    placeholder="Start Date"
                                />
                                <input
                                    type="datetime-local"
                                    value={handleISODate(subTask.dueDate)}
                                    onChange={(e) => handleSubtaskChange(index, 'dueDate', e.target.value)}
                                    placeholder="Due Date"
                                />
                                <label htmlFor="priority">Priority:</label>
                                <input
                                    type="range"
                                    id="priority"
                                    defaultValue={subTask.priority}
                                    min="1"
                                    max="5"
                                    onChange={(e) => handleSubtaskChange(index, 'priority', parseInt(e.target.value, 10))}
                                />
                                <span>{priority}</span>
                                <label htmlFor="duration">Duration:</label>
                                <div className="durationDropdown">
                                    <select
                                        onChange={(e) => handleSubtaskChange(index, 'dueDate', e.target.value)}
                                        value={subTask.duration}
                                    >
                                        <option value="S">Small</option>
                                        <option value="M">Medium</option>
                                        <option value="L">Large</option>
                                        <option value="XL">XLarge</option>
                                    </select>
                                </div>
                                <button type="button" onClick={() => removeSubtask(subTask, index)}>Remove</button>
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
                        <button className="newAdd"  type="submit" onClick={() => navigate("/")}>
                            Submit
                        </button>
                        {formErrors.date && <p className="error">{formErrors.date}</p>}
                    </div>
                </form>
            </div>
        </div>
    )
}

export default EditTask;
