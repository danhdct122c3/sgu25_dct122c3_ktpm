# Backend Test Cases

## Authentication & Access Control

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|---------------|---------------|------------|-----------------|
| TC_AUTH_001 | Successful Login | Customer | Authentication & Access Control | None | POST /auth/token<br>Body: {"username": "customer@example.com", "password": "password123"} | username: customer@example.com<br>password: password123 | 200 OK, JWT token in response |
| TC_AUTH_002 | Invalid Login Credentials | Customer | Authentication & Access Control | None | POST /auth/token<br>Body: {"username": "customer@example.com", "password": "wrongpass"} | username: customer@example.com<br>password: wrongpass | 401 Unauthorized, error message |
| TC_AUTH_003 | Introspect Valid Token | Customer | Authentication & Access Control | Valid JWT | POST /auth/introspect<br>Body: {"token": "valid_jwt"} | token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9... (valid) | 200 OK, valid: true |
| TC_AUTH_004 | Introspect Invalid Token | Customer | Authentication & Access Control | None | POST /auth/introspect<br>Body: {"token": "invalid_jwt"} | token: invalid_jwt | 200 OK, valid: false |
| TC_AUTH_005 | Access Protected Endpoint with Valid Token | Customer | Authentication & Access Control | Valid JWT | GET /auth/me<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 200 OK, user data |
| TC_AUTH_006 | Access Protected Endpoint without Token | Customer | Authentication & Access Control | None | GET /auth/me | N/A | 401 Unauthorized |
| TC_AUTH_007 | Logout | Customer | Authentication & Access Control | Valid JWT | POST /auth/logout<br>Body: {"token": "valid_jwt"} | token: valid_jwt | 200 OK, token invalidated |
| TC_AUTH_008 | Change Password | Customer | Authentication & Access Control | Valid JWT | POST /auth/change-password<br>Header: Authorization: Bearer valid_jwt<br>Body: {"oldPassword": "password123", "newPassword": "newpass123"} | oldPassword: password123<br>newPassword: newpass123 | 200 OK |
| TC_AUTH_009 | Access Admin Endpoint | Customer | Authentication & Access Control | Valid JWT | GET /users<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 403 Forbidden |
| TC_AUTH_010 | Password Reset Send Email | Customer | Authentication & Access Control | None | POST /auth/email/send<br>Body: {"email": "customer@example.com"} | email: customer@example.com | 200 OK |
| TC_AUTH_011 | Verify OTP | Customer | Authentication & Access Control | OTP sent | POST /auth/verify-otp<br>Body: {"email": "customer@example.com", "otpCode": "123456"} | email: customer@example.com<br>otpCode: 123456 | 200 OK |
| TC_AUTH_012 | Reset Password | Customer | Authentication & Access Control | OTP verified | POST /auth/reset-password<br>Body: {"email": "customer@example.com", "newPassword": "newpass123", "confirmPassword": "newpass123"} | email: customer@example.com<br>newPassword: newpass123<br>confirmPassword: newpass123 | 200 OK |

### Staff

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|---------------|---------------|------------|-----------------|
| TC_AUTH_013 | Successful Login | Staff | Authentication & Access Control | None | POST /auth/token<br>Body: {"username": "staff@example.com", "password": "password123"} | username: staff@example.com<br>password: password123 | 200 OK, JWT token in response |
| TC_AUTH_014 | Access Staff Endpoint | Staff | Authentication & Access Control | Valid JWT | GET /order-details<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 200 OK |
| TC_AUTH_015 | Access Admin Endpoint | Staff | Authentication & Access Control | Valid JWT | GET /users<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 403 Forbidden |

### Manager

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|---------------|------------|-----------------|
| TC_AUTH_016 | Successful Login | Manager | Authentication & Access Control | None | POST /auth/token<br>Body: {"username": "manager@example.com", "password": "password123"} | username: manager@example.com<br>password: password123 | 200 OK, JWT token in response |
| TC_AUTH_017 | Access Manager Endpoint | Manager | Authentication & Access Control | Valid JWT | GET /order-details<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 200 OK |
| TC_AUTH_018 | Access Admin Endpoint | Manager | Authentication & Access Control | Valid JWT | GET /users<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 403 Forbidden |

### Admin

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|---------------|------------|-----------------|
| TC_AUTH_019 | Successful Login | Admin | Authentication & Access Control | None | POST /auth/token<br>Body: {"username": "admin@example.com", "password": "password123"} | username: admin@example.com<br>password: password123 | 200 OK, JWT token in response |
| TC_AUTH_020 | Access Admin Endpoint | Admin | Authentication & Access Control | Valid JWT | GET /users<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 200 OK |
| TC_AUTH_021 | Create Admin User | Admin | Authentication & Access Control | No admin exists | POST /users/create-admin<br>Body: {"username": "newadmin", "email": "newadmin@example.com", "password": "pass123", "fullName": "New Admin"} | username: newadmin<br>email: newadmin@example.com<br>password: pass123<br>fullName: New Admin | 200 OK |

