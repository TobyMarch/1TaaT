export function calendarRequest() {
    const request = {
        calendarId: 'primary',
        timeMin: (new Date()).toISOString(),
        showDeleted: false,
        singleEvents: true,
        maxResults: 10,
        orderBy: 'startTime',
    }
    return request;
}

export function setGapiToken(accessTokenUrl, xsrfToken) {
    return fetch(accessTokenUrl, {
        method: 'get',
        credentials: 'include',
        headers: {
            "X-XSRF-TOKEN": xsrfToken
        }
    })
    .then(res => res.text())
    .then(token => {
        if (token === '') {
            window.location.href = window.location.origin;
        }
        window.gapi.client.setToken({
            access_token: token
        });
    })
}

export async function callGapi(request, refreshTokenUrl, xsrfToken) {
    let response;
    try {
        response = await window.gapi.client.calendar.events.list(request);
    } catch(err) {
        if(err.status === 401) {
            await setGapiToken(refreshTokenUrl, xsrfToken);
            response = await window.gapi.client.calendar.events.list(request);
        }
    }
    return response;
}