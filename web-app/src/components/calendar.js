import React, { useState, useEffect } from 'react';
import { 
    LOGOUT_ROUTE,
    ACCESS_TOKEN,
    REFRESH_ACCESS_TOKEN,
    GOOGLE_API_KEY,
    CALENDAR_DISCOVERY_DOC
 } from '../URLConstants';
import { useCookies } from 'react-cookie';
import { calendarRequest, setGapiToken, callGapi } from './gapi-utils';

function Calendar() {
    const [tasks, setTasks] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(undefined);
    const [cookies] = useCookies(["XSRF-TOKEN"]);

    useEffect(() => {
        const gapiLoaded = () => {
            window.gapi.load('client', initGapiClient)
        }
    
        const initGapiClient = async () => {
            await window.gapi.client.init({
                apiKey: GOOGLE_API_KEY,
                discoveryDocs: [CALENDAR_DISCOVERY_DOC]
            })

            await setGapiToken(ACCESS_TOKEN, cookies["XSRF-TOKEN"]);
            const response = await callGapi(calendarRequest(), REFRESH_ACCESS_TOKEN, cookies["XSRF-TOKEN"]);
            setTasks(response.result.items);
            setNextPageToken(response.result.nextPageToken);
        }

        const client = document.createElement("script");
        client.async = true;
        client.defer = true;
        client.onload = gapiLoaded;
        client.src = "https://apis.google.com/js/api.js";

        document.body.appendChild(client);
    }, []);

    const handleFetchEvents = async () => {
        const request = calendarRequest();
        if(nextPageToken != undefined) {
            request.pageToken = nextPageToken;
        }

        const response = await callGapi(request, REFRESH_ACCESS_TOKEN, cookies["XSRF-TOKEN"]);
        setTasks(response.result.items);
        setNextPageToken(response.result.nextPageToken);

        const events = response.result.items;
        if (!events || events.length === 0) {
          console.log('No events found.');
          return;
        }
    }

    const handleClearEvents = () => {
        setTasks([]);
    }

    const handleTestButton = async () => {
        console.log(tasks);
        console.log(nextPageToken);
    }

    const handleLogout = () => {
        fetch(LOGOUT_ROUTE, {
            method:'post',
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
            }
        })
        .then(res => {
            if (res.status === 200) {window.location.href = window.location.origin;}
        });
    }
    

    const tasksList = tasks.map((task, index) => {
        return <li key={index}>{task.summary}</li>
    })

    return (
        <div>
            <button onClick={handleFetchEvents}>Fetch Events</button>
            <button onClick={handleClearEvents}>Clear Events</button>
            <button onClick={handleTestButton}>Test Button</button>
            <button onClick={handleLogout}>Logout</button>
            <ul>{tasksList}</ul>
        </div>
    )
}

export default Calendar;