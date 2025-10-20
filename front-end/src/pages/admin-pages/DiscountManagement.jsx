import { useState, useEffect } from "react";
// import axios from "axios";
import { Button } from "@/components/ui/button";
import { Table, TableBody, TableCaption, TableCell, TableHeader, TableRow } from "@/components/ui/table";
// import { Checkbox } from "@/components/ui/checkbox";
import api from "@/config/axios";
import UpdateDiscountForm from "./UpdateDiscountForm";  
import { Link } from "react-router-dom";
import { IoIosAddCircleOutline } from "react-icons/io";

export default function DiscountManagement() {
  const [discounts, setDiscounts] = useState([]);
  useEffect(() => {
    const fetchDiscounts = async () => {
      const { data } = await api.get("discounts");
      setDiscounts(data.result);
    };

    fetchDiscounts();
  }, []);

  return (
    <div className="p-4 max-w-full mx-auto bg-white rounded-lg shadow-md overflow-x-auto">
      <h1 className="text-2xl font-semibold text-gray-800 mb-6">Quản Lý Mã Giảm Giá</h1>
      <Button variant="outline" className="hover:bg-green-600 hover:text-white mb-4">
        <Link to={"/admin/discount-management/new"} className="flex p-2 items-center">
          <IoIosAddCircleOutline className="mr-2 h-5 w-5" />
          <span>Thêm</span>
        </Link>
      </Button>
      
      {/* Danh sách mã giảm giá */}
      <div className="mt-6 overflow-x-auto">
        <div className="min-w-full">
          <Table className="w-full table-auto">
            <TableCaption className="text-gray-500">Danh sách mã giảm giá.</TableCaption>
            <TableHeader className="bg-gray-100">
              <TableRow>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Chỉnh sửa</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Tên mã</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Loại</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Giá trị</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Giới hạn</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Danh mục</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Bắt đầu</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Kết thúc</TableCell>
                <TableCell className="p-2 font-semibold text-sm whitespace-nowrap">Trạng thái</TableCell>
              </TableRow>
            </TableHeader>
            <TableBody>
              {Array.isArray(discounts) && discounts.length > 0 ? (
                discounts.map(discount => (
                  <TableRow key={discount.id} className="hover:bg-gray-50">
                    <TableCell className="p-2 text-yellow-500 cursor-pointer">
                      <UpdateDiscountForm discountId={discount.id} />
                    </TableCell>
                    <TableCell className="p-2 text-sm font-medium max-w-[120px] truncate" title={discount.code}>
                      {discount.code}
                    </TableCell>
                    <TableCell className="p-2">
                      <span className={`px-2 py-1 rounded-full text-xs whitespace-nowrap ${
                        discount.discountType === 'PERCENTAGE' 
                          ? 'bg-blue-100 text-blue-800' 
                          : 'bg-green-100 text-green-800'
                      }`}>
                        {discount.discountType === 'PERCENTAGE' ? '%' : 'VNĐ'}
                      </span>
                    </TableCell>
                    <TableCell className="p-2 text-sm whitespace-nowrap">
                      {discount.discountType === 'PERCENTAGE' 
                        ? `${discount.percentage}%` 
                        : `${discount.fixedAmount?.toLocaleString()}đ`
                      }
                    </TableCell>
                    <TableCell className="p-2 text-sm">
                      {discount.usageLimit ? (
                        <div className="flex flex-col">
                          <span className="whitespace-nowrap">{discount.usageLimit} lần</span>
                          <span className="text-xs text-gray-500 whitespace-nowrap">
                            Còn: {discount.usageLimit - (discount.usedCount || 0)}
                          </span>
                        </div>
                      ) : (
                        <span className="text-xs text-gray-500">Không giới hạn</span>
                      )}
                    </TableCell>
                    <TableCell className="p-2 text-sm max-w-[100px] truncate" title={
                      discount.categories && discount.categories.length > 0 
                        ? discount.categories.join(', ') 
                        : 'Tất cả danh mục'
                    }>
                      {discount.categories && discount.categories.length > 0 
                        ? discount.categories.join(', ') 
                        : 'Tất cả'
                      }
                    </TableCell>
                    <TableCell className="p-2 text-xs whitespace-nowrap">
                      {new Date(discount.startDate).toLocaleDateString('vi-VN')}
                    </TableCell>
                    <TableCell className="p-2 text-xs whitespace-nowrap">
                      {new Date(discount.endDate).toLocaleDateString('vi-VN')}
                    </TableCell>
                    <TableCell className="p-2">
                      {discount.active ? (
                        <span className="px-2 py-1 rounded-full text-xs bg-green-100 text-green-800 whitespace-nowrap">Hoạt động</span>
                      ) : (
                        <span className="px-2 py-1 rounded-full text-xs bg-red-100 text-red-800 whitespace-nowrap">Tắt</span>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan="9" className="p-4 text-center text-gray-500">
                    Không có mã giảm giá trong danh sách
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </div>
      </div>
    </div>
  );
}


