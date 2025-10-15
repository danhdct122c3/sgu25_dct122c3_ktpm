// src/store/auth-slice.js
import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { jwtDecode } from "jwt-decode";
import api from "@/config/axios";

// --- helper: khôi phục user từ token ---
const getUserFromToken = () => {
  const token = localStorage.getItem("token");
  if (!token) return null;
  try {
    const decoded = jwtDecode(token);
    if (decoded.exp * 1000 > Date.now()) {
      return decoded;
    }
    localStorage.removeItem("token");
    return null;
  } catch (e) {
    console.error("Invalid token:", e);
    localStorage.removeItem("token");
    return null;
  }
};

const initialState = {
  user: getUserFromToken(),
  token: localStorage.getItem("token") || null,
  isLoading: false,
  error: null,
};

// --- (khuyến nghị) logout qua thunk nếu muốn gọi API ---
export const logoutAsync = createAsyncThunk("auth/logoutAsync", async () => {
  const token = localStorage.getItem("token");
  try {
    if (token) {
      await api.post("/auth/logout", { token });
    }
  } catch (e) {
    console.warn("Logout API failed (ignored):", e?.message || e);
  } finally {
    localStorage.removeItem("token");
  }
});

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    loginStart: (state) => {
      state.isLoading = true;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      state.isLoading = false;
      state.token = action.payload;           // token dạng JWT string
      state.user = jwtDecode(action.payload); // decode lấy payload
      state.error = null;
      localStorage.setItem("token", action.payload);
    },
    loginFailure: (state, action) => {
      state.isLoading = false;
      state.error = action.payload;
      state.user = null;
      state.token = null;
      localStorage.removeItem("token");
    },
    // Logout sync (không gọi API ở reducer)
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.error = null;
      localStorage.removeItem("token");
    },
    updateUser: (state, action) => {
      state.user = { ...state.user, ...action.payload };
    },
  },
  extraReducers: (builder) => {
    builder.addCase(logoutAsync.fulfilled, (state) => {
      state.user = null;
      state.token = null;
      state.error = null;
    });
  },
});

// --- selectors ---
export const selectUser = (state) => state.auth.user;
export const selectToken = (state) => state.auth.token;
export const selectIsLoading = (state) => state.auth.isLoading;
export const selectError = (state) => state.auth.error;

// ✅ ĐÃ đăng nhập khi có cả token & user
export const selectIsLoggedIn = (state) =>
  Boolean(state.auth?.token && state.auth?.user);

// --- actions & reducer ---
export const { loginStart, loginSuccess, loginFailure, logout, updateUser } =
  authSlice.actions;

export default authSlice;