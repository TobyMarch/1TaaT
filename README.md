# 1TaaT
One Thing at a Time.

# 1TaaT - Backend

### backend documentation goes here



# 1TaaT - Frontend
The project was created using create-react-app. 

The `/web-app/src` directory holds the most relevant code that will be used for the front end part of the project.
The `/web-app/sample-java` directory has sample code for connecting the backend to the front end. 
Run it using `mvn spring-boot:run` and `mvn dependency:resolve`

## Files 
### `App.js`
App.js handles the input of the data: name, task, dueDate, addedDate, rating: 1-9, then sends it to the Java backend. 
App.js also handles the Web-app layout for now. 

### `List.js`
List.js handles all data received from the Java backend. It will take the data and display it as a specially formatted list. 

### `/sample-java` 
This directory is sample java code for interacting with the front end.

## Dependencies
Axios - is a promise-based HTTP library that lets developers make requests to either their own or a third-party server to fetch data.

## Start, Test, Build
In the `/web-app` directory, you can run:
Make sure to run `npm install` if it is a new git repository, to add the node_modules.

#### `npm start`
Runs the app in the development mode.\
Open [http://localhost:8080](http://localhost:8080) to view it in your browser.

#### `npm test`
Launches the test runner in the interactive watch mode.

#### `npm run build`
Builds the app for production to `/build` (`/build` is ignored by git).
