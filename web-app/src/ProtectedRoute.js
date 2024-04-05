import React, { useState, useEffect } from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { USER_CREDENTIALS_API_URL } from './URLConstants';

export function ProtectedRoute({ loginPage = false, redirectPath = '/login' }) {
    const [loggedIn, setLoggedIn] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch(USER_CREDENTIALS_API_URL, { credentials: 'include' })
          .then(response => response.text())
          .then(body => {
            if (body === '') {
              setLoggedIn(false);
              setLoading(false);
            } else {
              setLoggedIn(true);
              setLoading(false);
            }
          });
      }, []);

    if (loading) {
        return null
    }

    if (loginPage) {
        return (
            loggedIn ?
            <Navigate to={redirectPath} replace /> :
            <Outlet />
        )
    }

    return (
        loggedIn ?
        <Outlet /> :
        <Navigate to={redirectPath} replace />
    )
}