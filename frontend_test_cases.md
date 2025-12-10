# Frontend Test Cases

## Authentication & Access Control

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_AUTH_001 | Successful Login with Valid Credentials | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /login<br>2. Enter email and password<br>3. Click Login | email: customer@example.com<br>password: password123 | User logged in, redirected to home page |
| TC_AUTH_002 | Failed Login with Invalid Email | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /login<br>2. Enter invalid email and valid password<br>3. Click Login | email: invalid@example.com<br>password: password123 | Error message displayed, user not logged in |
| TC_AUTH_003 | Failed Login with Invalid Password | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /login<br>2. Enter valid email and invalid password<br>3. Click Login | email: customer@example.com<br>password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_004 | Successful Signup with Valid Data | Customer | Authentication & Access Control | User not registered | 1. Navigate to /register<br>2. Enter details<br>3. Click Signup | email: newcustomer@example.com<br>password: password123<br>name: John Doe | User registered, logged in, redirected to home |
| TC_AUTH_005 | Failed Signup with Invalid Email | Customer | Authentication & Access Control | User not registered | 1. Navigate to /register<br>2. Enter invalid email<br>3. Click Signup | email: invalidemail<br>password: password123 | Error message for invalid email |
| TC_AUTH_006 | Failed Signup with Existing Email | Customer | Authentication & Access Control | User not registered | 1. Navigate to /register<br>2. Enter existing email<br>3. Click Signup | email: customer@example.com<br>password: password123 | Error message for existing email |
| TC_AUTH_007 | Logout | Customer | Authentication & Access Control | User logged in | 1. Click logout<br>2. Confirm | N/A | User logged out, redirected to login |
| TC_AUTH_008 | Access Protected Page Without Login | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /cart | N/A | Redirected to /login |
| TC_AUTH_009 | Access Admin Page | Customer | Authentication & Access Control | User logged in as customer | 1. Navigate to /admin | N/A | Redirected to /unauthorized |

### Staff

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_AUTH_010 | Successful Login with Valid Credentials | Staff | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login<br>2. Enter email and password<br>3. Click Login | email: staff@example.com<br>password: password123 | User logged in, redirected to /staff |
| TC_AUTH_011 | Failed Login with Invalid Credentials | Staff | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login<br>2. Enter invalid email/password<br>3. Click Login | email: invalid@example.com<br>password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_012 | Logout | Staff | Authentication & Access Control | User logged in | 1. Click logout<br>2. Confirm | N/A | User logged out, redirected to /admin/login |
| TC_AUTH_013 | Access Staff Page | Staff | Authentication & Access Control | User logged in as staff | 1. Navigate to /staff | N/A | Staff dashboard displayed |
| TC_AUTH_014 | Access Manager Page | Staff | Authentication & Access Control | User logged in as staff | 1. Navigate to /manager | N/A | Redirected to /unauthorized |
| TC_AUTH_015 | Access Admin Page | Staff | Authentication & Access Control | User logged in as staff | 1. Navigate to /admin | N/A | Redirected to /unauthorized |

### Manager

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_AUTH_016 | Successful Login with Valid Credentials | Manager | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login<br>2. Enter email and password<br>3. Click Login | email: manager@example.com<br>password: password123 | User logged in, redirected to /manager |
| TC_AUTH_017 | Failed Login with Invalid Credentials | Manager | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login<br>2. Enter invalid email/password<br>3. Click Login | email: invalid@example.com<br>password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_018 | Logout | Manager | Authentication & Access Control | User logged in | 1. Click logout<br>2. Confirm | N/A | User logged out, redirected to /admin/login |
| TC_AUTH_019 | Access Manager Page | Manager | Authentication & Access Control | User logged in as manager | 1. Navigate to /manager | N/A | Manager dashboard displayed |
| TC_AUTH_020 | Access Admin Page | Manager | Authentication & Access Control | User logged in as manager | 1. Navigate to /admin | N/A | Redirected to /unauthorized |

### Admin

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_AUTH_021 | Successful Login with Valid Credentials | Admin | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login<br>2. Enter email and password<br>3. Click Login | email: admin@example.com<br>password: password123 | User logged in, redirected to /admin |
| TC_AUTH_022 | Failed Login with Invalid Credentials | Admin | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login<br>2. Enter invalid email/password<br>3. Click Login | email: invalid@example.com<br>password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_023 | Logout | Admin | Authentication & Access Control | User logged in | 1. Click logout<br>2. Confirm | N/A | User logged out, redirected to /admin/login |
| TC_AUTH_024 | Access Admin Page | Admin | Authentication & Access Control | User logged in as admin | 1. Navigate to /admin | N/A | Admin dashboard displayed |

