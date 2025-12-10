# Test Cases Template

## Auth Customer

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|-------|------|------|----------------|-------|-----------|-----------------|
| TC_AUTH_001 | Successful Login with Valid Credentials | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /login | usernamee: customer | User logged in, redirected to home page |
| TC_AUTH_001 | Successful Login with Valid Credentials | Customer | Authentication & Access Control | User not logged in | 2. Enter username and password | password: password123 | User logged in, redirected to home page |
| TC_AUTH_001 | Successful Login with Valid Credentials | Customer | Authentication & Access Control | User not logged in | 3. Click Login |  | User logged in, redirected to home page |
| TC_AUTH_002 | Failed Login with Invalid user name | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /login | username: invalid | Error message displayed, user not logged in |
| TC_AUTH_002 | Failed Login with Invalid user name | Customer | Authentication & Access Control | User not logged in | 2. Enter invalid username and valid password | password: password123 | Error message displayed, user not logged in |
| TC_AUTH_002 | Failed Login with Invalid user name | Customer | Authentication & Access Control | User not logged in | 3. Click Login |  | Error message displayed, user not logged in |
| TC_AUTH_003 | Failed Login with Invalid Password | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /login | username: customer | Error message displayed, user not logged in |
| TC_AUTH_003 | Failed Login with Invalid Password | Customer | Authentication & Access Control | User not logged in | 2. Enter valid username  and invalid password | password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_003 | Failed Login with Invalid Password | Customer | Authentication & Access Control | User not logged in | 3. Click Login |  | Error message displayed, user not logged in |
| TC_AUTH_004 | Successful Signup with Valid Data | Customer | Authentication & Access Control | User not registered | 1. Navigate to /register | email: newcustomer@example.com | User registered, logged in, redirected to home |
| TC_AUTH_004 | Successful Signup with Valid Data | Customer | Authentication & Access Control | User not registered | 21 | password: password123 | User registered, logged in, redirected to home |
| TC_AUTH_004 | Successful Signup with Valid Data | Customer | Authentication & Access Control | User not registered | 3. Click Signup |  | User registered, logged in, redirected to home |
| TC_AUTH_005 | Failed Signup with Invalid Email | Customer | Authentication & Access Control | User not registered | 1. Navigate to /register | email: invalidemail | Error message for invalid email |
| TC_AUTH_005 | Failed Signup with Invalid Email | Customer | Authentication & Access Control | User not registered | 2. Enter invalid email | password: password123 | Error message for invalid email |
| TC_AUTH_005 | Failed Signup with Invalid Email | Customer | Authentication & Access Control | User not registered | 3. Click Signup |  | Error message for invalid email |
| TC_AUTH_006 | Failed Signup with Existing Email | Customer | Authentication & Access Control | User not registered | 1. Navigate to /register | email: customer@example.com | Error message for existing email |
| TC_AUTH_006 | Failed Signup with Existing Email | Customer | Authentication & Access Control | User not registered | 2. Enter existing username | password: password123 | Error message for existing email |
| TC_AUTH_006 | Failed Signup with Existing Email | Customer | Authentication & Access Control | User not registered | 3. Click Signup |  | Error message for existing email |
| TC_AUTH_007 | Logout | Customer | Authentication & Access Control | User logged in | 1. Click logout | N/A | User logged out |
| TC_AUTH_007 | Logout | Customer | Authentication & Access Control | User logged in | 2. Confirm | N/A | User logged out |
| TC_AUTH_008 | Access Protected Page Without Login | Customer | Authentication & Access Control | User not logged in | 1. Navigate to /cart | N/A | Redirected to /login |
| TC_AUTH_009 | Access Admin Page | Customer | Authentication & Access Control | User logged in as customer | 1. Navigate to /admin | N/A | Redirected to /unauthorized |
| TC_AUTH_010 | Access Staff Page | Customer | Authentication & Access Control | User logged in as customer | 1. Navigate to /staff | N/A | Redirected to /unauthorized |
| TC_AUTH_011 | Access Manager Page | Customer | Authentication & Access Control | User logged in as customer | 1. Navigate to /manager | N/A | Redirected to /unauthorized |

