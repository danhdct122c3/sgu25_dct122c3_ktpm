import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Switch } from "@/components/ui/switch";
import { Separator } from "@/components/ui/separator";
import api from "@/config/axios";

const schema = z.object({
  username: z.string().min(2, { message: "Yêu cầu nhập tên người dùng" }),
  fullName: z.string().min(2, { message: "Yêu cầu nhập họ tên" }),
  email: z.string().email({ message: "Email không hợp lệ" }),
  phone: z.string().min(10, { message: "Số điện thoại phải có ít nhất 10 số" }),
  address: z.string().min(5, { message: "Địa chỉ quá ngắn" }),
  isActive: z.boolean(),
});

export default function UpdateMemberForm({ userId }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
  } = useForm({
    resolver: zodResolver(schema),
  });

  const [user, setUser] = useState({});
  const [isActive, setIsActive] = useState(false);
  const [isChanged, setIsChanged] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    if (dialogOpen) {
      const fetchUser = async () => {
        try {
          // Dùng endpoint /profile?username để lấy user bằng username
          const { data } = await api.get(`users/profile`, {
            params: { username: userId }
          });
          setUser(data.result);
          setIsActive(data.result.active);
          reset(data.result);
          setValue("isActive", data.result.active);
        } catch (error) {
          console.error("Lỗi khi tải thông tin người dùng:", error);
          toast.error("Không thể tải thông tin người dùng.");
        }
      };
      fetchUser();
    }
  }, [userId, reset, setValue, dialogOpen]);

  const handleSwitchChange = (checked) => {
    setIsActive(checked);
    setValue("isActive", checked);
    setIsChanged(checked !== user.active);
  };

  const onSubmit = async (formData) => {
    const updateData = { 
      fullName: formData.fullName,
      email: formData.email,
      phone: formData.phone,
      address: formData.address,
      isActive 
    };
    const toastId = toast.loading("Đang cập nhật...");

    try {
      console.log("🔍 Updating user with username:", userId);
      console.log("📦 Update data:", updateData);
      
      const response = await api.put(`/users/update/${userId}`, updateData, {
        headers: { "Content-Type": "application/json" },
      });

      if (response.status === 200) {
        toast.update(toastId, {
          render: "Cập nhật thành công!",
          type: "success",
          isLoading: false,
          autoClose: 2000,
        });
        setDialogOpen(false);
        // Reload page để cập nhật danh sách
        window.location.reload();
      } else {
        throw new Error(response.data.message || "Cập nhật thất bại");
      }
    } catch (error) {
      console.error("Lỗi khi cập nhật người dùng:", error);
      toast.update(toastId, {
        render: error.response?.data?.message || "Lỗi trong quá trình cập nhật.",
        type: "error",
        isLoading: false,
        autoClose: 2000,
      });
    }
  };

  return (
    <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
      <ToastContainer position="top-right" autoClose={2000} />
      <DialogTrigger asChild>
        <Button variant="outline">Chỉnh sửa</Button>
      </DialogTrigger>
      <DialogContent className="w-full max-w-2xl mx-auto">
        <DialogHeader>
          <DialogTitle>Chỉnh sửa thông tin thành viên</DialogTitle>
          <DialogDescription>
            Thực hiện thay đổi trạng thái tài khoản. Nhấn lưu khi bạn hoàn tất.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="username">Tên người dùng</Label>
            <Input
              id="username"
              name="username"
              defaultValue={user.username}
              {...register("username")}
              disabled
              className="bg-gray-100"
            />
            {errors.username?.message && <p className="text-red-600 text-sm">{errors.username?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="fullName">Họ và tên</Label>
            <Input
              id="fullName"
              name="fullName"
              defaultValue={user.fullName}
              {...register("fullName")}
              placeholder="Nhập họ và tên"
            />
            {errors.fullName?.message && <p className="text-red-600 text-sm">{errors.fullName?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <Input
              id="email"
              name="email"
              type="email"
              defaultValue={user.email}
              {...register("email")}
              placeholder="Nhập email"
            />
            {errors.email?.message && <p className="text-red-600 text-sm">{errors.email?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="phone">Số điện thoại</Label>
            <Input
              id="phone"
              name="phone"
              defaultValue={user.phone}
              {...register("phone")}
              placeholder="Nhập số điện thoại"
            />
            {errors.phone?.message && <p className="text-red-600 text-sm">{errors.phone?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="address">Địa chỉ</Label>
            <Input
              id="address"
              name="address"
              defaultValue={user.address}
              {...register("address")}
              placeholder="Nhập địa chỉ"
            />
            {errors.address?.message && <p className="text-red-600 text-sm">{errors.address?.message}</p>}
          </div>

          <div className="space-y-2">
            <div className="flex items-center space-x-2">
              <Switch
                id="isActive"
                checked={isActive}
                onCheckedChange={handleSwitchChange}
              />
              <Label htmlFor="isActive">Trạng thái hoạt động</Label>
            </div>
          </div>

          <Separator className="my-4" />

          <DialogFooter>
            <Button type="submit">
              Lưu thay đổi
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}