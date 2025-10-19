import React from "react";

import { format, set } from "date-fns";
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
import { useState, useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { selectUser } from "@/store/auth";
import { formatterToVND } from "@/utils/formatter";
import { ScrollArea } from "@radix-ui/react-scroll-area";
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
    
    try {
      const response = await api.get(`/order-details/user/${userName}`);
      console.log("📦 Order history:", response.data.result);
      setOrderList(response.data.result);
    } catch (error) {
      console.error("❌ Error fetching orders:", error);
      if (error.response?.status === 403) {
        console.error("🚫 Access denied - không có quyền xem đơn hàng này");
      }
    }
  };

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const response = await api.get(`/users/profile?username=${userName}`);
        setUserData(response.data.result);
      } catch (error) {
        console.error(error);
      }
    };
    fetchUser();
  }, [userName]);

  useEffect(() => {
    fetchOrders();
  }, [userName]);

  console.log(orderList);
  console.log(userData);

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-2xl font-bold mb-6">Đơn hàng đã đặt</h1>
      <div className="space-y-4">
        {orderList.length > 0 ? (
          orderList.map((order) => <OrderCard key={order.id} order={order} onOrderCancelled={fetchOrders} />)
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
  const [isOpen, setIsOpen] = useState(false);
  const [isCancelling, setIsCancelling] = useState(false);

  const handleCancelOrder = async () => {
    if (!window.confirm("Bạn có chắc muốn hủy đơn hàng này? Số lượng sản phẩm sẽ được hoàn lại vào kho.")) {
      return;
    }

    setIsCancelling(true);
    try {
      const response = await api.post(`/orders/${order.id}/cancel`);
      if (response.data.flag) {
        toast.success("Đơn hàng đã được hủy thành công. Số lượng đã được hoàn lại vào kho.");
        // Refresh order list
        if (onOrderCancelled) {
          onOrderCancelled();
        }
      }
    } catch (error) {
      console.error("Error cancelling order:", error);
      toast.error(error.response?.data?.message || "Không thể hủy đơn hàng. Vui lòng thử lại.");
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
    <Card>
      <CardHeader>
        <CardTitle className="flex justify-between items-center">
          <span>Đơn hàng #{order.id.slice(0, 8)}</span>
          <span
            className={`text-sm px-2 py-1 rounded ${getStatusStyle(
              order.orderStatus
            )}`}
          >
            {getStatusText(order.orderStatus)}
          </span>
        </CardTitle>
        <CardDescription>
          Đặt hàng vào ngày {format(new Date(order.orderDate), "dd/MM/yyyy")}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="flex justify-between items-center mb-4">
          <span className="font-semibold">
            Tổng tiền: {formatterToVND.format(order.finalTotal)} VNĐ
          </span>
          <div className="flex gap-2">
            {order.orderStatus === "PENDING" && (
              <Button 
                variant="destructive" 
                onClick={handleCancelOrder}
                disabled={isCancelling}
              >
                {isCancelling ? "Đang hủy..." : "Hủy đơn"}
              </Button>
            )}
            <Collapsible open={isOpen} onOpenChange={setIsOpen}>
              <CollapsibleTrigger asChild>
                <Button variant="outline">
                  {isOpen ? (
                    <>
                      Ẩn chi tiết
                      <ChevronUp className="ml-2 h-4 w-4" />
                    </>
                  ) : (
                    <>
                      Xem chi tiết
                      <ChevronDown className="ml-2 h-4 w-4" />
                    </>
                  )}
                </Button>
              </CollapsibleTrigger>
              <CollapsibleContent className="mt-4">
                <ul className="space-y-2">
                  {order.cartItems.map((item) => (
                    <li
                      key={item.variantId}
                      className="flex justify-between items-center"
                    >
                      <span className="me-2">
                        {item.productName}x{item.quantity}
                      </span>
                      <span className="font-semibold">
                        {formatterToVND.format(item.price * item.quantity)}
                      </span>
                    </li>
                  ))}
                </ul>
              </CollapsibleContent>
            </Collapsible>
          </div>
        </div>
        <div className="flex items-center text-sm text-muted-foreground">
          <Package className="mr-2 h-4 w-4" />
          <span>
            {order.cartItems.length}{" "}
            {order.cartItems.length === 1 ? "sản phẩm" : "sản phẩm"}
          </span>
        </div>
      </CardContent>
    </Card>
  );
}
