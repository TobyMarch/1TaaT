# 1TaaT
One Thing at a Time.

# 1TaaT - Backend

### `mvn spring-boot:run`
runs the java backend 

### Database connection
Replace the username and password in task-services/src/main/resources/application.properties with your connection string
spring.data.mongodb.uri=

# 1TaaT - Frontend
The project was created using create-react-app. 

## Files 
### `App.js`
Login.js - handles login screen and request to google, and Java backend
  App.js - handles layout of the app and the input of the data, then sends to the Java backend. 

## Dependencies
Axios - is a promise-based HTTP library that lets developers make requests to either their own or a third-party server to fetch data.

## Start, Test, Build
In the `/web-app` directory, you can run the following commands:

#### `npm install`
Make sure to run `npm install` if it is a new git repository, to add node_modules.

#### `npm start`
Runs the app in the development mode.\
Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

#### `npm test`
Launches the test runner in the interactive watch mode.

#### `npm run build`
Builds the app for production to `/build` (`/build` is ignored by git).
