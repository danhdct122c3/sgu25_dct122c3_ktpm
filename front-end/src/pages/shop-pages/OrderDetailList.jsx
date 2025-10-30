// src/pages/OrderDetailList.jsx
import React, { useState, useEffect } from "react";
import { format } from "date-fns";
import { ChevronDown, ChevronUp, Package } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import api from "@/config/axios";
import { useSelector } from "react-redux";
import { selectUser } from "@/store/auth";
import { formatterToVND } from "@/utils/formatter";
import { getImageUrl } from "@/utils/imageHelper";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export default function OrderDetailList() {
  const [userData, setUserData] = useState({});
  const [orderList, setOrderList] = useState([]);
  const navigate = useNavigate();

  const user = useSelector(selectUser);
  const userName = user ? user.sub : null;

  const fetchOrders = async () => {
    if (!userName) {
      console.log("Waiting for username...");
      return;
    }
    console.debug('fetchOrders: start', { userName });
    try {
      const response = await api.get(`/order-details/user/${userName}`);
      console.debug('fetchOrders: response length', response.data?.result?.length);
      setOrderList(response.data.result || []);
    } catch (error) {
      console.error("❌ Error fetching orders:", error);
      if (error.response?.status === 403) {
        console.error("🚫 Access denied - không có quyền xem đơn hàng này");
      }
    }
  };

  useEffect(() => {
    const fetchUser = async () => {
      if (!userName) return;
      try {
        const response = await api.get(`/users/profile?username=${userName}`);
        setUserData(response.data.result || {});
      } catch (error) {
        console.error(error);
      }
    };
    fetchUser();
  }, [userName]);

  useEffect(() => {
    fetchOrders();
  }, [userName]);

  return (
    <div className="mx-auto w-full max-w-screen-xl px-4">
      <h1 className="text-2xl font-bold mb-6">Đơn hàng đã đặt</h1>

      <div className="space-y-4">
        {orderList.length > 0 ? (
          orderList.map((order) => (
            <OrderCard
              key={String(order.id)}
              order={order}
              onOrderCancelled={fetchOrders}
            />
          ))
        ) : (
          <div>
            <p className="mb-4">Chưa có đơn hàng nào</p>
            <Button onClick={() => navigate("/")}>Shopping now</Button>
          </div>
        )}
      </div>
    </div>
  );
}

