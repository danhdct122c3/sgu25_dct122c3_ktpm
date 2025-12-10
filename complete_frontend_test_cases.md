# Complete Frontend Test Cases for ShoeShop Store

| ID | Title | Role | Flow | Pre-condition | Steps | Test Data | Expected Result |
|----|--------|------|------|---------------|--------|------------|-----------------|
| TC_AUTH_001 | Successful Customer Login | Customer | Authentication | User not logged in | 1. Navigate to /login<br>2. Enter valid email and password<br>3. Click Login button | email: customer@example.com<br>password: password123 | User redirected to home page, login success message displayed |
| TC_AUTH_002 | Invalid Customer Login Credentials | Customer | Authentication | User not logged in | 1. Navigate to /login<br>2. Enter invalid email or password<br>3. Click Login button | email: invalid@example.com<br>password: wrongpass | Error message "Invalid credentials" displayed, user remains on login page |
| TC_AUTH_003 | Customer Signup with Valid Data | Customer | Authentication | User not registered | 1. Navigate to /register<br>2. Fill all required fields<br>3. Click Signup button | email: newcustomer@example.com<br>password: password123<br>name: John Doe | Success message displayed, user redirected to home page |
| TC_AUTH_004 | Customer Signup with Invalid Email | Customer | Authentication | User not registered | 1. Navigate to /register<br>2. Enter invalid email format<br>3. Click Signup button | email: invalidemail<br>password: password123 | Error message "Invalid email format" displayed |
| TC_AUTH_005 | Customer Signup with Existing Email | Customer | Authentication | User not registered | 1. Navigate to /register<br>2. Enter existing email<br>3. Click Signup button | email: customer@example.com<br>password: password123 | Error message "Email already exists" displayed |
| TC_AUTH_006 | Customer Logout | Customer | Authentication | User logged in | 1. Click logout button<br>2. Confirm logout | N/A | User redirected to login page, session cleared |
| TC_AUTH_007 | Staff Login Success | Staff | Authentication | User not logged in | 1. Navigate to /admin/login<br>2. Enter valid staff credentials<br>3. Click Login button | email: staff@example.com<br>password: password123 | User redirected to /staff dashboard |
| TC_AUTH_008 | Manager Login Success | Manager | Authentication | User not logged in | 1. Navigate to /admin/login<br>2. Enter valid manager credentials<br>3. Click Login button | email: manager@example.com<br>password: password123 | User redirected to /manager dashboard |
| TC_AUTH_009 | Admin Login Success | Admin | Authentication | User not logged in | 1. Navigate to /admin/login<br>2. Enter valid admin credentials<br>3. Click Login button | email: admin@example.com<br>password: password123 | User redirected to /admin dashboard |
| TC_AUTH_010 | Unauthorized Access to Admin Page | Customer | Authentication | User logged in as customer | 1. Navigate to /admin | N/A | Redirected to /unauthorized page |
| TC_AUTH_011 | Password Reset Request | Customer | Authentication | User not logged in | 1. Navigate to /forgot-password<br>2. Enter email<br>3. Click Send Reset Link | email: customer@example.com | Success message "Reset link sent" displayed |
| TC_AUTH_012 | Password Reset with Valid OTP | Customer | Authentication | Reset email sent | 1. Navigate to reset link<br>2. Enter valid OTP<br>3. Enter new password<br>4. Click Reset | otp: 123456<br>newPassword: newpass123 | Password updated, redirected to login |
| TC_PC_001 | View Product Catalog | Customer | Product Catalog | User logged in | 1. Navigate to /shoes | N/A | Product grid displayed with images and prices |
| TC_PC_002 | Filter Products by Brand | Customer | Product Catalog | User logged in | 1. Navigate to /shoes<br>2. Select brand filter<br>3. Click Apply | brand: Nike | Products filtered to show only selected brand |
| TC_PC_003 | Filter Products by Price Range | Customer | Product Catalog | User logged in | 1. Navigate to /shoes<br>2. Set price range<br>3. Click Apply | minPrice: 50<br>maxPrice: 200 | Products within price range displayed |
| TC_PC_004 | Sort Products by Price Low to High | Customer | Product Catalog | User logged in | 1. Navigate to /shoes<br>2. Select "Price: Low to High" sort<br>3. Click Apply | N/A | Products sorted ascending by price |
| TC_PC_005 | Sort Products by Price High to Low | Customer | Product Catalog | User logged in | 1. Navigate to /shoes<br>2. Select "Price: High to Low" sort<br>3. Click Apply | N/A | Products sorted descending by price |
| TC_PC_006 | View Product Detail Page | Customer | Product Catalog | User logged in | 1. Navigate to /shoes<br>2. Click on product image/title | productId: 1 | Product detail page with images, description, variants |
| TC_PC_007 | Product Detail with Multiple Images | Customer | Product Catalog | User logged in | 1. Navigate to product detail<br>2. Click image thumbnails | N/A | Main image changes when thumbnails clicked |
| TC_PC_008 | Select Product Size and Color | Customer | Product Catalog | User logged in | 1. Navigate to product detail<br>2. Select size option<br>3. Select color option | size: 10<br>color: Black | Size and color selections updated in UI |
| TC_PC_009 | Out of Stock Product Display | Customer | Product Catalog | User logged in | 1. Navigate to out of stock product | N/A | "Out of Stock" badge displayed, add to cart disabled |
| TC_PC_010 | Product Search Functionality | Customer | Product Catalog | User logged in | 1. Enter search term in search box<br>2. Click Search | searchTerm: "running shoes" | Search results displayed matching query |
| TC_SC_001 | Add Product to Cart | Customer | Shopping Cart | User logged in | 1. Navigate to product detail<br>2. Select size/quantity<br>3. Click "Add to Cart" | size: 9<br>quantity: 2 | Success notification, cart icon shows updated count |
| TC_SC_002 | View Shopping Cart | Customer | Shopping Cart | User logged in, items in cart | 1. Click cart icon | N/A | Cart sidebar/slideout displays with items, quantities, prices |
| TC_SC_003 | Update Cart Item Quantity | Customer | Shopping Cart | User logged in, items in cart | 1. Open cart<br>2. Change quantity<br>3. Click Update | quantity: 3 | Quantity updated, total price recalculated |
| TC_SC_004 | Remove Item from Cart | Customer | Shopping Cart | User logged in, items in cart | 1. Open cart<br>2. Click remove on item | N/A | Item removed, total updated |
| TC_SC_005 | Clear Entire Cart | Customer | Shopping Cart | User logged in, items in cart | 1. Open cart<br>2. Click "Clear Cart" | N/A | All items removed, cart empty |
| TC_SC_006 | Cart Persistence Across Sessions | Customer | Shopping Cart | User logged in, items in cart | 1. Add items to cart<br>2. Logout<br>3. Login again<br>4. Check cart | N/A | Cart items preserved after re-login |
| TC_SC_007 | Cart Total Calculation | Customer | Shopping Cart | User logged in, multiple items | 1. Add multiple items<br>2. View cart total | item1: $50 qty 1<br>item2: $30 qty 2 | Total shows $110 (50 + 60) |
| TC_SC_008 | Maximum Cart Quantity Limit | Customer | Shopping Cart | User logged in | 1. Try to add quantity > 10 | quantity: 15 | Error message "Maximum quantity is 10" |
| TC_SC_009 | Cart with Discount Code | Customer | Shopping Cart | User logged in, items in cart | 1. Open cart<br>2. Enter discount code<br>3. Apply | discountCode: SAVE10 | Discount applied, total reduced by 10% |
| TC_SC_010 | Invalid Discount Code | Customer | Shopping Cart | User logged in, items in cart | 1. Open cart<br>2. Enter invalid code<br>3. Apply | discountCode: INVALID | Error message "Invalid discount code" |
| TC_CO_001 | Proceed to Checkout | Customer | Checkout | User logged in, items in cart | 1. Open cart<br>2. Click "Checkout" | N/A | Redirected to checkout page with cart summary |
| TC_CO_002 | Checkout with Empty Cart | Customer | Checkout | User logged in, cart empty | 1. Navigate to /checkout | N/A | Error message or redirect to cart page |
| TC_CO_003 | Enter Shipping Information | Customer | Checkout | At checkout page | 1. Fill shipping address<br>2. Fill contact info<br>3. Click Continue | address: 123 Main St<br>phone: 1234567890 | Form validation passes, proceed to payment |
| TC_CO_004 | Invalid Shipping Email | Customer | Checkout | At checkout page | 1. Enter invalid email<br>2. Click Continue | email: invalidemail | Error "Invalid email format" |
| TC_CO_005 | Select COD Payment Method | Customer | Checkout | At checkout page | 1. Select "Cash on Delivery"<br>2. Click Continue | N/A | COD option selected, payment summary shown |
| TC_CO_006 | Select VNPay Payment Method | Customer | Checkout | At checkout page | 1. Select "VNPay"<br>2. Click Continue | N/A | VNPay option selected, bank selection shown |
| TC_CO_007 | Review Order Summary | Customer | Checkout | At checkout page | 1. Review items, prices, total | N/A | All cart items, quantities, prices displayed correctly |
| TC_CO_008 | Apply Discount at Checkout | Customer | Checkout | At checkout page | 1. Enter discount code<br>2. Apply | discountCode: SAVE10 | Discount applied, total updated |
| TC_CO_009 | Checkout Form Validation | Customer | Checkout | At checkout page | 1. Leave required fields empty<br>2. Click Continue | N/A | Error messages for all required fields |
| TC_CO_010 | Checkout with Maximum Address Length | Customer | Checkout | At checkout page | 1. Enter very long address<br>2. Click Continue | address: (500+ characters) | Address truncated or error if too long |
| TC_PAY_001 | COD Order Confirmation | Customer | Payment | COD selected at checkout | 1. Review order<br>2. Click "Place Order" | N/A | Order confirmation page, order number displayed |
| TC_PAY_002 | VNPay Payment Redirect | Customer | Payment | VNPay selected at checkout | 1. Select bank<br>2. Click "Pay Now" | bankCode: NCB | Redirected to VNPay gateway URL |
| TC_PAY_003 | VNPay Success Return | Customer | Payment | Payment initiated | 1. Complete payment on VNPay<br>2. Return to site | N/A | Success page with order confirmation |
| TC_PAY_004 | VNPay Failure Return | Customer | Payment | Payment initiated | 1. Fail payment on VNPay<br>2. Return to site | N/A | Failure page with error message |
| TC_PAY_005 | VNPay Cancel Return | Customer | Payment | Payment initiated | 1. Cancel on VNPay<br>2. Return to site | N/A | Cancel page, order not completed |
| TC_PAY_006 | Payment Timeout Handling | Customer | Payment | VNPay payment initiated | 1. Wait for timeout<br>2. Check status | N/A | Timeout error, order status updated |
| TC_PAY_007 | VNPay Loading States | Customer | Payment | VNPay selected | 1. Click Pay Now<br>2. Observe UI | N/A | Loading spinner, disabled buttons during redirect |
| TC_PAY_008 | Payment Amount Display | Customer | Payment | At payment step | 1. Check payment amount | total: $150 | Correct amount $150 displayed |
| TC_PAY_009 | Multiple Payment Attempts | Customer | Payment | Failed payment | 1. Retry payment<br>2. Complete successfully | N/A | Second attempt succeeds |
| TC_PAY_010 | Payment Security Indicators | Customer | Payment | At VNPay redirect | 1. Check URL<br>2. Check SSL | N/A | HTTPS URL, security indicators present |
| TC_OT_001 | View Order History | Customer | Order Tracking | User logged in, has orders | 1. Navigate to /order-history | N/A | List of user's orders with status, dates |
| TC_OT_002 | View Order Detail | Customer | Order Tracking | User logged in, has orders | 1. Click on order in history | orderId: 123 | Order detail page with items, status, tracking |
| TC_OT_003 | Order Status Display | Customer | Order Tracking | User logged in, has orders | 1. View order status | status: SHIPPED | Status badge shows "Shipped" |
| TC_OT_004 | Order Tracking Information | Customer | Order Tracking | User logged in, shipped order | 1. View tracking info | N/A | Tracking number, carrier, estimated delivery |
| TC_OT_005 | Order History Pagination | Customer | Order Tracking | User logged in, many orders | 1. Navigate through pages | N/A | Orders paginated, navigation works |
| TC_OT_006 | Order History Filtering | Customer | Order Tracking | User logged in, multiple statuses | 1. Filter by status | status: DELIVERED | Only delivered orders shown |
| TC_OT_007 | Order History Search | Customer | Order Tracking | User logged in, many orders | 1. Search by order number | orderNumber: ORD001 | Matching order displayed |
| TC_OT_008 | Recent Orders Highlight | Customer | Order Tracking | User logged in, recent order | 1. Check recent orders | N/A | Recent orders highlighted or sorted first |
| TC_OT_009 | Order Download/Print | Customer | Order Tracking | User logged in, order detail | 1. Click Download/Print | N/A | PDF or printable version generated |
| TC_OT_010 | Order Status Updates | Customer | Order Tracking | User logged in, order in transit | 1. Refresh page | N/A | Status updates automatically or on refresh |
| TC_OM_001 | Staff View All Orders | Staff | Order Management | Staff logged in | 1. Navigate to /staff/member-order-history | N/A | List of all customer orders |
| TC_OM_002 | Staff View Order Detail | Staff | Order Management | Staff logged in | 1. Click on order | orderId: 123 | Order detail with customer info |
| TC_OM_003 | Staff Update Order Status | Staff | Order Management | Staff logged in, order pending | 1. Change status to CONFIRMED<br>2. Save | status: CONFIRMED | Status updated successfully |
| TC_OM_004 | Manager View All Orders | Manager | Order Management | Manager logged in | 1. Navigate to /manager/member-order-history | N/A | List of all orders with management options |
| TC_OM_005 | Manager Cancel Order | Manager | Order Management | Manager logged in, cancellable order | 1. Click Cancel Order<br>2. Confirm | orderId: 123 | Order status changed to CANCELLED |
| TC_OM_006 | Manager Create Discount | Manager | Order Management | Manager logged in | 1. Navigate to discount management<br>2. Create new discount | code: SAVE20<br>percentage: 20 | Discount created successfully |
| TC_OM_007 | Admin View User Management | Admin | Order Management | Admin logged in | 1. Navigate to /admin/account-management | N/A | List of all users with roles |
| TC_OM_008 | Admin Create New User | Admin | Order Management | Admin logged in | 1. Click Create User<br>2. Fill form<br>3. Save | email: newuser@example.com<br>role: STAFF | User created with specified role |
| TC_OM_009 | Admin Update User Role | Admin | Order Management | Admin logged in, user exists | 1. Edit user<br>2. Change role<br>3. Save | userId: 456<br>newRole: MANAGER | User role updated |
| TC_OM_010 | Admin Delete User | Admin | Order Management | Admin logged in, user exists | 1. Select user<br>2. Click Delete<br>3. Confirm | userId: 456 | User deleted from system |
| TC_DM_001 | Apply Valid Discount Code | Customer | Discount Management | User logged in, items in cart | 1. Open cart<br>2. Enter code<br>3. Apply | discountCode: SAVE10 | Discount applied, total reduced |
| TC_DM_002 | Apply Expired Discount Code | Customer | Discount Management | User logged in, items in cart | 1. Open cart<br>2. Enter expired code<br>3. Apply | discountCode: EXPIRED10 | Error "Discount code expired" |
| TC_DM_003 | Apply Used Discount Code | Customer | Discount Management | User logged in, items in cart | 1. Open cart<br>2. Enter used code<br>3. Apply | discountCode: USED10 | Error "Discount code already used" |
| TC_DM_004 | Discount Minimum Purchase | Customer | Discount Management | User logged in, cart below minimum | 1. Enter code<br>2. Apply | discountCode: MIN100 | Error "Minimum purchase $100 required" |
| TC_DM_005 | Discount Real-time Update | Customer | Discount Management | User logged in, items in cart | 1. Apply discount<br>2. Add more items | N/A | Discount recalculated automatically |
| TC_DM_006 | Remove Applied Discount | Customer | Discount Management | User logged in, discount applied | 1. Click Remove Discount | N/A | Discount removed, total updated |
| TC_DM_007 | Multiple Discount Attempts | Customer | Discount Management | User logged in, discount applied | 1. Try to apply another code | discountCode: SAVE20 | Error "Only one discount per order" |
| TC_DM_008 | Discount Case Sensitivity | Customer | Discount Management | User logged in, items in cart | 1. Enter code in wrong case<br>2. Apply | discountCode: save10 | Discount applied (case insensitive) |
| TC_DM_009 | Discount UI Feedback | Customer | Discount Management | User logged in, invalid code | 1. Enter invalid code<br>2. Apply | discountCode: INVALID | Red error styling, clear message |
| TC_DM_010 | Discount Success Feedback | Customer | Discount Management | User logged in, valid code | 1. Enter valid code<br>2. Apply | discountCode: SAVE10 | Green success message, amount saved shown |
| TC_URM_001 | Customer Profile View | Customer | User & Role Management | Customer logged in | 1. Navigate to /profile/me | N/A | Profile information displayed |
| TC_URM_002 | Customer Profile Edit | Customer | User & Role Management | Customer logged in | 1. Click Edit Profile<br>2. Update info<br>3. Save | name: Updated Name | Profile updated successfully |
| TC_URM_003 | Customer Change Password | Customer | User & Role Management | Customer logged in | 1. Navigate to change password<br>2. Enter old/new passwords<br>3. Save | oldPassword: password123<br>newPassword: newpass123 | Password changed, logout required |
| TC_URM_004 | Invalid Current Password | Customer | User & Role Management | Customer logged in | 1. Enter wrong current password<br>2. Save | oldPassword: wrongpass | Error "Current password incorrect" |
| TC_URM_005 | Password Confirmation Mismatch | Customer | User & Role Management | Customer logged in | 1. Enter new password<br>2. Enter different confirm<br>3. Save | newPassword: pass123<br>confirmPassword: pass456 | Error "Passwords do not match" |
| TC_URM_006 | Profile Validation | Customer | User & Role Management | Customer logged in | 1. Leave required fields empty<br>2. Save | N/A | Validation errors for required fields |
| TC_URM_007 | Admin User List View | Admin | User & Role Management | Admin logged in | 1. Navigate to user management | N/A | Paginated list of all users |
| TC_URM_008 | Admin User Search | Admin | User & Role Management | Admin logged in | 1. Enter search term<br>2. Search | searchTerm: john | Users matching search displayed |
| TC_URM_009 | Admin User Role Filter | Admin | User & Role Management | Admin logged in | 1. Filter by role | role: STAFF | Only staff users displayed |
| TC_URM_010 | Admin User Status Toggle | Admin | User & Role Management | Admin logged in, user exists | 1. Toggle user active status | userId: 123 | User status updated, confirmation shown |