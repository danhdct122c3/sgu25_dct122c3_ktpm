import React, { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog, DialogContent, DialogDescription, DialogFooter,
  DialogHeader, DialogTitle, DialogTrigger
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import api from "@/config/axios";
import { toast, Bounce } from "react-toastify";
import ImagesUpload from "./ImagesUpload";
import VariantShoeOnUpdateForm from "./VariantShoeOnUpdateForm";
import { getImageUrl } from "@/utils/imageHelper";

// ✅ Parse number từ input
const schema = z.object({
  name: z.string().min(2, { message: "Required" }),
  price: z.coerce.number().min(10, { message: "Required" }),
  fakePrice: z.coerce.number().min(10, { message: "Required" }),
  description: z.string().min(10, { message: "Required" }),
  status: z.enum(["true", "false"]).transform(v => v === "true"),
  gender: z.string(),
  category: z.string(),
  brandId: z.coerce.number(),
});

export default function UpdateShoeForm({ shoeId }) {
  const [open, setOpen] = useState(false);

  const {
    register, handleSubmit, formState: { errors }, reset, watch,
  } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      name: "", price: 0, fakePrice: 0, description: "",
      status: "true", gender: "UNISEX", category: "SNEAKER", brandId: 0,
    },
  });

  // ===== State chung =====
  const [shoe, setShoe] = React.useState({});
  const [updatedVariants, setUpdatedVariants] = React.useState({}); // {variantId: qty}
  const [isLoading, setIsLoading] = useState(false);
  const [brands, setBrands] = useState([]);
  const [allSizes, setAllSizes] = useState([]);
  const [availableSizes, setAvailableSizes] = useState([]);
  const [selectedNewSize, setSelectedNewSize] = useState("");
  const [isAddingSizeDialogOpen, setIsAddingSizeDialogOpen] = useState(false);

  // Ảnh
  const [selectedImages, setSelectedImages] = useState([]); // File[]
  const [currentImages, setCurrentImages] = useState([]);   // [{id,url}]

  // Watch values (debug)
  const currentBrandId = watch("brandId");
  const currentGender = watch("gender");
  const currentCategory = watch("category");
  const currentStatus = watch("status");

  // ===== Fetch Brands =====
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
        setBrands([
          { brandId: 1, brandName: "Nike" },
          { brandId: 2, brandName: "Adidas" },
          { brandId: 3, brandName: "Puma" },
          { brandId: 4, brandName: "Reebok" },
        ]);
      }
    };
    fetchBrands();
  }, []);

  // ===== Fetch Sizes =====
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

  // ===== Fetch Shoe =====
  useEffect(() => {
    const fetchShoe = async () => {
      try {
        const { data } = await api.get(`shoes/${shoeId}`);
        const s = data.result ?? data;

        // Tách brandId an toàn
        let extractedBrandId = null;
        if (s.brand?.id) extractedBrandId = s.brand.id;
        else if (s.brand?.brandId) extractedBrandId = s.brand.brandId;
        else if (s.brandId) extractedBrandId = s.brandId;

        setShoe(s);

        // Reset form (KHÔNG dùng defaultValue ở JSX)
        reset({
          name: s.name ?? "",
          price: s.price ?? 0,
          fakePrice: s.fakePrice ?? 0,
          description: s.description ?? "",
          status: (s.status ?? true) ? "true" : "false",
          gender: s.gender ?? "UNISEX",
          category: s.category ?? "SNEAKER",
          brandId: extractedBrandId ?? 0,
        });

        // Ảnh
        if (Array.isArray(s.images)) setCurrentImages(s.images);

        // Variants → qty
        const initialVariants = {};
        (s.variants ?? []).forEach(v => {
          initialVariants[v.id] = v.stockQuantity;
        });
        setUpdatedVariants(initialVariants);

        // Tính các size chưa có
        if (allSizes.length > 0 && Array.isArray(s.variants)) {
          const existingSizeNumbers = s.variants.map(v => {
            const sizeNum = v.sku?.split("-").pop();
            return parseInt(sizeNum, 10);
          });
          const available = allSizes.filter(sz => !existingSizeNumbers.includes(sz.sizeNumber));
          setAvailableSizes(available);
        }
      } catch (error) {
        console.error("❌ Error fetching shoe:", error);
        toast.error("Lỗi khi tải dữ liệu sản phẩm", { transition: Bounce });
      }
    };
    if (shoeId) fetchShoe();
  }, [shoeId, reset, allSizes]);

  // ===== Variants & Sizes =====
  const handleVariantChange = (variantId, quantity) => {
    setUpdatedVariants(prev => ({ ...prev, [variantId]: quantity }));
  };

  const handleAddNewSize = async () => {
    if (!selectedNewSize) {
      toast.error("Vui lòng chọn kích cỡ");
      return;
    }
    try {
      const sizeObj = allSizes.find(s => s.id === parseInt(selectedNewSize, 10));
      if (!sizeObj) {
        toast.error("Kích cỡ không hợp lệ");
        return;
      }
      const sku = `${(shoe.name || "").replace(/\s+/g, "-")}-${sizeObj.sizeNumber}`;
      const newVariant = { sizeId: sizeObj.id, sku, stockQuantity: 0 };

      const response = await api.post(`shoes/${shoeId}/variants`, newVariant);
      if (response.status === 200 || response.status === 201) {
        toast.success(`Đã thêm kích cỡ ${sizeObj.sizeNumber}!`);

        // Refresh
        const { data } = await api.get(`shoes/${shoeId}`);
        const updatedShoe = data.result ?? data;
        setShoe(updatedShoe);

        const newVariantId = response.data?.result?.id ?? response.data?.id;
        if (newVariantId) {
          setUpdatedVariants(prev => ({ ...prev, [newVariantId]: 0 }));
        }
        setAvailableSizes(prev => prev.filter(s => s.id !== parseInt(selectedNewSize, 10)));
        setSelectedNewSize("");
        setIsAddingSizeDialogOpen(false);
      }
    } catch (error) {
      console.error("❌ Error adding new size:", error);
      toast.error(error.response?.data?.message || "Lỗi khi thêm kích cỡ mới");
    }
  };

  // ===== Images =====
  const handleImagesSelect = (files) => {
    // Nếu muốn cộng dồn nhiều lần chọn:
    const arr = Array.from(files ?? []);
    setSelectedImages(arr); // hoặc: setSelectedImages(prev => [...prev, ...arr])
  };

  const handleRemoveImage = async (imageId) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa ảnh này?")) return;
    try {
      await api.delete(`/shoe-images/${imageId}`);
      setCurrentImages(prev => prev.filter(img => img.id !== imageId));
      toast.success("Đã xóa ảnh!");
    } catch (error) {
      console.error("Error removing image:", error);
      toast.error("Lỗi khi xóa ảnh");
    }
  };

  const uploadImages = async (files) => {
    const formData = new FormData();
    files.forEach(file => formData.append("files", file));
    const response = await api.post("/images/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return response.data?.result ?? [];
  };

  const onSubmit = async (data) => {
    if (isLoading) return;
    setIsLoading(true);
    const toastId = toast.loading("Updating product...");

    try {
      // 1) Upload file -> URLs
      let uploaded = [];
      if (selectedImages?.length) {
        uploaded = await uploadImages(selectedImages); // ["/uploads/shoes/a.jpg", ...]
      }

      // 2) Chống trùng theo URL (normalize)
      const normalize = (u) => u?.trim();
      const existingSet = new Set((currentImages ?? []).map(i => normalize(i.url)));
      const newUrls = uploaded.filter(u => !existingSet.has(normalize(u)));

      // 3) Gắn ảnh vào shoe bằng /shoe-images/shoe/{shoeId}
      if (newUrls.length) {
        await Promise.all(
          newUrls.map((url) => {
            const publicId = url.split("/").pop(); // filename làm publicId
            return api.post(`/shoe-images/shoe/${shoeId}`, { url, publicId });
          })
        );
      }

      // 4) Cập nhật field khác (KHÔNG gửi images)
      const payload = {
        name: data.name,
        price: data.price,
        fakePrice: data.fakePrice,
        status: data.status,
        gender: data.gender,
        category: data.category,
        description: data.description,
        variants: (shoe.variants ?? []).map(v => ({
          variantId: v.id,
          stockQuantity: updatedVariants[v.id] ?? v.stockQuantity,
        })),
      };
      await api.put(`/shoes/${shoeId}`, payload);

      // 5) Refetch & sort ảnh theo createdAt (mới nhất trước)
      const refresh = await api.get(`/shoes/${shoeId}`);
      const updated = refresh.data?.result ?? refresh.data;
      const sorted = (updated?.images ?? []).sort((a,b) =>
        new Date(b.createdAt || 0) - new Date(a.createdAt || 0)
      );
      setCurrentImages(sorted);
      setSelectedImages([]);

      toast.update(toastId, { render: "Product updated", type: "success", isLoading: false, autoClose: 1500 });
      setOpen(false);
    } catch (err) {
      console.error(err);
      toast.update(toastId, { render: err.response?.data?.message || "Update failed", type: "error", isLoading: false, autoClose: 2500 });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline" onClick={() => setOpen(true)}>Chỉnh sửa</Button>
      </DialogTrigger>

      <DialogContent className="w-full max-w-2xl mx-auto max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Chỉnh sửa thông tin giày</DialogTitle>
          <DialogDescription>
            Thay đổi thông tin giày ở đây. Lưu thay đổi khi bạn nhập xong.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* -------- Thông tin cơ bản -------- */}
          <div className="space-y-2">
            <Label htmlFor="name">Tên</Label>
            <Input id="name" {...register("name")} />
            {errors.name?.message && <p className="text-red-600">{errors.name.message}</p>}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="price">Giá</Label>
              <Input id="price" type="number" step="1" {...register("price")} />
              {errors.price?.message && <p className="text-red-600">{errors.price.message}</p>}
            </div>
            <div className="space-y-2">
              <Label htmlFor="fakePrice">Giá niêm yết</Label>
              <Input id="fakePrice" type="number" step="1" {...register("fakePrice")} />
              {errors.fakePrice?.message && <p className="text-red-600">{errors.fakePrice.message}</p>}
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="description">Mô tả</Label>
            <Input id="description" {...register("description")} />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-2">
              <Label htmlFor="gender">Giới tính</Label>
              <select id="gender" {...register("gender")} className="block w-full rounded-md border-gray-300 pl-3 pr-10 py-2">
                {/* ⚠️ Chỉnh enum cho khớp backend */}
                <option value="FEMALE">Nữ</option>
                <option value="MALE">Nam</option>
                <option value="UNISEX">UNISEX</option>
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="category">Thể loại</Label>
              <select id="category" {...register("category")} className="block w-full rounded-md border-gray-300 pl-3 pr-10 py-2">
                {/* ⚠️ Chỉnh enum cho khớp backend */}
                <option value="SNEAKER">Sneaker</option>
                <option value="SPORT">Thể thao</option>
                <option value="CASUAL">Thường ngày</option>
              </select>
            </div>
            <div className="space-y-2">
              <Label htmlFor="brandId">Nhãn hiệu</Label>
              <select id="brandId" {...register("brandId")} className="block w-full rounded-md border-gray-300 pl-3 pr-10 py-2">
                <option value="">-- Chọn thương hiệu --</option>
                {Array.isArray(brands) && brands.length > 0 ? (
                  brands.map((brand) => (
                    <option key={brand.brandId ?? brand.id} value={brand.brandId ?? brand.id}>
                      {brand.brandName ?? brand.name}
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

          <Separator className="my-2" />

          {/* -------- Biến thể hiện tại -------- */}
          <VariantShoeOnUpdateForm
            variants={shoe.variants}
            onVariantChange={handleVariantChange}
            updatedVariants={updatedVariants}
          />

          {/* -------- Thêm size mới -------- */}
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
              <p className="text-sm text-gray-500">Đã thêm đủ tất cả các size trong hệ thống</p>
            )}

            {isAddingSizeDialogOpen && availableSizes.length > 0 && (
              <div className="flex gap-2 items-end">
                <div className="flex-1 space-y-2">
                  <Label htmlFor="newSize">Chọn kích cỡ</Label>
                  <select
                    id="newSize"
                    value={selectedNewSize}
                    onChange={(e) => setSelectedNewSize(e.target.value)}
                    className="block w-full rounded-md border-gray-300 pl-3 pr-10 py-2"
                  >
                    <option value="">-- Chọn size --</option>
                    {availableSizes.map((size) => (
                      <option key={size.id} value={size.id}>
                        Size {size.sizeNumber}
                      </option>
                    ))}
                  </select>
                </div>
                <Button type="button" onClick={handleAddNewSize} disabled={!selectedNewSize}>
                  Thêm
                </Button>
              </div>
            )}
          </div>

          {/* -------- Ảnh hiện tại -------- */}
          <div className="space-y-2 border-t pt-4">
            <Label className="text-lg font-semibold">Hình ảnh hiện tại</Label>
            {currentImages?.length > 0 ? (
              <div className="grid grid-cols-3 gap-4">
                {currentImages.map((image, index) => (
                  <div key={image.id ?? index} className="relative group">
                    <img
                      src={getImageUrl(image.url)}
                      alt="Product"
                      className="w-full h-32 object-cover rounded-lg border"
                      onError={(e) => {
                        e.currentTarget.src =
                          'data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100"><text x="50%" y="50%" text-anchor="middle" dominant-baseline="middle">No Image</text></svg>';
                      }}
                    />
                    <button
                      type="button"
                      onClick={() => handleRemoveImage(image.id)}
                      className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                      title="Xóa ảnh này"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                        <path
                          fillRule="evenodd"
                          d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                          clipRule="evenodd"
                        />
                      </svg>
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500 text-sm">Chưa có hình ảnh</p>
            )}
          </div>

          {/* -------- Thêm ảnh mới -------- */}
          <div className="space-y-2 border-t pt-4">
            <Label className="text-lg font-semibold">Thêm hình ảnh mới</Label>
            <ImagesUpload onImagesSelect={handleImagesSelect} />
            {/* (tuỳ chọn) preview ảnh mới */}
            {selectedImages.length > 0 && (
              <div className="grid grid-cols-3 gap-4 mt-2">
                {selectedImages.map((file, idx) => (
                  <div key={idx} className="relative group">
                    <img
                      src={URL.createObjectURL(file)}
                      alt="Preview"
                      className="w-full h-32 object-cover rounded-lg border"
                    />
                    <button
                      type="button"
                      className="absolute top-2 right-2 bg-gray-700 text-white rounded-full p-1 opacity-0 group-hover:opacity-100 transition-opacity"
                      onClick={() => setSelectedImages(prev => prev.filter((_, i) => i !== idx))}
                    >
                      Xóa
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          <Separator className="my-4" />

          <DialogFooter>
            <Button type="submit" disabled={isLoading}>
              {isLoading ? "Loading..." : "Save changes"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}