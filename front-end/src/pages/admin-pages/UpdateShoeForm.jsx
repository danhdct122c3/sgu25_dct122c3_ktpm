import React, { useState } from "react";
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
import { useEffect } from "react";
import api from "@/config/axios";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Separator } from "@/components/ui/separator";
import VariantShoeOnUpdateForm from "./VariantShoeOnUpdateForm";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import ImagesUpload from "./ImagesUpload";
import { getImageUrl } from "@/utils/imageHelper";


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

export default function UpdateShoeForm({ shoeId }) {
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    setValue,
    watch,
  } = useForm({
    resolver: zodResolver(schema),
  });

  const [shoe, setShoe] = React.useState({});
  const [updatedVariants, setUpdatedVariants] = React.useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [brands, setBrands] = useState([]);
  const [allSizes, setAllSizes] = useState([]);
  const [availableSizes, setAvailableSizes] = useState([]);
  const [selectedNewSize, setSelectedNewSize] = useState("");
  const [isAddingSizeDialogOpen, setIsAddingSizeDialogOpen] = useState(false);
  const [selectedImages, setSelectedImages] = useState([]);
  const [currentImages, setCurrentImages] = useState([]);
  const navigate = useNavigate();

  // Watch form values
  const currentBrandId = watch("brandId");
  const currentGender = watch("gender");
  const currentCategory = watch("category");
  const currentStatus = watch("status");

  const handleVariantChange = (variantId, quantity) => {
    setUpdatedVariants((prev) => ({
      ...prev,
      [variantId]: quantity,
    }));
  };

  const handleAddNewSize = async () => {
    if (!selectedNewSize) {
      toast.error("Vui lòng chọn kích cỡ");
      return;
    }

    try {
      // Find the selected size object
      const sizeObj = allSizes.find(s => s.id === parseInt(selectedNewSize));
      
      console.log("🔍 Selected size ID:", selectedNewSize);
      console.log("🔍 Size object found:", sizeObj);
      
      if (!sizeObj) {
        toast.error("Kích cỡ không hợp lệ");
        return;
      }

      // Create SKU for new variant
      const sku = `${shoe.name.replace(/\s+/g, "-")}-${sizeObj.sizeNumber}`;
      
      const newVariant = {
        sizeId: sizeObj.id,  // ← QUAN TRỌNG: Gửi sizeId, không phải sizeNumber
        sku: sku,
        stockQuantity: 0,
      };

      console.log("📤 Sending variant to API:", newVariant);
      console.log("📤 Shoe ID:", shoeId);
      
      // Use correct endpoint format
      const response = await api.post(`shoes/${shoeId}/variants`, newVariant);
      
      console.log("✅ API Response:", response);
      
      if (response.status === 200 || response.status === 201) {
        toast.success(`Đã thêm kích cỡ ${sizeObj.sizeNumber}!`);
        
        // Refresh shoe data to get the new variant
        const { data } = await api.get(`shoes/${shoeId}`);
        setShoe(data.result);
        
        // Add the new variant to updatedVariants
        const newVariantId = response.data.result?.id || response.data.id;
        setUpdatedVariants((prev) => ({
          ...prev,
          [newVariantId]: 0,
        }));

        // Update available sizes
        setAvailableSizes((prev) => 
          prev.filter((s) => s.id !== parseInt(selectedNewSize))
        );
        
        setSelectedNewSize("");
        setIsAddingSizeDialogOpen(false);
      }
    } catch (error) {
      console.error("❌ Error adding new size:", error);
      console.error("❌ Error details:", error.response?.data);
      toast.error(error.response?.data?.message || "Lỗi khi thêm kích cỡ mới");
    }
  };

  const handleImagesSelect = (files) => {
    setSelectedImages(files);
  };

  const handleRemoveImage = async (imageId) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ảnh này?")) {
      try {
        await api.delete(`/shoe-images/${imageId}`);
        setCurrentImages((prev) => prev.filter((img) => img.id !== imageId));
        toast.success("Đã xóa ảnh!");
      } catch (error) {
        console.error("Error removing image:", error);
        toast.error("Lỗi khi xóa ảnh");
      }
    }
  };

  const uploadImages = async (files) => {
    console.log("📤 Uploading", files.length, "images to backend...");
    
    try {
      const formData = new FormData();
      files.forEach((file) => {
        formData.append("files", file);
        console.log("  - Adding file:", file.name, "Size:", file.size, "Type:", file.type);
      });

      const response = await api.post("/images/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      if (response.data && response.data.result) {
        console.log("✅ Images uploaded successfully:", response.data.result);
        return response.data.result; // Array of image URLs
      } else {
        throw new Error("Invalid response from server");
      }
    } catch (error) {
      console.error("❌ Error uploading images:", error);
      throw error;
    }
  };

  // Fetch brands from API
  useEffect(() => {
    const fetchBrands = async () => {
      try {
        const response = await api.get("/brands");
        if (response.data.result && Array.isArray(response.data.result)) {
          setBrands(response.data.result);
        } else if (Array.isArray(response.data)) {
          setBrands(response.data);
        }
      } catch (error) {
        console.error("Failed to fetch brands:", error);
        // Fallback brands nếu API fail
        setBrands([
          { brandId: 1, brandName: "Nike" },
          { brandId: 2, brandName: "Adidas" },
          { brandId: 3, brandName: "Puma" },
          { brandId: 4, brandName: "Reebok" }
        ]);
      }
    };
    fetchBrands();
  }, []);

  // Fetch all available sizes from database
  useEffect(() => {
    const fetchAllSizes = async () => {
      try {
        const response = await api.get("/shoes/sizes");
        if (response.data.result && Array.isArray(response.data.result)) {
          setAllSizes(response.data.result);
        } else if (Array.isArray(response.data)) {
          setAllSizes(response.data);
        }
      } catch (error) {
        console.error("Failed to fetch sizes:", error);
      }
    };
    fetchAllSizes();
  }, []);

  // Debug: Watch brandId changes
  useEffect(() => {
    console.log("🔍 Current brandId value:", currentBrandId);
    console.log("🔍 Shoe brandId:", shoe.brandId);
    console.log("🔍 Brands list:", brands);
  }, [currentBrandId, shoe.brandId, brands]);

  useEffect(() => {
    const fetchShoe = async () => {
      try {
        const { data } = await api.get(`shoes/${shoeId}`);
        console.log("📦 Shoe data loaded:", data.result);
        
        // IMPORTANT: Backend doesn't return brandId directly
        // We need to extract it from the shoe object
        // Check if brand info exists in different formats
        let extractedBrandId = null;
        
        if (data.result.brand && data.result.brand.id) {
          extractedBrandId = data.result.brand.id;
        } else if (data.result.brand && data.result.brand.brandId) {
          extractedBrandId = data.result.brand.brandId;
        } else if (data.result.brandId) {
          extractedBrandId = data.result.brandId;
        }
        
        console.log("🔍 Extracted Brand ID:", extractedBrandId);
        console.log("🔍 Full shoe object:", data.result);
        
        setShoe(data.result);
        
        // Prepare form data with correct status format and brandId
        const formData = {
          ...data.result,
          brandId: extractedBrandId, // Add brandId explicitly
          status: data.result.status ? "true" : "false", // Convert boolean to string
        };
        
        console.log("✅ Form data prepared:", formData);
        
        // Reset form with prepared data - this will set all values including brandId
        reset(formData);

        // Set current images
        if (data.result.images && Array.isArray(data.result.images)) {
          setCurrentImages(data.result.images);
        }

        const initialVariants = {};
        data.result.variants?.forEach((variant) => {
          initialVariants[variant.id] = variant.stockQuantity;
        });
        setUpdatedVariants(initialVariants);

        // Calculate available sizes (sizes not yet added to this product)
        if (allSizes.length > 0 && data.result.variants) {
          const existingSizeNumbers = data.result.variants.map((variant) => {
            const sizeNum = variant.sku.split("-").pop();
            return parseInt(sizeNum);
          });
          
          console.log("📏 Existing sizes:", existingSizeNumbers);
          console.log("📏 All sizes:", allSizes.map(s => s.sizeNumber));
          
          const available = allSizes.filter(
            (size) => !existingSizeNumbers.includes(size.sizeNumber)
          );
          
          console.log("✅ Available sizes:", available.map(s => s.sizeNumber));
          setAvailableSizes(available);
        }
      } catch (error) {
        console.error("❌ Error fetching shoe:", error);
        toast.error("Lỗi khi tải dữ liệu sản phẩm");
      }
    };
    fetchShoe();
  }, [shoeId, reset, allSizes]);

  const onSubmit = async (data) => {
    setIsLoading(true);
    const toastId = toast.loading("Updating product...");
    try {
      // Upload new images if any
      let uploadedImageUrls = [];
      if (selectedImages && selectedImages.length > 0) {
        try {
          uploadedImageUrls = await uploadImages(selectedImages);
        } catch (error) {
          console.error("Error uploading images:", error);
          toast.update(toastId, {
            render: "Failed to upload images",
            type: "error",
            isLoading: false,
            autoClose: 2000,
          });
          setIsLoading(false);
          return;
        }
      }

      // Prepare images array (existing + new)
      const images = [
        ...currentImages.map((img) => ({ url: img.url })),
        ...uploadedImageUrls.map((url) => ({ url })),
      ];

      const formData = {
        ...data,
        images: images.length > 0 ? images : undefined,
        variants: shoe.variants.map((variant) => ({
          variantId: variant.id,
          stockQuantity: updatedVariants[variant.id] ?? variant.stockQuantity,
        })),
      };
      console.log(formData);
      try {
        const response = await api.put(`shoes/${shoeId}`, formData);
        if (response.status === 200) {
          console.log("Product updated successfully:", response.data);
          
          // Refresh shoe data from server to get updated images
          try {
            const refreshResponse = await api.get(`shoes/${shoeId}`);
            if (refreshResponse.data && refreshResponse.data.result) {
              const updatedShoe = refreshResponse.data.result;
              
              // Update current images with fresh data from server
              setCurrentImages(updatedShoe.images || []);
              setSelectedImages([]); // Clear selected images
              
              console.log("✅ Images refreshed from server:", updatedShoe.images);
            }
          } catch (refreshError) {
            console.error("Failed to refresh shoe data:", refreshError);
            // Still show success but images might not update
          }
          
          toast.update(toastId, {
            render: "Product updated successfully",
            type: "success",
            isLoading: false,
            autoClose: 2000,
          });
          setIsLoading(false);
          
          // Optionally navigate away after success
          // setTimeout(() => {
          //   navigate("/admin/manage-shoes");
          // }, 2500);
        } else {
          toast.update(toastId, {
            render: "Failed to update product",
            type: "error",
            isLoading: false,
            autoClose: 2000,
          });
          setIsLoading(false)
        }
      } catch (error) {
        console.error("Error updating product:", error);
        setIsLoading(false);
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <Dialog className="min-h-screen">
      <DialogTrigger asChild>
        <Button
          variant="outline"
          className="hover:bg-slate-950 hover:text-white"
        >
          Chỉnh sửa
        </Button>
      </DialogTrigger>
      <DialogContent className="w-full max-w-2xl mx-auto max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chỉnh sửa thông tin giày</DialogTitle>
          <DialogDescription>
           Thay đổi thôn tin giày ở đây. Lưu thay đổi khi bạn nhập xong.
          </DialogDescription>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <div className="space-y-2">
            <Label htmlFor="name">Tên</Label>
            <Input
              id="name"
              name="name"
              defaultValue={shoe.name}
              {...register("name")}
            />
            {errors.name?.message && (
              <p className="text-red-600">{errors.name?.message}</p>
            )}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="price">Giá</Label>
              <Input
                id="price"
                name="price"
                type="number"
                step="0.01"
                defaultValue={shoe.price}
                {...register("price", { valueAsNumber: true })}
              />
              {errors.price?.message && (
                <p className="text-red-600">{errors.price?.message}</p>
              )}
            </div>
            <div className="space-y-2">
              <Label htmlFor="fakePrice">Giá niêm yết</Label>
              <Input
                id="fakePrice"
                name="fakePrice"
                type="number"
                step="0.01"
                defaultValue={shoe.fakePrice}
                {...register("fakePrice", { valueAsNumber: true })}
              />
              {errors.fakePrice?.message && (
                <p className="text-red-600">{errors.fakePrice?.message}</p>
              )}
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="description">Mô tả</Label>
            <Input
              id="description"
              name="description"
              defaultValue={shoe.description}
              {...register("description")}
            />
          </div>
          <div className="grid grid-cols-3 gap-4">
            <div className="space-y-2">
              <Label htmlFor="gender">Giới tính</Label>
              <select
                {...register("gender")}
                className="block rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
              >
                <option value="WOMEN">Nữ</option>
                <option value="MAN">Nam</option>
                <option value="UNISEX">UNISEX</option>
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="category">Thể loại</Label>
              <select
                {...register("category")}
                className="block rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
              >
                <option value="RUNNING">Giày chạy bộ</option>
                <option value="SPORT">Giày thể thao</option>
                <option value="CASUAL">Giày thường</option>
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="brandId">Nhãn hiệu:</Label>
              <select
                {...register("brandId", { valueAsNumber: true })}
                className="block rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
              >
                <option value="">-- Chọn thương hiệu --</option>
                {brands && brands.length > 0 ? (
                  brands.map((brand) => (
                    <option key={brand.brandId || brand.id} value={brand.brandId || brand.id}>
                      {brand.brandName || brand.name}
                    </option>
                  ))
                ) : (
                  <>
                    <option value="1">Nike</option>
                    <option value="2">Adidas</option>
                    <option value="3">Puma</option>
                    <option value="4">Reebok</option>
                  </>
                )}
              </select>
            </div>
          </div>
          <div className="space-y-2">
            <Label htmlFor="status">Trạng thái:</Label>
            <select
              {...register("status")}
              className="block w-1/3 rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
            >
              <option value="true">Hoạt động</option>
              <option value="false">Tắt</option>
            </select>
            {errors.status?.message && (
              <p className="text-red-600">{errors.status?.message}</p>
            )}
          </div>

          <VariantShoeOnUpdateForm
            variants={shoe.variants}
            onVariantChange={handleVariantChange}
            updatedVariants={updatedVariants}
          />

          {/* Add New Size Section */}
          <div className="space-y-2 border-t pt-4">
            <div className="flex items-center justify-between">
              <Label className="text-lg font-semibold">Thêm kích cỡ mới</Label>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => setIsAddingSizeDialogOpen(!isAddingSizeDialogOpen)}
                disabled={availableSizes.length === 0}
              >
                {isAddingSizeDialogOpen ? "Hủy" : "+ Thêm size"}
              </Button>
            </div>
            
            {availableSizes.length === 0 && (
              <p className="text-sm text-gray-500">
                Đã thêm đủ tất cả các size có trong hệ thống
              </p>
            )}
            
            {isAddingSizeDialogOpen && availableSizes.length > 0 && (
              <div className="flex gap-2 items-end">
                <div className="flex-1 space-y-2">
                  <Label htmlFor="newSize">Chọn kích cỡ</Label>
                  <select
                    id="newSize"
                    value={selectedNewSize}
                    onChange={(e) => setSelectedNewSize(e.target.value)}
                    className="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50 pl-3 pr-10 py-2 text-base"
                  >
                    <option value="">-- Chọn size --</option>
                    {availableSizes.map((size) => (
                      <option key={size.id} value={size.id}>
                        Size {size.sizeNumber}
                      </option>
                    ))}
                  </select>
                </div>
                <Button
                  type="button"
                  onClick={handleAddNewSize}
                  disabled={!selectedNewSize}
                >
                  Thêm
                </Button>
              </div>
            )}
          </div>

          {/* Current Images Section */}
          <div className="space-y-2 border-t pt-4">
            <Label className="text-lg font-semibold">Hình ảnh hiện tại</Label>
            {currentImages && currentImages.length > 0 ? (
              <div className="grid grid-cols-3 gap-4">
                {currentImages.map((image, index) => (
                  <div key={image.id || index} className="relative group">
                    <img
                      src={getImageUrl(image.url)}
                      alt="Product"
                      className="w-full h-32 object-cover rounded-lg border"
                      onError={(e) => {
                        e.target.src = 'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100"><text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle">No Image</text></svg>';
                      }}
                    />
                    <button
                      type="button"
                      onClick={() => handleRemoveImage(image.id)}
                      className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                      </svg>
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-sm">Chưa có hình ảnh</p>
            )}
          </div>

          {/* Add New Images Section */}
          <div className="space-y-2 border-t pt-4">
            <Label className="text-lg font-semibold">Thêm hình ảnh mới</Label>
            <ImagesUpload onImagesSelect={handleImagesSelect} />
          </div>

          <Separator className="my-4" />

          <DialogFooter>
            <Button  type="submit" disabled={isLoading}>
              {isLoading ? "Loading..." : "Save changes"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
