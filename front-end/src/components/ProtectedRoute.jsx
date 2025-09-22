import { useSelector } from "react-redux";
import { Navigate, Link } from "react-router-dom";
import { selectUser, selectToken } from "../store/auth";

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const user = useSelector(selectUser);
  const token = useSelector(selectToken);

  console.log("ProtectedRoute - User:", user);
  console.log("ProtectedRoute - Token:", token);
  console.log("ProtectedRoute - Required Role:", requiredRole);

  // Kiểm tra xem user đã đăng nhập chưa
  if (!token || !user) {
    console.log("No token or user, redirecting to login");
    return <Navigate to="/login" replace />;
  }

  // Kiểm tra role nếu được yêu cầu
  if (requiredRole) {
    // Kiểm tra nhiều trường có thể chứa role
    const userRoles = user.scope ? user.scope.split(' ') : 
                     user.roles ? user.roles :
                     user.authorities ? user.authorities :
                     [];
    
    console.log("User roles:", userRoles);
    
    // Kiểm tra xem user có role ADMIN không
    const hasAdminRole = userRoles.includes(requiredRole) || 
                        userRoles.includes('ADMIN') ||
                        userRoles.includes('ROLE_ADMIN') ||
                        user.role === 'ADMIN' ||
                        user.role === requiredRole;
    
    console.log("Has required role:", hasAdminRole);
    
    if (!hasAdminRole) {
      return (
        <div className="flex items-center justify-center min-h-screen bg-gray-50">
          <div className="text-center max-w-md mx-auto p-8 bg-white rounded-lg shadow-lg">
            <div className="mb-6">
              <svg className="mx-auto h-16 w-16 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-2.464 0L4.35 16.5c-.77.833.192 2.5 1.732 2.5z" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold text-red-600 mb-4">
              🚫 Không có quyền truy cập
            </h1>
            <p className="text-gray-600 mb-6">
              Bạn không có quyền Admin để truy cập vào trang quản trị này.
            </p>
            <div className="space-y-3">
              <Link 
                to="/"
                className="block w-full px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
              >
                🏠 Về trang chủ
              </Link>
              <button 
                onClick={() => window.history.back()}
                className="block w-full px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600 transition-colors"
              >
                ⬅️ Quay lại
              </button>
            </div>
            <p className="text-sm text-gray-500 mt-4">
              Liên hệ quản trị viên nếu bạn cần quyền truy cập.
            </p>
          </div>
        </div>
      );
    }
  }

  return children;
};

export default ProtectedRoute;