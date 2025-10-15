// App.js
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, Outlet } from 'react-router-dom';

import AdminAside from './components/admin-com/AdminAside';
import WelcomeAdmin from './pages/admin-pages/WelcomeAdmin';
import DiscountManagement from './pages/admin-pages/DiscountManagement';
import MemberManagemant from './pages/admin-pages/MemberManagemant';
import MemberOrderHistory from './pages/admin-pages/MemberOrderHistory';

// Layout chứa sidebar + chỗ render nội dung
function AdminLayout() {
  return (
    <>
      <AdminAside />
      {/* tuỳ UI của bạn: padding / margin-left nếu Aside cố định */}
      <main style={{ padding: 16 }}>
        <Outlet />
      </main>
    </>
  );
}

function NotFound() {
  return <div style={{ padding: 24 }}>404 — Page not found</div>;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* redirect "/" -> "/admin" */}
        <Route path="/" element={<Navigate to="/admin" replace />} />

        {/* nhóm route admin */}
        <Route path="/admin" element={<AdminLayout />}>
          <Route index element={<WelcomeAdmin />} />
          <Route path="discount-management" element={<DiscountManagement />} />
          <Route path="member-order-history" element={<MemberOrderHistory />} />
          <Route path="account-management" element={<MemberManagemant />} />
        </Route>

        {/* catch-all */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
}