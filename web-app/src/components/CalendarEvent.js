import React, { useState } from 'react';

export function CalendarEvent({event, handleSubmit}) {
    const [priority, setPriority] = useState(3);
    const dueDate = event.end.dateTime ? event.end.dateTime : event.end.date;

    const submitClicked = (e) => {
        e.preventDefault();

        const currentDate = new Date().toISOString().split("T")[0];

        const task = {
            owner: "",
            title: event.summary,
            createdDate: `${currentDate}T12:00`,
            dueDate,
            priority
        }

        handleSubmit(task);
    }
    
    return (
        <div>
            <h2>{event.summary}</h2>
            <p className="dueDate">
                Due: {String(dueDate).split("T")[0]}<br/>
                Priority: {priority}
            </p>
            <button onClick={(e) => submitClicked(e)}>Add Task</button>
        </div>
    )
}