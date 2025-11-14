/* eslint-disable react/no-unescaped-entities */
import { useEffect, useState } from "react";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useSelector, useDispatch } from "react-redux";
import { selectItems } from "@/store/cart-slice";
import { selectUser } from "@/store/auth";
import api from "@/config/axios";
import { ToastContainer, toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { formatterToVND } from "../../utils/formatter";
import { cartActions } from "@/store";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";

import { FaHome } from "react-icons/fa";

function CheckOut() {
  const items = useSelector(selectItems);
  const { 
    originalPrice, 
    discountAmount, 
    storePickup, 
    tax, 
    total, 
    discountId,
    appliedCoupon,
    discountCategories,
    discountShoeIds,
    discountDescription
  } = useSelector((state) => state.cartTotal);

  const [loading, setLoading] = useState(false);
  const [userData, setUserData] = useState({});
  const [ setError] = useState(null);

  const user = useSelector(selectUser);
  const userName = user ? user.sub : null;
  const navigate = useNavigate();
  const dispatch = useDispatch();

  console.log(userData);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await api.get(`/users/profile?username=${userName}`);
        const data = response.data.result;
        setUserData(data);
      } catch (error) {
        console.log(error);
      }
    };

    if (userName) {
      fetchUserData();
    }
  }, [userName]);

  const validateUserData = () => {
    if (
      !userData.fullName ||
      !userData.phone ||
      !userData.address ||
      !userData.email
    ) {
      toast.error("Please update your profile");
      setTimeout(() => {
        navigate("/profile/me");
      }, 4000);
      return false;
    }

    return true;
  };

  const createOrder = async () => {
    const toastId = toast.loading("Creating order...");
    try {
      // Map cart items to match backend CartItemRequest format
      const orderItems = items.map(item => ({
        variantId: item.variantId,
        quantity: item.quantity,
        price: item.price,
        productId: item.productId
      }));

      const response = await api.post("/orders/create", {
        originalTotal: originalPrice,
        discountAmount: discountAmount || 0,
        finalTotal: total,
        discountId: discountId || null,
        userId: userData.id,
        items: orderItems,
      });

      if (response.data.flag) {
        toast.update(toastId, {
          render: "Order created successfully",
          type: "success",
          isLoading: false,
          autoClose: 3000,
        });

        setTimeout(() => {
          navigate("/order-history");
          dispatch(cartActions.clearCart());
        }, 4000);
        return response.data.result;
      }
      throw new Error("Failed to create order");
    } catch (error) {
      console.error(error);
      toast.update(toastId, {
        render: "Order creation failed",
        type: "error",
        isLoading: false,
        autoClose: 3000,
      });
      throw error;
    }
  };

  const initializeVNPayment = async (orderId) => {
    try {
      const ipResponse = await fetch("https://api.ipify.org?format=json");
      const ipData = await ipResponse.json();

      const paymentData = {
        orderId: orderId,
        ipAddress: ipData.ip,
      };

      const response = await api.post("payment/create-payment", paymentData);

      if (response.data.flag && response.data.result) {
        window.location.href = response.data.result;
      } else {
        throw new Error(
          response.data.message || "Failed to initialize payment"
        );
      }
    } catch (error) {
      console.error(error);
      throw new Error("Failed to initialize VNPAY payment");
    }
  };

  const handleOrderCOD = async () => {
    setLoading(true);
    try {
      if (!validateUserData()) {
        setLoading(false);
        return;
      }
      await createOrder();
      setLoading(false);
    } catch (error) {
      setError(error.message);
      setLoading(false);
    }
  };

  const handleOrderVNPay = async () => {
    setLoading(true);
    try {
      if (!validateUserData()) {
        setLoading(false);
        return;
      }

      // Lấy IP address
      const ipResponse = await fetch("https://api.ipify.org?format=json");
      const ipData = await ipResponse.json();

      // Map cart items
      const orderItems = items.map(item => ({
        variantId: item.variantId,
        quantity: item.quantity,
        price: item.price,
        productId: item.productId
      }));

      // Gọi endpoint mới: tạo đơn hàng và payment URL cùng lúc
      // Đơn hàng sẽ KHÔNG trừ tồn kho, chỉ trừ khi thanh toán thành công
      const response = await api.post("/payment/create-payment-order", {
        originalTotal: originalPrice,
        discountAmount: discountAmount || 0,
        finalTotal: total,
        discountId: discountId || null,
        userId: userData.id,
        items: orderItems,
        ipAddress: ipData.ip
      });

      if (response.data.flag && response.data.result) {
        // Chuyển đến VNPay
        window.location.href = response.data.result;
      } else {
        throw new Error(response.data.message || "Failed to create payment");
      }

    } catch (error) {
      console.error("Error creating VNPay payment:", error);
      toast.error("Tạo thanh toán thất bại. Vui lòng thử lại!");
      setLoading(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-6">
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
      <div className="px-4 py-2">
        <Breadcrumb>
          <BreadcrumbList>
            <BreadcrumbItem>
              <BreadcrumbLink href="/">
                <FaHome />
              </BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbLink href="/checkout">Thanh toán</BreadcrumbLink>
            </BreadcrumbItem>
          </BreadcrumbList>
        </Breadcrumb>
      </div>
      <div className="grid lg:grid-cols-2 gap-8">
        <div className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Thông tin khách hàng</CardTitle>
              <CardDescription>
                <Button variant="link" onClick={() => navigate("/profile/me")}>
                  Cập nhật thông tin
                </Button>
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="fullname">Họ và tên</Label>
                <Input 
                  id="fullname" 
                  value={userData.fullName || ''} 
                  onChange={(e) => setUserData({...userData, fullName: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  type="email"
                  value={userData.email || ''}
                  onChange={(e) => setUserData({...userData, email: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="phone">Số điện thoại</Label>
                <Input
                  id="phone"
                  type="tel"
                  value={userData.phone || ''}
                  onChange={(e) => setUserData({...userData, phone: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="address">Địa chỉ giao hàng</Label>
                <Input 
                  id="address" 
                  value={userData.address || ''} 
                  onChange={(e) => setUserData({...userData, address: e.target.value})}
                />
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Phương thức thanh toán</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <Button
                  onClick={handleOrderCOD}
                  className="w-full"
                  variant="outline"
                  disabled={loading}
                >
                  {loading ? "Processing..." : "Pay with COD"}
                </Button>
                <Button
                  onClick={handleOrderVNPay}
                  className="w-full bg-[#0D5CB6] hover:bg-[#0D5CB6]/90"
                  disabled={loading}
                >
                  {loading ? "Processing..." : "Pay with VNPay"}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        <div>
          <Card>
            <CardHeader>
              <CardTitle>Chi tiết đơn hàng</CardTitle>
            </CardHeader>
            <CardContent>
              <h3 className="font-semibold">Các sản phẩm trong giỏ hàng</h3>
              <ScrollArea className="h-72 p-4">
                {items.map((item) => (
                  <div
                    key={item.variantId}
                    className="flex items-center space-x-4 p-4"
                  >
                    <div className="relative h-20 w-20 overflow-hidden rounded-lg border">
                      <img src={item.imageUrl} alt="" />
                    </div>
                    <div className="flex-1 space-y-1">
                      <h4 className="font-medium">{item.name}</h4>
                      <p className="text-sm text-muted-foreground">
                        Số lượng: {item.quantity}
                      </p>
                      <p className="font-medium">
                        {formatterToVND.format(item.price)}
                      </p>
                    </div>
                  </div>
                ))}
              </ScrollArea>
            </CardContent>

            <Separator />

            <CardContent>
              <div className="space-y-4 my-4">
                <h3 className="font-semibold">Tổng quan đơn hàng</h3>
                <div className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">
                      Tổng giá niêm yết:
                    </span>
                    <span>{formatterToVND.format(originalPrice)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Thuế (10%):</span>
                    <span>{formatterToVND.format(tax)}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Phí giao hàng:</span>
                    <span>{formatterToVND.format(storePickup)}</span>
                  </div>
                  {discountAmount > 0 && (
                    <div className="space-y-2">
                      <div className="flex justify-between text-green-700">
                        <span className="text-muted-foreground">
                          Giảm giá {appliedCoupon && `(${appliedCoupon})`}
                        </span>
                        <span>-{formatterToVND.format(discountAmount)}</span>
                      </div>
                      
                      {discountDescription && (
                        <div className="text-sm text-gray-600 italic pl-4">
                          {discountDescription}
                        </div>
                      )}
                      
                      {(discountCategories?.length > 0 || discountShoeIds?.length > 0) && (
                        <div className="text-xs text-blue-600 bg-blue-50 p-3 rounded-lg ml-4">
                          <div className="font-medium mb-2 flex items-center gap-1">
                            <span className="w-2 h-2 bg-blue-600 rounded-full"></span>
                            Phạm vi áp dụng:
                          </div>
                          {discountCategories?.length > 0 && (
                            <div className="mb-1">
                              <span className="font-medium">Danh mục:</span> {discountCategories.join(", ")}
                            </div>
                          )}
                          {discountShoeIds?.length > 0 && (
                            <div className="mb-1">
                              <span className="font-medium">Sản phẩm cụ thể:</span> {discountShoeIds.length} sản phẩm được chọn
                            </div>
                          )}
                          {(!discountCategories?.length && !discountShoeIds?.length) && (
                            <div>
                              <span className="font-medium">Áp dụng:</span> Tất cả sản phẩm
                            </div>
                          )}
                        </div>
                      )}
                    </div>
                  )}
                  <Separator />
                  <div className="flex justify-between font-bold text-lg">
                    <span>Tổng cộng:</span>
                    <span>{formatterToVND.format(total)}</span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}

export default CheckOut;
