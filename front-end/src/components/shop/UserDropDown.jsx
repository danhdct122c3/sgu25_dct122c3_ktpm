import { Link } from "react-router-dom";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { CircleUser } from "lucide-react";
import { useSelector, useDispatch } from "react-redux";
import { selectUser } from "../../store/auth"
import { authActions } from "../../store";


export default function UserDropDown() {
  const dispatch = useDispatch();
  const user = useSelector(selectUser);

  const name = user ? user.sub : null;
  const userRole = user ? user.scope : null;
  const normalizedRole = (userRole || "").replace("ROLE_", "");
  

  const handleLogout = () => {
    dispatch(authActions.logout());
  }

  return (
    <div className="cursor-pointer">
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <CircleUser className="w-8 h-8" />
        </DropdownMenuTrigger>
        <DropdownMenuContent className="-translate-x-1/3">
          <DropdownMenuLabel>{name ? "Chào mừng, " + name : "Hồ sơ"}</DropdownMenuLabel>
          <DropdownMenuSeparator />
          {/* Hiển thị link portal theo vai trò đăng nhập */}
          {user && normalizedRole === "ADMIN" && (
            <div>
              <DropdownMenuItem asChild>
                <Link to={"/admin"} className="cursor-pointer">Quản trị viên</Link>
              </DropdownMenuItem>
            </div>
          )}
          {user && normalizedRole === "MANAGER" && (
            <div>
              <DropdownMenuItem asChild>
                <Link to={"/manager"} className="cursor-pointer">Quản lý</Link>
              </DropdownMenuItem>
            </div>
          )}
          {user && normalizedRole === "STAFF" && (
            <div>
              <DropdownMenuItem asChild>
                <Link to={"/staff"} className="cursor-pointer">Nhân viên</Link>
              </DropdownMenuItem>
            </div>
          )}
          {user ? (
            <div>
              <DropdownMenuItem asChild>
                <Link to={"/profile/me"} className="cursor-pointer">Hồ sơ</Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild>
                <Link to={"/order-history"} className="cursor-pointer">Lịch sử đơn hàng</Link>
              </DropdownMenuItem>
              {/* Đăng xuất xử lý trực tiếp, không cần Link */}
              <DropdownMenuItem onClick={handleLogout}>Đăng xuất</DropdownMenuItem>
            </div>
          ) : (
            <div>
              <DropdownMenuItem asChild>
                <Link to={"/login"} className="cursor-pointer">Đăng nhập</Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild>
                <Link to={"/register"} className="cursor-pointer">Đăng ký</Link>
              </DropdownMenuItem>
            </div>
          )}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
