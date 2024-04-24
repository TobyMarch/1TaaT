// UtilityFunctions.js
import axios from 'axios';
import { TASK_API_URL, ARCHIVED_API_URL } from "./URLConstants";

export const handleLogout = async () => {
    // Perform logout operation, possibly clearing cookies or local storage
    console.log('Logging out user...');
    // Add additional logic as needed
};

export const handleTitleChange = (event, setTitle, setTitleCharCount) => {
    const newTitle = event.target.value.slice(0, 50);
    setTitle(newTitle);
    setTitleCharCount(newTitle.length);
};

export const handleDescriptionChange = (event, setDescription, setDescriptionCharCount) => {
    const newDescription = event.target.value.slice(0, 350);
    setDescription(newDescription);
    setDescriptionCharCount(newDescription.length);
};

export const handleOptionChange = (event, setSelectedOption, sortItems) => {
    setSelectedOption(event.target.value);
    sortItems(event.target.value);
};

export const toggleMenu = (setMenuVisible) => {
    setMenuVisible(prevState => !prevState);
};

export const toggleColumns = (setIsThreeColumns) => {
    setIsThreeColumns(prevState => !prevState);
};

export const handleSubmit = async (e, formData, resetForm, setItems, cookies) => {
    e.preventDefault();
    try {
        const response = await axios.post(TASK_API_URL, formData, {
            withCredentials: true,
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
            },
        });
        setItems(prevItems => [...prevItems, response.data]);
        resetForm();
        alert("Task added successfully");
    } catch (error) {
        console.error("Error submitting new task:", error);
        alert("Failed to add task: " + error.message);
    }
};

export const doneTask = async (taskId, setItems, cookies) => {
    try {
        await axios.put(`${TASK_API_URL}/${taskId}/archive`, {}, {
            withCredentials: true,
            headers: {
                "X-XSRF-TOKEN": cookies['XSRF-TOKEN'],
            }
        });
        setItems(prevItems => prevItems.filter(item => item.id !== taskId));
        alert("Task marked as done successfully");
    } catch (error) {
        console.error("Error marking task as done:", error);
        alert("Failed to mark task as done");
    }
};

export const removeTask = async (taskId, setItems, cookies) => {
    try {
        await axios.delete(`${TASK_API_URL}/${taskId}`, {
            withCredentials: true,
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
            },
        });
        setItems(prevItems => prevItems.filter(item => item.id !== taskId));
        alert("Task removed successfully");
    } catch (error) {
        console.error("Error removing the task:", error);
        alert("Failed to remove task");
    }
};

export const isOverdue = (dueDateString) => {
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Normalize today's date to midnight for accurate comparison
    const dueDate = new Date(dueDateString);
    return dueDate < today;
};

export const sortItems = (items, setItems, filter) => {
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

