import React, { createContext, useState } from 'react';
import { Platform } from 'react-native';

const API_URL = Platform.OS === 'android' ? 'http://10.0.2.2:8080/api' : 'http://localhost:8080/api';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [userToken, setUserToken] = useState(null);

  const login = async (username, password) => {
    const response = await fetch(`${API_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });

    if (response.ok) {
      const data = await response.json();
      setUserToken(data.jwt);
    } else {
      throw new Error('Invalid credentials');
    }
  };

  const logout = () => {
    setUserToken(null);
  };

  return (
    <AuthContext.Provider value={{ login, logout, userToken }}>
      {children}
    </AuthContext.Provider>
  );
};
