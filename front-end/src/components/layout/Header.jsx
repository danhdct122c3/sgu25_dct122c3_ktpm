import React from "react";
import { Link } from "react-router-dom";
import { useSelector } from "react-redux";
import UserDropDown from "../shop/UserDropDown";
import ShoppingBag from "../shop/ShoppingBag";
import {
  NavigationMenu,
  NavigationMenuIndicator,
  NavigationMenuItem,
  NavigationMenuList,
  NavigationMenuViewport,
} from "@/components/ui/navigation-menu";

export default function Header() {
  // Đã đăng nhập khi có cả token & user trong Redux
  const isLoggedIn = useSelector((s) => Boolean(s.auth?.token && s.auth?.user));

  return (
    <header className="fixed top-0 w-full z-50 bg-white shadow">
      <div className="container flex items-center justify-between mx-auto gap-5 h-24">
        <Link to={"/"}>
          <h1 className="text-red-800 text-4xl italic font-bold">SuperTeam</h1>
        </Link>

        <NavigationMenu>
          <NavigationMenuList className="hidden lg:flex gap-6">
            <NavigationMenuItem>
              <Link to={"/"} className="text-orange-500">Trang chủ</Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to={"/shoes"}>Sản phẩm</Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to={"/terms"}>Điều khoản và điều kiện</Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to={"/privacy"}>Chính sách bảo mật</Link>
            </NavigationMenuItem>
            <NavigationMenuItem>
              <Link to={"/faq"}>Câu hỏi thường gặp</Link>
            </NavigationMenuItem>
          </NavigationMenuList>
          <NavigationMenuIndicator />
          <NavigationMenuViewport />
        </NavigationMenu>

        <div className="flex items-center gap-5">
          <UserDropDown />
          {/* ✅ Chỉ hiển thị icon giỏ khi đã đăng nhập */}
          {isLoggedIn && (
            <Link to="/cart">
              <ShoppingBag />
            </Link>
          )}
        </div>
      </div>
    </header>
  );
}