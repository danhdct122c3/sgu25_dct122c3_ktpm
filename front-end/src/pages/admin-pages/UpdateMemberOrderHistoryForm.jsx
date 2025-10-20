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
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { ToastContainer, toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import api from "@/config/axios";
import { formatterToVND } from "@/utils/formatter";

const schema = z
  .object({
    username: z.string().min(2, { message: "username is required" }),
    finalTotal: z.number(),
    orderDate: z.string().refine((value) => !isNaN(Date.parse(value)), {
      message: "Start date must be a valid date",
    }),
    orderStatus: z.string(),
  });

// Định nghĩa quy trình chuyển đổi trạng thái theo nghiệp vụ
const ORDER_STATUS_TRANSITIONS = {
  CREATED: ['CONFIRMED', 'CANCELLED', 'REJECTED', 'PAID'],
  CONFIRMED: ['PREPARING', 'CANCELLED', 'REJECTED'],
  PREPARING: ['READY_FOR_DELIVERY', 'CANCELLED'],
  READY_FOR_DELIVERY: ['OUT_FOR_DELIVERY'],
  OUT_FOR_DELIVERY: ['DELIVERED'],
  PAID: ['CONFIRMED', 'CANCELLED', 'REJECTED'],
  DELIVERED: [], // Trạng thái cuối
  CANCELLED: [], // Trạng thái cuối
  REJECTED: [], // Trạng thái cuối
  PAYMENT_FAILED: [] // Trạng thái cuối
};

// Mapping trạng thái với hiển thị tiếng Việt
const ORDER_STATUS_DISPLAY = {
  CREATED: { label: '📝 Đã tạo', color: 'text-blue-600', bg: 'bg-blue-50' },
  CONFIRMED: { label: '✅ Đã xác nhận', color: 'text-green-600', bg: 'bg-green-50' },
  PREPARING: { label: '👨‍🍳 Đang chuẩn bị', color: 'text-yellow-600', bg: 'bg-yellow-50' },
  READY_FOR_DELIVERY: { label: '📦 Sẵn sàng giao hàng', color: 'text-purple-600', bg: 'bg-purple-50' },
  OUT_FOR_DELIVERY: { label: '🚚 Đang giao hàng', color: 'text-orange-600', bg: 'bg-orange-50' },
  DELIVERED: { label: '✅ Đã giao thành công', color: 'text-green-700', bg: 'bg-green-100' },
  CANCELLED: { label: '❌ Đã hủy', color: 'text-red-600', bg: 'bg-red-50' },
  REJECTED: { label: '🚫 Đã từ chối', color: 'text-red-700', bg: 'bg-red-100' },
  PAID: { label: '💳 Đã thanh toán', color: 'text-green-600', bg: 'bg-green-50' },
  PAYMENT_FAILED: { label: '💳 Thanh toán thất bại', color: 'text-red-600', bg: 'bg-red-50' }
};

export default function UpdateMemberOrderHistory ({ orderId }) {
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
  const [customerOrder, setCustomerOrder] = useState({});
  const [currentStatus, setCurrentStatus] = useState('');
  const [availableStatuses, setAvailableStatuses] = useState([]);
  const navigate = useNavigate();

  // Watch orderStatus để cập nhật trạng thái hiện tại
  const watchedStatus = watch('orderStatus');

  useEffect(() => {
    const fetchCustomerOrder = async () => {
      setIsLoading(true);
      try {
        const { data } = await api.get(`order-details/order/${orderId}`); 
        const orderData = data.result;
        
        setCustomerOrder(orderData);
        setCurrentStatus(orderData.orderStatus);
        
        // Tính toán các trạng thái có thể chuyển đổi
        const possibleStatuses = ORDER_STATUS_TRANSITIONS[orderData.orderStatus] || [];
        setAvailableStatuses([orderData.orderStatus, ...possibleStatuses]);
        
        reset({
          finalTotal: orderData.finalTotal, 
          orderStatus: orderData.orderStatus,
          username: orderData.username, 
          orderDate: new Date(orderData.orderDate).toISOString().slice(0, 16),
        });
      } catch (error) {
        console.error("Error fetching order:", error);
        toast.error("Không thể tải thông tin đơn hàng.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchCustomerOrder();
  }, [orderId, reset]);

  const onSubmit = async (data) => {
    setIsLoading(true);
    const toastId = toast.loading("Đang cập nhật đơn hàng...");
    
    try {
      // Kiểm tra tính hợp lệ của việc chuyển đổi trạng thái
      if (data.orderStatus !== currentStatus) {
        const allowedTransitions = ORDER_STATUS_TRANSITIONS[currentStatus] || [];
        if (!allowedTransitions.includes(data.orderStatus)) {
          toast.update(toastId, {
            render: `Không thể chuyển từ trạng thái "${ORDER_STATUS_DISPLAY[currentStatus]?.label}" sang "${ORDER_STATUS_DISPLAY[data.orderStatus]?.label}"`,
            type: "error",
            isLoading: false,
            autoClose: 3000,
          });
          return;
        }
      }

      const response = await api.put(`order-details/order/${orderId}`, {
        ...data,
        finalTotal: data.finalTotal, 
        username: data.username, 
        orderDate: new Date(data.orderDate).toISOString(),
      });

      if (response.status === 200 && response.data.flag) {
        toast.update(toastId, {
          render: "Cập nhật đơn hàng thành công!",
          type: "success",
          isLoading: false,
          autoClose: 2000,
        });
        setTimeout(() => {
          window.location.reload(); // Reload để cập nhật danh sách
        }, 2000);
      } else {
        toast.update(toastId, {
          render: response.data.message || "Cập nhật đơn hàng thất bại.",
          type: "error",
          isLoading: false,
          autoClose: 2000,
        });
      }
    } catch (error) {
      console.error("Error updating Order:", error);
      toast.update(toastId, {
        render: "Có lỗi xảy ra khi cập nhật đơn hàng.",
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
        theme="light" 
      />
      <DialogTrigger asChild>
        <Button variant="outline" className="hover:bg-slate-950 hover:text-white">
          Cập nhật
        </Button>
      </DialogTrigger>
      <DialogContent className="w-full max-w-3xl mx-auto max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold">Cập nhật trạng thái đơn hàng</DialogTitle>
          <DialogDescription>
            Quản lý trạng thái đơn hàng theo quy trình nghiệp vụ. Chỉ có thể chuyển sang các trạng thái được phép.
          </DialogDescription>
        </DialogHeader>
        
        {/* Hiển thị quy trình */}
        <div className="bg-gray-50 p-4 rounded-lg mb-4">
          <h4 className="font-semibold mb-2">Quy trình đơn hàng:</h4>
          <div className="text-sm text-gray-600 space-y-1">
            <div>📝 Đã tạo → ✅ Đã xác nhận → 👨‍🍳 Đang chuẩn bị → 📦 Sẵn sàng giao → 🚚 Đang giao → ✅ Đã giao</div>
            <div className="text-xs mt-2">
              <span className="font-medium">Lưu ý:</span> Có thể hủy (❌) hoặc từ chối (🚫) ở một số giai đoạn. Thanh toán (💳) có thể xảy ra ở nhiều thời điểm.
            </div>
          </div>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          {/* Thông tin đơn hàng (chỉ đọc) */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="username">Tên khách hàng</Label>
              <Input
                id="username"
                name="username"
                defaultValue={customerOrder.username}
                {...register("username")}
                disabled
                className="bg-gray-50"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="finalTotal">Tổng tiền</Label>
              <Input
                id="finalTotal"
                name="finalTotal"
                type="number"
                step="0.01"
                defaultValue={customerOrder.finalTotal}
                {...register("finalTotal", { valueAsNumber: true })}
                disabled
                className="bg-gray-50"
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="orderDate">Ngày đặt hàng</Label>
            <Input
              id="orderDate"
              type="datetime-local"
              defaultValue={customerOrder.orderDate}
              {...register("orderDate")}
              disabled
              className="bg-gray-50"
            />
          </div>

          {/* Trạng thái hiện tại */}
          <div className="space-y-2">
            <Label className="text-sm font-medium">Trạng thái hiện tại</Label>
            <div className={`p-3 rounded-lg ${ORDER_STATUS_DISPLAY[currentStatus]?.bg || 'bg-gray-50'}`}>
              <span className={`font-medium ${ORDER_STATUS_DISPLAY[currentStatus]?.color || 'text-gray-700'}`}>
                {ORDER_STATUS_DISPLAY[currentStatus]?.label || currentStatus}
              </span>
            </div>
          </div>

          {/* Chọn trạng thái mới */}
          <div className="space-y-2">
            <Label htmlFor="orderStatus" className="text-sm font-medium text-gray-700">
              Chuyển sang trạng thái
            </Label>
            <select 
              {...register("orderStatus")} 
              className="w-full p-3 border border-gray-300 rounded-lg shadow-sm focus:border-indigo-500 focus:ring-2 focus:ring-indigo-200 focus:outline-none transition-all duration-200"
            >
              {availableStatuses.map((status) => (
                <option key={status} value={status} className="py-2">
                  {ORDER_STATUS_DISPLAY[status]?.label || status}
                  {status === currentStatus ? ' (Hiện tại)' : ''}
                </option>
              ))}
            </select>
            
            {availableStatuses.length <= 1 ? (
              <p className="text-xs text-amber-600 mt-1">
                ⚠️ Đơn hàng đã ở trạng thái cuối, không thể thay đổi
              </p>
            ) : (
              <p className="text-xs text-gray-500 mt-1">
                Chỉ có thể chuyển sang các trạng thái được phép theo quy trình nghiệp vụ
              </p>
            )}
          </div>

          {/* Hiển thị cảnh báo nếu chuyển sang trạng thái đặc biệt */}
          {watchedStatus && watchedStatus !== currentStatus && (
            <div className="bg-yellow-50 border border-yellow-200 p-3 rounded-lg">
              <div className="flex items-start space-x-2">
                <span className="text-yellow-600">⚠️</span>
                <div className="text-sm">
                  <p className="font-medium text-yellow-800">Xác nhận thay đổi trạng thái</p>
                  <p className="text-yellow-700">
                    Từ: <span className="font-medium">{ORDER_STATUS_DISPLAY[currentStatus]?.label}</span>
                    {' → '}
                    Sang: <span className="font-medium">{ORDER_STATUS_DISPLAY[watchedStatus]?.label}</span>
                  </p>
                  {(watchedStatus === 'CANCELLED' || watchedStatus === 'REJECTED') && (
                    <p className="text-red-600 mt-1">
                      🔄 Lưu ý: Hành động này sẽ hoàn trả số lượng sản phẩm về kho
                    </p>
                  )}
                </div>
              </div>
            </div>
          )}

          <DialogFooter className="flex justify-between">
            <div className="text-xs text-gray-500">
              Mã đơn hàng: {orderId}
            </div>
            <Button 
              type="submit" 
              disabled={isLoading || availableStatuses.length <= 1} 
              className="bg-blue-600 text-white hover:bg-blue-700 disabled:bg-gray-400"
            >
              {isLoading ? 'Đang cập nhật...' : 'Lưu thay đổi'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
