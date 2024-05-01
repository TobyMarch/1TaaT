import React, { useState, useEffect } from 'react';
import {
    LOGOUT_ROUTE,
    ACCESS_TOKEN,
    REFRESH_ACCESS_TOKEN,
    GOOGLE_API_KEY,
    CALENDAR_DISCOVERY_DOC,
    TASK_API_URL
 } from '../URLConstants';
import { useCookies } from 'react-cookie';
import { calendarRequest, setGapiToken, callGapi } from './gapi-utils';
import { CalendarEvent } from './CalendarEvent';

function Calendar() {
    const [events, setEvents] = useState([]);
    const [nextPageToken, setNextPageToken] = useState(undefined);
    const [prevPageToken, setPrevPageToken] = useState(undefined);
    const [curPageToken, setCurPageToken] = useState(undefined);
    const [cookies] = useCookies(["XSRF-TOKEN"]);
    const [pageTokens, setPageTokens] = useState([]);

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
            const response = await callGapi(calendarRequest, REFRESH_ACCESS_TOKEN, cookies["XSRF-TOKEN"]);
            setEvents(response.result.items);
            setNextPageToken(response.result.nextPageToken);
        }

        const client = document.createElement("script");
        client.async = true;
        client.defer = true;
        client.onload = gapiLoaded;
        client.src = "https://apis.google.com/js/api.js";

        document.body.appendChild(client);
    }, []);

    const handleNextPage = () => {
        const token = nextPageToken;
        setPageTokens([...pageTokens, curPageToken]);
        setCurPageToken(nextPageToken);
        setPrevPageToken(curPageToken);
        handleFetchEvents(token);
    }

    const handlePrevPage = () => {
        const token = prevPageToken;
        setCurPageToken(prevPageToken);
        setPrevPageToken(pageTokens[pageTokens.length - 1])
        setPageTokens([...pageTokens].filter(t => t != token));
        handleFetchEvents(token);
    }

    const handleFetchEvents = async (token) => {
        const request = calendarRequest;
        request.pageToken = token;

        const response = await callGapi(request, REFRESH_ACCESS_TOKEN, cookies["XSRF-TOKEN"]);
        setEvents(response.result.items);
        setNextPageToken(response.result.nextPageToken);

        const events = response.result.items;
        if (!events || events.length === 0) {
          console.log('No events found.');
          return;
        }
    }

    const handleTestButton = () => {
        // console.log(events);
        console.log(nextPageToken);
        console.log(curPageToken);
        console.log(prevPageToken);
        // console.log(pageTokens);
        // console.log(window.gapi);
        // handleFetchEvents();
    }

    const handleSubmit = (task) => {
        fetch(TASK_API_URL, {
            body: JSON.stringify([task]),
            method:'post',
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
                "Content-Type": "application/json"
            }
        })
        .then(res => res.json())
        .then(data => console.log(data));
    }

    const eventsList = events.map((event, index) =>
        <CalendarEvent
            key={index}
            event={event}
            handleSubmit={handleSubmit}
        />
)

    return (
       <div className="google-calendar-container">
            <button onClick={handleTestButton}>Test</button>
            <ul>{eventsList}</ul>
            <div>
                {prevPageToken &&
                    <button onClick={handleNextPage}>Prev Page</button>
                }
                {nextPageToken &&
                    <button onClick={handlePrevPage}>Next Page</button>
                }
            </div>
        </div>
    )
}

export default Calendar;