## Product Catalog & Checkout

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_PC_001 | View Product List | Customer | Product Catalog & Checkout | Logged in as customer | 1. Navigate to /shoes | N/A | Product list displayed |
| TC_PC_002 | Filter Products by Brand | Customer | Product Catalog & Checkout | Logged in as customer | 1. Navigate to /shoes<br>2. Select brand filter | brand: Nike | Filtered products shown |
| TC_PC_003 | Sort Products by Price | Customer | Product Catalog & Checkout | Logged in as customer | 1. Navigate to /shoes<br>2. Select sort by price | sort: ascending | Products sorted by price |
| TC_PC_004 | View Product Detail | Customer | Product Catalog & Checkout | Logged in as customer | 1. Navigate to /shoes/:id | shoe id: 1 | Product detail displayed |
| TC_PC_005 | Add Item to Cart | Customer | Product Catalog & Checkout | Logged in as customer | 1. Navigate to shoe detail<br>2. Select size and quantity<br>3. Click Add to Cart | shoe id: 1<br>size: 10<br>quantity: 2 | Item added to cart, notification shown |
| TC_PC_006 | View Cart | Customer | Product Catalog & Checkout | Logged in as customer, items in cart | 1. Navigate to /cart | cart items: shoe 1 qty 2 | Cart displayed with items and total |
| TC_PC_007 | Update Cart Item Quantity | Customer | Product Catalog & Checkout | Logged in as customer, items in cart | 1. Navigate to /cart<br>2. Change quantity<br>3. Update | quantity: 3 | Cart updated, total recalculated |
| TC_PC_008 | Remove Item from Cart | Customer | Product Catalog & Checkout | Logged in as customer, items in cart | 1. Navigate to /cart<br>2. Remove item | N/A | Item removed from cart |
| TC_PC_009 | Empty Cart Checkout Attempt | Customer | Product Catalog & Checkout | Logged in as customer, cart empty | 1. Navigate to /checkout | N/A | Error message or redirect to cart |
| TC_PC_010 | Checkout with Valid Data | Customer | Product Catalog & Checkout | Logged in, cart not empty | 1. Navigate to /checkout<br>2. Enter shipping info<br>3. Proceed | address: 123 Main St, City<br>phone: 1234567890 | Checkout form displayed |

## Order Payment

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_OP_001 | COD Payment Success | Customer | Order Payment | At checkout | 1. Select COD payment<br>2. Submit order | N/A | Order placed successfully, confirmation shown |
| TC_OP_002 | VNPay Payment Initiation | Customer | Order Payment | At checkout | 1. Select VNPay payment<br>2. Submit order | N/A | Redirected to VNPay payment page |
| TC_OP_003 | VNPay Payment Success Callback | Customer | Order Payment | Payment initiated | 1. Complete payment on VNPay<br>2. Callback received | N/A | Redirected back, order success message |
| TC_OP_004 | VNPay Payment Failure Callback | Customer | Order Payment | Payment initiated | 1. Fail payment on VNPay<br>2. Callback received | N/A | Redirected back, error message displayed |
| TC_OP_005 | Invalid Payment Method | Customer | Order Payment | At checkout | 1. Select invalid payment<br>2. Submit | payment: invalid | Error message for invalid payment |

## Order Tracking & Order Management

### Customer

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_OT_001 | View Order History | Customer | Order Tracking & Order Management | Logged in as customer, has orders | 1. Navigate to /order-history | N/A | List of user's orders displayed |
| TC_OT_002 | View Order Detail | Customer | Order Tracking & Order Management | Logged in as customer, has orders | 1. Click on order in history | order id: 1 | Order detail page shown |
| TC_OT_003 | Track Order Status | Customer | Order Tracking & Order Management | Logged in as customer, order placed | 1. View order detail | N/A | Current status displayed (e.g., Pending, Shipped) |

### Staff

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_OM_004 | View Member Order History | Staff | Order Tracking & Order Management | Logged in as staff | 1. Navigate to /staff/member-order-history | N/A | List of all orders displayed |
| TC_OM_005 | View Order Detail | Staff | Order Tracking & Order Management | Logged in as staff | 1. Click on order | order id: 1<br>user id: 1 | Order detail shown |
| TC_OM_006 | Search Orders | Staff | Order Tracking & Order Management | Logged in as staff | 1. Enter search criteria<br>2. Search | search: order id 1 | Filtered orders displayed |

### Manager

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_OM_007 | View Member Order History | Manager | Order Tracking & Order Management | Logged in as manager | 1. Navigate to /manager/member-order-history | N/A | List of all orders displayed |
| TC_OM_008 | View Order Detail | Manager | Order Tracking & Order Management | Logged in as manager | 1. Click on order | order id: 1<br>user id: 1 | Order detail shown |
| TC_OM_009 | Approve Order | Manager | Order Tracking & Order Management | Logged in as manager, order pending | 1. View order detail<br>2. Click approve | order id: 1 | Order status updated to approved |
| TC_OM_010 | Create Discount | Manager | Order Tracking & Order Management | Logged in as manager | 1. Navigate to /manager/discount-management/new<br>2. Enter discount data<br>3. Submit | code: SAVE20<br>percentage: 20<br>expiry: 2025-12-31 | Discount created successfully |

### Admin

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_OM_011 | View Account Management | Admin | Order Tracking & Order Management | Logged in as admin | 1. Navigate to /admin/account-management | N/A | List of users displayed |
| TC_OM_012 | Create New User | Admin | Order Tracking & Order Management | Logged in as admin | 1. Navigate to account management<br>2. Create user | email: newuser@example.com<br>role: STAFF | User created successfully |
| TC_OM_013 | Update User Role | Admin | Order Tracking & Order Management | Logged in as admin, user exists | 1. Select user<br>2. Update role | user id: 1<br>new role: MANAGER | User role updated |