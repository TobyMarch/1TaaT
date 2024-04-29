import React, { useState } from 'react';

export function CalendarEvent({event, handleSubmit}) {
    const [priority, setPriority] = useState(3);
    const createdDate = event.created;
    const startDate = event.start.dateTime ? event.start.dateTime : event.start.date;
    const dueDate = event.end.dateTime ? event.end.dateTime : event.end.date;

    const submitClicked = (e) => {
        e.preventDefault();

        const task = {
            externalId: event.id,
            title: event.summary,
            createdDate: new Date(createdDate).toISOString(),
            startDate: new Date(startDate).toISOString(),
            dueDate: new Date(dueDate).toISOString(),
            priority,
            duration: "M"
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
            <input
                type="range"
                id="priority"
                value={priority}
                min="1"
                max="5"
                onChange={(e) => setPriority(parseInt(e.target.value, 10))}
              />
            <button onClick={(e) => submitClicked(e)}>Add Task</button>
        </div>
    )
}