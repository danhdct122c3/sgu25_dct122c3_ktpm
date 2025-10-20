import  { useEffect, useState } from "react";
import { AlertCircle, CheckCircle2, CreditCard, Calendar, Building, Hash, Banknote, Info } from "lucide-react";
// import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";
import api from "@/config/axios";
import { formatterToVND, formatPaymentDate } from "@/utils/formatter";
import { useDispatch } from "react-redux";
import { cartActions } from "@/store";

export default function PaymentCallbackPage() {
  const [status, setStatus] = useState("loading");
  const [paymentDetails, setPaymentDetails] = useState({});
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const handlePaymentCallback = async () => {
      try {
        const urlParams = new URLSearchParams(window.location.search);
        const orderInfo = urlParams.get('vnp_OrderInfo');

        console.log(urlParams.toString());

        const response = await api.get(
          `payment/payment-callback?${urlParams.toString()}`
        );
        console.log(response.data);

        if (response.data.flag === true) {
          const paymentData = response.data.result.paymentDetail;
          setStatus("success");
          setPaymentDetails({
            amount: paymentData.amount, 
            orderInfo: orderInfo,
            payDate: formatPaymentDate(paymentData.paymentDate),
            bankCode: paymentData.bankCode,
            transactionNo: paymentData.transactionNo,
            cardType: paymentData.cardType,
          });
          dispatch(cartActions.clearCart());
        } else {
          setStatus("error");
          setError(response.data.message || "Xác minh thanh toán thất bại");
        }
      } catch (error) {
        setStatus("error");
        setError(
          error.response?.data?.message || "Xác minh thanh toán thất bại"
        );
      }
    };
    handlePaymentCallback();
  }, [dispatch]);

  const handleReturn = () => {
    navigate("/");
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <Card className="max-w-lg w-full shadow-xl border-0">
        <CardHeader className="text-center pb-4">
          <CardTitle className="text-3xl font-bold">
            {status === "success" && (
              <div className="flex items-center justify-center gap-2 text-green-600">
                <CheckCircle2 className="h-8 w-8" />
                Thanh toán thành công
              </div>
            )}
            {status === "error" && (
              <div className="flex items-center justify-center gap-2 text-red-600">
                <AlertCircle className="h-8 w-8" />
                Thanh toán thất bại
              </div>
            )}
            {status === "loading" && (
              <div className="flex items-center justify-center gap-2 text-blue-600">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                Đang xử lý thanh toán
              </div>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-6">
          {status === "loading" && (
            <div className="flex flex-col items-center space-y-4 py-8">
              <div className="animate-spin rounded-full h-16 w-16 border-4 border-blue-200 border-t-blue-600"></div>
              <p className="text-gray-600 text-center">
                Đang xác minh thông tin thanh toán của bạn...
                <br />
                <span className="text-sm">Vui lòng không đóng trang này</span>
              </p>
            </div>
          )}

          {status === "success" && paymentDetails && (
            <div className="space-y-6">
              <div className="bg-green-50 border border-green-200 rounded-lg p-4">
                <div className="flex items-center gap-2 text-green-800 mb-2">
                  <CheckCircle2 className="h-5 w-5" />
                  <span className="font-semibold">Giao dịch thành công!</span>
                </div>
                <p className="text-green-700 text-sm">
                  Đơn hàng của bạn đã được thanh toán thành công. Chúng tôi sẽ xử lý và giao hàng trong thời gian sớm nhất.
                </p>
              </div>
              
              <div className="bg-white border border-gray-200 rounded-lg p-4 space-y-4">
                <h3 className="font-semibold text-gray-800 border-b pb-2">Chi tiết giao dịch</h3>
                <div className="grid gap-3">
                  <div className="flex items-center gap-3">
                    <Banknote className="h-5 w-5 text-green-600" />
                    <div className="flex-1">
                      <span className="text-sm text-gray-600">Số tiền</span>
                      <p className="font-semibold text-lg text-green-600">
                        {formatterToVND.format(paymentDetails.amount)}
                      </p>
                    </div>
                  </div>
                  
                  <div className="flex items-center gap-3">
                    <Info className="h-5 w-5 text-blue-600" />
                    <div className="flex-1">
                      <span className="text-sm text-gray-600">Thông tin đơn hàng</span>
                      <p className="font-medium">{paymentDetails.orderInfo}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-center gap-3">
                    <Calendar className="h-5 w-5 text-purple-600" />
                    <div className="flex-1">
                      <span className="text-sm text-gray-600">Ngày thanh toán</span>
                      <p className="font-medium">{paymentDetails.payDate}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-center gap-3">
                    <Building className="h-5 w-5 text-orange-600" />
                    <div className="flex-1">
                      <span className="text-sm text-gray-600">Ngân hàng</span>
                      <p className="font-medium">{paymentDetails.bankCode}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-center gap-3">
                    <Hash className="h-5 w-5 text-indigo-600" />
                    <div className="flex-1">
                      <span className="text-sm text-gray-600">Mã giao dịch</span>
                      <p className="font-medium font-mono text-sm">{paymentDetails.transactionNo}</p>
                    </div>
                  </div>
                  
                  <div className="flex items-center gap-3">
                    <CreditCard className="h-5 w-5 text-pink-600" />
                    <div className="flex-1">
                      <span className="text-sm text-gray-600">Loại thẻ</span>
                      <p className="font-medium">{paymentDetails.cardType}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {status === "error" && (
            <div className="space-y-4">
              <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <div className="flex items-center gap-2 text-red-800 mb-2">
                  <AlertCircle className="h-5 w-5" />
                  <span className="font-semibold">Giao dịch thất bại</span>
                </div>
                <p className="text-red-700 text-sm mb-3">{error}</p>
                <div className="text-sm text-red-600">
                  <p className="mb-1">• Kiểm tra lại thông tin thẻ và số dư tài khoản</p>
                  <p className="mb-1">• Thử lại sau vài phút</p>
                  <p>• Liên hệ ngân hàng nếu vấn đề vẫn tiếp tục</p>
                </div>
              </div>
            </div>
          )}

          <div className="flex gap-3 pt-4">
            <Button
              onClick={handleReturn}
              className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-medium py-3 px-6 rounded-lg transition-colors duration-200"
            >
              Quay lại trang chủ
            </Button>
            {status === "success" && (
              <Button
                onClick={() => navigate("/order-history")}
                variant="outline"
                className="flex-1 border-blue-600 text-blue-600 hover:bg-blue-50 font-medium py-3 px-6 rounded-lg transition-colors duration-200"
              >
                Xem đơn hàng
              </Button>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
