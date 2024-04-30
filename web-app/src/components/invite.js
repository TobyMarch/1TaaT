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
            setInvitations(data);
        })
    },[])

    const handleSendInvite = (invitation) => {
        fetch(TASK_SVC_URL + "/api/invitations/invite", {
            method:'post',
            body: JSON.stringify(invitation),
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
                "Content-Type": 'application/json'
            }
        })
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
            <ul>
                {inviteList}
            </ul>
        </div>
    )
}

export default Invite;