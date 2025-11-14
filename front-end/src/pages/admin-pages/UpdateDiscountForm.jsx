import{ useState, useEffect } from "react";
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
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { ToastContainer, toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import api from "@/config/axios";
import { useSelector } from "react-redux";
import { selectUser } from "@/store/auth";

const schema = z
  .object({
    code: z.string().min(2, { message: "Code is required" }),
    discountType: z.string(),
    percentage: z.number().nullable().optional(), // Cho phép null hoặc không nhập
    fixedAmount: z.number().nullable().optional(), // Cho phép null hoặc không nhập
    minimumOrderAmount: z.number().nullable().default(0), // Nếu không nhập thì mặc định là 0
    description: z.string().min(5, { message: "Description must be at least 5 characters" }),
    startDate: z.string().refine((value) => !isNaN(Date.parse(value)), {
      message: "Start date must be a valid date",
    }),
    endDate: z.string().refine((value) => !isNaN(Date.parse(value)), {
      message: "End date must be a valid date",
    }),
    active: z
      .enum(["true", "false"], {
        invalid_type_error: "Status must be a boolean",
        required_error: "Status is required",
      }),
    categories: z.array(z.string()).optional(), // Danh mục áp dụng
  });

export default function UpdateDiscountForm({ discountId }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
  } = useForm({
    resolver: zodResolver(schema),
  });

  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const user = useSelector(selectUser);
  const role = (user?.scope || "").replace("ROLE_", "");
  const basePrefix = role === "MANAGER" ? "/manager" : role === "STAFF" ? "/staff" : "/admin";

  const [discount, setDiscount] = useState({});

  useEffect(() => {
    const fetchDiscount = async () => {
      setIsLoading(true);
      try {
        const { data } = await api.get(`discounts/${discountId}`);
        reset({
          ...data.result,
          percentage: data.result.percentage ?? null, // Gán null nếu không có giá trị
          fixedAmount: data.result.fixedAmount ?? null, // Gán null nếu không có giá trị
          minimumOrderAmount: data.result.minimumOrderAmount ?? 0, // Gán 0 nếu không có giá trị
          description: data.result.description ?? "", // Thêm trường description
          startDate: new Date(data.result.startDate).toISOString().slice(0, 16), // Chuyển đổi startDate sang ISO 8601
          endDate: new Date(data.result.endDate).toISOString().slice(0, 16), // Chuyển đổi endDate sang ISO 8601
        });
      } catch (error) {
        console.error("Error fetching discount:", error);
        toast.error("Failed to fetch discount details.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchDiscount();
  }, [discountId, reset]);

  const onSubmit = async (data) => {
    setIsLoading(true);
    const toastId = toast.loading("Updating discount...");
    try {
      console.log("Form data submitted:", data);

      // Gửi yêu cầu API với dữ liệu `data`
      const mappedCategories = data.categories?.map((category) => category.toUpperCase());
      const updatedData = {
        ...data,
        categories: mappedCategories,
      };

      const response = await api.put(`discounts/${discountId}`, {
        ...updatedData,
        percentage: updatedData.percentage === "" ? null : updatedData.percentage, // Nếu trống, set là null
        fixedAmount: updatedData.fixedAmount === "" ? null : updatedData.fixedAmount, // Nếu trống, set là null
        minimumOrderAmount: updatedData.minimumOrderAmount === "" ? 0 : updatedData.minimumOrderAmount, // Nếu trống, set là 0
        startDate: new Date(updatedData.startDate).toISOString(), // Chuyển startDate sang ISO 8601
        endDate: new Date(updatedData.endDate).toISOString(), // Chuyển endDate sang ISO 8601
      });

      if (response.status === 200 && response.data.flag) {
        toast.update(toastId, {
          render: "Discount updated successfully!",
          type: "success",
          isLoading: false,
          autoClose: 2000,
        });
        setTimeout(() => navigate(`${basePrefix}/discount-management`), 2000);
      } else {
        toast.update(toastId, {
          render: response.data.message || "Failed to update discount.",
          type: "error",
          isLoading: false,
          autoClose: 2000,
        });
      }
    } catch (error) {
      console.error("Error updating discount:", error);
      toast.update(toastId, {
        render: "An error occurred while updating the discount.",
        type: "error",
        isLoading: false,
        autoClose: 2000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog className="min-h-screen">
      <ToastContainer 
      position="top-right" 
      hideProgressBar={false} 
      newestOnTop={false} 
      closeOnClick 
      rtl={false} 
      pauseOnFocusLoss 
      draggable 
      pauseOnHover 
      theme="light" />
      <DialogTrigger asChild>
        <Button variant="outline" className="hover:bg-slate-950 hover:text-white">
          Chỉnh Sửa
        </Button>
      </DialogTrigger>
      <DialogContent className="w-full max-w-4xl mx-auto max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chỉnh sửa thông tin mã giảm giá</DialogTitle>
          <DialogDescription>
            Thay đổi thông tin giảm giá ở đây. Nhấn Lưu khi bạn đã nhập xong các thay đổi.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="code">Tên mã</Label>
            <Input
              id="code"
              name="code"
              defaultValue={discount.code}
              {...register("code")}
            />
            {errors.code?.message && <p className="text-red-600 text-sm">{errors.code?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Mô tả</Label>
            <Input
              id="description"
              name="description"
              defaultValue={discount.description}
              {...register("description")}
            />
            {errors.description?.message && <p className="text-red-600 text-sm">{errors.description?.message}</p>}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Label htmlFor="active">Trạng thái</Label>
              <select {...register("active")} className="w-full p-2 border rounded-md">
                <option value="true">Hoạt động</option>
                <option value="false">Tắt</option>
              </select>
              {errors.active?.message && <p className="text-red-600 text-sm">{errors.active?.message}</p>}
            </div>

            <div>
              <Label htmlFor="discountType">Loại giảm giá</Label>
              <select {...register("discountType")} className="w-full p-2 border rounded-md">
                <option value="PERCENTAGE">Phần trăm</option>
                <option value="FIXED_AMOUNT">Số tiền cố định</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Label htmlFor="percentage">Phần trăm</Label>
              <Input
                id="percentage"
                name="percentage"
                type="number"
                step="0.01"
                min="0"
                max="100"
                defaultValue={discount.percentage ?? null}
                {...register("percentage", { valueAsNumber: true })}
                disabled={discount.discountType !== "PERCENTAGE"}
              />
            </div>

            <div>
              <Label htmlFor="fixedAmount">Số tiền cố định</Label>
              <Input
                id="fixedAmount"
                name="fixedAmount"
                type="number"
                step="1000"
                min="0"
                defaultValue={discount.fixedAmount ?? null}
                {...register("fixedAmount", { valueAsNumber: true })}
                disabled={discount.discountType !== "FIXED_AMOUNT"}
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Label htmlFor="minimumOrderAmount">Số tiền đơn hàng tối thiểu</Label>
              <Input
                id="minimumOrderAmount"
                name="minimumOrderAmount"
                type="number"
                step="1000"
                min="0"
                defaultValue={discount.minimumOrderAmount ?? 0}
                {...register("minimumOrderAmount", { valueAsNumber: true })}
              />
            </div>

            <div>
              <Label htmlFor="usageLimit">Giới hạn sử dụng</Label>
              <Input
                id="usageLimit"
                name="usageLimit"
                type="number"
                placeholder="Để trống nếu không giới hạn"
                defaultValue={discount.usageLimit ?? null}
                {...register("usageLimit", { valueAsNumber: true })}
              />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Label htmlFor="startDate">Ngày bắt đầu</Label>
              <Input
                id="startDate"
                type="datetime-local"
                defaultValue={discount.startDate}
                {...register("startDate")}
              />
            </div>

            <div>
              <Label htmlFor="endDate">Ngày kết thúc</Label>
              <Input
                id="endDate"
                type="datetime-local"
                defaultValue={discount.endDate}
                {...register("endDate")}
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="categories">Danh mục áp dụng</Label>
            <div className="flex flex-wrap gap-4">
              <div>
                <input
                  type="checkbox"
                  id="category-sports"
                  value="sports"
                  {...register("categories")}
                />
                <Label htmlFor="category-sports">Giày thể thao</Label>
              </div>
              <div>
                <input
                  type="checkbox"
                  id="category-running"
                  value="running"
                  {...register("categories")}
                />
                <Label htmlFor="category-running">Giày chạy bộ</Label>
              </div>
              <div>
                <input
                  type="checkbox"
                  id="category-casual"
                  value="casual"
                  {...register("categories")}
                />
                <Label htmlFor="category-casual">Giày thường</Label>
              </div>
            </div>
            <p className="text-gray-500 text-sm">Để trống nếu áp dụng cho tất cả danh mục</p>
          </div>

          <div className="flex flex-col sm:flex-row justify-end gap-4 mt-6">
            <Button type="button" onClick={() => setIsDialogOpen(false)} className="px-6 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600">Hủy</Button>
            <Button type="submit" className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">Lưu thay đổi</Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