function OrderCard({ order, onOrderCancelled }) {
  React.useEffect(() => {
    console.debug('OrderCard mount', { orderId: order?.id });
    return () => console.debug('OrderCard unmount', { orderId: order?.id });
  }, [order?.id]);
  const [isOpen, setIsOpen] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);

  // Guards an toàn
  const items = order?.cartItems ?? [];
  const orderDate = order?.orderDate ? new Date(order.orderDate) : null;

  // Tính tiền an toàn
  const subTotal = items.reduce(
    (sum, it) => sum + (it?.price ?? 0) * (it?.quantity ?? 0),
    0
  );
  const shippingFee = order?.shippingFee ?? 0;
  const discount = order?.discount ?? 0;
  const grandTotal = order?.finalTotal ?? subTotal + shippingFee - discount;

  const handleCancelOrder = async () => {
    if (
      !window.confirm(
        "Bạn có chắc muốn hủy đơn hàng này? Số lượng sản phẩm sẽ được hoàn lại vào kho."
      )
    ) {
      return;
    }
    setIsCancelling(true);
    try {
      const response = await api.post(`/orders/${order.id}/cancel`);
      if (response.data.flag) {
        toast.success(
          "Đơn hàng đã được hủy thành công. Số lượng đã được hoàn lại vào kho."
        );
        onOrderCancelled?.();
      }
    } catch (error) {
      console.error("Error cancelling order:", error);
      toast.error(
        error.response?.data?.message ||
          "Không thể hủy đơn hàng. Vui lòng thử lại."
      );
    } finally {
      setIsCancelling(false);
    }
  };

  const getStatusStyle = (status) => {
    switch (status) {
      case "PAID":
        return "bg-green-100 text-green-800";
      case "PENDING":
        return "bg-yellow-100 text-yellow-800";
      case "CANCELED":
        return "bg-red-100 text-red-800";
      case "RECEIVED":
        return "bg-purple-100 text-purple-800";
      case "SHIPPED":
        return "bg-blue-100 text-blue-800";
      case "PAYMENT_FAILED":
        return "bg-red-100 text-red-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  const getStatusText = (status) => {
    switch (status) {
      case "PAID":
        return "Đã thanh toán";
      case "PENDING":
        return "Chờ xử lý";
      case "CANCELED":
        return "Đã hủy";
      case "RECEIVED":
        return "Đã nhận";
      case "SHIPPED":
        return "Đã giao";
      case "PAYMENT_FAILED":
        return "Thanh toán thất bại";
      default:
        return status;
    }
  };

  return (
    <Card className="overflow-hidden">
      <CardHeader>
        <CardTitle className="flex justify-between items-center">
          <span>Đơn hàng #{String(order?.id).slice(0, 8)}</span>
          <span
            className={`text-sm px-2 py-1 rounded ${getStatusStyle(
              order?.orderStatus
            )}`}
          >
            {getStatusText(order?.orderStatus)}
          </span>
        </CardTitle>
        <CardDescription>
          Đặt hàng vào ngày {orderDate ? format(orderDate, "dd/MM/yyyy") : "—"}
        </CardDescription>
      </CardHeader>
        <CardContent>
          {/* Collapsible bao trọn khu action + nội dung để trigger không bị “nhảy” */}
          <Collapsible open={isOpen} onOpenChange={setIsOpen}>
            {/* HÀNG ACTION: CỐ ĐỊNH VỊ TRÍ */}
            <div className="flex justify-end items-center gap-2 mb-4">
              {order?.orderStatus === "PENDING" && (
                <Button
                  variant="destructive"
                  onClick={handleCancelOrder}
                  disabled={isCancelling}
                >
                  {isCancelling ? "Đang hủy..." : "Hủy đơn"}
                </Button>
              )}

              {/* Trigger luôn nằm ở đây, không đi theo bảng */}
              <CollapsibleTrigger asChild>
                <Button variant="outline">
                  {isOpen ? (
                    <>Ẩn chi tiết <ChevronUp className="ml-2 h-4 w-4" /></>
                  ) : (
                    <>Xem chi tiết <ChevronDown className="ml-2 h-4 w-4" /></>
                  )}
                </Button>
              </CollapsibleTrigger>
            </div>

            {/* NỘI DUNG CHI TIẾT: đặt dưới, không ảnh hưởng vị trí nút */}
            <CollapsibleContent>
              <div className="rounded-lg border overflow-x-auto">
                {/* tăng min-w để ảnh to vẫn đẹp */}
                <table className="w-full min-w-[1160px] table-fixed">
                  {/* 👉 GỘP ảnh + sản phẩm chung 1 cột */}
                  <colgroup>
                    <col className="w-[52%]" /> {/* Ảnh + Tên sản phẩm */}
                    <col className="w-[12%]" /> {/* Số lượng */}
                    <col className="w-[18%]" /> {/* Đơn giá */}
                    <col className="w-[18%]" /> {/* Thành tiền */}
                  </colgroup>

                  <thead className="bg-muted/40">
                    <tr className="text-sm">
                      <th className="px-4 py-3 text-left">Sản phẩm</th>
                      <th className="px-4 py-3 text-center">Số lượng</th>
                      <th className="px-4 py-3 text-right">Đơn giá</th>
                      <th className="px-4 py-3 text-right">Thành tiền</th>
                    </tr>
                  </thead>

                  <tbody>
                    {(order?.cartItems ?? []).map((it) => {
                      const qty   = it?.quantity ?? 0;
                      const price = it?.price ?? 0;
                      const total = qty * price;

                      // Sử dụng helper chung để chuẩn hóa URL ảnh (ghép backend base nếu cần)
                      // Trả về null nếu không có, và dùng inline data URI làm fallback để tránh gọi dịch vụ bên ngoài
                      const INLINE_PLACEHOLDER =
                        'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-size="20">No image</text></svg>';

                      const rawImg =
                        getImageUrl(it?.imageUrl) ||
                        getImageUrl(it?.image) ||
                        getImageUrl(it?.images?.[0]?.url) ||
                        getImageUrl(it?.product?.imageUrl);

                      const img = rawImg || INLINE_PLACEHOLDER;

                      const name  = it?.productName ?? it?.product?.name ?? "Sản phẩm";
                      const size  = it?.size ? ` • Size ${it.size}` : "";
                      const sku   = it?.sku  ? ` • Mã: ${it.sku}` : "";

                      return (
                        <tr
                          key={it?.variantId ?? it?.sku ?? `${name}-${it?.size ?? ""}`}
                          className="border-t odd:bg-white even:bg-gray-50 hover:bg-muted/30 transition-colors"
                        >
                          {/* CỘT 1: Ảnh + tên sản phẩm (gộp chung) */}
                          <td className="px-4 py-3">
                            <div className="flex items-center gap-4">
                              {/* Ảnh: to hơn (160×160), object-contain, click mở lớn */}
                              <a
                                href={img}
                                target="_blank"
                                rel="noopener noreferrer"
                                title="Mở ảnh lớn"
                                className="inline-block w-40 h-40 bg-white grid place-items-center overflow-hidden rounded border flex-shrink-0"
                              >
                                <img
                                  src={img}
                                  alt={name}
                                  className="h-full w-full object-contain"
                                  onError={(e) => {
                                    // Don't point to external placeholder; use inline image data URI
                                    e.currentTarget.src = INLINE_PLACEHOLDER;
                                  }}
                                />
                              </a>

                              {/* Tên + info phụ */}
                              <div className="min-w-0">
                                <p className="font-medium truncate" title={name}>{name}</p>
                                <p className="text-xs text-muted-foreground truncate">{size}{sku}</p>
                              </div>
                            </div>
                          </td>

                          {/* CỘT 2: Số lượng */}
                          <td className="px-4 py-3 text-center">{qty}</td>

                          {/* CỘT 3: Đơn giá */}
                          <td className="px-4 py-3 text-right whitespace-nowrap">
                            {formatterToVND.format(price)}
                          </td>

                          {/* CỘT 4: Thành tiền */}
                          <td className="px-4 py-3 text-right font-semibold whitespace-nowrap">
                            {formatterToVND.format(total)}
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>

                  {/* TỔNG KẾT: BỎ “Tạm tính”, giữ Giảm giá, Ship, Tổng cộng */}
                  <tfoot>
                    <tr className="border-t">
                      <td colSpan={3} className="px-4 py-2 text-right text-sm">Giảm giá</td>
                      <td className="px-4 py-2 text-right text-sm">-{formatterToVND.format(discount)}</td>
                    </tr>
                    <tr className="border-t">
                      <td colSpan={3} className="px-4 py-2 text-right text-sm">Phí vận chuyển</td>
                      <td className="px-4 py-2 text-right text-sm">{formatterToVND.format(shippingFee)}</td>
                    </tr>
                    <tr className="border-t bg-muted/30">
                      <td colSpan={3} className="px-4 py-3 text-right font-semibold">Tổng cộng</td>
                      <td className="px-4 py-3 text-right font-semibold">{formatterToVND.format(grandTotal)}</td>
                    </tr>
                  </tfoot>
                </table>
              </div>

              {/* Footer nhỏ */}
              <div className="mt-3 text-sm text-muted-foreground">
                {(order?.cartItems ?? []).length} sản phẩm
              </div>
            </CollapsibleContent>
          </Collapsible>
        </CardContent>
    </Card>
  );
}
