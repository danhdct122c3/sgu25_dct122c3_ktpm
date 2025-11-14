import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Shield } from "lucide-react";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import api from "@/config/axios";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { selectIsLoading, selectError } from "../../store/auth";
import { authActions } from "@/store";
import { jwtDecode } from "jwt-decode";

const schema = z.object({
  username: z.string().min(1, { message: "Tên đăng nhập là bắt buộc" }),
  password: z.string().min(8, { message: "Mật khẩu ít nhất 8 ký tự" }),
});

function AdminLogin() {
  const navigate = useNavigate();
  const isLoading = useSelector(selectIsLoading);
  const error = useSelector(selectError);
  const dispatch = useDispatch();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
  });

  const handleLogin = async (data) => {
    dispatch(authActions.loginStart());

    try {
      const response = await api.post("/auth/token", data);
      const token = response.data.result.token;

      // Decode token để check role
      const decodedToken = jwtDecode(token);
      const userRole = decodedToken.scope;

      console.log("🔑 Admin Login - Token decoded:", decodedToken);
      console.log("👤 Admin Login - User role:", userRole);

      // Chuẩn hóa role (xử lý cả "ADMIN" và "ROLE_ADMIN")
      const normalizedRole = userRole?.replace("ROLE_", "");
      console.log("✅ Admin Login - Normalized role:", normalizedRole);

      // Kiểm tra role - cho phép ADMIN, MANAGER và STAFF
      if (normalizedRole !== "ADMIN" && normalizedRole !== "MANAGER" && normalizedRole !== "STAFF") {
        console.log("❌ Admin Login - Access denied for role:", normalizedRole);
        alert("⚠️ Chỉ có Admin/Manager/Staff mới có thể đăng nhập tại đây!\nVui lòng sử dụng trang đăng nhập cho khách hàng.");
        dispatch(authActions.loginFailure());
        return;
      }

      console.log("✅ Admin Login - Access granted!");
      localStorage.setItem("token", token);
      dispatch(authActions.loginSuccess(token));

      // Redirect dựa vào role
      if (normalizedRole === "ADMIN") {
        console.log("↪️ Redirecting to /admin");
        navigate("/admin");
      } else if (normalizedRole === "MANAGER") {
        console.log("↪️ Redirecting to /manager");
        navigate("/manager");
      } else if (normalizedRole === "STAFF") {
        console.log("↪️ Redirecting to /staff");
        navigate("/staff");
      }
    } catch (err) {
      console.error("❌ Admin Login Error:", err);
      console.error("Error response:", err.response?.data);
      alert("❌ Tên đăng nhập hoặc mật khẩu không đúng");
      dispatch(authActions.loginFailure());
    }
  };

  return (
    <div
      className="flex items-center justify-center min-h-screen"
      style={{
        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
      }}
    >
      <div className="w-full max-w-md p-6">
        <div className="bg-white rounded-2xl shadow-2xl overflow-hidden">
          {/* Header with gradient */}
          <div className="bg-gradient-to-r from-purple-600 to-indigo-600 p-8 text-center">
            <div className="inline-flex items-center justify-center w-20 h-20 bg-white rounded-full mb-4">
              <Shield className="w-10 h-10 text-purple-600" />
            </div>
            <h1 className="text-3xl font-bold text-white mb-2">
              Quản Trị Viên
            </h1>
            <p className="text-purple-100 text-sm">
              SuperTeam Shoe Store - Admin Portal
            </p>
          </div>

          <form onSubmit={handleSubmit(handleLogin)}>
            <Card className="w-full border-0 shadow-none">
              <CardHeader className="pb-4 pt-6">
                <CardDescription className="text-center text-gray-600 text-base">
                  🔐 Đăng nhập với tài khoản Admin/Manager
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-5">
                <div className="space-y-2">
                  <Label htmlFor="username" className="text-gray-700 font-medium">
                    Tên đăng nhập
                  </Label>
                  <Input
                    id="username"
                    type="text"
                    placeholder="Nhập tên đăng nhập"
                    className="border-gray-300 focus:border-purple-500 focus:ring-purple-500 h-11"
                    {...register("username")}
                  />
                  {errors.username && (
                    <p className="text-red-500 text-sm flex items-center gap-1">
                      <span>⚠️</span>
                      {errors.username.message}
                    </p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="password" className="text-gray-700 font-medium">
                    Mật khẩu
                  </Label>
                  <Input
                    id="password"
                    type="password"
                    placeholder="Nhập mật khẩu"
                    className="border-gray-300 focus:border-purple-500 focus:ring-purple-500 h-11"
                    {...register("password")}
                  />
                  {errors.password && (
                    <p className="text-red-500 text-sm flex items-center gap-1">
                      <span>⚠️</span>
                      {errors.password.message}
                    </p>
                  )}
                </div>

                {/* Security Notice */}
                <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 mt-4">
                  <p className="text-xs text-blue-800 text-center">
                    🛡️ <strong>Bảo mật:</strong> Trang này chỉ dành cho quản trị viên.
                    <br />
                    Mọi hoạt động đều được ghi log.
                  </p>
                </div>
              </CardContent>

              <CardFooter className="flex flex-col space-y-4 pb-8">
                <Button
                  type="submit"
                  disabled={isLoading}
                  className="w-full bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-700 hover:to-indigo-700 text-white font-semibold h-12 rounded-lg shadow-md transition-all duration-200 transform hover:scale-[1.02]"
                >
                  {isLoading ? (
                    <span className="flex items-center gap-2">
                      <svg
                        className="animate-spin h-5 w-5"
                        xmlns="http://www.w3.org/2000/svg"
                        fill="none"
                        viewBox="0 0 24 24"
                      >
                        <circle
                          className="opacity-25"
                          cx="12"
                          cy="12"
                          r="10"
                          stroke="currentColor"
                          strokeWidth="4"
                        ></circle>
                        <path
                          className="opacity-75"
                          fill="currentColor"
                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                        ></path>
                      </svg>
                      Đang xác thực...
                    </span>
                  ) : (
                    <span className="flex items-center justify-center gap-2">
                      <Shield className="w-5 h-5" />
                      Đăng nhập
                    </span>
                  )}
                </Button>

                {error && (
                  <div className="bg-red-50 border border-red-200 rounded-lg p-3 w-full">
                    <p className="text-red-600 text-sm text-center">{error}</p>
                  </div>
                )}

                {/* Divider */}
                <div className="relative w-full">
                  <div className="absolute inset-0 flex items-center">
                    <span className="w-full border-t border-gray-300" />
                  </div>
                  <div className="relative flex justify-center text-xs uppercase">
                    <span className="bg-white px-2 text-gray-500">hoặc</span>
                  </div>
                </div>

                {/* Link to user login */}
                <a
                  href="/login"
                  className="text-center text-sm text-gray-600 hover:text-purple-600 transition-colors duration-200 font-medium"
                >
                  👤 Đăng nhập với tài khoản khách hàng
                </a>

                {/* Back to home */}
                <a
                  href="/"
                  className="text-center text-xs text-gray-500 hover:text-gray-700 transition-colors duration-200"
                >
                  ← Quay lại trang chủ
                </a>
              </CardFooter>
            </Card>
          </form>
        </div>

        {/* Footer info */}
        <p className="text-center text-white text-xs mt-6 opacity-80">
          © 2025 SuperTeam Shoe Store. All rights reserved.
        </p>
      </div>
    </div>
  );
}

export default AdminLogin;
