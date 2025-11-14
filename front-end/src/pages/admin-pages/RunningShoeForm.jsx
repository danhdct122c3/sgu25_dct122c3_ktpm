import React, { useState, useEffect } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@radix-ui/react-label";
import ImagesUpload from "./ImagesUpload";
import axios from "axios";
import api from "@/config/axios";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { useNavigate } from "react-router-dom";
import { Separator } from "@/components/ui/separator";
import { Link, useParams } from "react-router-dom";
import { ArrowLeft, User, Mail, Phone, Home, ShoppingCart, Tag } from "lucide-react";
const schema = z.object({
  name: z.string().min(2, { message: "Required" }),
  price: z.number().min(10, { message: "Required" }),
  description: z.string().min(10),
  status: z
    .enum(["true", "false"], {
      invalid_type_error: "Status must be a boolean",
      required_error: "Status is required",
    })
    .transform((value) => value === "true"),
  fakePrice: z.number().min(10, { message: "Required" }),
  gender: z.string(),
  category: z.string(),
  brandId: z.number(),
});

export default function RunningShoeForm() {
  const navigate = useNavigate();
  // Xác định base path theo vai trò để điều hướng đúng portal
  const token = localStorage.getItem("token");
  let basePrefix = "/admin";
  try {
    const scope = token ? JSON.parse(atob(token.split(".")[1]))?.scope : null;
    const normalizedRole = (scope || "").replace("ROLE_", "");
    basePrefix = normalizedRole === "MANAGER" ? "/manager" : normalizedRole === "STAFF" ? "/staff" : "/admin";
  } catch (_) {}
  const [brands, setBrands] = useState([]);
  const [sizes, setSizes] = useState([]);

  const [formData, setFormData] = useState({
    selectedFiles: [],
    images: [],
    variants: [],
  });

  // Fetch sizes from API
  useEffect(() => {
    const fetchSizes = async () => {
      try {
        const response = await api.get("/shoes/sizes");
        if (response.data.flag && response.data.result) {
          setSizes(response.data.result);
        }
      } catch (error) {
        console.error("Failed to fetch sizes:", error);
      }
    };
    fetchSizes();
  }, []);

  // Fetch brands on component mount
  useEffect(() => {
    const fetchBrands = async () => {
      try {
        console.log("=== FETCHING BRANDS ===");
        
        // Try multiple possible API endpoints
        const possibleEndpoints = ["/brands", "/api/brands", "/v1/brands", "/api/v1/brands"];
        let response = null;
        let workingEndpoint = null;
        
        for (const endpoint of possibleEndpoints) {
          try {
            console.log(`Trying endpoint: ${endpoint}`);
            response = await api.get(endpoint);
            workingEndpoint = endpoint;
            console.log(`✅ SUCCESS with ${endpoint}:`, response.data);
            break;
          } catch (err) {
            console.log(`❌ FAILED ${endpoint}:`, err.response?.status || err.message);
          }
        }
        
        if (response && response.data) {
          let brandsData = [];
          
          // Try multiple response formats
          if (response.data.result && Array.isArray(response.data.result)) {
            brandsData = response.data.result;
          } else if (response.data.data && Array.isArray(response.data.data)) {
            brandsData = response.data.data;
          } else if (Array.isArray(response.data)) {
            brandsData = response.data;
          }
          
          console.log("Processed brands data:", brandsData);
          
          if (brandsData.length > 0) {
            setBrands(brandsData);
          } else {
            console.warn("No brands found, using fallback");
            setBrands([
              {brandId: 1, brandName: "Nike"},
              {brandId: 2, brandName: "Adidas"},
              {brandId: 3, brandName: "Puma"},
              {brandId: 4, brandName: "Converse"},
              {brandId: 5, brandName: "Vans"},
              {brandId: 6, brandName: "New Balance"},
              {brandId: 7, brandName: "Reebok"},
              {brandId: 8, brandName: "Fila"},
              {brandId: 9, brandName: "Skechers"},
              {brandId: 10, brandName: "Under Armour"}
            ]);
          }
        } else {
          throw new Error("No valid response from any brands endpoint");
        }
        
      } catch (error) {
        console.error("=== BRANDS FETCH ERROR ===");
        console.error("Error details:", error);
        console.error("========================");
        
        // Always set fallback brands
        console.log("Setting fallback brands");
        setBrands([
          {brandId: 1, brandName: "Nike"},
          {brandId: 2, brandName: "Adidas"},
          {brandId: 3, brandName: "Puma"},
          {brandId: 4, brandName: "Converse"},
          {brandId: 5, brandName: "Vans"},
          {brandId: 6, brandName: "New Balance"},
          {brandId: 7, brandName: "Reebok"},
          {brandId: 8, brandName: "Fila"},
          {brandId: 9, brandName: "Skechers"},
          {brandId: 10, brandName: "Under Armour"}
        ]);
      }
    };

    fetchBrands();
  }, []);

  const handleImagesSelect = (files) => {
    setFormData((prev) => ({
      ...prev,
      selectedFiles: files,
    }));
  };

  // Test function để debug API
  const testAPI = async () => {
    try {
      console.log("=== TESTING API CONNECTION ===");
      
      // Check authentication status
      const token = localStorage.getItem("token");
      console.log("Current token:", token ? "EXISTS" : "MISSING");
      
      // Test GET brands
      const brandsResponse = await api.get("/brands");
      console.log("Brands API works:", brandsResponse.status);
      
      // Test 1: Minimal data với string enum và variants
      const testData1 = {
        name: "Test Product",
        price: 100000,
        description: "Test Description",
        status: true,
        fakePrice: 50000,
        gender: "MALE",  // Try different enum value
        category: "BASKETBALL",  // Try different enum value
        brandId: 1,
        images: [],
        variants: [{
          size: "42",
          color: "Đen", 
          quantity: 10
        }]
      };
      
      console.log("=== TEST 1: Different enums ===");
      console.log(JSON.stringify(testData1, null, 2));
      
      try {
        const test1Response = await api.post("/shoes", testData1);
        console.log("✅ Test 1 worked:", test1Response.status);
      } catch (error) {
        console.log("❌ Test 1 failed:", error.response?.data?.message);
      }
      
      // Test 2: Minimal data với required fields only + variants
      const testData2 = {
        name: "Test Product 2",
        price: 100000,
        brandId: 1,
        variants: [{
          size: "40",
          color: "Trắng", 
          quantity: 5
        }]
      };
      
      console.log("=== TEST 2: Minimal required fields ===");
      console.log(JSON.stringify(testData2, null, 2));
      
      try {
        const test2Response = await api.post("/shoes", testData2);
      console.log("✅ Test 2 worked:", test2Response.status);
      } catch (error) {
        console.log("❌ Test 2 failed:", error.response?.data?.message);
      }
      
    } catch (error) {
      console.error("=== TEST API ERROR ===");
      console.error("Status:", error.response?.status);
      console.error("Response Data:", JSON.stringify(error.response?.data, null, 2));
      console.error("Request had auth header?", error.config?.headers?.Authorization ? "YES" : "NO");
      console.error("======================");
    }
  };

  const uploadImages = async (files) => {
    const uploadPreset = "capstone-preset";
    const cloudName = "dgdxkeu53";
    const images = [];

    try {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        const formData = new FormData();
        formData.append("file", file);
        formData.append("upload_preset", uploadPreset);

        const response = await axios.post(
          `https://api.cloudinary.com/v1_1/${cloudName}/image/upload`,
          formData
        );

        images.push({
          url: response.data.secure_url,
          publicId: response.data.public_id,
        });
      }
      return images;
    } catch (error) {
      console.error("Error uploading images:", error);
      throw error;
    }
  };

  const handleVariantChange = (variantData) => {
    setFormData((prev) => {
      const existingVariantIndex = prev.variants.findIndex(
        (v) => v.sizeId === variantData.sizeId
      );

      let newVariants;
      if (existingVariantIndex >= 0) {
        newVariants = [...prev.variants];
        newVariants[existingVariantIndex] = variantData;
      } else {
        newVariants = [...prev.variants, variantData];
      }

      return {
        ...prev,
        variants: newVariants,
      };
    });
  };

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      name: "",
      price: 10,
      description: "Mô tả giày đang được cập nhật",
      status: "true",
      fakePrice: 20,
      gender: "UNISEX",
      category: "RUNNING",
      brandId: 1,
    },
  });

  const onSubmit = async (data) => {
    const toastId = toast.loading("Creating product...");

    try {
      // Validation trước khi upload
      if (!data.name || data.name.trim() === "") {
        toast.update(toastId, {
          render: "Tên sản phẩm không được để trống",
          type: "error",
          isLoading: false,
          autoClose: 3000,
        });
        return;
      }

      if (!formData.selectedFiles || formData.selectedFiles.length === 0) {
        toast.update(toastId, {
          render: "Vui lòng chọn ít nhất một hình ảnh",
          type: "error",
          isLoading: false,
          autoClose: 3000,
        });
        return;
      }

      const uploadedImages = await uploadImages(formData.selectedFiles);
      
      // Đảm bảo có ít nhất 1 variant (backend requirement)
      let variants = formData.variants || [];
      if (variants.length === 0) {
        // Tạo default variant nếu không có
        variants = [{
          size: "42",  // Default size
          color: "Đen", // Default color  
          quantity: 10  // Default quantity
        }];
      }
      
      // Tạo finalData với cấu trúc rõ ràng
      const finalData = {
        name: data.name.trim(),
        price: Number(data.price),
        description: data.description || "Mô tả giày đang được cập nhật",
        status: data.status === "true" || data.status === true,
        fakePrice: Number(data.fakePrice),
        gender: data.gender,
        category: data.category,
        brandId: Number(data.brandId),
        images: uploadedImages,
        variants: variants,
      };

      console.log("=== FINAL DATA TO SEND ===");
      console.log(JSON.stringify(finalData, null, 2));
      console.log("=========================");

      try {
        const response = await api.post("/shoes", finalData);
        if (response.status === 200) {
          console.log("Product created successfully:", response.data);

          toast.update(toastId, {
            render: "Product created successfully",
            type: "success",
            isLoading: false,
            autoClose: 3000,
          });

          setTimeout(() => {
            navigate(`${basePrefix}/manage-shoes`);
          }, 4000);
        }
      } catch (error) {
        console.error("=== API ERROR DETAILS ===");
        console.error("Status:", error.response?.status);
        console.error("Status Text:", error.response?.statusText);
        console.error("Response Headers:", error.response?.headers);
        console.error("Response Data (full):", JSON.stringify(error.response?.data, null, 2));
        console.error("Request URL:", error.config?.url);
        console.error("Request Method:", error.config?.method);
        console.error("Request Headers:", error.config?.headers);
        console.error("Request Data (sent):", JSON.stringify(finalData, null, 2));
        console.error("Full Error Object:", error);
        console.error("========================");

        // Hiển thị lỗi chi tiết cho user
        let errorMessage = "Lỗi tạo sản phẩm";
        
        if (error.response?.status === 400) {
          if (error.response?.data?.message) {
            errorMessage = `Lỗi dữ liệu: ${error.response.data.message}`;
          } else if (error.response?.data?.error) {
            errorMessage = `Lỗi: ${error.response.data.error}`;
          } else {
            errorMessage = "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại thông tin.";
          }
        } else if (error.response?.status === 401) {
          errorMessage = "Không có quyền truy cập. Vui lòng đăng nhập lại.";
        } else if (error.response?.status === 403) {
          errorMessage = "Không có quyền thực hiện thao tác này.";
        } else if (error.response?.status >= 500) {
          errorMessage = "Lỗi server. Vui lòng thử lại sau.";
        }

        toast.update(toastId, {
          render: errorMessage,
          type: "error",
          isLoading: false,
          autoClose: 5000,
        });
      }
    } catch (error) {
      console.error("=== FORM SUBMISSION ERROR ===");
      console.error("Error:", error);
      console.error("=============================");
      
      // Xử lý lỗi upload ảnh hoặc lỗi validation
      let errorMessage = "Có lỗi xảy ra";
      
      if (error.message && error.message.includes("uploading")) {
        errorMessage = "Lỗi upload hình ảnh. Vui lòng thử lại.";
      } else if (error.message) {
        errorMessage = error.message;
      }

      toast.update(toastId, {
        render: errorMessage,
        type: "error",
        isLoading: false,
        autoClose: 5000,
      });
    }
  };

  return (
    <div className="p-6 max-w-full h-screen mx-auto bg-white rounded-lg shadow-md">
      <ToastContainer
        position="top-right"
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition="Bounce"
      />
      <div className="flex items-center justify-between mb-4">
      <Link to={"/admin/manage-shoes"}>
        <Button variant="ghost" className="flex items-center gap-2">
          <ArrowLeft className="h-4 w-4" />
          Quay lại
        </Button>
        </Link>
        <h1 className="text-4xl font-bold">Chi tiết đơn hàng</h1>
        <div className="w-24" /> {/* Spacer for alignment */}
      </div>
      
      {/* Debug Test Button */}
      <div className="mb-4 p-4 bg-gray-100 rounded">
        <h2 className="text-lg font-semibold mb-2">🔧 Debug Tools</h2>
        <div className="space-x-2 mb-2">
          <button 
            type="button"
            onClick={testAPI}
            className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          >
            Test Different Data
          </button>
          <button 
            type="button"
            onClick={() => {
              const token = localStorage.getItem("token");
              console.log("Token check:", token ? "✅ EXISTS" : "❌ MISSING");
              if (token) {
                try {
                  const decoded = JSON.parse(atob(token.split('.')[1]));
                  console.log("Token payload:", decoded);
                } catch(e) {
                  console.log("Invalid token format");
                }
              }
            }}
            className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600"
          >
            Check Auth Status
          </button>
          <button 
            type="button"
            onClick={async () => {
              try {
                console.log("=== CHECKING BRANDS ===");
                const response = await api.get("/brands");
                console.log("Available brands:", response.data);
                console.log("======================");
              } catch (error) {
                console.error("Error fetching brands:", error);
              }
            }}
            className="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600"
          >
            Check Brands
          </button>
        </div>
        <div className="text-sm text-yellow-700 bg-yellow-100 p-2 rounded mb-2">
          <strong>💡 Backend Requirements:</strong>
          <ul className="list-disc list-inside mt-1">
            <li>Cần ít nhất 1 hình ảnh</li>
            <li>Cần ít nhất 1 variant (size, color, quantity)</li>
            <li>Nếu không có variant, hệ thống sẽ tự tạo default variant</li>
          </ul>
        </div>
        <div className="text-sm text-yellow-700 bg-yellow-100 p-2 rounded">
          <strong>💡 Backend Debug Tip:</strong> Kiểm tra backend console/logs để xem chi tiết validation errors. 
          "Invalid Message Key" thường do enum validation hoặc i18n config.
        </div>
        <span className="text-sm text-gray-600 block mt-2">
          Kiểm tra console để xem kết quả test
        </span>
      </div>
      
      <h1 className="text-2xl font-bold mb-4">Thêm Sản phẩm</h1>
      <form onSubmit={handleSubmit(onSubmit)} className="space-y-2">
        <div className="space-y-2">
          <Label htmlFor="name">Tên sản phẩm</Label>
          <Input
            label="Tên sản phẩm"
            type="text"
            placeholder="Tên sản phẩm"
            {...register("name")}
          />
          {errors.name?.message && (
            <p className="text-red-600">{errors.name?.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="price">Giá</Label>
          <Input
            label="Giá"
            type="number"
            placeholder="Giá "
            {...register("price", { valueAsNumber: true })}
          />
          {errors.price?.message && (
            <p className="text-red-600">{errors.price?.message}</p>
          )}
        </div>
        <div className="space-y-2">
          
          <Label htmlFor="description">Mô tả</Label>
          <Input
            label="Mô tả"
            type="text"
            placeholder="Mô tả"
            {...register("description")}
          />
          {errors.description?.message && (
            <p className="text-red-600">{errors.description?.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="status">Trạng thái:</Label>
          <select
            {...register("status")}
            className="block w-1/4 rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
          >
            <option value="">Chọn trạng thái</option>
            <option value="true">Kích hoạt</option>
            <option value="false">Không kích hoạt</option>
          </select>
          {errors.status?.message && (
            <p className="text-red-600">{errors.status?.message}</p>
          )}
        </div>
        <div className="space-y-2">
          <Label htmlFor="fakePrice">Giá niêm yết</Label> {/* Thay "Giá ảo" thành "Giá" */}
          <Input
            label="Giá"
            type="number"
            placeholder="Giá"
            {...register("fakePrice", { valueAsNumber: true })}
          />
          {errors.fakePrice?.message && (
            <p className="text-red-600">{errors.fakePrice?.message}</p>
          )}
        </div>
        <div className="flex justify-between">
          <div className="space-y-2">
            <Label htmlFor="gender">Giới tính:</Label>
            <select
              {...register("gender")}
              className="block rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
            >
              <option value="WOMEN">Nữ</option>
              <option value="MAN">Nam</option>
              <option value="UNISEX">Unisex</option>
            </select>
            {errors.gender?.message && (
              <p className="text-red-600">{errors.gender?.message}</p>
            )}
          </div>
          <div className="space-y-2">
            <Label htmlFor="category">Danh mục:</Label>
            <select
              {...register("category")}
              className="block rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
            >
              <option value="RUNNING">Giày chạy bộ</option>
              <option value="SPORT">Giày thể thao</option>
              <option value="CASUAL">Giày thường</option>
            </select>
            {errors.category?.message && (
              <p className="text-red-600">{errors.category?.message}</p>
            )}
          </div>
          <div className="space-y-2">
            <Label htmlFor="brandId">Thương hiệu:</Label>
            <select
              {...register("brandId", { valueAsNumber: true })}
              className="block rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
              style={{ minHeight: '40px', fontSize: '16px' }}
            >
              <option value="">-- Chọn thương hiệu --</option>
              {brands && brands.length > 0 ? (
                brands.map((brand) => (
                  <option key={brand.brandId || brand.id} value={brand.brandId || brand.id}>
                    {brand.brandName || brand.name || `Brand ${brand.brandId || brand.id}`}
                  </option>
                ))
              ) : (
                <>
                  <option value="" disabled>Loading brands...</option>
                  <option value="1">Nike (fallback)</option>
                  <option value="2">Adidas (fallback)</option>
                  <option value="3">Puma (fallback)</option>
                  <option value="4">Reebok (fallback)</option>
                </>
              )}
            </select>
            {errors.brandId?.message && (
              <p className="text-red-600">{errors.brandId?.message}</p>
            )}
            <div className="text-xs text-gray-500 p-2 bg-yellow-50 rounded">
              🔍 Debug: {brands.length} brands loaded | Check console for API details
              <br />
              Current brands: {JSON.stringify(brands.map(b => ({
                id: b.brandId || b.id, 
                name: b.brandName || b.name
              })))}
            </div>
          </div>
        </div>
        <ImagesUpload onImagesSelect={handleImagesSelect} />
        
        {/* Variants Input Form - Chọn size và số lượng */}
        <div className="space-y-4 p-4 border rounded-lg bg-gray-50">
          <h3 className="text-lg font-semibold">🦶 Kho giày</h3>
          <div className="text-sm text-gray-600 mb-3">
            Chọn size giày và nhập số lượng tồn kho:
          </div>
          
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium mb-1">Size giày:</label>
              <select 
                className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                onChange={(e) => {
                  const size = e.target.value;
                  if (size) {
                    handleVariantChange({
                      sizeId: parseInt(size),
                      stockQuantity: 10 // default quantity
                    });
                  }
                }}
              >
                <option value="">Chọn size</option>
                {sizes.map((size) => (
                  <option key={size.id} value={size.id}>
                    US {size.sizeNumber}
                  </option>
                ))}
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium mb-1">Số lượng:</label>
              <input 
                type="number" 
                min="1" 
                placeholder="Nhập số lượng"
                className="w-full p-2 border rounded focus:ring-2 focus:ring-blue-500"
                onChange={(e) => {
                  // Update quantity cho size đã chọn
                  if (formData.variants.length > 0) {
                    const lastVariant = formData.variants[formData.variants.length - 1];
                    handleVariantChange({
                      ...lastVariant,
                      stockQuantity: parseInt(e.target.value) || 0
                    });
                  }
                }}
              />
            </div>
          </div>
          
          <div className="text-xs text-gray-500 p-2 bg-blue-50 rounded">
            📦 Current variants: {JSON.stringify(formData.variants)}
          </div>
        </div>

        <Separator className="my-4" />
        <Button type="submit">Gửi</Button>
      </form>
    </div>
  );
}
