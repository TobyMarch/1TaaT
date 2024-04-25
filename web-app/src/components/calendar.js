import React, { useState, useEffect } from 'react';
import { ADD_USER_API_URL, GOOGLE_CLIENT_URL, USER_CALENDAR_API_URL, USER_CALENDAR_SAVED_API_URL, USER_TOKEN_REFRESH_API_URL } from '../URLConstants';
import { useCookies } from 'react-cookie';

const CLIENT_ID = "1049783711075-nd5nmsc6gj474f8b5ntf9o2brmu3l9ol.apps.googleusercontent.com";
const SCOPES = "https://www.googleapis.com/auth/admin.directory.resource.calendar.readonly";
const USER_ID = "";

const DISCOVERY_DOC = 'https://www.googleapis.com/discovery/v1/apis/calendar/v3/rest';

function Calendar() {
    const [tasks, setTasks] = useState([]);
    const [cookies] = useCookies(["XSRF-TOKEN"]);

    useEffect(() => {
        const script = document.createElement("script");
        script.async = true;
        script.defer = true;
        script.src = GOOGLE_CLIENT_URL;

        const client = document.createElement("script");
        client.async = true;
        client.defer = true;
        client.src = "https://apis.google.com/js/api.js";

        document.body.appendChild(script);
        document.body.appendChild(client);
    }, []);

    const gapiLoaded = () => {
        gapi.load('client')
    }

    const initGapiClient = async () => {
        await gapi.client.init({

        })
    }

    const handleSyncCalendar = async () => {
        try {
            fetch(USER_TOKEN_REFRESH_API_URL, {
                method: 'get',
                credentials: 'include'
            })
                .then(response => {
                    console.log(response);
                    return response.json()
                })
                .then(data => {
                    console.log(data);
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
                                console.log(response);
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

    // const handleSyncCalendar = () => {
    //     window.location.href = "http://localhost:8080/oauth2/authorization/calendar";
    // }

    const handleAddUser = async () => {
        // localStorage.setItem("currentUser", USER_ID);
        // try {
        //     const data = {
        //         userId: USER_ID
        //     }
        //     fetch(ADD_USER_API_URL, {
        //         method: 'post',
        //         headers: {'Content-Type': 'application/json'},
        //         body: JSON.stringify(data)
        //     })
        //     .then(response => response.json())
        //     .then(data => console.log(data));
        // } catch (e) {
        //     console.error(e);
        // }
        fetch("http://localhost:8080/api/calendar/checkRefreshToken", {
            method:'get',
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
            }
        });
    }

    const handleLogout = () => {
        fetch('http://localhost:8080/logout', {
            method:'post',
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
            }
        })
        .then(res => {
            if (res.status == 200) {window.location.href = window.location.origin;}
        });
    }

    const tasksList = tasks.map((task, index) => {
        return <li key={index}>{task.summary}</li>
    })

    return (
        <div>
            <button onClick={handleSyncCalendar}>Sync Calendar</button>
            <button onClick={handleAddUser}>Add User</button>
            <button onClick={handleLogout}>Logout</button>
            <ul>{tasksList}</ul>
        </div>
    )
}

export default Calendar;