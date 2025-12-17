# ShoeShop Screen Design Documentation

## 1. Đăng nhập (Login) - SCR001

### 1.1. Thông tin chung màn hình
- **Screen ID**: SCR001
- **Screen Name**: Đăng nhập
- **Module / Feature**: Authentication
- **Actor sử dụng**: User, Admin, Staff, Manager
- **Mục tiêu màn hình**: Cho phép người dùng xác thực và truy cập vào hệ thống
- **Mức độ ưu tiên kiểm thử**: High

### 1.2. Trạng thái điều hướng (Navigation Flow)
- **Mô tả luồng truy cập**: Người dùng truy cập từ trang chủ hoặc các trang yêu cầu đăng nhập
- **Trang trước**: Home (/)
- **Điều kiện truy cập**: Chưa đăng nhập
- **Hành động dẫn đến**: Click link "Đăng nhập" hoặc tự động redirect
- **Trang sau**: Home (/) nếu thành công
- **Khi nhấn nút nào → điều hướng tới đâu**: Nút "Đăng nhập" → Home nếu success
- **Điều kiện chuyển trang**: Success → Home, Error → ở lại

### 1.3. URL & Routing
- **URL màn hình**: /login
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép
- **Redirect nếu không hợp lệ**: Không áp dụng

### 1.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Login | POST | /api/auth/login | Xác thực thông tin đăng nhập | Submit form |

### 1.5. Cấu trúc Layer
- **Header**: Logo, Tên hệ thống
- **Main Content**: Form đăng nhập với các trường input
- **Action Area**: Nút "Đăng nhập"
- **Notification / Message**: Thông báo lỗi nếu có

### 1.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Tên người dùng | Textbox | Yes | Yes | Không rỗng, không ký tự đặc biệt |  |
| 2 | Mật khẩu | Password | Yes | Yes | >=8 ký tự |  |
| 3 | Ghi nhớ tài khoản | Checkbox | No | Yes | - |  |
| 4 | Quên mật khẩu | Link | No | - | - |  |
| 5 | Đăng nhập | Button | - | - | - |  |
| 6 | Tạo tài khoản | Link | No | - | - |  |

### 1.7. Trạng thái màn hình
- **Loading state**: Khi gửi request đăng nhập
- **Normal state**: Form trống, sẵn sàng nhập
- **Error state**: Hiển thị thông báo lỗi khi sai thông tin
- **Success state**: Chuyển hướng sau đăng nhập thành công

### 1.8. Quy tắc nghiệp vụ
- Username bắt buộc nhập, không chứa ký tự đặc biệt
- Password bắt buộc nhập, tối thiểu 8 ký tự
- Nếu tick "Ghi nhớ tài khoản", lưu thông tin vào cookie/localStorage
- Nếu quên mật khẩu, chuyển đến trang reset

### 1.9. Điểm cần kiểm thử
- Validate input fields
- API integration cho login
- Điều hướng sau login
- Xử lý lỗi và security

### 1.10. Liên kết Test Case
- TC_LOGIN_001, TC_LOGIN_002, TC_LOGIN_003

## 2. Đăng ký (Register) - SCR05

### 2.1. Thông tin chung màn hình
- **Screen ID**: SCR05
- **Screen Name**: Đăng ký
- **Module / Feature**: Authentication
- **Actor sử dụng**: User
- **Mục tiêu màn hình**: Cho phép người dùng tạo tài khoản mới
- **Mức độ ưu tiên kiểm thử**: High

### 2.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ trang đăng nhập, click link "Tạo tài khoản"
- **Trang trước**: Đăng nhập (/login)
- **Điều kiện truy cập**: Chưa đăng nhập
- **Hành động dẫn đến**: Click link từ trang đăng nhập
- **Trang sau**: Đăng nhập (/login) nếu thành công
- **Khi nhấn nút nào → điều hướng tới đâu**: Nút "Đăng ký" → Đăng nhập nếu success
- **Điều kiện chuyển trang**: Success → Đăng nhập, Error → ở lại

### 2.3. URL & Routing
- **URL màn hình**: /register
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép
- **Redirect nếu không hợp lệ**: Không áp dụng

