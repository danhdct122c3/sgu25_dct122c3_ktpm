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

// Định nghĩa quy trình chuyển đổi trạng thái theo nghiệp vụ - chỉ cho phép chuyển sang trạng thái tiếp theo
// COD: CREATED → CONFIRMED → PREPARING → READY_FOR_DELIVERY → OUT_FOR_DELIVERY → DELIVERED
// VNPay: CREATED → PAID (tự động) → CONFIRMED → PREPARING → READY_FOR_DELIVERY → OUT_FOR_DELIVERY → DELIVERED
const ORDER_STATUS_WORKFLOW = {
  CREATED: {
    nextCOD: 'CONFIRMED', // COD bỏ qua PAID
    nextVNPay: null, // VNPay phải đợi hệ thống tự chuyển sang PAID, admin không được chuyển thủ công
    labelCOD: 'Xác nhận đơn hàng',
    labelVNPay: 'Chờ thanh toán',
    icon: '✅',
    descriptionCOD: 'Xác nhận đơn hàng COD và bắt đầu xử lý',
    descriptionVNPay: 'Đơn VNPay đang chờ khách thanh toán. Trạng thái PAID sẽ tự động cập nhật khi thanh toán thành công.'
  },
  PAID: {
    next: 'CONFIRMED', 
    label: 'Xác nhận đơn hàng',
    icon: '✅',
    description: 'Xác nhận đơn hàng đã thanh toán và bắt đầu xử lý'
  },
  CONFIRMED: {
    next: 'PREPARING',
    label: 'Bắt đầu chuẩn bị',
    icon: '👨‍🍳',
    description: 'Đóng gói và chuẩn bị hàng'
  },
  PREPARING: {
    next: 'READY_FOR_DELIVERY',
    label: 'Sẵn sàng giao hàng',
    icon: '📦',
    description: 'Hàng đã đóng gói xong'
  },
  READY_FOR_DELIVERY: {
    next: 'OUT_FOR_DELIVERY',
    label: 'Bắt đầu giao hàng',
    icon: '🚚',
    description: 'Shipper đã nhận và đang giao'
  },
  OUT_FOR_DELIVERY: {
    next: 'DELIVERED',
    label: 'Hoàn thành giao hàng',
    icon: '✅',
    description: 'Khách hàng đã nhận hàng'
  },
  DELIVERED: null, // Trạng thái cuối - không có nút tiếp theo
  CANCELLED: null, // Trạng thái cuối
  REJECTED: null, // Trạng thái cuối  
  PAYMENT_FAILED: null // Trạng thái cuối
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
  const [isProcessing, setIsProcessing] = useState(false);
  const [isCODOrder, setIsCODOrder] = useState(false); // Phân biệt COD vs VNPay
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCustomerOrder = async () => {
      setIsLoading(true);
      try {
        const { data } = await api.get(`order-details/order/${orderId}`); 
        const orderData = data.result;
        
        setCustomerOrder(orderData);
        setCurrentStatus(orderData.orderStatus);
        
        // Phân biệt COD (không có paymentDetail) vs VNPay (có paymentDetail)
        setIsCODOrder(!orderData.bankCode && !orderData.cardType);
        
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

  // Chuyển sang trạng thái tiếp theo trong workflow
  const handleNextStatus = async () => {
    const workflow = ORDER_STATUS_WORKFLOW[currentStatus];
    if (!workflow) return;

    // Xác định trạng thái tiếp theo dựa trên COD hay VNPay
    let nextStatus;
    if (currentStatus === 'CREATED') {
      if (isCODOrder) {
        nextStatus = workflow.nextCOD; // COD: CREATED → CONFIRMED
      } else {
        // VNPay: không cho admin chuyển thủ công từ CREATED → PAID
        toast.error("⚠️ Đơn VNPay phải đợi khách thanh toán. Trạng thái PAID sẽ tự động cập nhật!", {
          autoClose: 4000
        });
        return;
      }
    } else {
      nextStatus = workflow.next;
    }

    if (!nextStatus) return;

    setIsProcessing(true);
    const toastId = toast.loading(`Đang chuyển sang: ${ORDER_STATUS_DISPLAY[nextStatus]?.label}...`);
    
    try {
      const response = await api.put(`order-details/order/${orderId}`, {
        finalTotal: customerOrder.finalTotal, 
        username: customerOrder.username, 
        orderDate: new Date(customerOrder.orderDate).toISOString(),
        orderStatus: nextStatus
      });

      if (response.status === 200 && response.data.flag) {
        toast.update(toastId, {
          render: `✅ Đã chuyển sang: ${ORDER_STATUS_DISPLAY[nextStatus]?.label}`,
          type: "success",
          isLoading: false,
          autoClose: 2000,
        });
        setTimeout(() => {
          window.location.reload();
        }, 2000);
      } else {
        toast.update(toastId, {
          render: response.data.message || "Cập nhật trạng thái thất bại.",
          type: "error",
          isLoading: false,
          autoClose: 2000,
        });
      }
    } catch (error) {
      console.error("Error updating status:", error);
      toast.update(toastId, {
        render: error.response?.data?.message || "Có lỗi xảy ra khi cập nhật trạng thái.",
        type: "error",
        isLoading: false,
        autoClose: 2000,
      });
    } finally {
      setIsProcessing(false);
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
        
        <div className="space-y-6">
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
          <div className="space-y-3">
            <Label className="text-sm font-medium flex items-center gap-2">
              <span>📍</span>
              <span>Trạng thái hiện tại</span>
              {isCODOrder && (
                <span className="text-xs bg-green-100 text-green-700 px-2 py-0.5 rounded-full font-medium">
                  💵 COD
                </span>
              )}
              {!isCODOrder && (
                <span className="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full font-medium">
                  💳 VNPay
                </span>
              )}
            </Label>
            <div className={`p-4 rounded-lg border-2 ${ORDER_STATUS_DISPLAY[currentStatus]?.bg || 'bg-gray-50'} border-${ORDER_STATUS_DISPLAY[currentStatus]?.color?.split('-')[1] || 'gray'}-200`}>
              <div className="flex items-center justify-between">
                <span className={`font-bold text-lg ${ORDER_STATUS_DISPLAY[currentStatus]?.color || 'text-gray-700'}`}>
                  {ORDER_STATUS_DISPLAY[currentStatus]?.label || currentStatus}
                </span>
                {ORDER_STATUS_WORKFLOW[currentStatus] === null && (
                  <span className="text-xs bg-gray-200 text-gray-600 px-2 py-1 rounded-full">
                    Trạng thái cuối
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Action buttons - Chuyển trạng thái hoặc từ chối */}
          {ORDER_STATUS_WORKFLOW[currentStatus] !== null && (
            <div className="space-y-3 pt-4 border-t border-gray-200">
              <Label className="text-sm font-medium flex items-center gap-2">
                <span>⚡</span>
                <span>Hành động tiếp theo</span>
              </Label>
              
              <div className="grid grid-cols-1 gap-3">
                {/* Button chuyển sang trạng thái tiếp theo */}
                {(() => {
                  const workflow = ORDER_STATUS_WORKFLOW[currentStatus];
                  
                  // Xác định trạng thái tiếp theo và nội dung button
                  let nextStatus, buttonLabel, buttonDescription;
                  
                  if (currentStatus === 'CREATED') {
                    if (isCODOrder) {
                      nextStatus = workflow?.nextCOD;
                      buttonLabel = workflow?.labelCOD;
                      buttonDescription = workflow?.descriptionCOD;
                    } else {
                      // VNPay - không hiển thị button, chỉ hiển thị thông báo
                      return (
                        <div className="bg-amber-50 border border-amber-200 rounded-lg p-4">
                          <div className="flex items-start gap-3">
                            <span className="text-2xl">⏳</span>
                            <div>
                              <p className="text-sm font-medium text-amber-800">
                                Đang chờ khách hàng thanh toán VNPay
                              </p>
                              <p className="text-xs text-amber-700 mt-1">
                                {workflow?.descriptionVNPay}
                              </p>
                              <div className="mt-2 text-xs bg-white border border-amber-300 rounded p-2">
                                <p className="font-medium text-amber-900">💡 Lưu ý:</p>
                                <p className="text-amber-700">
                                  • Trạng thái sẽ <strong>tự động</strong> chuyển sang PAID khi thanh toán thành công<br/>
                                  • Admin <strong>không được</strong> chuyển thủ công để tránh nhầm lẫn<br/>
                                  • Nếu khách không thanh toán, có thể từ chối đơn hàng bên dưới
                                </p>
                              </div>
                            </div>
                          </div>
                        </div>
                      );
                    }
                  } else {
                    nextStatus = workflow?.next;
                    buttonLabel = workflow?.label;
                    buttonDescription = workflow?.description;
                  }

                  // Hiển thị button nếu có trạng thái tiếp theo
                  if (nextStatus) {
                    return (
                      <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
                        <div className="mb-2">
                          <p className="text-sm font-medium text-gray-700">
                            {workflow?.icon} {buttonLabel}
                          </p>
                          <p className="text-xs text-gray-500 mt-1">
                            {buttonDescription}
                          </p>
                        </div>
                        <Button
                          type="button"
                          onClick={handleNextStatus}
                          disabled={isProcessing}
                          className="w-full bg-blue-600 hover:bg-blue-700 text-white font-medium py-2.5"
                        >
                          {isProcessing ? (
                            <span className="flex items-center justify-center gap-2">
                              <span className="animate-spin">⏳</span>
                              <span>Đang xử lý...</span>
                            </span>
                          ) : (
                            <span className="flex items-center justify-center gap-2">
                              <span>→</span>
                              <span>Chuyển sang: {ORDER_STATUS_DISPLAY[nextStatus]?.label}</span>
                            </span>
                          )}
                        </Button>
                      </div>
                    );
                  }
                  
                  return null;
                })()}
              </div>
            </div>
          )}

          {/* Thông báo khi đã ở trạng thái cuối */}
          {ORDER_STATUS_WORKFLOW[currentStatus] === null && (
            <div className="bg-gray-50 border border-gray-200 rounded-lg p-4 text-center">
              <p className="text-sm text-gray-600">
                ℹ️ Đơn hàng đã ở trạng thái cuối, không thể thay đổi
              </p>
            </div>
          )}
        </div>

        <DialogFooter className="border-t pt-4">
          <div className="w-full flex justify-between items-center">
            <div className="text-xs text-gray-500">
              Mã đơn hàng: <span className="font-mono font-medium">{orderId}</span>
            </div>
          </div>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
