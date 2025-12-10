# Technical Workflows for ShoeShop Store

## 1. Login / Logout

The login/logout workflow involves frontend form submission, backend authentication service validation against the database, JWT token generation and validation, and session management. The system uses Spring Security for authentication, with user credentials stored encrypted in MySQL database. Logout invalidates the JWT token and clears client-side session data.

```mermaid
sequenceDiagram
    participant C as Customer (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database

    C->>FE: Navigate to /login
    FE->>FE: Display login form
    C->>FE: Enter email/password, submit
    FE->>BE: POST /auth/token (credentials)
    BE->>DB: SELECT user WHERE email=? AND active=1
    DB-->>BE: Return user record
    BE->>BE: Validate password hash
    BE->>BE: Generate JWT token (userId, role, exp)
    BE-->>FE: Return JWT token
    FE->>FE: Store token in localStorage
    FE-->>C: Redirect to role-specific dashboard
    Note over C,DB: Logout Flow
    C->>FE: Click logout
    FE->>FE: Remove token from localStorage
    FE->>BE: POST /auth/logout (token)
    BE->>BE: Invalidate token (optional blacklist)
    BE-->>FE: OK
    FE-->>C: Redirect to /login
```

## 2. View Catalog

The catalog viewing workflow demonstrates frontend pagination requests, backend service layer filtering and sorting logic, and database queries with JOIN operations. The system implements lazy loading for performance, with product images served from cloud storage. Filtering uses indexed database columns for optimal query performance.

```mermaid
sequenceDiagram
    participant C as Customer (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database

    C->>FE: Navigate to /shoes
    FE->>BE: GET /shoes?page=0&size=20
    BE->>DB: SELECT p.*, b.brand_name FROM products p<br>JOIN brands b ON p.brand_id = b.id<br>WHERE p.status=1 ORDER BY p.created_at DESC<br>LIMIT 20 OFFSET 0
    DB-->>BE: Return product list with brand names
    BE-->>FE: Return paginated product data
    FE-->>C: Render product grid with images
    C->>FE: Apply brand filter (Nike)
    FE->>BE: GET /shoes?brandId=1&page=0&size=20
    BE->>DB: SELECT p.*, b.brand_name FROM products p<br>JOIN brands b ON p.brand_id = b.id<br>WHERE p.brand_id=1 AND p.status=1<br>ORDER BY p.created_at DESC
    DB-->>BE: Return filtered products
    BE-->>FE: Return filtered data
    FE-->>C: Update product grid
    C->>FE: Click product for details
    FE->>BE: GET /shoes/{id}
    BE->>DB: SELECT p.*, pv.*, pi.* FROM products p<br>LEFT JOIN product_variants pv ON p.id = pv.product_id<br>LEFT JOIN product_images pi ON p.id = pi.product_id<br>WHERE p.id=?
    DB-->>BE: Return complete product data
    BE-->>FE: Return product details
    FE-->>C: Display product detail page
```

## 3. Order Checkout

The checkout workflow involves frontend form validation, backend order creation with transaction management, inventory reservation, and cart-to-order conversion. The system uses database transactions to ensure data consistency, with rollback capabilities for failed operations.

```mermaid
sequenceDiagram
    participant C as Customer (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database

    C->>FE: Click "Checkout" in cart
    FE->>BE: GET /cart (with JWT token)
    BE->>DB: SELECT ci.*, p.* FROM cart_items ci<br>JOIN products p ON ci.product_id = p.id<br>WHERE ci.user_id=? AND ci.session_id=?
    DB-->>BE: Return cart items
    BE-->>FE: Return cart summary
    FE-->>C: Display checkout form
    C->>FE: Enter shipping info, submit
    FE->>BE: POST /orders (cart data, shipping, JWT)
    BE->>BE: Validate JWT token and extract userId
    BE->>DB: BEGIN TRANSACTION
    BE->>DB: INSERT INTO orders (user_id, status, total, shipping_address)
    DB-->>BE: Return order_id
    BE->>DB: INSERT INTO order_items (order_id, product_id, quantity, price)
    BE->>DB: UPDATE products SET stock_quantity = stock_quantity - ? WHERE id=?
    BE->>DB: DELETE FROM cart_items WHERE user_id=?
    BE->>DB: COMMIT
    DB-->>BE: Transaction successful
    BE-->>FE: Return order confirmation
    FE-->>C: Display order success page
```

## 4. Payment (COD)

The COD payment workflow handles order status updates without external payment processing, focusing on inventory management and order lifecycle tracking. The system updates payment status and triggers order fulfillment processes.

```mermaid
sequenceDiagram
    participant C as Customer (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database

    C->>FE: Select COD payment method
    FE->>BE: POST /orders/{orderId}/pay (paymentMethod=COD, JWT)
    BE->>BE: Validate order ownership via JWT
    BE->>DB: UPDATE orders SET payment_method='COD', payment_status='PENDING'<br>WHERE id=? AND user_id=?
    DB-->>BE: Update successful
    BE-->>FE: Return payment confirmation
    FE-->>C: Display COD confirmation
    Note over BE,DB: Later - Staff Processing
    rect rgb(240, 248, 255)
        BE->>DB: UPDATE orders SET status='CONFIRMED' WHERE id=?
        DB-->>BE: Status updated
        BE->>DB: UPDATE orders SET status='SHIPPED' WHERE id=?
        DB-->>BE: Shipped
        BE->>DB: UPDATE orders SET status='DELIVERED', payment_status='COMPLETED' WHERE id=?
        DB-->>BE: Delivered
    end
```