### 2.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Register | POST | /api/auth/register | Tạo tài khoản mới | Submit form |

### 2.5. Cấu trúc Layer
- **Header**: Logo, Tên hệ thống
- **Main Content**: Form đăng ký
- **Action Area**: Nút "Đăng ký"
- **Notification / Message**: Thông báo lỗi hoặc thành công

### 2.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Tên người dùng | Textbox | Yes | Yes | Không rỗng, không ký tự đặc biệt |  |
| 2 | Email | Textbox | Yes | Yes | Định dạng email hợp lệ |  |
| 3 | Mật khẩu | Password | Yes | Yes | >=8 ký tự |  |
| 4 | Xác nhận mật khẩu | Password | Yes | Yes | Phải khớp với mật khẩu |  |
| 5 | Chấp nhận điều khoản | Checkbox | Yes | Yes | Phải tick |  |
| 6 | Đăng ký | Button | - | - | - |  |
| 7 | Đăng nhập | Link | No | - | - |  |

### 2.7. Trạng thái màn hình
- **Loading state**: Khi gửi request đăng ký
- **Normal state**: Form trống
- **Error state**: Hiển thị lỗi validation
- **Success state**: Thông báo thành công và chuyển hướng

### 2.8. Quy tắc nghiệp vụ
- Tất cả trường bắt buộc
- Email phải hợp lệ
- Password >=8 ký tự, xác nhận phải khớp
- Phải chấp nhận điều khoản
- Username unique

### 2.9. Điểm cần kiểm thử
- Validate tất cả fields
- API register
- Điều hướng
- Xử lý duplicate username/email

### 2.10. Liên kết Test Case
- TC_REGISTER_001, TC_REGISTER_002, TC_REGISTER_003

## 3. Home - SCR004

### 3.1. Thông tin chung màn hình
- **Screen ID**: SCR004
- **Screen Name**: Home
- **Module / Feature**: Home Page
- **Actor sử dụng**: All users
- **Mục tiêu màn hình**: Hiển thị trang chủ với sản phẩm và thông tin chính
- **Mức độ ưu tiên kiểm thử**: High

### 3.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Trang mặc định khi truy cập website
- **Trang trước**: N/A
- **Điều kiện truy cập**: Không yêu cầu
- **Hành động dẫn đến**: Truy cập root URL
- **Trang sau**: Các trang sản phẩm, chi tiết, etc.
- **Khi nhấn nút nào → điều hướng tới đâu**: Click sản phẩm → Chi tiết sản phẩm, Menu → Các trang khác
- **Điều kiện chuyển trang**: Always success

### 3.3. URL & Routing
- **URL màn hình**: /
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép
- **Redirect nếu không hợp lệ**: Không áp dụng

### 3.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get featured products | GET | /api/products/featured | Load sản phẩm nổi bật | Page load |
| 2 | Get categories | GET | /api/categories | Load danh mục | Page load |

### 3.5. Cấu trúc Layer
- **Header**: Navigation, Logo, Search, Cart, User menu
- **Main Content**: Banner carousel, Featured products, Categories
- **Footer**: Links, Contact info

### 3.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Banner Carousel | Image Slider | No | No | - |  |
| 2 | Featured Products | List | No | No | - |  |
| 3 | Categories | Menu | No | No | - |  |
| 4 | Search Bar | Textbox | No | Yes | - |  |
| 5 | Cart Icon | Link | No | - | - |  |
| 6 | User Menu | Dropdown | No | - | - |  |

### 3.7. Trạng thái màn hình
- **Loading state**: Khi load dữ liệu
- **Normal state**: Hiển thị đầy đủ
- **Empty state**: Nếu không có dữ liệu
- **Error state**: Lỗi load API

### 3.8. Quy tắc nghiệp vụ
- Hiển thị banner carousel
- Sản phẩm nổi bật theo thứ tự
- Responsive design

### 3.9. Điểm cần kiểm thử
- Load performance
- Responsive UI
- API integration
- Navigation links

### 3.10. Liên kết Test Case
- TC_HOME_001, TC_HOME_002, TC_HOME_003

