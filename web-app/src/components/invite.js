import React, { useState, useEffect } from 'react';
import { useCookies } from 'react-cookie';
import { TASK_SVC_URL } from '../URLConstants';

function Invite() {
    const [invitations, setInvitations] = useState([]);
    const [inviteId, setInviteId] = useState("");
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
            if (data.length > 0) {
                setInviteId(data[0].id);
            }
        })
    },[])

    const invitation = {
        userEmail: "walkerblack4@gmail.com",
        taskId: "662ea2c66147ee0822e11c77"
    }

    const handleSendInvite = () => {
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

    const handleAcceptInvite = async () => {
        await fetch(TASK_SVC_URL + "/api/invitations/accept", {
            method:'post',
            body: JSON.stringify(invitations[0]),
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"],
                "Content-Type": 'application/json'
            }
        })
        .then(res => res.text())
        .then(msg => {
            console.log(msg);
            if (msg == "success") {
                let pendingInvites = [...invitations].filter(i => i.id != inviteId);
                setInvitations(pendingInvites);
            }
        })
    }

    const handleRejectInvite = async () => {
        invitation.id = inviteId;
        await fetch(TASK_SVC_URL + "/api/invitations/reject", {
            method:'post',
            body: JSON.stringify(invitation),
            credentials: 'include',
            headers: {
                "X-XSRF-TOKEN": cookies["XSRF-TOKEN"]
            }
        })
        .then(res => res.text())
        .then(msg => {
            console.log(msg);
            if (msg == "success") {
                let pendingInvites = [...invitations].filter(i => i.id != inviteId);
                setInvitations(pendingInvites);
            }
        })
    }

    const inviteList = invitations.map((invitation, index) =>
        <li key={index}>{invitation.taskId}</li>
    )
    return (
        <div>
            <button>Load Invites</button>
            <button onClick={handleSendInvite}>Send Invite</button>
            <button onClick={handleAcceptInvite}>Accept Invite</button>
            <button onClick={handleRejectInvite}>Reject Invite</button>
            <ul>
                {inviteList}
            </ul>
        </div>
    )
}

export default Invite;