## 5. Payment (VNPay)

The VNPay payment workflow demonstrates external payment gateway integration with secure redirects, callback processing, and signature validation. The system handles both success and failure scenarios with proper order status management.

```mermaid
sequenceDiagram
    participant C as Customer (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database
    participant VNP as VNPay Gateway

    C->>FE: Select VNPay, choose bank
    FE->>BE: POST /payment/create (orderId, bankCode, JWT)
    BE->>DB: SELECT * FROM orders WHERE id=? AND user_id=?
    DB-->>BE: Return order details
    BE->>BE: Generate VNPay parameters (amount, orderId, returnUrl, etc.)
    BE->>BE: Create secure hash with secret key
    BE->>VNP: POST payment request with signed parameters
    VNP-->>BE: Return payment URL
    BE->>DB: INSERT INTO payments (order_id, vnPay_txn_ref, status='PENDING')
    DB-->>BE: Payment record created
    BE-->>FE: Return VNPay payment URL
    FE-->>C: Redirect to VNPay gateway
    C->>VNP: Complete bank payment
    VNP->>VNP: Process payment with bank
    VNP->>BE: POST /payment/callback (success/failure parameters)
    BE->>BE: Validate VNPay signature
    alt Success Response (vnp_ResponseCode=00)
        BE->>DB: UPDATE orders SET status='CONFIRMED' WHERE id=?
        BE->>DB: UPDATE payments SET status='COMPLETED' WHERE order_id=?
    else Failure Response
        BE->>DB: UPDATE payments SET status='FAILED' WHERE order_id=?
    end
    BE-->>VNP: Return success acknowledgment
    VNP-->>C: Redirect to return URL with result
    C->>FE: Return to application
    FE->>BE: GET /orders/{orderId} to check status
    BE->>DB: SELECT status FROM orders WHERE id=?
    DB-->>BE: Return current status
    BE-->>FE: Return order status
    FE-->>C: Display success/failure page
```

## 6. Order Management (Staff/Manager/Admin)

The order management workflow shows role-based access control with different permission levels for staff, managers, and admins. The system enforces authorization at the API level while providing appropriate interfaces for each role.

```mermaid
sequenceDiagram
    participant S as Staff/Manager/Admin (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database

    S->>FE: Login with role-specific credentials
    FE->>BE: POST /auth/token
    BE->>DB: Validate credentials and role
    DB-->>BE: Return user with role
    BE->>BE: Generate JWT with role claims
    BE-->>FE: Return role-specific token
    FE-->>S: Redirect to role dashboard
    S->>FE: Access order management
    FE->>BE: GET /orders/staff/all (JWT token)
    BE->>BE: Validate JWT and extract role
    alt Role = Staff
        BE->>DB: SELECT * FROM orders WHERE status IN ('CREATED', 'CONFIRMED', 'PREPARING')
    else Role = Manager
        BE->>DB: SELECT * FROM orders (all statuses)
    else Role = Admin
        BE->>DB: SELECT * FROM orders (full access)
    end
    DB-->>BE: Return orders based on role
    BE-->>FE: Return filtered order list
    FE-->>S: Display orders with role-appropriate actions
    S->>FE: Update order status (e.g., CONFIRMED)
    FE->>BE: PUT /orders/{id}/status (newStatus, JWT)
    BE->>BE: Check role permissions for status transition
    BE->>DB: UPDATE orders SET status=?, updated_by=?, updated_at=NOW() WHERE id=?
    DB-->>BE: Update successful
    BE-->>FE: Return success
    FE-->>S: Show status update confirmation
```

## 7. Order Tracking (Customer)

The order tracking workflow demonstrates customer access to their order history with real-time status updates. The system provides personalized views while maintaining data security and performance.

```mermaid
sequenceDiagram
    participant C as Customer (Browser)
    participant FE as Frontend (Web App)
    participant BE as Backend (API)
    participant DB as Database

    C->>FE: Navigate to order history
    FE->>BE: GET /orders/user (JWT token)
    BE->>BE: Extract userId from JWT
    BE->>DB: SELECT o.*, oi.*, p.name FROM orders o<br>JOIN order_items oi ON o.id = oi.order_id<br>JOIN products p ON oi.product_id = p.id<br>WHERE o.user_id = ?<br>ORDER BY o.created_at DESC
    DB-->>BE: Return user's orders with items
    BE-->>FE: Return order history
    FE-->>C: Display order list with statuses
    C->>FE: Click specific order for details
    FE->>BE: GET /orders/{orderId}/user (JWT token)
    BE->>BE: Validate order ownership (userId matches)
    BE->>DB: SELECT o.*, oi.*, p.*, os.* FROM orders o<br>JOIN order_items oi ON o.id = oi.order_id<br>JOIN products p ON oi.product_id = p.id<br>LEFT JOIN order_status_history os ON o.id = os.order_id<br>WHERE o.id = ? AND o.user_id = ?
    DB-->>BE: Return detailed order info
    BE-->>FE: Return order details
    FE-->>C: Display complete order information
    Note over BE,DB: Status Update Notification
    rect rgb(240, 248, 255)
        BE->>DB: UPDATE orders SET status='SHIPPED' WHERE id=?
        DB-->>BE: Status updated
        BE->>BE: Send email notification to customer
        BE-->>C: Email: "Your order has shipped"
    end