## 4. Sản phẩm (Products) - SCR006

### 4.1. Thông tin chung màn hình
- **Screen ID**: SCR006
- **Screen Name**: Sản phẩm
- **Module / Feature**: Product Listing
- **Actor sử dụng**: All users
- **Mục tiêu màn hình**: Hiển thị danh sách sản phẩm với bộ lọc
- **Mức độ ưu tiên kiểm thử**: High

### 4.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ home, menu, hoặc search
- **Trang trước**: Home (/)
- **Điều kiện truy cập**: Không yêu cầu
- **Hành động dẫn đến**: Click menu "Sản phẩm" hoặc search
- **Trang sau**: Chi tiết sản phẩm
- **Khi nhấn nút nào → điều hướng tới đâu**: Click sản phẩm → Chi tiết
- **Điều kiện chuyển trang**: Always

### 4.3. URL & Routing
- **URL màn hình**: /shoes
- **Tham số URL**: ?brand=...&size=...&price=...
- **Điều kiện truy cập URL trực tiếp**: Cho phép
- **Redirect nếu không hợp lệ**: Không áp dụng

### 4.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get products | GET | /api/products | Load danh sách sản phẩm | Page load, filter change |
| 2 | Get brands | GET | /api/brands | Load danh sách brand | Initial load |

### 4.5. Cấu trúc Layer
- **Header**: Navigation
- **Sidebar**: Filters (brand, size, price)
- **Main Content**: Product grid
- **Footer**: Pagination

### 4.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Product Grid | List | No | No | - |  |
| 2 | Brand Filter | Checkbox | No | Yes | - |  |
| 3 | Size Filter | Checkbox | No | Yes | - |  |
| 4 | Price Filter | Range | No | Yes | - |  |
| 5 | Sort By | Select | No | Yes | - |  |
| 6 | Pagination | Buttons | No | - | - |  |

### 4.7. Trạng thái màn hình
- **Loading state**: Khi load products
- **Normal state**: Hiển thị grid
- **Empty state**: Không có sản phẩm
- **Error state**: Lỗi API

### 4.8. Quy tắc nghiệp vụ
- Bộ lọc theo brand, size, price
- Sort theo popularity, price
- Pagination

### 4.9. Điểm cần kiểm thử
- Filtering functionality
- Sorting
- Pagination
- API calls

### 4.10. Liên kết Test Case
- TC_PRODUCTS_001, TC_PRODUCTS_002, TC_PRODUCTS_003

## 5. Chi tiết sản phẩm (Product Detail) - SCR007

### 5.1. Thông tin chung màn hình
- **Screen ID**: SCR007
- **Screen Name**: Chi tiết sản phẩm
- **Module / Feature**: Product Detail
- **Actor sử dụng**: All users
- **Mục tiêu màn hình**: Hiển thị chi tiết sản phẩm và cho phép thêm vào giỏ
- **Mức độ ưu tiên kiểm thử**: High

### 5.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ danh sách sản phẩm
- **Trang trước**: Sản phẩm (/shoes)
- **Điều kiện truy cập**: Không yêu cầu
- **Hành động dẫn đến**: Click sản phẩm
- **Trang sau**: Giỏ hàng (/cart)
- **Khi nhấn nút nào → điều hướng tới đâu**: "Thêm vào giỏ" → Giỏ hàng
- **Điều kiện chuyển trang**: Success → Giỏ hàng

### 5.3. URL & Routing
- **URL màn hình**: /shoes/:id
- **Tham số URL**: id (product id)
- **Điều kiện truy cập URL trực tiếp**: Cho phép
- **Redirect nếu không hợp lệ**: 404 hoặc Home

### 5.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get product detail | GET | /api/products/:id | Load chi tiết sản phẩm | Page load |
| 2 | Add to cart | POST | /api/cart/add | Thêm vào giỏ | Click button |

### 5.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Product images, info, variants
- **Action Area**: Size select, Add to cart

