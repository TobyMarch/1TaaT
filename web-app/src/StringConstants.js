// Determine the environment mode (development or production)
export const environment = process.env.REACT_APP_ENVIRONMENT;

// Function to get the appropriate service URL based on the environment
export const getAnsweringSvcUrl = () => {
  return environment === "prod"
    ? "https://task-services.onrender.com" // Production URL
    : "http://localhost:8080"; // Development URL
};

// Export the URL so it can be used throughout your application
export const ANSWERING_SVC_URL = getAnsweringSvcUrl();