## Auth Staff

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|-------|------|------|----------------|-------|-----------|-----------------|
| TC_AUTH_012 | Successful Login with Valid Credentials | Staff | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login | email: staff@example.com | User logged in, redirected to /staff |
| TC_AUTH_012 | Successful Login with Valid Credentials | Staff | Authentication & Access Control | User not logged in | 2. Enter email and password | password: password123 | User logged in, redirected to /staff |
| TC_AUTH_012 | Successful Login with Valid Credentials | Staff | Authentication & Access Control | User not logged in | 3. Click Login |  | User logged in, redirected to /staff |
| TC_AUTH_013 | Failed Login with Invalid Credentials | Staff | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login | email: invalid@example.com | Error message displayed, user not logged in |
| TC_AUTH_013 | Failed Login with Invalid Credentials | Staff | Authentication & Access Control | User not logged in | 2. Enter invalid email/password | password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_013 | Failed Login with Invalid Credentials | Staff | Authentication & Access Control | User not logged in | 3. Click Login |  | Error message displayed, user not logged in |
| TC_AUTH_014 | Logout | Staff | Authentication & Access Control | User logged in | 1. Click logout | N/A | User logged out, redirected to /logout |
| TC_AUTH_014 | Logout | Staff | Authentication & Access Control | User logged in | 2. Confirm | N/A | User logged out, redirected to /logout |
| TC_AUTH_015 | Access Staff Page | Staff | Authentication & Access Control | User logged in as staff | 1. Navigate to /staff | N/A | Staff dashboard displayed |
| TC_AUTH_016 | Access Manager Page | Staff | Authentication & Access Control | User logged in as staff | 1. Navigate to /manager | N/A | Redirected to /unauthorized |
| TC_AUTH_017 | Access Admin Page | Staff | Authentication & Access Control | User logged in as staff | 1. Navigate to /admin | N/A | Redirected to /unauthorized |

## Auth Manager

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|-------|------|------|----------------|-------|-----------|-----------------|
| TC_AUTH_018 | Successful Login with Valid Credentials | Manager | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login | email: manager@example.com | User logged in, redirected to /manager |
| TC_AUTH_018 | Successful Login with Valid Credentials | Manager | Authentication & Access Control | User not logged in | 2. Enter email and password | password: password123 | User logged in, redirected to /manager |
| TC_AUTH_018 | Successful Login with Valid Credentials | Manager | Authentication & Access Control | User not logged in | 3. Click Login |  | User logged in, redirected to /manager |
| TC_AUTH_019 | Failed Login with Invalid Credentials | Manager | Authentication & Access Control | User not logged in | 1. Navigate to /admin/login | email: invalid@example.com | Error message displayed, user not logged in |
| TC_AUTH_019 | Failed Login with Invalid Credentials | Manager | Authentication & Access Control | User not logged in | 2. Enter invalid email/password | password: wrongpass | Error message displayed, user not logged in |
| TC_AUTH_019 | Failed Login with Invalid Credentials | Manager | Authentication & Access Control | User not logged in | 3. Click Login |  | Error message displayed, user not logged in |
| TC_AUTH_020 | Logout | Manager | Authentication & Access Control | User logged in | 1. Click logout | N/A | User logged out, redirected to /logout |
| TC_AUTH_020 | Logout | Manager | Authentication & Access Control | User logged in | 2. Confirm | N/A | User logged out, redirected to /logout |
| TC_AUTH_021 | Access Manager Page | Manager | Authentication & Access Control | User logged in as manager | 1. Navigate to /manager | N/A | Manager dashboard displayed |
| TC_AUTH_022 | Access Admin Page | Manager | Authentication & Access Control | User logged in as manager | 1. Navigate to /admin | N/A | Redirected to /unauthorized |
| TC_AUTH_023 | Access Staff Page | Manager | Authentication & Access Control | User logged in as manager | 1. Navigate to /staff | N/A | Redirected to /unauthorized |

