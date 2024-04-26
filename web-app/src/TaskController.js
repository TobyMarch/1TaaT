//includes all the axios calls to TaskController.java and some related logic
import axios from 'axios';
import {
  TASK_API_URL,
  TOP_TASK_API_URL,
  PAGINATED_TASKS_API_URL,
  ARCHIVED_API_URL,
  LOGOUT_ROUTE,
} from "./URLConstants";
import { useCookies } from "react-cookie";

const fetchTasks = async (isThreeColumns, cookies) => {
  try {
    const response = await axios.get(
      isThreeColumns ? PAGINATED_TASKS_API_URL : TOP_TASK_API_URL,
      { withCredentials: true, headers: { "X-XSRF-TOKEN": cookies["XSRF-TOKEN"] } }
    );
    if (Array.isArray(response.data.content)) {
      return response.data.content;
    }
    return [];
  } catch (error) {
    console.error("Error fetching tasks:", error);
    return [];
  }
};

const fetchArchivedTasks = async (cookies) => {
  try {
    const response = await axios.get(ARCHIVED_API_URL, {
      withCredentials: true,
      headers: { "X-XSRF-TOKEN": cookies["XSRF-TOKEN"] },
    });
    return response.data || [];
  } catch (error) {
    console.error('Failed to fetch archived tasks:', error);
    return [];
  }
};

const handleLogout = async (cookies) => {
  try {
    const response = await axios.post(LOGOUT_ROUTE, {}, {
      withCredentials: true,
      headers: { "X-XSRF-TOKEN": cookies["XSRF-TOKEN"] },
    });
    if (response.status === 200) {
      window.location.href = window.location.origin;
    }
  } catch (error) {
    console.error("Logout failed:", error);
  }
};

const updateTask = async (taskId, taskDetails, cookies) => {
  try {
    await axios.put(`${TASK_API_URL}/${taskId}`, taskDetails, {
      withCredentials: true,
      headers: { "X-XSRF-TOKEN": cookies["XSRF-TOKEN"] },
    });
  } catch (error) {
    console.error("Error updating task:", error);
  }
};

const deleteTask = async (taskId, cookies) => {
  try {
    await axios.delete(`${TASK_API_URL}/${taskId}`, {
      withCredentials: true,
      headers: { "X-XSRF-TOKEN": cookies["XSRF-TOKEN"] },
    });
  } catch (error) {
    console.error("Error removing task:", error);
  }
};

export {
  fetchTasks,
  fetchArchivedTasks,
  handleLogout,
  updateTask,
  deleteTask,
};