## Catalog, Cart & Checkout APIs

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|---------------|------------|-----------------|
| TC_CC_001 | Get Product List | Customer | Catalog, Cart & Checkout APIs | None | GET /shoes | N/A | 200 OK, list of shoes |
| TC_CC_002 | Get Product by ID | Customer | Catalog, Cart & Checkout APIs | None | GET /shoes/1 | shoeId: 1 | 200 OK, shoe details |
| TC_CC_003 | Filter Products | Customer | Catalog, Cart & Checkout APIs | None | GET /shoes?brandId=1&gender=MALE | brandId: 1<br>gender: MALE | 200 OK, filtered shoes |
| TC_CC_004 | Get Cart | Customer | Catalog, Cart & Checkout APIs | Valid JWT | GET /cart<br>Header: Authorization: Bearer valid_jwt | valid_jwt | 200 OK, cart data |
| TC_CC_005 | Add to Cart | Customer | Catalog, Cart & Checkout APIs | Valid JWT | POST /cart/add<br>Header: Authorization: Bearer valid_jwt<br>Body: {"variantId": "variant1", "quantity": 2} | variantId: variant1<br>quantity: 2 | 200 OK, item added |
| TC_CC_006 | Update Cart Item | Customer | Catalog, Cart & Checkout APIs | Valid JWT, item in cart | PUT /cart/update<br>Header: Authorization: Bearer valid_jwt<br>Body: {"variantId": "variant1", "quantity": 3} | variantId: variant1<br>quantity: 3 | 200 OK, quantity updated |
| TC_CC_007 | Remove from Cart | Customer | Catalog, Cart & Checkout APIs | Valid JWT, item in cart | DELETE /cart/remove/variant1<br>Header: Authorization: Bearer valid_jwt | variantId: variant1 | 200 OK, item removed |
| TC_CC_008 | Clear Cart | Customer | Catalog, Cart & Checkout APIs | Valid JWT, cart not empty | DELETE /cart/clear<br>Header: Authorization: Bearer valid_jwt | N/A | 200 OK, cart cleared |
| TC_CC_009 | Create Order | Customer | Catalog, Cart & Checkout APIs | Valid JWT, cart not empty | POST /orders/create<br>Header: Authorization: Bearer valid_jwt<br>Body: {"shippingAddress": "123 Main St", "paymentMethod": "COD"} | shippingAddress: 123 Main St<br>paymentMethod: COD | 200 OK, order created |
| TC_CC_010 | Apply Discount | Customer | Catalog, Cart & Checkout APIs | None | POST /orders/apply-discount<br>Body: {"discount": "SAVE10", "orderAmount": 100} | discount: SAVE10<br>orderAmount: 100 | 200 OK, discount applied |

## Order Payment APIs

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|---------------|------------|-----------------|
| TC_OP_001 | VNPay Payment Creation | Customer | Order Payment APIs | Order created | POST /payment/create<br>Header: Authorization: Bearer valid_jwt<br>Body: {"orderId": "order1", "amount": 100, "bankCode": "NCB"} | orderId: order1<br>amount: 100<br>bankCode: NCB | 200 OK, VNPay URL |
| TC_OP_002 | VNPay Callback Success | Customer | Order Payment APIs | Payment initiated | POST /payment/payment-callback<br>Body: {"vnp_ResponseCode": "00", "vnp_TxnRef": "order1", "vnp_SecureHash": "valid_hash"} | vnp_ResponseCode: 00<br>vnp_TxnRef: order1<br>vnp_SecureHash: valid_hash | 200 OK, payment success |
| TC_OP_003 | VNPay Callback Failure | Customer | Order Payment APIs | Payment initiated | POST /payment/payment-callback<br>Body: {"vnp_ResponseCode": "01", "vnp_TxnRef": "order1", "vnp_SecureHash": "valid_hash"} | vnp_ResponseCode: 01<br>vnp_TxnRef: order1<br>vnp_SecureHash: valid_hash | 200 OK, payment failed |
| TC_OP_004 | Invalid VNPay Signature | Customer | Order Payment APIs | None | POST /payment/payment-callback<br>Body: {"vnp_ResponseCode": "00", "vnp_TxnRef": "order1", "vnp_SecureHash": "invalid_hash"} | vnp_ResponseCode: 00<br>vnp_TxnRef: order1<br>vnp_SecureHash: invalid_hash | 400 Bad Request |
| TC_OP_005 | Concurrent Payment Attempt | Customer | Order Payment APIs | Payment in progress | POST /payment/create (multiple simultaneous)<br>Header: Authorization: Bearer valid_jwt<br>Body: {"orderId": "order1", "amount": 100} | orderId: order1<br>amount: 100 | 409 Conflict or idempotent |

