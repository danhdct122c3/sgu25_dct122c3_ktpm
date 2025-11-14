
export default function WelcomeManager() {
  return (
    <div className="p-6 max-w-full h-screen mx-auto bg-white rounded-lg shadow-md">
      <div className="flex items-center justify-between">
        <h2 className="text-3xl font-bold">Bảng quản lý (Manager)</h2>
      </div>
      <div className="mt-8 grid gap-4">
        <div className="bg-green-50 border border-green-200 rounded-lg p-4">
          <p className="text-green-700">Chào mừng bạn đến khu vực dành cho Quản lý.</p>
          <p className="text-green-700">Sử dụng menu bên trái để truy cập: Quản lý giày, Giảm giá, Doanh thu, Lịch sử đơn hàng.</p>
        </div>
      </div>
    </div>
  );
}
