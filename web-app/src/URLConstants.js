// Determine the environment mode (development or production)
export const environment = process.env.REACT_APP_ENVIRONMENT;

// Function to get the appropriate service URL based on the environment
export const getTaskServiceUrl = () => {
  return environment === "prod"
    ? "https://onetaat-services.com" // Production URL
    : "http://localhost:8080"; // Development URL
};

// Export the URL so it can be used throughout your application
export const TASK_SVC_URL = getTaskServiceUrl();

export const TASK_API_URL = TASK_SVC_URL + "/api/tasks";

export const USER_CREDENTIALS_API_URL = TASK_SVC_URL + "/api/users/user"

export const ALL_TASKS_API_URL = TASK_API_URL + "/all";

// Example parameters: page=0&size=50&sort=priority,DESC'
export const PAGINATED_TASKS_API_URL = TASK_API_URL + "/list";

export const TOP_TASK_API_URL = TASK_API_URL + "/top";

export const ARCHIVED_API_URL = TASK_API_URL + "/archived";

export const USER_CALENDAR_API_URL = TASK_SVC_URL + "/api/calendar";

export const USER_CALENDAR_SAVED_API_URL = TASK_SVC_URL + "/api/calendarSaved";

export const USER_TOKEN_REFRESH_API_URL = TASK_SVC_URL + "/api/users/checkUserRefreshToken";

export const ADD_USER_API_URL = TASK_SVC_URL + "/api/users/addUser";

export const GOOGLE_CLIENT_URL = "https://accounts.google.com/gsi/client";

export const AUTH_ROUTE = TASK_SVC_URL + "/private";
