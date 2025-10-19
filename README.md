# Shoe Store Web Application

This capstone project is a comprehensive web application for managing an online shoe store, developed using Spring Boot for the back-end and ReactJS for the front-end. The system is designed to streamline the shopping experience for customers while providing efficient management tools for administrators.

## Features
### 🔐 **Authentication**

- **2 Trang Đăng Nhập Riêng Biệt:**
  - 👤 **User Login**: `/login` - Dành cho khách hàng
  - 🛡️ **Admin Login**: `/admin/login` - Dành cho Admin/Manager
- **Auto-redirect dựa vào Role:**
  - User login thành công → Redirect về trang chủ `/`
  - Admin login thành công → Redirect về dashboard `/admin`
  - Manager login thành công → Redirect về `/manager`
- **Role-based Access Control:**
  - Admin có thể login ở cả 2 trang nhưng được redirect về `/admin`
  - User chỉ có thể login tại `/login`
  - Tự động kiểm tra quyền và redirect về đúng trang login nếu unauthorized

### 👥 Khách Hàng (User)

- 🔍 **Browsing & Search**
- View detailed product information, including images, descriptions, price, and availability.
- Add products to the cart and manage the cart items.
- Secure user authentication and profile management.
- Online payment integration for seamless checkout.

### Admin Features:

- CRUD operations for products, brands, and categories.
- Upload and manage product images.
- Handle orders and track order statuses (e.g., Pending, Paid, Shipped).
- Generate sales reports for analytics.

## Technology Stack:

- **Back-end**: Spring Boot, Spring Data JPA, Spring Security, JWT for authentication.
- **Front-end**: ReactJS, Redux Toolkit for state management, Axios for API integration.
- **Database**: MySQL for data persistence.
- **Deployment**: Docker for containerization and AWS for back-end hosting.