### 5.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Product Images | Gallery | No | No | - |  |
| 2 | Product Name | Text | No | No | - |  |
| 3 | Price | Text | No | No | - |  |
| 4 | Size Select | Select | Yes | Yes | - |  |
| 5 | Quantity | Input | Yes | Yes | >0 |  |
| 6 | Add to Cart | Button | - | - | - |  |

### 5.7. Trạng thái màn hình
- **Loading state**: Load product
- **Normal state**: Hiển thị đầy đủ
- **Error state**: Sản phẩm không tồn tại

### 5.8. Quy tắc nghiệp vụ
- Chọn size bắt buộc
- Quantity >0
- Stock check

### 5.9. Điểm cần kiểm thử
- Load product detail
- Add to cart
- Size/quantity validation

### 5.10. Liên kết Test Case
- TC_PRODUCT_DETAIL_001, TC_PRODUCT_DETAIL_002, TC_PRODUCT_DETAIL_003

## 6. Giỏ hàng (Cart) - SCR008

### 6.1. Thông tin chung màn hình
- **Screen ID**: SCR008
- **Screen Name**: Giỏ hàng
- **Module / Feature**: Shopping Cart
- **Actor sử dụng**: User (logged in)
- **Mục tiêu màn hình**: Hiển thị và quản lý giỏ hàng
- **Mức độ ưu tiên kiểm thử**: High

### 6.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ header cart icon hoặc sau add to cart
- **Trang trước**: Chi tiết sản phẩm
- **Điều kiện truy cập**: Đã login
- **Hành động dẫn đến**: Click cart icon
- **Trang sau**: Checkout (/checkout)
- **Khi nhấn nút nào → điều hướng tới đâu**: "Thanh toán" → Checkout
- **Điều kiện chuyển trang**: Success → Checkout

### 6.3. URL & Routing
- **URL màn hình**: /cart
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu login
- **Redirect nếu không hợp lệ**: Login

### 6.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get cart | GET | /api/cart | Load giỏ hàng | Page load |
| 2 | Update quantity | PUT | /api/cart/update | Cập nhật số lượng | Change quantity |
| 3 | Remove item | DELETE | /api/cart/remove | Xóa item | Click remove |

### 6.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Cart items list, totals
- **Action Area**: Update cart, Proceed to checkout

### 6.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Cart Items | List | No | No | - |  |
| 2 | Quantity | Input | Yes | Yes | >0 |  |
| 3 | Remove Button | Button | - | - | - |  |
| 4 | Subtotal | Text | No | No | - |  |
| 5 | Total | Text | No | No | - |  |
| 6 | Update Cart | Button | - | - | - |  |
| 7 | Checkout | Button | - | - | - |  |

### 6.7. Trạng thái màn hình
- **Loading state**: Load cart
- **Normal state**: Hiển thị items
- **Empty state**: Giỏ trống
- **Error state**: Lỗi update

### 6.8. Quy tắc nghiệp vụ
- Update quantity real-time
- Recalculate totals
- Stock validation

### 6.9. Điểm cần kiểm thử
- Add/remove items
- Update quantity
- Total calculation
- Checkout flow

### 6.10. Liên kết Test Case
- TC_CART_001, TC_CART_002, TC_CART_003

## 7. Lịch sử đơn hàng (Order History) - SCR009

### 7.1. Thông tin chung màn hình
- **Screen ID**: SCR009
- **Screen Name**: Lịch sử đơn hàng
- **Module / Feature**: Order History
- **Actor sử dụng**: User (logged in)
- **Mục tiêu màn hình**: Hiển thị lịch sử đơn hàng
- **Mức độ ưu tiên kiểm thử**: Medium

### 7.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ user menu
- **Trang trước**: Profile
- **Điều kiện truy cập**: Đã login
- **Hành động dẫn đến**: Click "Lịch sử đơn hàng"
- **Trang sau**: Chi tiết đơn hàng
- **Khi nhấn nút nào → điều hướng tới đâu**: Click đơn hàng → Chi tiết
- **Điều kiện chuyển trang**: Always

### 7.3. URL & Routing
- **URL màn hình**: /order-history
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu login
- **Redirect nếu không hợp lệ**: Login

### 7.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get order history | GET | /api/orders/history | Load lịch sử đơn hàng | Page load |