## Auth Admin

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_AUTH_024 | Successful Login with Valid Credentials | Auth | Admin | User not logged in | Đang xử lý | 1. Navigate to /admin/login | email: admin@example.com | User logged in, redirected to /admin | Danh |
| TC_AUTH_024 | Successful Login with Valid Credentials | Auth | Admin | User not logged in | Đang xử lý | 2. Enter email and password | password: password123 | User logged in, redirected to /admin | Danh |
| TC_AUTH_024 | Successful Login with Valid Credentials | Auth | Admin | User not logged in | Đang xử lý | 3. Click Login |  | User logged in, redirected to /admin | Danh |
| TC_AUTH_025 | Failed Login with Invalid Credentials | Auth | Admin | User not logged in | Đang xử lý | 1. Navigate to /admin/login | email: invalid@example.com | Error message displayed, user not logged in | Danh |
| TC_AUTH_025 | Failed Login with Invalid Credentials | Auth | Admin | User not logged in | Đang xử lý | 2. Enter invalid email/password | password: wrongpass | Error message displayed, user not logged in | Danh |
| TC_AUTH_025 | Failed Login with Invalid Credentials | Auth | Admin | User not logged in | Đang xử lý | 3. Click Login |  | Error message displayed, user not logged in | Danh |
| TC_AUTH_026 | Logout | Auth | Admin | User logged in | Đang xử lý | 1. Click logout | N/A | User logged out, redirected to /logout | Danh |
| TC_AUTH_026 | Logout | Auth | Admin | User logged in | Đang xử lý | 2. Confirm | N/A | User logged out, redirected to /logout | Danh |
| TC_AUTH_027 | Access Admin Page | Auth | Admin | User logged in as admin | Đang xử lý | 1. Navigate to /admin | N/A | Admin dashboard displayed | Danh |
| TC_AUTH_028 | Access Staff Page | Auth | Admin | User logged in as admin | Đang xử lý | 1. Navigate to /staff | N/A | Redirected to /unauthorized | Danh |
| TC_AUTH_029 | Access Manager Page | Auth | Admin | User logged in as admin | Đang xử lý | 1. Navigate to /manager | N/A | Redirected to /unauthorized | Danh |

## ProductCatalog

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_PC_001 | View Product Catalog | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to /shoes | N/A | Product grid displayed with images and prices | Khang |
| TC_PC_002 | Filter Products by Brand | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to /shoes | brand: Nike | Products filtered to show only selected brand | Khang |
| TC_PC_002 | Filter Products by Brand | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Select brand filter | brand: Nike | Products filtered to show only selected brand | Khang |
| TC_PC_002 | Filter Products by Brand | ProductCatalog | Customer | User logged in | Đang xử lý | 3. Click Apply | brand: Nike | Products filtered to show only selected brand | Khang |
| TC_PC_003 | Filter Products by Price Range | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to /shoes | minPrice: 50 | Products within price range displayed | Khang |
| TC_PC_003 | Filter Products by Price Range | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Set price range | maxPrice: 200 | Products within price range displayed | Khang |
| TC_PC_003 | Filter Products by Price Range | ProductCatalog | Customer | User logged in | Đang xử lý | 3. Click Apply |  | Products within price range displayed | Khang |
| TC_PC_004 | Sort Products by Price Low to High | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to /shoes | N/A | Products sorted ascending by price | Khang |
| TC_PC_004 | Sort Products by Price Low to High | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Select "Price: Low to High" sort | N/A | Products sorted ascending by price | Khang |
| TC_PC_004 | Sort Products by Price Low to High | ProductCatalog | Customer | User logged in | Đang xử lý | 3. Click Apply | N/A | Products sorted ascending by price | Khang |
| TC_PC_005 | Sort Products by Price High to Low | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to /shoes | N/A | Products sorted descending by price | Khang |
| TC_PC_005 | Sort Products by Price High to Low | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Select "Price: High to Low" sort | N/A | Products sorted descending by price | Khang |
| TC_PC_005 | Sort Products by Price High to Low | ProductCatalog | Customer | User logged in | Đang xử lý | 3. Click Apply | N/A | Products sorted descending by price | Khang |
| TC_PC_006 | View Product Detail Page | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to /shoes | productId: 1 | Product detail page with images, description, variants | Khang |
| TC_PC_006 | View Product Detail Page | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Click on product image/title | productId: 1 | Product detail page with images, description, variants | Khang |
| TC_PC_007 | Product Detail with Multiple Images | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to product detail | N/A | Main image changes when thumbnails clicked | Khang |
| TC_PC_007 | Product Detail with Multiple Images | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Click image thumbnails | N/A | Main image changes when thumbnails clicked | Khang |
| TC_PC_008 | Select Product Size and Color | ProductCatalog | Customer | User logged in | Đang xử lý | 1. Navigate to product detail | size: 10 | Size and color selections updated in UI | Khang |
| TC_PC_008 | Select Product Size and Color | ProductCatalog | Customer | User logged in | Đang xử lý | 2. Select size option | color: Black | Size and color selections updated in UI | Khang |

