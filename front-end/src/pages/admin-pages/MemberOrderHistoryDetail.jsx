import React, { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import api from "@/config/axios";
import { Card } from "@/components/ui/card";
import { ArrowLeft, User, Mail, Phone, Home, ShoppingCart, Tag } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import { formatterToVND } from "@/utils/formatter";
import { getImageUrl } from "@/utils/imageHelper";
import { DollarSign, Package } from "lucide-react";
export default function MemberOrderHistoryDetail() {
  const { orderId, userId } = useParams(); // Get both orderId and userId from the URL
  const [orderDetail, setOrderDetail] = useState(null);
  const [userDetail, setUserDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchOrderDetail = async () => {
      try {
        const response = await api.get(`/order-details/order/${orderId}/user/${userId}`);
        setOrderDetail(response.data.result);
        setLoading(false);
      } catch (err) {
        setError("Không thể tải chi tiết đơn hàng.");
        setLoading(false);
      }
    };

    const fetchUserDetail = async () => {
      try {
        const response = await api.get(`/users/${userId}`);
        setUserDetail(response.data.result);
      } catch (err) {
        setError("Không thể tải thông tin khách hàng.");
      }
    };

    if (orderId && userId) {
      fetchOrderDetail();
      fetchUserDetail();
    } else {
      setError("Missing orderId or userId.");
      setLoading(false);
    }
  }, [orderId, userId]);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>{error}</div>;

  // Compute totals
  const subTotal = (orderDetail?.cartItems || []).reduce((s, it) => s + ((it?.price || 0) * (it?.quantity || 0)), 0);
  const shippingFee = orderDetail?.shippingFee ?? 0;
  const discount = orderDetail?.discount ?? 0;
  const finalTotal = orderDetail?.finalTotal ?? subTotal + shippingFee - discount;

  return (
    <div className="container mx-auto p-6">
      <div className="mb-6 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Link to={"/admin/member-order-history"}>
            <Button variant="ghost" className="flex items-center gap-2">
              <ArrowLeft className="h-4 w-4" />
              Quay lại
            </Button>
          </Link>
          <div>
            <h1 className="text-2xl font-semibold">Chi tiết đơn hàng</h1>
            <p className="text-sm text-muted-foreground">Mã đơn: <span className="font-medium">{orderDetail?.id}</span></p>
          </div>
        </div>
        <div className="text-right">
          <div className="inline-block px-3 py-1 rounded-full bg-gray-100 text-sm">{orderDetail?.orderStatus}</div>
          <div className="text-sm text-muted-foreground">{orderDetail?.orderDate ? new Date(orderDetail.orderDate).toLocaleString() : ''}</div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* LEFT: items list (span 2 cols) */}
        <div className="lg:col-span-2 space-y-4">
          <Card className="p-4">
            <h2 className="text-lg font-medium mb-3">Sản phẩm trong đơn</h2>
            <div className="divide-y">
              {(orderDetail?.cartItems || []).map((it) => {
                const INLINE_PLACEHOLDER = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="320" height="180"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-size="20">No image</text></svg>';
                const rawImg = getImageUrl(it?.imageUrl) || getImageUrl(it?.image) || getImageUrl(it?.images?.[0]?.url) || null;
                const img = rawImg || INLINE_PLACEHOLDER;

                return (
                  <div key={it.variantId} className="py-4 flex items-center gap-4">
                    <div className="w-24 h-24 bg-white rounded border grid place-items-center overflow-hidden">
                      <a href={img} target="_blank" rel="noreferrer" className="w-full h-full grid place-items-center">
                        <img src={img} alt={it.productName} className="object-contain w-full h-full" onError={(e)=>{e.currentTarget.src=INLINE_PLACEHOLDER}} />
                      </a>
                    </div>
                    <div className="flex-1">
                      <div className="font-medium text-lg">{it.productName}</div>
                      <div className="text-sm text-muted-foreground">Mã: {it.variantId} • Số lượng: {it.quantity}</div>
                    </div>
                    <div className="w-36 text-right">
                      <div className="text-sm text-muted-foreground">Đơn giá</div>
                      <div className="font-medium">{formatterToVND.format(it.price)}</div>
                    </div>
                    <div className="w-36 text-right">
                      <div className="text-sm text-muted-foreground">Thành tiền</div>
                      <div className="font-semibold">{formatterToVND.format((it.price||0)*(it.quantity||0))}</div>
                    </div>
                  </div>
                );
              })}
            </div>
          </Card>
        </div>

        {/* RIGHT: customer + totals */}
        <div className="space-y-4">
          <Card className="p-4">
            <h3 className="text-md font-medium mb-2">Khách hàng</h3>
            <div className="text-sm text-gray-700">
              <div className="mb-1"><strong>{userDetail?.fullName || '—'}</strong></div>
              <div className="text-muted-foreground">{userDetail?.email}</div>
              <div className="text-muted-foreground">{userDetail?.phone}</div>
              <div className="mt-2 text-sm">{userDetail?.address}</div>
            </div>
          </Card>

          <Card className="p-4">
            <h3 className="text-md font-medium mb-3">Tổng cộng đơn hàng</h3>
            <div className="text-sm">
              <div className="flex justify-between mb-2"><div className="text-muted-foreground">Tạm tính</div><div>{formatterToVND.format(subTotal)}</div></div>
              <div className="flex justify-between mb-2"><div className="text-muted-foreground">Giảm giá</div><div>-{formatterToVND.format(discount)}</div></div>
              <div className="flex justify-between mb-2"><div className="text-muted-foreground">Phí vận chuyển</div><div>{formatterToVND.format(shippingFee)}</div></div>
              <div className="border-t mt-2 pt-2 flex justify-between font-semibold text-lg"><div>Tổng cộng</div><div>{formatterToVND.format(finalTotal)}</div></div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