### 7.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Order list

### 7.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Order List | Table | No | No | - |  |
| 2 | Order ID | Text | No | No | - |  |
| 3 | Date | Text | No | No | - |  |
| 4 | Status | Text | No | No | - |  |
| 5 | Total | Text | No | No | - |  |

### 7.7. Trạng thái màn hình
- **Loading state**: Load orders
- **Normal state**: Hiển thị list
- **Empty state**: Không có đơn hàng
- **Error state**: Lỗi load

### 7.8. Quy tắc nghiệp vụ
- Hiển thị theo thứ tự thời gian
- Status: Pending, Shipped, Delivered

### 7.9. Điểm cần kiểm thử
- Load order history
- Display details
- Status updates

### 7.10. Liên kết Test Case
- TC_ORDER_HISTORY_001, TC_ORDER_HISTORY_002

## 8. Thanh toán (Checkout) - SCR0010

### 8.1. Thông tin chung màn hình
- **Screen ID**: SCR0010
- **Screen Name**: Thanh toán
- **Module / Feature**: Checkout
- **Actor sử dụng**: User (logged in)
- **Mục tiêu màn hình**: Hoàn tất thanh toán đơn hàng
- **Mức độ ưu tiên kiểm thử**: High

### 8.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ giỏ hàng
- **Trang trước**: Giỏ hàng (/cart)
- **Điều kiện truy cập**: Đã login, có items trong cart
- **Hành động dẫn đến**: Click "Thanh toán"
- **Trang sau**: Payment success hoặc fail
- **Khi nhấn nút nào → điều hướng tới đâu**: "Thanh toán" → Payment gateway
- **Điều kiện chuyển trang**: Success → Success page

### 8.3. URL & Routing
- **URL màn hình**: /checkout
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu login và có cart
- **Redirect nếu không hợp lệ**: Cart hoặc Login

### 8.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Create order | POST | /api/orders/create | Tạo đơn hàng | Submit form |
| 2 | Payment | POST | /api/payment/process | Xử lý thanh toán | Submit payment |

### 8.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Billing info, Payment method, Order summary
- **Action Area**: Place order

### 8.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Name | Textbox | Yes | Yes | Không rỗng |  |
| 2 | Address | Textarea | Yes | Yes | Không rỗng |  |
| 3 | Phone | Textbox | Yes | Yes | Định dạng phone |  |
| 4 | Email | Textbox | Yes | Yes | Email hợp lệ |  |
| 5 | Payment Method | Radio | Yes | Yes | - |  |
| 6 | Place Order | Button | - | - | - |  |

### 8.7. Trạng thái màn hình
- **Loading state**: Process payment
- **Normal state**: Form checkout
- **Error state**: Validation errors
- **Success state**: Order confirmed

### 8.8. Quy tắc nghiệp vụ
- Tất cả fields bắt buộc
- Validate address, phone, email
- Payment method selection
- Order total calculation

### 8.9. Điểm cần kiểm thử
- Form validation
- Payment integration
- Order creation
- Error handling

### 8.10. Liên kết Test Case
- TC_CHECKOUT_001, TC_CHECKOUT_002, TC_CHECKOUT_003

## 9. Quản lý giày (Manage Shoes) - SCR011

### 9.1. Thông tin chung màn hình
- **Screen ID**: SCR011
- **Screen Name**: Quản lý giày
- **Module / Feature**: Shoe Management
- **Actor sử dụng**: Manager, Admin
- **Mục tiêu màn hình**: Quản lý danh sách sản phẩm giày
- **Mức độ ưu tiên kiểm thử**: High

### 9.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ dashboard admin/manager
- **Trang trước**: Dashboard
- **Điều kiện truy cập**: Đã login, có quyền admin/manager
- **Hành động dẫn đến**: Click menu "Quản lý giày"
- **Trang sau**: Thêm mới giày, Chi tiết giày
- **Khi nhấn nút nào → điều hướng tới đâu**: "Thêm mới" → Add shoe, "Edit" → Edit shoe
- **Điều kiện chuyển trang**: Always

