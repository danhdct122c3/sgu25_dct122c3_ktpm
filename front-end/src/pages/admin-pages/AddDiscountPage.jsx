import  { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useForm } from "react-hook-form";
import { useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { ToastContainer, toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import api from "@/config/axios";
import "react-toastify/dist/ReactToastify.css";
import { Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

const schema = z.object({
  code: z.string().min(2),
  description: z.string().min(5),
  discountType: z.enum(["FIXED_AMOUNT", "PERCENTAGE"]),
  percentage: z.number().nullable().optional(),
  fixedAmount: z.number().nullable().optional(),
  minimumOrderAmount: z.number().nullable().default(0), // Nếu không nhập thì mặc định là 0
  usageLimit: z.number().nullable().optional(), // Thêm trường usageLimit
  startDate: z.string(),
  endDate: z.string(),
  active: z.enum(["true", "false"]),
  categories: z.array(z.string()).optional(), // Thêm trường categories
  shoeIds: z.array(z.string()).optional(), // use string ids for consistency
}).refine((data) => {
  if (data.discountType === "PERCENTAGE") {
    return data.percentage != null && data.fixedAmount == null;
  } else {
    return data.fixedAmount != null && data.percentage == null;
  }
}, {
  message: "Please provide either percentage or fixed amount based on discount type",
  path: ["discountType"]
});

export default function AddDiscountForm() {
  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
    setValue
  } = useForm({
    resolver: zodResolver(schema),
  });

  const [isLoading, setIsLoading] = useState(false);
  const [categories, setCategories] = useState([]);
  const [shoes, setShoes] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);
  // normalize shoe ids as strings
  const [selectedShoes, setSelectedShoes] = useState([]);
  const navigate = useNavigate();
  const userRole = localStorage.getItem("token") ? JSON.parse(atob(localStorage.getItem("token").split(".")[1]))?.scope : null;
  const normalizedRole = (userRole || "").replace("ROLE_", "");
  const basePrefix = normalizedRole === "MANAGER" ? "/manager" : normalizedRole === "STAFF" ? "/staff" : "/admin";

  useEffect(() => {
    // Keep behavior: if FIXED_AMOUNT, ensure fixedAmount default
    if (watch("discountType") === "FIXED_AMOUNT") {
      setValue("fixedAmount", 0);
    } else {
      setValue("fixedAmount", null);
    }
  }, [watch("discountType")]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const shoesResponse = await api.get("/shoes");
        if (shoesResponse.data.result && Array.isArray(shoesResponse.data.result)) {
          setShoes(shoesResponse.data.result);
        }
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    setCategories([
      { value: "SPORT", name: "Giày thể thao" },
      { value: "RUNNING", name: "Giày chạy bộ" },
      { value: "CASUAL", name: "Giày thường" },
    ]);
  }, []);

  // handlers for categories/shoes
  const toggleCategory = (value) => {
    setSelectedCategories(prev => {
      if (prev.includes(value)) return prev.filter(c => c !== value);
      return [...prev, value];
    });
  };

  const addShoeByValue = (value) => {
    // value is shoe.id as string
    if (!selectedShoes.includes(value)) setSelectedShoes(prev => [...prev, value]);
  };

  const removeShoe = (value) => {
    setSelectedShoes(prev => prev.filter(id => id !== value));
  };

  const onSubmit = async (data) => {
    setIsLoading(true);
    const toastId = toast.loading("Adding discount...");
    try {
      const startDate = new Date(data.startDate);
      const endDate = new Date(data.endDate);

      if (startDate >= endDate) {
        throw new Error("Start date must be before end date");
      }

      const formattedData = {
        code: data.code,
        description: data.description,
        discountType: data.discountType,
        active: data.active === "true",
        startDate: startDate.toISOString(),
        endDate: endDate.toISOString(),
        minimumOrderAmount: data.minimumOrderAmount || 0,
        usageLimit: data.usageLimit || null,
        percentage: data.discountType === "PERCENTAGE" ? data.percentage : null,
        fixedAmount: data.discountType === "FIXED_AMOUNT" ? data.fixedAmount : null,
        categories: selectedCategories.length > 0 ? selectedCategories : null,
        shoeIds: selectedShoes.length > 0 ? selectedShoes : null,
      };

      const response = await api.post("/discounts", formattedData);

      if (response.status === 200 || response.status === 201) {
        toast.update(toastId, {
          render: "Discount added successfully!",
          type: "success",
          isLoading: false,
          autoClose: 2000,
        });
        setTimeout(() => navigate(`${basePrefix}/discount-management`), 1200);
      } else {
        throw new Error(response.data.message || "Failed to add discount");
      }
    } catch (error) {
      console.error("Error details:", error.response?.data);
      toast.update(toastId, {
        render: error.response?.data?.message || error.message || "Failed to add discount",
        type: "error",
        isLoading: false,
        autoClose: 2000,
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-5xl mx-auto bg-white rounded-lg shadow-md"> {/* constrain width and center */}
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
      <div className="flex items-center justify-between mb-4">
        <Link to={`${basePrefix}/discount-management`}>
          <Button variant="ghost" className="flex items-center gap-2">
            <ArrowLeft className="h-4 w-4" />
            Quay Lại
          </Button>
        </Link>
        <h1 className="text-2xl md:text-4xl font-bold text-center">Thêm Mã Giảm Giá</h1>
        <div className="w-24" />
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <div className="space-y-2">
          <Label htmlFor="code" className="block text-gray-700">Tên mã</Label>
          <Input id="code" name="code" placeholder="Nhập tên mã" className="w-full" {...register("code")} />
          {errors.code?.message && <p className="text-red-600 text-sm">{errors.code?.message}</p>}
        </div>

        <div className="space-y-2">
          <Label htmlFor="description" className="block text-gray-700">Mô tả</Label>
          <Input id="description" name="description" placeholder="Nhập mô tả" className="w-full" {...register("description")} />
          {errors.description?.message && <p className="text-red-600 text-sm">{errors.description?.message}</p>}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6"> {/* responsive pairs */}
          <div className="space-y-2">
            <Label htmlFor="active" className="block text-gray-700">Trạng thái</Label>
            <select {...register("active")} className="w-full p-2 border rounded-md">
              <option value="true">Hoạt động</option>
              <option value="false">Tắt</option>
            </select>
            {errors.active?.message && <p className="text-red-600 text-sm">{errors.active?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="discountType" className="block text-gray-700">Loại giảm giá</Label>
            <select {...register("discountType")} className="w-full p-2 border rounded-md">
              <option value="FIXED_AMOUNT">Số tiền cố định</option>
              <option value="PERCENTAGE">Phần trăm</option>
            </select>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <Label htmlFor="percentage" className="block text-gray-700">Phần trăm</Label>
            <Input id="percentage" name="percentage" type="number" disabled={watch("discountType") !== "PERCENTAGE"} {...register("percentage", {
              setValueAs: v => watch("discountType") === "PERCENTAGE" ? Number(v) : null
            })} />
            {errors.percentage?.message && <p className="text-red-600 text-sm">{errors.percentage?.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="fixedAmount" className="block text-gray-700">Số tiền cố định</Label>
            <Input id="fixedAmount" name="fixedAmount" type="number" disabled={watch("discountType") !== "FIXED_AMOUNT"} {...register("fixedAmount", {
              setValueAs: v => watch("discountType") === "FIXED_AMOUNT" ? Number(v) : null
            })} />
            {errors.fixedAmount?.message && <p className="text-red-600 text-sm">{errors.fixedAmount?.message}</p>}
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <Label htmlFor="minimumOrderAmount" className="block text-gray-700">Số tiền đơn hàng tối thiểu</Label>
            <Input type="number" id="minimumOrderAmount" {...register("minimumOrderAmount", { valueAsNumber: true })} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="usageLimit" className="block text-gray-700">Giới hạn sử dụng</Label>
            <Input type="number" id="usageLimit" placeholder="Để trống nếu không giới hạn" {...register("usageLimit", { valueAsNumber: true })} />
          </div>
        </div>

        <div className="space-y-2">
          <Label className="block text-gray-700">Danh mục áp dụng</Label>
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
            {categories.map((category) => (
              <div key={category.value} className="flex items-center space-x-2">
                <Checkbox id={`category-${category.value}`} checked={selectedCategories.includes(category.value)} onCheckedChange={(checked) => toggleCategory(category.value)} />
                <Label htmlFor={`category-${category.value}`} className="text-sm">{category.name}</Label>
              </div>
            ))}
          </div>
          <p className="text-sm text-gray-500">Để trống nếu áp dụng cho tất cả danh mục</p>
        </div>

        <div className="space-y-2">
          <Label className="block text-gray-700">Sản phẩm cụ thể</Label>
          <Select onValueChange={(value) => { addShoeByValue(value); }}>
            <SelectTrigger>
              <SelectValue placeholder="Chọn sản phẩm" />
            </SelectTrigger>
            <SelectContent>
              {shoes.filter(shoe => !selectedShoes.includes(String(shoe.id))).map((shoe) => (
                <SelectItem key={shoe.id} value={shoe.id.toString()}>
                  {shoe.name} - {shoe.price?.toLocaleString()}đ
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          {selectedShoes.length > 0 && (
            <div className="mt-2">
              <p className="text-sm text-gray-600 mb-2">Sản phẩm đã chọn:</p>
              <div className="flex flex-wrap gap-2">
                {selectedShoes.map((shoeId) => {
                  const shoe = shoes.find(s => s.id === Number(shoeId));
                  return shoe ? (
                    <div key={shoeId} className="bg-blue-100 text-blue-800 px-2 py-1 rounded-md text-sm flex items-center gap-1">
                      {shoe.name}
                      <button type="button" onClick={() => removeShoe(shoeId)} className="text-blue-600 hover:text-blue-800">×</button>
                    </div>
                  ) : null;
                })}
              </div>
            </div>
          )}
          <p className="text-sm text-gray-500">Để trống nếu áp dụng cho tất cả sản phẩm</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="space-y-2">
            <Label htmlFor="startDate" className="block text-gray-700">Ngày bắt đầu</Label>
            <Input id="startDate" type="datetime-local" {...register("startDate", { validate: value => { const startDate = new Date(value); return startDate >= new Date() || "Start date must be in the future"; } })} />
          </div>

          <div className="space-y-2">
            <Label htmlFor="endDate" className="block text-gray-700">Ngày kết thúc</Label>
            <Input id="endDate" type="datetime-local" {...register("endDate", { validate: (value, formValues) => { const startDate = new Date(formValues.startDate); const endDate = new Date(value); return endDate > startDate || "End date must be after start date"; } })} />
          </div>
        </div>

        <div className="flex flex-col sm:flex-row justify-end gap-4 mt-6">{/* responsive button layout */}
          <Button type="submit" disabled={isLoading} className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">{isLoading ? "Saving..." : "Save Discount"}</Button>
        </div>
      </form>
    </div>
  );
}