## Shopping Cart

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_SC_001 | Add Product to Cart | ShoppingCart | Customer | User logged in | Đang xử lý | 1. Navigate to product detail | size: 9 | Success notification, cart icon shows updated count | Khang |
| TC_SC_001 | Add Product to Cart | ShoppingCart | Customer | User logged in | Đang xử lý | 2. Select size/quantity | quantity: 2 | Success notification, cart icon shows updated count | Khang |
| TC_SC_001 | Add Product to Cart | ShoppingCart | Customer | User logged in | Đang xử lý | 3. Click "Add to Cart" |  | Success notification, cart icon shows updated count | Khang |
| TC_SC_002 | View Shopping Cart | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Click cart icon | N/A | Cart sidebar/slideout displays with items, quantities, prices | Khang |
| TC_SC_003 | Update Cart Item Quantity | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | quantity: 3 | Quantity updated, total price recalculated | Khang |
| TC_SC_003 | Update Cart Item Quantity | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 2. Change quantity | quantity: 3 | Quantity updated, total price recalculated | Khang |
| TC_SC_003 | Update Cart Item Quantity | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 3. Click Update | quantity: 3 | Quantity updated, total price recalculated | Khang |
| TC_SC_004 | Remove Item from Cart | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | N/A | Item removed, total updated | Khang |
| TC_SC_004 | Remove Item from Cart | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 2. Click remove on item | N/A | Item removed, total updated | Khang |
| TC_SC_005 | Clear Entire Cart | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | N/A | All items removed, cart empty | Khang |
| TC_SC_005 | Clear Entire Cart | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 2. Click "Clear Cart" | N/A | All items removed, cart empty | Khang |
| TC_SC_006 | Cart Persistence Across Sessions | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Add items to cart | N/A | Cart items preserved after re-login | Khang |
| TC_SC_006 | Cart Persistence Across Sessions | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 2. Logout | N/A | Cart items preserved after re-login | Khang |
| TC_SC_006 | Cart Persistence Across Sessions | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 3. Login again | N/A | Cart items preserved after re-login | Khang |
| TC_SC_006 | Cart Persistence Across Sessions | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 4. Check cart | N/A | Cart items preserved after re-login | Khang |
| TC_SC_007 | Cart Total Calculation | ShoppingCart | Customer | User logged in, multiple items | Đang xử lý | 1. Add multiple items | item1: 5012:30 qty 2 | Total shows $110 (50 + 60) | Khang |
| TC_SC_007 | Cart Total Calculation | ShoppingCart | Customer | User logged in, multiple items | Đang xử lý | 2. View cart total | item1: 5012:30 qty 2 | Total shows $110 (50 + 60) | Khang |
| TC_SC_008 | Maximum Cart Quantity Limit | ShoppingCart | Customer | User logged in | Đang xử lý | 1. Try to add quantity > 10 | quantity: 15 | Error message "Maximum quantity is 10" | Khang |
| TC_SC_009 | Cart with Discount Code | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | discountCode: SAVE10 | Discount applied, total reduced by 10% | Khang |
| TC_SC_009 | Cart with Discount Code | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 2. Enter discount code | discountCode: SAVE10 | Discount applied, total reduced by 10% | Khang |
| TC_SC_009 | Cart with Discount Code | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 3. Apply | discountCode: SAVE10 | Discount applied, total reduced by 10% | Khang |
| TC_SC_010 | Invalid Discount Code | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | discountCode: INVALID | Error message "Invalid discount code" | Khang |
| TC_SC_010 | Invalid Discount Code | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 2. Enter invalid code | discountCode: INVALID | Error message "Invalid discount code" | Khang |
| TC_SC_010 | Invalid Discount Code | ShoppingCart | Customer | User logged in, items in cart | Đang xử lý | 3. Apply | discountCode: INVALID | Error message "Invalid discount code" | Khang |

