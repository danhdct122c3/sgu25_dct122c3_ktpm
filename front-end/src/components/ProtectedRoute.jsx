import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import { selectUser, selectToken } from "../store/auth";
import PropTypes from "prop-types";

const normalizeRole = (role) => (role || "").replace("ROLE_", "").toUpperCase();

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const user = useSelector(selectUser);
  const token = useSelector(selectToken);

  console.log(" ProtectedRoute - User:", user);
  console.log(" ProtectedRoute - Token exists:", !!token);
  console.log(" ProtectedRoute - Required Role:", requiredRole);

  // QUAN TRỌNG: Kiểm tra xem user đã đăng nhập chưa
  // Nếu không có token HOẶC không có user => chuyển về trang login ĐÚNG
  if (!token || !user) {
    console.log(" No token or user found - redirecting to login");
    
    // Redirect về đúng trang login dựa vào required role
    // Nếu yêu cầu ADMIN/MANAGER role => redirect đến /admin/login
    // Nếu không có role hoặc yêu cầu USER => redirect đến /login
    const loginPath = ["ADMIN", "MANAGER", "STAFF"].includes(requiredRole) 
      ? "/admin/login" 
      : "/login";
    
    return <Navigate to={loginPath} replace />;
  }

  // QUAN TRỌNG: Kiểm tra role nếu được yêu cầu (chỉ có 2 role: USER và ADMIN)
  if (requiredRole) {
    const userRole = normalizeRole(user.scope);
    const requiredRoles = Array.isArray(requiredRole)
      ? requiredRole.map(normalizeRole)
      : [normalizeRole(requiredRole)];

    const hasRequiredRole = requiredRoles.includes(userRole);
    console.log("👤 User role:", userRole, " Required:", requiredRoles, " ok:", hasRequiredRole);

    if (!hasRequiredRole) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  console.log(" Access granted - rendering protected content");
  return children;
};

ProtectedRoute.propTypes = {
  children: PropTypes.node.isRequired,
  requiredRole: PropTypes.string,
};

export default ProtectedRoute;