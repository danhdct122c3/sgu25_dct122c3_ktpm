import { useSelector } from "react-redux";
import { Navigate, Link } from "react-router-dom";
import { selectUser, selectToken } from "../store/auth";

const ProtectedRoute = ({ children, requiredRole = null }) => {
  const user = useSelector(selectUser);
  const token = useSelector(selectToken);

  console.log("🔒 ProtectedRoute - User:", user);
  console.log("🔒 ProtectedRoute - Token exists:", !!token);
  console.log("🔒 ProtectedRoute - Required Role:", requiredRole);

  // QUAN TRỌNG: Kiểm tra xem user đã đăng nhập chưa
  // Nếu không có token HOẶC không có user => chuyển về trang login ĐÚNG
  if (!token || !user) {
    console.log("❌ No token or user found - redirecting to login");
    
    // Redirect về đúng trang login dựa vào required role
    // Nếu yêu cầu ADMIN/MANAGER role => redirect đến /admin/login
    // Nếu không có role hoặc yêu cầu USER => redirect đến /login
    const loginPath = requiredRole === "ADMIN" || requiredRole === "MANAGER" 
      ? "/admin/login" 
      : "/login";
    
    return <Navigate to={loginPath} replace />;
  }

  // QUAN TRỌNG: Kiểm tra role nếu được yêu cầu (chỉ có 2 role: USER và ADMIN)
  if (requiredRole) {
    // Lấy role từ JWT token (scope field)
    // Backend trả về "ROLE_USER" hoặc "ROLE_ADMIN" trong scope
    const userRole = user.scope || '';
    
    console.log("👤 User role from token:", userRole);
    console.log("🎯 Required role:", requiredRole);
    
    // Chuẩn hóa role để so sánh
    // Hỗ trợ cả 2 format: "ADMIN" và "ROLE_ADMIN"
    const normalizedUserRole = userRole.replace('ROLE_', '');
    const normalizedRequiredRole = requiredRole.replace('ROLE_', '');
    
    const hasRequiredRole = normalizedUserRole === normalizedRequiredRole;
    
    console.log("✅ Has required role:", hasRequiredRole);
    
    // Nếu KHÔNG có quyền => hiển thị trang lỗi
    if (!hasRequiredRole) {
      console.log("❌ Access denied - insufficient permissions");
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
              Bạn không có quyền <span className="font-bold">{requiredRole}</span> để truy cập vào trang này.
            </p>
            <div className="text-sm text-gray-500 mb-6 bg-gray-100 p-3 rounded">
              <p>Quyền của bạn: <span className="font-semibold text-blue-600">{normalizedUserRole}</span></p>
              <p>Quyền yêu cầu: <span className="font-semibold text-red-600">{normalizedRequiredRole}</span></p>
            </div>
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

  console.log("✅ Access granted - rendering protected content");
  return children;
};

export default ProtectedRoute;