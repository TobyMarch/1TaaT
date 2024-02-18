# 1TaaT
One Thing at a Time.










# 1TaaT - Frontend

The `/web-app/src` directory holds the most relevant code that will be used for the project.
The project was created using create-react-app. 


## Dependencies

Axios - is a promise-based HTTP library that lets developers make requests to either their own or a third-party server to fetch data.

## Files 

### `App.js`

App.js handles the input of the data: name, task, dueDate, addedDate, rating: 1-9, then sends it to the Java backend. 

App.js also handles the Web-app layout for now. 

### `List.js`

List.js handles all data received from the Java backend. It will take the data and display it as a specially formatted list. 

## Start, Test, Build

In the `/web-app` directory, you can run:

#### `npm start`
Runs the app in the development mode.\
Open [http://localhost:8080](http://localhost:8080) to view it in your browser.

#### `npm test`
Launches the test runner in the interactive watch mode.

#### `npm run build`
Builds the app for production to `/build` (`/build` is ignored by git).