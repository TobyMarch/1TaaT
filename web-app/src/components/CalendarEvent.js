import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

export function CalendarEvent({event, handleSubmit}) {
    const [priority, setPriority] = useState(3);
    const [duration, setDuration] = useState("M");
    const createdDate = event.created;
    const startDate = event.start.dateTime ? event.start.dateTime : event.start.date;
    const dueDate = event.end.dateTime ? event.end.dateTime : event.end.date;
  const navigate = useNavigate();

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
navigate('/');
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

         <button className="back" onClick={() => navigate('/')}>
  Back
</button>
            <button onClick={(e) => submitClicked(e)}>Add Task</button>
        </div>
            )
}

// <div>
//         <h2>{event.summary}</h2> {/* Displaying the task summary (Title) */}
//         <p className="dueDate">
//             Start: {String(startDate).split("T")[0]}<br />
//             End: {String(dueDate).split("T")[0]}<br />
//             Duration: {duration}<br />
//             Priority: {priority}<br />
//         </p>
//
//         {/* Inputs for editing the task details */}
//         <div className="task-input">
//           <label htmlFor="title">Task Title:</label><br />
//           <input
//             type="text"
//             id="title"
//             value={title}
//             onChange={(e) => setTitle(e.target.value)}
//             maxLength={50}
//             required
//           />
//         </div>
//
//         <div className="task-input">
//           <label htmlFor="task">Description:</label><br />
//           <textarea
//             id="task"
//             value={description}
//             onChange={(e) => setDescription(e.target.value)}
//             maxLength={200}
//           />
//         </div>
//
//         {/* Subtasks Section */}
//         <div className="subTasks-section">
//           <label>Subtasks:</label>
//           {subTasks.map((subTask, index) => (
//             <div key={index} className="subTask-input">
//               <input
//                 type="text"
//                 value={subTask.title}
//                 onChange={(e) => handleSubtaskChange(index, 'title', e.target.value)}
//                 placeholder="Subtask title"
//               />
//               <textarea
//                 value={subTask.description}
//                 onChange={(e) => handleSubtaskChange(index, 'description', e.target.value)}
//                 placeholder="Description"
//               />
//               <button onClick={() => removeSubtask(index)}>Remove Subtask</button>
//             </div>
//           ))}
//           <button onClick={addSubtask}>Add Subtask</button>
//         </div>
//
//         {/* Priority Input */}
//         <input
//             type="range"
//             id="priority"
//             value={priority}
//             min="1"
//             max="5"
//             onChange={(e) => setPriority(parseInt(e.target.value, 10))}
//         />
//
//         {/* Duration Dropdown */}
//         <div className="durationDropdown">
//             <select
//                 onChange={(e) => setDuration(e.target.value)}
//                 value={duration}
//             >
//                 <option value="S">Small</option>
//                 <option value="M">Medium</option>
//                 <option value="L">Large</option>
//                 <option value="XL">Extra Large</option>
//             </select>
//         </div>
//
//         <button onClick={(e) => submitClicked(e)}>Add Task</button>
//     </div>
// );