## Checkout

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_CO_001 | Proceed to Checkout | Checkout | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | N/A | Redirected to checkout page with cart summary | Thành |
| TC_CO_001 | Proceed to Checkout | Checkout | Customer | User logged in, items in cart | Đang xử lý | 2. Click "Checkout" | N/A | Redirected to checkout page with cart summary | Thành |
| TC_CO_002 | Enter Shipping Information | Checkout | Customer | At checkout page | Đang xử lý | 1. Fill shipping address | address: 123 Main St | Form validation passes, proceed to payment | Thành |
| TC_CO_002 | Enter Shipping Information | Checkout | Customer | At checkout page | Đang xử lý | 2. Fill contact info | phone: 1234567890 | Form validation passes, proceed to payment | Thành |
| TC_CO_002 | Enter Shipping Information | Checkout | Customer | At checkout page | Đang xử lý | 3. Click Continue |  | Form validation passes, proceed to payment | Thành |
| TC_CO_003 | Select COD Payment Method | Checkout | Customer | At checkout page | Đang xử lý | 1. Select "Cash on Delivery" | N/A | COD option selected, payment summary shown | Thành |
| TC_CO_003 | Select COD Payment Method | Checkout | Customer | At checkout page | Đang xử lý | 2. Click Continue | N/A | COD option selected, payment summary shown | Thành |
| TC_CO_004 | Select VNPay Payment Method | Checkout | Customer | At checkout page | Đang xử lý | 1. Select "VNPay" | N/A | VNPay option selected, bank selection shown | Thành |
| TC_CO_004 | Select VNPay Payment Method | Checkout | Customer | At checkout page | Đang xử lý | 2. Click Continue | N/A | VNPay option selected, bank selection shown | Thành |
| TC_CO_005 | Review Order Summary | Checkout | Customer | At checkout page | Đang xử lý | 1. Review items, prices, total | N/A | All cart items, quantities, prices displayed correctly | Thành |
| TC_CO_006 | Apply Discount at Checkout | Checkout | Customer | At checkout page | Đang xử lý | 1. Enter discount code | discountCode: SAVE10 | Discount applied, total updated | Thành |
| TC_CO_006 | Apply Discount at Checkout | Checkout | Customer | At checkout page | Đang xử lý | 2. Apply | discountCode: SAVE10 | Discount applied, total updated | Thành |
| TC_CO_007 | Checkout Form Validation | Checkout | Customer | At checkout page | Đang xử lý | 1. Leave required fields empty | N/A | Error messages yêu cầu cập nhật thông tin địa chỉ cá nhân, chuyển sang trang nhập hồ sơ cá nhân | Thành |
| TC_CO_007 | Checkout Form Validation | Checkout | Customer | At checkout page | Đang xử lý | 2. Click Continue | N/A | Error messages yêu cầu cập nhật thông tin địa chỉ cá nhân, chuyển sang trang nhập hồ sơ cá nhân | Thành |