## Order Tracking & Order Status Update Logic

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|---------------|------------|-----------------|
| TC_OT_001 | Get Own Orders | Customer | Order Tracking & Order Status Update Logic | Valid JWT | GET /order-details/user/customer@example.com<br>Header: Authorization: Bearer valid_jwt | username: customer@example.com | 200 OK, user's orders |
| TC_OT_002 | Get Order by ID and User | Customer | Order Tracking & Order Status Update Logic | Valid JWT | GET /order-details/order/order1/user/user1<br>Header: Authorization: Bearer valid_jwt | orderId: order1<br>userId: user1 | 200 OK, order details |
| TC_OT_003 | Cancel Own Order | Customer | Order Tracking & Order Status Update Logic | Valid JWT, order in CREATED | POST /orders/order1/cancel<br>Header: Authorization: Bearer valid_jwt | orderId: order1 | 200 OK, order cancelled |
| TC_OT_004 | Cancel Order Invalid Status | Customer | Order Tracking & Order Status Update Logic | Valid JWT, order in SHIPPED | POST /orders/order1/cancel<br>Header: Authorization: Bearer valid_jwt | orderId: order1 | 400 Bad Request |

### Staff

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|---------------|------------|-----------------|
| TC_OT_005 | Get All Orders | Staff | Order Tracking & Order Status Update Logic | Valid JWT | GET /order-details<br>Header: Authorization: Bearer valid_jwt | N/A | 200 OK, all orders |
| TC_OT_006 | Get Order by ID | Staff | Order Tracking & Order Status Update Logic | Valid JWT | GET /order-details/order/order1<br>Header: Authorization: Bearer valid_jwt | orderId: order1 | 200 OK, order details |
| TC_OT_007 | Update Order Status | Staff | Order Tracking & Order Status Update Logic | Valid JWT | PUT /order-details/order/order1<br>Header: Authorization: Bearer valid_jwt<br>Body: {"status": "CONFIRMED"} | orderId: order1<br>status: CONFIRMED | 200 OK, status updated |
| TC_OT_008 | Get Order Status Counts | Staff | Order Tracking & Order Status Update Logic | Valid JWT | GET /order-details/list-order<br>Header: Authorization: Bearer valid_jwt | N/A | 200 OK, paginated orders with counts |

### Manager

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|------|---------------|------------|-----------------|
| TC_OT_009 | Get All Orders | Manager | Order Tracking & Order Status Update Logic | Valid JWT | GET /order-details<br>Header: Authorization: Bearer valid_jwt | N/A | 200 OK, all orders |
| TC_OT_010 | Update Order Status | Manager | Order Tracking & Order Status Update Logic | Valid JWT | PUT /order-details/order/order1<br>Header: Authorization: Bearer valid_jwt<br>Body: {"status": "SHIPPED"} | orderId: order1<br>status: SHIPPED | 200 OK, status updated |
| TC_OT_011 | Cancel Any Order | Manager | Order Tracking & Order Status Update Logic | Valid JWT | POST /orders/order1/cancel<br>Header: Authorization: Bearer valid_jwt | orderId: order1 | 200 OK, order cancelled |

### Admin

| ID | Title | Role | Flow | Pre-condition | Steps/Request | Test Data | Expected Result |
|----|--------|------|------|------|------|------|---------------|------------|-----------------|
| TC_OT_012 | Get All Users | Admin | Order Tracking & Order Status Update Logic | Valid JWT | GET /users<br>Header: Authorization: Bearer valid_jwt | N/A | 200 OK, all users |
| TC_OT_013 | Create User | Admin | Order Tracking & Order Status Update Logic | Valid JWT | POST /users/register<br>Header: Authorization: Bearer valid_jwt<br>Body: {"username": "newuser", "email": "new@example.com", "password": "pass123", "fullName": "New User"} | username: newuser<br>email: new@example.com<br>password: pass123<br>fullName: New User | 200 OK, user created |
| TC_OT_014 | Update User | Admin | Order Tracking & Order Status Update Logic | Valid JWT | PUT /users/user1<br>Header: Authorization: Bearer valid_jwt<br>Body: {"fullName": "Updated Name"} | userId: user1<br>fullName: Updated Name | 200 OK, user updated |
| TC_OT_015 | Delete User | Admin | Order Tracking & Order Status Update Logic | Valid JWT | DELETE /users/user1<br>Header: Authorization: Bearer valid_jwt | userId: user1 | 200 OK, user deleted |