### 9.3. URL & Routing
- **URL màn hình**: /admin/manage-shoes hoặc /manager/manage-shoes
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu có quyền
- **Redirect nếu không hợp lệ**: Unauthorized

### 9.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get shoes | GET | /api/admin/shoes | Load danh sách giày | Page load |
| 2 | Delete shoe | DELETE | /api/admin/shoes/:id | Xóa giày | Click delete |
| 3 | Update shoe status | PUT | /api/admin/shoes/:id/status | Cập nhật trạng thái | Change status |

### 9.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Shoe list table
- **Action Area**: Add new, Edit, Delete buttons

### 9.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Shoe List | Table | No | No | - |  |
| 2 | Add New Button | Button | - | - | - |  |
| 3 | Edit Button | Button | - | - | - |  |
| 4 | Delete Button | Button | - | - | - |  |
| 5 | Status Dropdown | Select | No | Yes | - |  |

### 9.7. Trạng thái màn hình
- **Loading state**: Load shoes
- **Normal state**: Hiển thị table
- **Empty state**: Không có giày
- **Error state**: Lỗi load

### 9.8. Quy tắc nghiệp vụ
- Chỉ admin/manager có quyền
- Validate permissions
- Confirm delete

### 9.9. Điểm cần kiểm thử
- Load shoe list
- CRUD operations
- Permission checks
- UI responsiveness

### 9.10. Liên kết Test Case
- TC_MANAGE_SHOES_001, TC_MANAGE_SHOES_002, TC_MANAGE_SHOES_003

## 10. Quản lý đơn hàng (Manage Orders) - SCR012

### 10.1. Thông tin chung màn hình
- **Screen ID**: SCR012
- **Screen Name**: Quản lý đơn hàng
- **Module / Feature**: Order Management
- **Actor sử dụng**: Manager, Admin
- **Mục tiêu màn hình**: Quản lý đơn hàng của members
- **Mức độ ưu tiên kiểm thử**: High

### 10.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ dashboard manager/admin
- **Trang trước**: Dashboard
- **Điều kiện truy cập**: Đã login, có quyền manager/admin
- **Hành động dẫn đến**: Click "Quản lý đơn hàng"
- **Trang sau**: Chi tiết đơn hàng
- **Khi nhấn nút nào → điều hướng tới đâu**: Click đơn hàng → Chi tiết
- **Điều kiện chuyển trang**: Always

### 10.3. URL & Routing
- **URL màn hình**: /manager/member-order-history hoặc /admin/orders
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu có quyền
- **Redirect nếu không hợp lệ**: Unauthorized

### 10.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get orders | GET | /api/admin/orders | Load danh sách đơn hàng | Page load |
| 2 | Update order status | PUT | /api/admin/orders/:id/status | Cập nhật trạng thái | Change status |

### 10.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Order list table
- **Action Area**: View details, Update status

### 10.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Order List | Table | No | No | - |  |
| 2 | Status Dropdown | Select | No | Yes | - |  |
| 3 | View Details | Button | - | - | - |  |

### 10.7. Trạng thái màn hình
- **Loading state**: Load orders
- **Normal state**: Hiển thị table
- **Empty state**: Không có đơn hàng
- **Error state**: Lỗi load

### 10.8. Quy tắc nghiệp vụ
- Chỉ manager/admin có quyền
- Update status theo quy trình

### 10.9. Điểm cần kiểm thử
- Load order list
- Update status
- Permission checks

### 10.10. Liên kết Test Case
- TC_MANAGE_ORDERS_001, TC_MANAGE_ORDERS_002

## 11. Thêm mới sản phẩm (Add New Product) - SCR013

### 11.1. Thông tin chung màn hình
- **Screen ID**: SCR013
- **Screen Name**: Thêm mới sản phẩm
- **Module / Feature**: Add New Shoe
- **Actor sử dụng**: Manager, Admin
- **Mục tiêu màn hình**: Thêm sản phẩm giày mới
- **Mức độ ưu tiên kiểm thử**: High