## Payment

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_PAY_001 | COD Order Confirmation | Payment | Customer | COD selected at checkout | Đang xử lý | 1. Review order | N/A | Order confirmation page, order number displayed | Thành |
| TC_PAY_001 | COD Order Confirmation | Payment | Customer | COD selected at checkout | Đang xử lý | 2. Click "Place Order" | N/A | Order confirmation page, order number displayed | Thành |
| TC_PAY_002 | VNPay Payment Redirect | Payment | Customer | VNPay selected at checkout | Đang xử lý | 1. Select bank | bankCode: NCB | Redirected to VNPay gateway URL | Thành |
| TC_PAY_002 | VNPay Payment Redirect | Payment | Customer | VNPay selected at checkout | Đang xử lý | 2. Click "Pay Now" | bankCode: NCB | Redirected to VNPay gateway URL | Thành |
| TC_PAY_003 | VNPay Success Return | Payment | Customer | Payment initiated | Đang xử lý | 1. Complete payment on VNPay | N/A | Success page with order confirmation | Thành |
| TC_PAY_003 | VNPay Success Return | Payment | Customer | Payment initiated | Đang xử lý | 2. Return to site | N/A | Success page with order confirmation | Thành |
| TC_PAY_004 | VNPay Failure Return | Payment | Customer | Payment initiated | Đang xử lý | 1. Fail payment on VNPay | N/A | Failure page with error message | Thành |
| TC_PAY_004 | VNPay Failure Return | Payment | Customer | Payment initiated | Đang xử lý | 2. Return to site | N/A | Failure page with error message | Thành |
| TC_PAY_005 | VNPay Cancel Return | Payment | Customer | Payment initiated | Đang xử lý | 1. Cancel on VNPay | N/A | Cancel page, order not completed | Thành |
| TC_PAY_005 | VNPay Cancel Return | Payment | Customer | Payment initiated | Đang xử lý | 2. Return to site | N/A | Cancel page, order not completed | Thành |
| TC_PAY_008 | Payment Amount Display | Payment | Customer | At payment step | Đang xử lý | 1. Check payment amount | total: $150 | Correct amount $150 displayed | Thành |
| TC_PAY_009 | Multiple Payment Attempts | Payment | Customer | Failed payment | Hoàn thành | 1. Retry payment | N/A | Second attempt succeeds | Thành |
| TC_PAY_009 | Multiple Payment Attempts | Payment | Customer | Failed payment | Hoàn thành | 2. Complete successfully | N/A | Second attempt succeeds | Thành |

## Order Tracking

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_OT_001 | View Order History | Order Tracking | Customer | User logged in, has orders | Đang xử lý | 1. Navigate to /order-history | N/A | List of user's orders with status, dates | Thành |
| TC_OT_002 | View Order Detail | Order Tracking | Customer | User logged in, has orders | Đang xử lý | 1. Click on order in history | orderId: 123 | Order detail page with items, status, tracking | Thành |
| TC_OT_003 | Order Status Display | Order Tracking | Customer | User logged in, has orders | Đang xử lý | 1. View order status | status: Deliveried | Status badge shows "Deliveried" | Thành |
| TC_OT_004 | Order Tracking Information | Order Tracking | Customer | User logged in, shipped order | Đang xử lý | 1. View tracking info | N/A | Tracking number, carrier, estimated delivery | Thành |
| TC_OT_005 | Order Status Updates | Order Tracking | Customer | User logged in, order in transit | Đang xử lý | 1. Refresh page | N/A | Status updates automatically or on refresh | Thành |

## OrderManagement

| ID | Module | Title | Role | Pre-condition | Steps | Status | Test Data | Expected Result | Người thực hiện |
|----|--------|-------|------|----------------|-------|--------|-----------|-----------------|-----------------|
| TC_OM_001 | OrderManagement | Staff View All Orders | Customer | Staff logged in | 1. Navigate to /staff/member-order-history | Đang xử lý | N/A | List of all customer orders | Thành |
| TC_OM_002 | OrderManagement | Staff View Order Detail | Customer | Staff logged in | 1. Click on order | Đang xử lý | orderId: 123 | Order detail with customer info | Thành |
| TC_OM_003 | OrderManagement | Staff Update Order Status | Customer | Staff logged in, order pending | 1. Change status to CONFIRMED | Đang xử lý | status: CONFIRMED | Status updated successfully | Thành |
| TC_OM_003 | OrderManagement | Staff Update Order Status | Customer | Staff logged in, order pending | 2. Save | Đang xử lý | status: CONFIRMED | Status updated successfully | Thành |
| TC_OM_004 | OrderManagement | Manager View All Orders | Customer | Manager logged in | 1. Navigate to /manager/member-order-history | Đang xử lý | N/A | List of all orders with management options | Thành |
| TC_OM_005 | OrderManagement | Manager Update Order Status | Customer | Manager logged in, order pendingr | 1. Change status to CONFIRMED | Đang xử lý | orderId: 123 | Status updated successfully | Thành |
| TC_OM_005 | OrderManagement | Manager Update Order Status | Customer | Manager logged in, order pendingr | 2. Save | Đang xử lý | orderId: 123 | Status updated successfully | Thành |

