import React, { useState, useEffect } from 'react';
import { ADD_USER_API_URL, GOOGLE_CLIENT_URL, USER_CALENDAR_API_URL, USER_CALENDAR_SAVED_API_URL, USER_TOKEN_REFRESH_API_URL } from '../URLConstants';

const CLIENT_ID = "";
const SCOPES = "";
const USER_ID = "";

function Calendar() {
    const[tasks, setTasks] = useState([]);

    useEffect(() => {
        const script = document.createElement("script");
        script.async = true;
        script.defer = true;
        script.src = GOOGLE_CLIENT_URL;
        document.body.appendChild(script);
      }, []);

    const handleSyncCalendar = async () => {
        try {
            fetch(`${USER_TOKEN_REFRESH_API_URL}?userId=${USER_ID}`, { 
                method: 'get'
            })
            .then(response => response.json())
            .then(data => {
                if (data === true) {
                    fetch(`${USER_CALENDAR_API_URL}?userId=${USER_ID}`, { 
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
                            fetch(`${USER_CALENDAR_SAVED_API_URL}?code=${response.code}&userId=${USER_ID}`, { 
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
            fetch(ADD_USER_API_URL, {
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