### 11.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ quản lý giày
- **Trang trước**: Quản lý giày
- **Điều kiện truy cập**: Đã login, có quyền
- **Hành động dẫn đến**: Click "Thêm mới"
- **Trang sau**: Quản lý giày
- **Khi nhấn nút nào → điều hướng tới đâu**: "Lưu" → Quản lý giày
- **Điều kiện chuyển trang**: Success → Back to list

### 11.3. URL & Routing
- **URL màn hình**: /manager/manage-shoes/new
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu có quyền
- **Redirect nếu không hợp lệ**: Unauthorized

### 11.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Create shoe | POST | /api/admin/shoes | Tạo giày mới | Submit form |

### 11.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Form thêm giày
- **Action Area**: Save, Cancel

### 11.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Name | Textbox | Yes | Yes | Không rỗng |  |
| 2 | Brand | Select | Yes | Yes | - |  |
| 3 | Price | Number | Yes | Yes | >0 |  |
| 4 | Description | Textarea | No | Yes | - |  |
| 5 | Images | File Upload | No | Yes | - |  |
| 6 | Save | Button | - | - | - |  |
| 7 | Cancel | Button | - | - | - |  |

### 11.7. Trạng thái màn hình
- **Loading state**: Submit form
- **Normal state**: Form trống
- **Error state**: Validation errors
- **Success state**: Redirect back

### 11.8. Quy tắc nghiệp vụ
- Validate all required fields
- Image upload
- Unique name check

### 11.9. Điểm cần kiểm thử
- Form validation
- Image upload
- API create
- Redirect

### 11.10. Liên kết Test Case
- TC_ADD_SHOE_001, TC_ADD_SHOE_002

## 12. Quản lý mã giám giá (Discount Management) - SCR0014

### 12.1. Thông tin chung màn hình
- **Screen ID**: SCR0014
- **Screen Name**: Quản lý mã giám giá
- **Module / Feature**: Discount Management
- **Actor sử dụng**: Manager, Admin
- **Mục tiêu màn hình**: Quản lý các mã giảm giá
- **Mức độ ưu tiên kiểm thử**: Medium

### 12.2. Trạng thái điều hướng
- **Mô tả luồng truy cập**: Từ dashboard manager/admin
- **Trang trước**: Dashboard
- **Điều kiện truy cập**: Đã login, có quyền
- **Hành động dẫn đến**: Click "Quản lý giảm giá"
- **Trang sau**: Thêm mới discount
- **Khi nhấn nút nào → điều hướng tới đâu**: "Thêm mới" → Add discount
- **Điều kiện chuyển trang**: Always

### 12.3. URL & Routing
- **URL màn hình**: /manager/discount-management
- **Tham số URL**: Không có
- **Điều kiện truy cập URL trực tiếp**: Cho phép nếu có quyền
- **Redirect nếu không hợp lệ**: Unauthorized

### 12.4. API liên quan
| STT | API Name | Method | Endpoint | Mục đích | Khi nào gọi |
|-----|----------|--------|----------|----------|-------------|
| 1 | Get discounts | GET | /api/admin/discounts | Load danh sách discount | Page load |
| 2 | Delete discount | DELETE | /api/admin/discounts/:id | Xóa discount | Click delete |

### 12.5. Cấu trúc Layer
- **Header**: Navigation
- **Main Content**: Discount list table
- **Action Area**: Add new, Delete

### 12.6. Danh sách UI Item
| STT | Item Name | Type | Required | Editable | Validation | Ghi chú |
|-----|-----------|------|----------|----------|------------|---------|
| 1 | Discount List | Table | No | No | - |  |
| 2 | Add New Button | Button | - | - | - |  |
| 3 | Delete Button | Button | - | - | - |  |

### 12.7. Trạng thái màn hình
- **Loading state**: Load discounts
- **Normal state**: Hiển thị table
- **Empty state**: Không có discount
- **Error state**: Lỗi load

### 12.8. Quy tắc nghiệp vụ
- Validate discount codes
- Expiration dates

### 12.9. Điểm cần kiểm thử
- Load discount list
- CRUD operations
- Code validation

### 12.10. Liên kết Test Case
- TC_DISCOUNT_MANAGEMENT_001, TC_DISCOUNT_MANAGEMENT_002