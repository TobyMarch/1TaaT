import React, { useState, useEffect } from 'react';

const CLIENT_ID = "1049783711075-nd5nmsc6gj474f8b5ntf9o2brmu3l9ol.apps.googleusercontent.com";
const SCOPES = "https://www.googleapis.com/auth/calendar.readonly";
const USER_ID = "wablack";

function Calendar() {
    const[tasks, setTasks] = useState([]);

    useEffect(() => {
        const script = document.createElement("script");
        script.async = true;
        script.defer = true;
        script.src = "https://accounts.google.com/gsi/client";
        document.body.appendChild(script);
      }, []);

    const handleSyncCalendar = async () => {
        try {
            fetch(`http://localhost:8080/api/users/checkUserRefreshToken?userId=${USER_ID}`, { 
                method: 'get'
            })
            .then(response => response.json())
            .then(data => {
                if (data === true) {
                    fetch(`http://localhost:8080/api/calendar?userId=${USER_ID}`, { 
                        method: 'get'
                    })
                    .then(response => response.json())
                    // .then(data => console.log(data));
                    .then(data => {
                        console.log(data)
                        setTasks(data)
                    });
                } else {
                    const client = window.google.accounts.oauth2.initCodeClient({
                    client_id: CLIENT_ID,
                    scope: SCOPES,
                    ux_mode: 'popup',
                    callback: async (response) => {
                        try {
                            if (!response.code) {
                                return;
                            }
                            fetch(`http://localhost:8080/api/calendarSaved?code=${response.code}&userId=${USER_ID}`, { 
                                method: 'get'
                            })
                            .then(response => response.json())
                            // .then(data => console.log(data));
                            .then(data => setTasks(data));
                        } catch (error) {
                            console.log(error);
                        }
                    }
                });
                client.requestCode();
                }
            });
        } catch (error) {
            console.log(error);
        }
    }

    const handleAddUser = async () => {
        localStorage.setItem("currentUser", USER_ID);
        try {
            const data = {
                userId: USER_ID
            }
            fetch("http://localhost:8080/api/users/addUser", {
                method: 'post',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            })
            .then(response => response.json())
            .then(data => console.log(data));
        } catch (e) {
            console.error(e);
        }
    }

    const tasksList = tasks.map((task, index) => {
        return <li key={index}>{task.summary}</li>
    })

    return (
        <div>
            <button onClick={handleSyncCalendar}>Sync Calendar</button>
            <button onClick={handleAddUser}>Add User</button>
            <ul>{tasksList}</ul>
        </div>
    )
}

export default Calendar;