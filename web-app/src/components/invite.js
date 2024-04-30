import React, { useState, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import { TASK_SVC_URL } from '../URLConstants';

function Invite() {
    const [invitations, setInvitations] = useState([]);
    const [cookies] = useCookies(["XSRF-TOKEN"]);

    useEffect(() => {
        fetch(TASK_SVC_URL + "/api/invitations", {
            method:'get',
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
            }
        })
        .then(res => res.json())
        .then(data => {
            console.log(data);
            setInvitations(data);
        })
    },[])

    const handleSendInvite = () => {
        const invitation = {
            userEmail: "walkerblack4@gmail.com",
            taskTitle: "Booking for Boston Calling",
            taskId: "662ea2c66147ee0822e11c77"
        }
        console.log(invitation);
        fetch(TASK_SVC_URL + "/api/invitations/invite", {
            method:'post',
            body: JSON.stringify(invitation),
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
                "Content-Type": 'application/json'
            }
        })
        .then(res => res.text())
        .then(msg => console.log(msg))
    }

    const handleInviteResponse = async (event, invitation) => {
        const method = event.target.name === 'accept' ? 'post' : 'delete';
        await fetch(TASK_SVC_URL + `/api/invitations/${event.target.name}`, {
            method: method,
            body: JSON.stringify(invitation),
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
                "Content-Type": 'application/json'
            }
        })
        .then(res => {
            if(res.status === 200) {
                let pendingInvites = [...invitations].filter(i => i.id != invitation.id);
                setInvitations(pendingInvites);
            }
            return res.text();
        })
        .then(msg => {console.log(msg)});
    }

    const inviteList = invitations.map((invitation, index) =>
        <li key={index}>
            {invitation.taskTitle}
            <button name='accept' onClick={(e) => handleInviteResponse(e, invitation)}>Accept</button>
            <button name='reject' onClick={(e) => handleInviteResponse(e, invitation)}>Decline</button>
        </li>
    )

    return (
        <div>
            <button onClick={handleSendInvite}>Send Invite</button>
            <ul>
                {inviteList}
            </ul>
        </div>
    )
}

export default Invite;