## DiscountManagement

| ID | Title | Module | Role | Pre-condition | Status | Steps | Test Data | Expected Result | Người thực hiện |
|----|-------|--------|------|----------------|--------|-------|-----------|-----------------|-----------------|
| TC_DM_001 | Apply Valid Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | discountCode: test2 (20%) | Discount applied, total reduced | Quân |
| TC_DM_001 | Apply Valid Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 2. Enter code | discountCode: test2 (20%) | Discount applied, total reduced | Quân |
| TC_DM_001 | Apply Valid Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 3. Apply | discountCode: test2 (20%) | Discount applied, total reduced | Quân |
| TC_DM_002 | Apply Expired Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | discountCode: giamgia1 (10%) | Error "Discount code expired" | Quân |
| TC_DM_002 | Apply Expired Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 2. Enter expired code | discountCode: giamgia1 (10%) | Error "Discount code expired" | Quân |
| TC_DM_002 | Apply Expired Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 3. Apply | discountCode: giamgia1 (10%) | Error "Discount code expired" | Quân |
| TC_DM_003 | Apply Used Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 1. Open cart | discountCode: test5 (50%) | Error "Discount code already used" | Quân |
| TC_DM_003 | Apply Used Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 2. Enter used code | discountCode: test5 (50%) | Error "Discount code already used" | Quân |
| TC_DM_003 | Apply Used Discount Code | Discount | Customer | User logged in, items in cart | Đang xử lý | 3. Apply | discountCode: test5 (50%) | Error "Discount code already used" | Quân |
| TC_DM_004 | Discount Minimum Purchase | Discount | Customer | User logged in, cart below minimum | Đang xử lý | 1. Enter code | discountCode: test4 (min: 200.000đ) | Error "Minimum purchase 200,000đ required" | Quân |
| TC_DM_004 | Discount Minimum Purchase | Discount | Customer | User logged in, cart below minimum | Đang xử lý | 2. Apply | discountCode: test4 (min: 200.000đ) | Error "Minimum purchase 200,000đ required" | Quân |
| TC_DM_006 | Remove Applied Discount | Discount | Customer | User logged in, discount applied | Đang xử lý | 1. Click Remove Discount | N/A | Discount removed, total updated | Quân |
| TC_DM_007 | Multiple Discount Attempts | Discount | Customer | User logged in, discount applied | Đang xử lý | 1. Try to apply another code | discountCode: SAVE20 | Error "Only one discount per order" | Quân |
| TC_DM_008 | Discount Case Sensitivity | Discount | Customer | User logged in, items in cart | Đang xử lý | 1. Enter code in wrong case | discountCode: test3 | Discount applied (case insensitive) | Quân |
| TC_DM_008 | Discount Case Sensitivity | Discount | Customer | User logged in, items in cart | Đang xử lý | 2. Apply | discountCode: test3 | Discount applied (case insensitive) | Quân |
| TC_DM_009 | Manager Create Discount | Discount | Manager | Manager logged in | Đang xử lý | 1. Navigate to discount management | code: test | Discount created successfully | Quân |
| TC_DM_009 | Manager Create Discount | Discount | Manager | Manager logged in | Đang xử lý | 2. Create new discount | percentage: 10 | Discount created successfully | Quân |

## Test Data

(This sheet appears to be empty or contains no data rows.)