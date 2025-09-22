import { Link } from "react-router-dom";

const UnauthorizedPage = () => {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="text-center max-w-md mx-auto p-8 bg-white rounded-lg shadow-lg">
        <div className="mb-6">
          <svg className="mx-auto h-20 w-20 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636m12.728 12.728L18.364 5.636M5.636 18.364l12.728-12.728" />
          </svg>
        </div>
        <h1 className="text-3xl font-bold text-red-600 mb-4">
          403 - Truy cập bị từ chối
        </h1>
        <p className="text-gray-600 mb-6 text-lg">
          Bạn không có quyền truy cập vào trang này.
        </p>
        <p className="text-gray-500 mb-8">
          Trang này yêu cầu quyền quản trị viên (Admin). Vui lòng đăng nhập bằng tài khoản Admin hoặc liên hệ quản trị viên để được cấp quyền.
        </p>
        <div className="space-y-3">
          <Link 
            to="/"
            className="block w-full px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors font-medium"
          >
            🏠 Về trang chủ
          </Link>
          <Link 
            to="/login"
            className="block w-full px-6 py-3 bg-green-500 text-white rounded-lg hover:bg-green-600 transition-colors font-medium"
          >
            🔑 Đăng nhập lại
          </Link>
        </div>
      </div>
    </div>
  );
};

export default UnauthorizedPage;