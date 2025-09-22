import axios from "axios";
import store from "../store/index";
import { useDispatch } from "react-redux";
import { authActions } from "../store/index";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    console.log('📡 API Request:', config.method?.toUpperCase(), config.url);
    
    const token = store.getState().auth.token;
    
    // Special logging for profile/user calls
    if (config.url?.includes('/users/') || config.url?.includes('/profile')) {
      console.log('👤 USER API CALL DETECTED');
      console.log('  URL:', config.url);
      console.log('  Method:', config.method);
      console.log('  Token exists:', !!token);
      console.log('  Token preview:', token?.substring(0, 30) + '...');
      console.log('  Headers before:', config.headers);
    }

    const publicPaths = [
      "/users/register",
      "/auth/token",
      "/auth/introspect",
      "/auth/logout",
      "/brands",
      "/orders/apply-discount",
      "/chat/shoe-data",
      "/chat/discount-data", // Fixed missing slash
    ];

    const isPublicPath = publicPaths.some(path => 
      config.url?.startsWith(path) || 
      config.url?.includes(path)
    ) || (config.url?.includes("/shoes") && config.method?.toLowerCase() === "get");

    console.log('Is public path:', isPublicPath, 'for URL:', config.url);

    if (!isPublicPath && token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('✅ Added Authorization header for:', config.url);
      console.log('Token preview:', token?.substring(0, 20) + '...');
    } else if (!isPublicPath && !token) {
      console.warn('❌ Private path but NO TOKEN available for:', config.url);
    } else {
      // For public paths, ensure we don't send the Authorization header
      delete config.headers.Authorization;
      console.log('ℹ️ Public path, no auth needed:', config.url);
    }

    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor for error handling
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API Error:', {
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data,
      url: error.config?.url,
      method: error.config?.method
    });

    // Handle specific error cases
    if (error.response?.status === 401) {
      console.log('Unauthorized - clearing token');
      // Clear auth state if unauthorized
      store.dispatch(authActions.logout());
    }

    return Promise.reject(error);
  }
);

export default api;
