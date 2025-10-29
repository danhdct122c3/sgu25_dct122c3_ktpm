import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import api from "@/config/axios";

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from "@/components/ui/select";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { formatterToVND } from "@/utils/formatter";
import { getImageUrl } from "@/utils/imageHelper";
import UpdateShoeForm from "../admin-pages/UpdateShoeForm";
import { IoIosAddCircleOutline } from "react-icons/io";
import { Link } from "react-router-dom";

const ShoeManagement = () => {
  const [shoeData, setShoeData] = useState(null);
  const [brands, setBrands] = useState([]);
  const [name, setName] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [brandId, setBrandId] = useState("");
  const [gender, setGender] = useState("");
  const [category, setCategory] = useState("");
  const [sortOrder, setSortOrder] = useState("date");
  const [status, setStatus] = useState("true");
  const isActive = status === "true";

  const [page, setPage] = useState(1);
  const [size, setSize] = useState(5);

  const fetchShoeData = useCallback(async () => {
    try {
      const params = {
        name,
        minPrice: minPrice || undefined,
        maxPrice: maxPrice || undefined,
        brandId: brandId || undefined,
        gender: gender || undefined,
        category: category || undefined,
        sortOrder,
        page,
        size,
        status: isActive,
      };

      const response = await api.get("shoes/list-shoes", { params });
      setShoeData(response.data.result);
    } catch (error) {
      console.error("Error fetching shoe data:", error);
    }
  }, [name, minPrice, maxPrice, brandId, gender, category, sortOrder, page, size, status]);

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
          { brandId: 4, brandName: "Reebok" }
        ]);
      }
    };
    fetchBrands();
  }, []);

  useEffect(() => {
    fetchShoeData();
  }, [fetchShoeData, name, minPrice, maxPrice, brandId, gender, category, sortOrder, page, size, status]);

  const handlePageChange = (newPage) => setPage(newPage);

  const handleSearch = () => {
    setPage(1);
    fetchShoeData();
  };

  if (!shoeData) {
    return (
      <div className="flex items-center justify-center h-screen">
        Loading...
      </div>
    );
  }

  const handleDelete = async (id) => {
    try {
      const currentShoe = shoeData.data.find((shoe) => shoe.id === id);
      const updateRequest = {
        name: currentShoe.name,
        price: currentShoe.price,
        status: false,
        fakePrice: currentShoe.fakePrice,
        gender: currentShoe.gender,
        category: currentShoe.category,
        description: currentShoe.description,
        variants: currentShoe.variants.map((variant) => ({
          variantId: variant.id,
          stockQuantity: variant.stockQuantity,
        })),
      };
      await api.put(`/shoes/${id}`, updateRequest);
      fetchShoeData();
    } catch (error) {
      console.error("Error deleting shoe:", error);
    }
  };

  return (
    <div className="p-6 mx-auto w-full max-w-screen-2xl min-h-screen bg-white rounded-lg shadow-md">
      <h1 className="text-3xl font-bold mb-6">Quản Lý Giày</h1>

      {/* Filters */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-3">
        <Input
          placeholder="Tìm kiếm theo tên giày"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <div className="space-y-2">
          <div className="flex space-x-2">
            <Input
              type="number"
              placeholder="Giá thấp nhất"
              value={minPrice}
              onChange={(e) =>
                setMinPrice(e.target.value ? Number(e.target.value) : null)
              }
            />
            <Input
              type="number"
              placeholder="Giá cao nhất"
              value={maxPrice}
              onChange={(e) =>
                setMaxPrice(e.target.value ? Number(e.target.value) : null)
              }
            />
          </div>
        </div>

        <Select value={gender} onValueChange={setGender}>
          <SelectTrigger>
            <SelectValue placeholder="Chọn giới tính" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="MAN">Nam</SelectItem>
            <SelectItem value="WOMAN">Nữ</SelectItem>
            <SelectItem value="UNISEX">Unisex</SelectItem>
          </SelectContent>
        </Select>

        <Select value={category} onValueChange={setCategory}>
          <SelectTrigger>
            <SelectValue placeholder="Chọn thể loại" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="SPORT">Giày thể thao</SelectItem>
            <SelectItem value="RUNNING">Giày chạy bộ</SelectItem>
            <SelectItem value="CASUAL">Giày thường</SelectItem>
          </SelectContent>
        </Select>

        <Select value={brandId} onValueChange={setBrandId}>
          <SelectTrigger>
            <SelectValue placeholder="Chọn nhãn hiệu" />
          </SelectTrigger>
          <SelectContent>
            {brands && brands.length > 0 ? (
              brands.map((brand) => (
                <SelectItem
                  key={brand.brandId || brand.id}
                  value={String(brand.brandId || brand.id)}
                >
                  {brand.brandName || brand.name}
                </SelectItem>
              ))
            ) : (
              <>
                <SelectItem value="1">Nike</SelectItem>
                <SelectItem value="2">Adidas</SelectItem>
                <SelectItem value="3">Puma</SelectItem>
                <SelectItem value="4">Reebok</SelectItem>
              </>
            )}
          </SelectContent>
        </Select>

        <Select value={sortOrder} onValueChange={setSortOrder}>
          <SelectTrigger>
            <SelectValue placeholder="Thứ tự sắp xếp" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="date">Cũ - Mới</SelectItem>
            <SelectItem value="date_desc">Mới - Cũ</SelectItem>
            <SelectItem value="desc">Price: High to Low</SelectItem>
            <SelectItem value="asc">Price: Low to High</SelectItem>
          </SelectContent>
        </Select>

        <Select value={status} onValueChange={setStatus}>
          <SelectTrigger>
            <SelectValue placeholder="Tất cả" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="true">Hoạt động</SelectItem>
            <SelectItem value="false">Tắt</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Actions */}
      <div className="flex flex-wrap gap-2 mb-3">
        <Button onClick={handleSearch} className="w-full md:w-auto">
          Tìm kiếm
        </Button>
        <Button variant="outline" className="hover:bg-green-600 hover:text-white">
          <Link to={"/admin/manage-shoes/new"} className="flex items-center gap-2 p-2">
            <IoIosAddCircleOutline className="h-5 w-5" />
            <span>Thêm</span>
          </Link>
        </Button>
      </div>

      {/* Table: căn đều 5 cột, nội dung căn giữa */}
      <div className="rounded-md border overflow-x-auto">
        <Table className="w-full min-w-[1000px] table-fixed">
          {/* 5 cột đều nhau: mỗi cột 20% */}
          <colgroup>
            <col className="w-[20%]" />
            <col className="w-[20%]" />
            <col className="w-[20%]" />
            <col className="w-[20%]" />
            <col className="w-[20%]" />
          </colgroup>

          <TableHeader>
            <TableRow>
              <TableHead className="text-center">Tên giày</TableHead>
              <TableHead className="text-center">Hình</TableHead>
              <TableHead className="text-center">Giá</TableHead>
              <TableHead className="text-center">Số lượng</TableHead>
              <TableHead className="text-center">Chỉnh sửa</TableHead>
            </TableRow>
          </TableHeader>

          <TableBody>
            {shoeData.data.map((shoe) => (
              <TableRow key={shoe.id}>
                {/* Tên giày: căn giữa + truncate chống tràn */}
                <TableCell className="text-center">
                  <span
                    className="mx-auto block max-w-[280px] truncate"
                    title={shoe.name}
                  >
                    {shoe.name}
                  </span>
                </TableCell>

                {/* Hình: căn giữa */}
                <TableCell className="text-center">
                  {shoe.images && shoe.images.length > 0 ? (
                    <img
                      src={getImageUrl(shoe.images[0].url)}
                      alt={shoe.name}
                      className="inline-block h-16 w-16 lg:h-20 lg:w-20 object-cover rounded border"
                      onError={(e) => {
                        e.currentTarget.src = "https://via.placeholder.com/80?text=IMG";
                      }}
                    />
                  ) : (
                    <div className="inline-flex h-16 w-16 lg:h-20 lg:w-20 bg-gray-200 rounded items-center justify-center text-xs text-gray-500">
                      No image
                    </div>
                  )}
                </TableCell>

                {/* Giá: căn giữa + không xuống dòng */}
                <TableCell className="text-center whitespace-nowrap">
                  {formatterToVND.format(shoe.price)}
                </TableCell>

                {/* Số lượng: căn giữa */}
                <TableCell className="text-center">
                  {shoe.variants && shoe.variants.length > 0
                    ? shoe.variants.reduce(
                        (total, variant) => total + variant.stockQuantity,
                        0
                      )
                    : 0}
                </TableCell>

                {/* Chỉnh sửa: căn giữa */}
                <TableCell className="text-center">
                  <div className="inline-flex flex-wrap gap-2 justify-center">
                    <UpdateShoeForm shoeId={shoe.id} />
                    <Button
                      variant="destructive"
                      onClick={() => handleDelete(shoe.id)}
                    >
                      Xóa
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      <div className="flex flex-col sm:flex-row justify-between items-center mt-4 space-y-4 sm:space-y-0">
        <div className="text-sm text-muted-foreground">
          Hiện số trang {shoeData.currentPage} trên {shoeData.totalPages} (tổng số giày: {shoeData.totalElements})
        </div>
        <div className="flex flex-wrap gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(shoeData.currentPage - 1)}
            disabled={shoeData.currentPage === 1}
          >
            <ChevronLeft className="h-4 w-4" />
          </Button>
          {Array.from({ length: shoeData.totalPages }, (_, i) => i + 1).map((pageNum) => (
            <Button
              key={pageNum}
              variant={shoeData.currentPage === pageNum ? "default" : "outline"}
              size="sm"
              onClick={() => handlePageChange(pageNum)}
            >
              {pageNum}
            </Button>
          ))}
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(shoeData.currentPage + 1)}
            disabled={shoeData.currentPage === shoeData.totalPages}
          >
            <ChevronRight className="h-4 w-4" />
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ShoeManagement;