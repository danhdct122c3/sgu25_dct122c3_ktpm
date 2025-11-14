import StaffAside from "@/components/staff-com/StaffAside";
import { Outlet } from "react-router-dom";

export default function RootLayoutStaff() {
  return (
    <div className="grid gap-4 sm:grid-cols-12 grid-cols-1 h-screen">
      <div className="sm:col-span-2 sm:block hidden h-screen">
        <StaffAside />
      </div>
      <div className="sm:col-span-10">
        <>
          <Outlet />
        </>
      </div>
    </div>
  );
}