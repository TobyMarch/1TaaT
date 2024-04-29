import React, { useState } from 'react';

export function CalendarEvent({event, handleSubmit}) {
    const [priority, setPriority] = useState(3);
    const [duration, setDuration] = useState("M");
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
            duration
        }

        handleSubmit(task);
    }
    
    return (
        <div>
            <h2>{event.summary}</h2>
            <p className="dueDate">
                Start: {String(startDate).split("T")[0]}<br />
                End: {String(dueDate).split("T")[0]}<br />
                Duration: {duration}<br />
                Priority: {priority}<br />
            </p>
            <input
                type="range"
                id="priority"
                value={priority}
                min="1"
                max="5"
                onChange={(e) => setPriority(parseInt(e.target.value, 10))}
            />
            <div className="durationDropdown">
                <select
                    onChange={(e) => setDuration(e.target.value)}
                    value={duration}
                >
                    <option value="S">Small</option>
                    <option value="M">Medium</option>
                    <option value="L">Large</option>
                    <option value="XL">Extra Large</option>
                </select>
            </div>
            <button onClick={(e) => submitClicked(e)}>Add Task</button>
        </div>
    )
}