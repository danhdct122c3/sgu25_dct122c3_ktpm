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
  const options = ["mới nhất", "cũ nhất"];

  // Controlled form state for create/edit
  const [form, setForm] = useState({
    code: "",
    discountType: "PERCENTAGE",
    percentage: "",
    fixedAmount: "",
    minimumOrderAmount: 0,
    usageLimit: "",
    description: "",
    startDate: null,
    endDate: null,
    categories: [], // values like 'SPORT'
    shoeIds: [], // strings like '123'
    isActive: true,
  });

  // Local date objects for calendar UI
  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const handleSelection = (value, discount = null) => {
    setSelectedOption(value);

    // Mở hộp thoại khi chọn 'edit'
    if (value === "edit" && discount) {
      setEditingDiscount(discount);

      // Normalize shoeIds to strings for consistent handling
      const shoeIds = (discount.shoeIds || []).map((id) => String(id));

      setForm({
        code: discount.code || "",
        discountType: discount.discountType || "PERCENTAGE",
        percentage: discount.percentage ?? "",
        fixedAmount: discount.fixedAmount ?? "",
        minimumOrderAmount: discount.minimumOrderAmount ?? 0,
        usageLimit: discount.usageLimit ?? "",
        description: discount.description ?? "",
        startDate: discount.startDate ? new Date(discount.startDate).toISOString() : null,
        endDate: discount.endDate ? new Date(discount.endDate).toISOString() : null,
        categories: discount.categories || [],
        shoeIds: shoeIds,
        isActive: discount.isActive === undefined ? true : discount.isActive,
      });

      setStartDate(discount.startDate ? new Date(discount.startDate) : null);
      setEndDate(discount.endDate ? new Date(discount.endDate) : null);

      setIsDialogOpen(true);
    }

    // If delete is selected, call delete API
    if (value === "delete" && discount) {
      if (confirm(`Xác nhận xóa mã ${discount.code}?`)) {
        api.delete(`/discounts/${discount.id}`)
          .then(() => {
            fetchDiscounts();
          })
          .catch(err => console.error(err));
      }
    }
  };

  const [discounts, setDiscounts] = React.useState([]);

  const fetchDiscounts = async () => {
    try {
      const { data } = await api.get("discounts");
      setDiscounts(data.result || []);
    } catch (err) {
      console.error("Failed to fetch discounts", err);
    }
  };

  useEffect(() => {
    fetchDiscounts();

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

  // Handlers for controlled form inputs
  const handleInputChange = (key, value) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const toggleCategory = (categoryValue) => {
    setForm((prev) => {
      const exists = prev.categories.includes(categoryValue);
      return {
        ...prev,
        categories: exists ? prev.categories.filter(c => c !== categoryValue) : [...prev.categories, categoryValue]
      };
    });
  };

  const addShoeByValue = (value) => {
    // value is shoe.id as string
    setForm(prev => {
      if (prev.shoeIds.includes(value)) return prev;
      return { ...prev, shoeIds: [...prev.shoeIds, value] };
    });
  };

  const removeShoe = (value) => {
    setForm(prev => ({ ...prev, shoeIds: prev.shoeIds.filter(id => id !== value) }));
  };

  const openCreateDialog = () => {
    setEditingDiscount(null);
    setForm({
      code: "",
      discountType: "PERCENTAGE",
      percentage: "",
      fixedAmount: "",
      minimumOrderAmount: 0,
      usageLimit: "",
      description: "",
      startDate: null,
      endDate: null,
      categories: [],
      shoeIds: [],
      isActive: true,
    });
    setStartDate(null);
    setEndDate(null);
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    try {
      // Validate dates
      if (startDate && endDate && startDate > endDate) {
        alert('Ngày bắt đầu phải trước ngày kết thúc');
        return;
      }

      const payload = {
        code: form.code,
        discountType: form.discountType,
        percentage: form.percentage === "" ? null : Number(form.percentage),
        fixedAmount: form.fixedAmount === "" ? null : Number(form.fixedAmount),
        minimumOrderAmount: form.minimumOrderAmount === "" ? 0 : Number(form.minimumOrderAmount),
        usageLimit: form.usageLimit === "" ? null : Number(form.usageLimit),
        description: form.description,
        startDate: startDate ? new Date(startDate).toISOString() : null,
        endDate: endDate ? new Date(endDate).toISOString() : null,
        categories: form.categories,
        shoeIds: form.shoeIds, // backend expects array of strings
        active: form.isActive,
      };

      if (editingDiscount) {
        await api.put(`/discounts/${editingDiscount.id}`, payload);
      } else {
        await api.post(`/discounts`, payload);
      }

      // refresh list and close
      await fetchDiscounts();
      setIsDialogOpen(false);
    } catch (err) {
      console.error('Save discount failed', err);
      alert('Lưu mã giảm giá thất bại');
    }
  };

  return (
    <div>
      <h1 className="mt-5 text-4xl font-bold" align="center">Quản lý mã giảm giá</h1>

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
            <DialogTitle align="center">{editingDiscount ? 'Chỉnh sửa mã giảm giá' : 'Tạo mã giảm giá'}</DialogTitle>
          </DialogHeader>

          <form className="space-y-4" onSubmit={(e) => { e.preventDefault(); handleSave(); }}>
            <div className="space-y-2">
              <Label htmlFor="code">Tên mã</Label>
              <Input id="code" type="text" value={form.code} onChange={(e) => handleInputChange('code', e.target.value)} />
            </div>

            <div className="space-y-2">
              <Label htmlFor="description">Mô tả</Label>
              <Input id="description" type="text" value={form.description} onChange={(e) => handleInputChange('description', e.target.value)} />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="active">Trạng thái</Label>
                <select id="active" className="w-full p-2 border rounded-md" value={String(form.isActive)} onChange={(e) => handleInputChange('isActive', e.target.value === 'true')}>
                  <option value="true">Hoạt động</option>
                  <option value="false">Tắt</option>
                </select>
              </div>

              <div>
                <Label htmlFor="discountType">Loại giảm giá</Label>
                <select id="discountType" className="w-full p-2 border rounded-md" value={form.discountType} onChange={(e) => handleInputChange('discountType', e.target.value)}>
                  <option value="PERCENTAGE">Phần trăm</option>
                  <option value="FIXED_AMOUNT">Số tiền cố định</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="percentage">Phần trăm</Label>
                <Input id="percentage" type="number" value={form.percentage ?? ''} onChange={(e) => handleInputChange('percentage', e.target.value === '' ? '' : Number(e.target.value))} disabled={form.discountType !== 'PERCENTAGE'} />
              </div>

              <div>
                <Label htmlFor="fixedAmount">Số tiền cố định</Label>
                <Input id="fixedAmount" type="number" value={form.fixedAmount ?? ''} onChange={(e) => handleInputChange('fixedAmount', e.target.value === '' ? '' : Number(e.target.value))} disabled={form.discountType !== 'FIXED_AMOUNT'} />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="minimumOrderAmount">Số tiền đơn hàng tối thiểu</Label>
                <Input id="minimumOrderAmount" type="number" value={form.minimumOrderAmount ?? 0} onChange={(e) => handleInputChange('minimumOrderAmount', e.target.value === '' ? 0 : Number(e.target.value))} />
              </div>

              <div>
                <Label htmlFor="usageLimit">Giới hạn sử dụng</Label>
                <Input id="usageLimit" type="number" placeholder="Để trống nếu không giới hạn" value={form.usageLimit ?? ''} onChange={(e) => handleInputChange('usageLimit', e.target.value === '' ? '' : Number(e.target.value))} />
              </div>
            </div>

            <div className="space-y-2">
              <Label>Danh mục áp dụng</Label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                {categories.map((category) => (
                  <div key={category.value} className="flex items-center space-x-2">
                    <Checkbox id={`category-${category.value}`} checked={form.categories.includes(category.value)} onCheckedChange={() => toggleCategory(category.value)} />
                    <Label htmlFor={`category-${category.value}`} className="text-sm">{category.name}</Label>
                  </div>
                ))}
              </div>
              <p className="text-sm text-gray-500">Để trống nếu áp dụng cho tất cả danh mục</p>
            </div>

            <div className="space-y-2">
              <Label>Sản phẩm cụ thể</Label>
              <Select onValueChange={(value) => addShoeByValue(value)}>
                <SelectTrigger>
                  <SelectValue placeholder="Chọn sản phẩm" />
                </SelectTrigger>
                <SelectContent>
                  {shoes.filter(shoe => !form.shoeIds.includes(String(shoe.id))).map((shoe) => (
                    <SelectItem key={shoe.id} value={shoe.id.toString()}>
                      {shoe.name} - {shoe.price?.toLocaleString()}đ
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              {form.shoeIds.length > 0 && (
                <div className="mt-2">
                  <p className="text-sm text-gray-600 mb-2">Sản phẩm đã chọn:</p>
                  <div className="flex flex-wrap gap-2">
                    {form.shoeIds.map((shoeId) => {
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

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <Label htmlFor="startDate">Ngày bắt đầu</Label>
                <Input id="startDate" type="datetime-local" value={form.startDate ? new Date(form.startDate).toISOString().slice(0, 16) : ''} onChange={(e) => handleInputChange('startDate', e.target.value ? new Date(e.target.value).toISOString() : null)} />
              </div>

              <div>
                <Label htmlFor="endDate">Ngày kết thúc</Label>
                <Input id="endDate" type="datetime-local" value={form.endDate ? new Date(form.endDate).toISOString().slice(0, 16) : ''} onChange={(e) => handleInputChange('endDate', e.target.value ? new Date(e.target.value).toISOString() : null)} />
              </div>
            </div>

            <div className="flex flex-col sm:flex-row justify-end gap-4 mt-6">
              <Button type="button" onClick={() => setIsDialogOpen(false)} className="px-6 py-2 bg-gray-500 text-white rounded-md hover:bg-gray-600">Hủy</Button>
              <Button type="submit" className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700">Lưu thay đổi</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Add button to create new discount */}
      <div className="fixed bottom-6 right-6">
        <Button className="bg-blue-600 text-white rounded-full px-4 py-2" onClick={openCreateDialog}>Tạo mã mới</Button>
      </div>
    </div>
  );
}

export default DiscountManagement;
