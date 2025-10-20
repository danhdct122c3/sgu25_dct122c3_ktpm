import React, { useState, useEffect } from "react";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import {
  Table,
  TableBody,
  TableCaption,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Checkbox } from "@/components/ui/checkbox";
// import AdminAside from "@/components/admin-com/AdminAside";
import { ComboboxSortDiscount } from "@/components/ui/combobox";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { format } from "date-fns";
import { Calendar as CalendarIcon } from "lucide-react";
import { cn } from "@/lib/utils";
import { Calendar } from "@/components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import api from "@/config/axios";

export function DiscountManagement() {

  const [selectedOption, setSelectedOption] = useState("");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingDiscount, setEditingDiscount] = useState(null);
  const [categories, setCategories] = useState([]);
  const [shoes, setShoes] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedShoes, setSelectedShoes] = useState([]);
  const options = ["mới nhất", "cũ nhất"];
  
  // Tách riêng trạng thái cho ngày bắt đầu và ngày kết thúc
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const handleSelection = (value, discount = null) => {
    setSelectedOption(value);

    // Mở hộp thoại khi chọn 'edit'
    if (value === "edit" && discount) {
      setEditingDiscount(discount);
      setSelectedCategories(discount.categories || []);
      setSelectedShoes(discount.shoeIds || []);
      setStartDate(discount.startDate ? new Date(discount.startDate) : null);
      setEndDate(discount.endDate ? new Date(discount.endDate) : null);
      setIsDialogOpen(true);
    }
  };

  const [discounts, setDiscounts] = React.useState([]);
  
  useEffect(() => {
    const fetchDiscounts = async () => {
      const { data } = await api.get("discounts");
      setDiscounts(data.result);
    };

    const fetchData = async () => {
      try {
        // Fetch shoes
        const shoesResponse = await api.get("/shoes");
        if (shoesResponse.data.result && Array.isArray(shoesResponse.data.result)) {
          setShoes(shoesResponse.data.result);
        }
      } catch (error) {
        console.error("Failed to fetch data:", error);
      }
    };

    fetchDiscounts();
    fetchData();
  }, []);

  // Set available categories (hardcoded for now)
  useEffect(() => {
    setCategories([
      { value: "SPORT", name: "Giày thể thao" },
      { value: "RUNNING", name: "Giày chạy bộ" },
      { value: "CASUAL", name: "Giày thường" },
    ]);
  }, []);

  return (
    <div>
      <h1 className="mt-5 text-4xl font-bold" align="center">
        Quản lý mã giảm giá
      </h1>

      <div className="mt-5">
        <div className="grid gap-4 sm:grid-cols-10 grid-cols-1">
          <div className="sm:col-span-5 mt-3">
            <Pagination>
              <PaginationContent>
                <PaginationItem>
                  <PaginationPrevious href="#" />
                </PaginationItem>
                <PaginationItem>
                  <PaginationLink href="#">1</PaginationLink>
                  <PaginationLink href="#">2</PaginationLink>
                  <PaginationLink href="#">3</PaginationLink>
                </PaginationItem>
                <PaginationItem>
                  <PaginationEllipsis />
                </PaginationItem>
                <PaginationItem>
                  <PaginationNext href="#" />
                </PaginationItem>
              </PaginationContent>
            </Pagination>
          </div>

          <div className="sm:col-span-5">
            <div className="grid gap-4 sm:grid-cols-5 grid-cols-1">
              <div className="sm:col-span-2">
                <div className="relative mt-1">
                  <ComboboxSortDiscount
                    as="div"
                    value={selectedOption}
                    onChange={handleSelection}
                  />
                </div>
              </div>

              <div className="sm:col-span-3">
                <Button className="bg-green-500 text-indigo-50" variant="default">
                  Lưu vào Excel
                </Button>
              </div>
            </div>
          </div>
        </div>

        <div className="mt-10">
          <Table>
            <TableCaption>Danh sách mã giảm giá gần đây của bạn.</TableCaption>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[100px]">Chỉnh sửa</TableHead>
                <TableHead>ID</TableHead>
                <TableHead>Mã giảm giá</TableHead>
                <TableHead>Loại giảm giá</TableHead>
                <TableHead>Giá trị</TableHead>
                <TableHead>Giới hạn sử dụng</TableHead>
                <TableHead>Danh mục</TableHead>
                <TableHead>Ngày bắt đầu</TableHead>
                <TableHead>Ngày kết thúc</TableHead>
                <TableHead>Hoạt động</TableHead>
              </TableRow>
            </TableHeader>

            <TableBody>
            {discounts.map((discount, index) => (
              <TableRow key={discount.id}>
                <TableCell>
                  <Select onValueChange={(value) => handleSelection(value, discount)}>
                    <SelectTrigger className="w-[90px]">
                      <SelectValue placeholder="Chỉnh sửa" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectGroup>
                        <SelectLabel>Hành động</SelectLabel>
                        <SelectItem value="edit" className="text-yellow-500">Chỉnh sửa</SelectItem>
                        <SelectItem value="delete" className="text-red-500">Xóa</SelectItem>
                      </SelectGroup>
                    </SelectContent>
                  </Select>
                </TableCell>
                <TableCell>{index + 1}</TableCell>
                <TableCell>{discount.code}</TableCell>
                <TableCell>
                  <span className={`px-2 py-1 rounded-full text-xs ${
                    discount.discountType === 'PERCENTAGE' 
                      ? 'bg-blue-100 text-blue-800' 
                      : 'bg-green-100 text-green-800'
                  }`}>
                    {discount.discountType === 'PERCENTAGE' ? 'Phần trăm' : 'Số tiền cố định'}
                  </span>
                </TableCell>
                <TableCell>
                  {discount.discountType === 'PERCENTAGE' 
                    ? `${discount.percentage}%` 
                    : `${discount.fixedAmount?.toLocaleString()}đ`
                  }
                </TableCell>
                <TableCell>
                  {discount.usageLimit ? (
                    <div className="flex flex-col">
                      <span>{discount.usageLimit} lần</span>
                      <span className="text-xs text-gray-500">
                        Còn lại: {discount.usageLimit - (discount.usedCount || 0)} lần
                      </span>
                    </div>
                  ) : 'Không giới hạn'}
                </TableCell>
                <TableCell>
                  {discount.categories && discount.categories.length > 0 
                    ? discount.categories.join(', ') 
                    : 'Tất cả danh mục'
                  }
                </TableCell>
                <TableCell>{new Date(discount.startDate).toLocaleDateString('vi-VN')}</TableCell>
                <TableCell>{new Date(discount.endDate).toLocaleDateString('vi-VN')}</TableCell>
                <TableCell>
                  <span className={`px-2 py-1 rounded-full text-xs ${
                    discount.isActive 
                      ? 'bg-green-100 text-green-800' 
                      : 'bg-red-100 text-red-800'
                  }`}>
                    {discount.isActive ? "Hoạt động" : "Tạm dừng"}
                  </span>
                </TableCell>
              </TableRow>
            ))}
            </TableBody>
          </Table>
        </div>
      </div>

      {/* Hộp thoại chỉnh sửa mã giảm giá */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogTrigger asChild>
          <Button style={{ display: 'none' }}>Mở hộp thoại</Button>
        </DialogTrigger>
        <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle align="center">Chỉnh sửa mã giảm giá</DialogTitle>
          </DialogHeader>
          <form className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="disId">Mã giảm giá</Label>
                <Input 
                  id="disId" 
                  type="text" 
                  className="mb-2" 
                  defaultValue={editingDiscount?.code || ""}
                />
              </div>
              
              <div>
                <Label htmlFor="discountType">Loại giảm giá</Label>
                <select 
                  id="discountType"
                  className="w-full p-2 border rounded-md mb-2"
                  defaultValue={editingDiscount?.discountType || "PERCENTAGE"}
                >
                  <option value="PERCENTAGE">Phần trăm</option>
                  <option value="FIXED_AMOUNT">Số tiền cố định</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="disPer">Phần trăm</Label>
                <Input 
                  id="disPer" 
                  type="number" 
                  className="mb-2" 
                  defaultValue={editingDiscount?.percentage || ""}
                />
              </div>
              
              <div>
                <Label htmlFor="fixedAmount">Số tiền cố định</Label>
                <Input 
                  id="fixedAmount" 
                  type="number" 
                  className="mb-2" 
                  defaultValue={editingDiscount?.fixedAmount || ""}
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="minimumOrderAmount">Số tiền đơn hàng tối thiểu</Label>
                <Input 
                  id="minimumOrderAmount" 
                  type="number" 
                  className="mb-2" 
                  defaultValue={editingDiscount?.minimumOrderAmount || 0}
                />
              </div>
              
              <div>
                <Label htmlFor="usageLimit">Giới hạn sử dụng</Label>
                <Input 
                  id="usageLimit" 
                  type="number" 
                  className="mb-2" 
                  placeholder="Để trống nếu không giới hạn"
                  defaultValue={editingDiscount?.usageLimit || ""}
                />
              </div>
            </div>

            <div>
              <Label htmlFor="description">Mô tả</Label>
              <Input 
                id="description" 
                type="text" 
                className="mb-2" 
                defaultValue={editingDiscount?.description || ""}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="disStart">Ngày bắt đầu</Label>
                <Popover id="disStart" className="mb-2">
                  <PopoverTrigger asChild>
                    <Button
                      variant={"outline"}
                      className={cn(
                        "w-full justify-start text-left font-normal",
                        !startDate && "text-muted-foreground"
                      )}
                    >
                      <CalendarIcon className="mr-2 h-4 w-4" />
                      {startDate ? format(startDate, "PPP") : <span>Chọn ngày bắt đầu</span>}
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0">
                    <Calendar
                      mode="single"
                      selected={startDate}
                      onSelect={setStartDate}
                      initialFocus
                    />
                  </PopoverContent>
                </Popover>
              </div>

              <div>
                <Label htmlFor="disEnd">Ngày kết thúc</Label>
                <Popover id="disEnd" className="mb-2">
                  <PopoverTrigger asChild>
                    <Button
                      variant={"outline"}
                      className={cn(
                        "w-full justify-start text-left font-normal",
                        !endDate && "text-muted-foreground"
                      )}
                    >
                      <CalendarIcon className="mr-2 h-4 w-4" />
                      {endDate ? format(endDate, "PPP") : <span>Chọn ngày kết thúc</span>}
                    </Button>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0">
                    <Calendar
                      mode="single"
                      selected={endDate}
                      onSelect={setEndDate}
                      initialFocus
                    />
                  </PopoverContent>
                </Popover>
              </div>
            </div>

            <div className="space-y-2">
              <Label className="block text-gray-700">Danh mục áp dụng</Label>
              <div className="grid grid-cols-3 gap-2">
                {categories.map((category) => (
                  <div key={category.value} className="flex items-center space-x-2">
                    <Checkbox
                      id={`category-${category.value}`}
                      checked={selectedCategories.includes(category.value)}
                      onCheckedChange={(checked) => {
                        if (checked) {
                          setSelectedCategories([...selectedCategories, category.value]);
                        } else {
                          setSelectedCategories(selectedCategories.filter(c => c !== category.value));
                        }
                      }}
                    />
                    <Label htmlFor={`category-${category.value}`} className="text-sm">
                      {category.name}
                    </Label>
                  </div>
                ))}
              </div>
              <p className="text-sm text-gray-500">Để trống nếu áp dụng cho tất cả danh mục</p>
            </div>

            <div className="space-y-2">
              <Label className="block text-gray-700">Sản phẩm cụ thể</Label>
              <Select
                onValueChange={(value) => {
                  const shoeId = parseInt(value);
                  if (!selectedShoes.includes(shoeId)) {
                    setSelectedShoes([...selectedShoes, shoeId]);
                  }
                }}
              >
                <SelectTrigger>
                  <SelectValue placeholder="Chọn sản phẩm" />
                </SelectTrigger>
                <SelectContent>
                  {shoes.filter(shoe => !selectedShoes.includes(shoe.id)).map((shoe) => (
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
                      const shoe = shoes.find(s => s.id === shoeId);
                      return shoe ? (
                        <div key={shoeId} className="bg-blue-100 text-blue-800 px-2 py-1 rounded-md text-sm flex items-center gap-1">
                          {shoe.name}
                          <button
                            type="button"
                            onClick={() => setSelectedShoes(selectedShoes.filter(id => id !== shoeId))}
                            className="text-blue-600 hover:text-blue-800"
                          >
                            ×
                          </button>
                        </div>
                      ) : null;
                    })}
                  </div>
                </div>
              )}
              <p className="text-sm text-gray-500">Để trống nếu áp dụng cho tất cả sản phẩm</p>
            </div>

            <div className="mt-5 gap-4 sm:flex">
              <Button 
                type="button"
                className="bg-gray-500 text-indigo-50"
                onClick={() => setIsDialogOpen(false)}
              >
                Quay lại
              </Button>
              <Button className="bg-green-500 text-indigo-50">Lưu</Button>
              <Button 
                type="button"
                className="bg-red-500 text-indigo-50"
                onClick={() => setIsDialogOpen(false)}
              >
                Hủy
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}

export default DiscountManagement;
