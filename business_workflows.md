# Business Workflows for ShoeShop Store

## 1. Login / Logout

This workflow handles user authentication and session management for all four roles in the ShoeShop system. It ensures secure access to role-specific features while maintaining session integrity throughout the user journey.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer/Staff/Manager/Admin | Navigate to login page | User wants to access system | Login form displayed | Public access |
| 2 | Customer/Staff/Manager/Admin | Enter credentials | Valid email/password | Credentials validated against user database | Password encryption used |
| 3 | System | Verify credentials | IF credentials match AND account active | User authenticated, JWT token generated | Role information included in token |
| 4 | System | Redirect to dashboard | IF authentication successful | User redirected to role-specific dashboard | Customer: shop, Staff: orders, Manager: management, Admin: admin |
| 5 | System | Display error message | IF authentication fails | Error message shown, login form remains | Account lockout after multiple failures |
| 6 | Customer/Staff/Manager/Admin | Access protected features | User navigates system | Role-based menus and features displayed | Authorization checked on each action |
| 7 | Customer/Staff/Manager/Admin | Click logout | User wants to end session | Session terminated, token invalidated | Secure logout |
| 8 | System | Redirect to login | After logout | User returned to login page | Session completely cleared |

**Swimlane Summary:**
- **Customer/Staff/Manager/Admin**: Enter credentials, navigate system, logout
- **System**: Validate credentials, generate tokens, enforce authorization, manage sessions

## 2. View Catalog

This workflow enables customers to browse and explore the product catalog with advanced filtering and search capabilities. It supports informed purchasing decisions through detailed product information and visual presentation.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer | Navigate to catalog | User wants to browse products | Product catalog displayed | Default sorting by newest |
| 2 | Customer | Browse products | Scroll through catalog | Products loaded with pagination | Lazy loading for performance |
| 3 | Customer | Apply filters | Select brand, price range, category | Catalog filtered in real-time | Multiple filters can be combined |
| 4 | System | Update product list | Based on filter criteria | Filtered products displayed | Search index used for performance |
| 5 | Customer | Sort products | Select sort option (price, name, rating) | Products reordered | Ascending/descending options |
| 6 | System | Apply sorting | Based on selected criteria | Sorted product list shown | Maintains current filters |
| 7 | Customer | Click product | Select specific product | Product detail page opened | Product ID passed in URL |
| 8 | System | Display product details | Product exists in catalog | Full product info, images, variants shown | Stock status indicated |
| 9 | Customer | View product images | Click image thumbnails | Main image changes | Zoom functionality available |
| 10 | Customer | Select product options | Choose size, color, quantity | Options updated in UI | Availability checked |
| 11 | Customer | Add to cart | Click "Add to Cart" button | Product added to shopping cart | Cart counter updated |
| 12 | System | Update cart | IF product in stock AND valid options | Cart total recalculated | Inventory reserved temporarily |

**Swimlane Summary:**
- **Customer**: Browse, filter, sort, view details, add to cart
- **System**: Display products, apply filters/sorting, show details, manage cart

## 3. Order Checkout

This workflow guides customers through the complete order creation process from cart review to order confirmation. It ensures accurate order information and validates all requirements before payment processing.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer | Review cart | Navigate to cart page | Cart contents displayed | Quantities and totals shown |
| 2 | Customer | Modify cart | Update quantities or remove items | Cart updated in real-time | Totals recalculated automatically |
| 3 | Customer | Proceed to checkout | Click "Checkout" button | IF cart not empty | Checkout page opened |
| 4 | System | Validate cart | IF cart empty | Error message displayed | Redirect to catalog |
| 5 | Customer | Enter shipping information | Fill address, phone, email | Form validation applied | Required fields marked |
| 6 | System | Validate shipping info | IF all required fields complete | Form accepted | Address format validated |
| 7 | Customer | Review order summary | Check items, prices, totals | Order summary displayed | Shipping costs included |
| 8 | Customer | Apply discount code | Enter coupon code | IF valid code entered | Discount applied to total |
| 9 | System | Validate discount | IF code exists AND not expired AND meets conditions | Discount calculated | Error shown for invalid codes |
| 10 | Customer | Select payment method | Choose COD or VNPay | Payment options displayed | Available methods shown |
| 11 | Customer | Confirm order | Click "Place Order" button | Order creation initiated | Final validation performed |
| 12 | System | Create order | IF all validations pass | Order saved to database | Order number generated |
| 13 | System | Update inventory | Order items reserved | Stock quantities reduced | Prevents overselling |
| 14 | System | Send confirmation | Order created successfully | Confirmation email sent | Order details included |

**Swimlane Summary:**
- **Customer**: Review cart, enter shipping, apply discounts, select payment, confirm
- **System**: Validate data, create order, update inventory, send confirmation

## 4. Payment (COD)

This workflow handles cash on delivery payment processing, providing a simple payment option for customers who prefer to pay upon receipt of goods.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer | Select COD payment | During checkout | COD option chosen | No immediate payment required |
| 2 | Customer | Confirm order | Click "Place Order" | Order submitted with COD | Payment method recorded |
| 3 | System | Process COD order | Order created successfully | Order status set to "CREATED" | Payment status marked as "PENDING" |
| 4 | System | Prepare order | Staff processes order | Status updated to "CONFIRMED" | Inventory already reserved |
| 5 | System | Ship order | Order ready for delivery | Status changed to "SHIPPED" | Tracking information added |
| 6 | Customer | Receive delivery | Package delivered | Customer receives goods | Delivery confirmation required |
| 7 | Customer | Pay cash | Pay delivery person | Payment collected | Exact change preferred |
| 8 | System | Confirm delivery | Delivery partner updates | Status changed to "DELIVERED" | Payment status updated to "COMPLETED" |
| 9 | System | Process payment | Cash payment recorded | Financial records updated | Revenue recognized |

**Swimlane Summary:**
- **Customer**: Select COD, receive delivery, pay cash
- **System**: Process order, update statuses, record payment

## 5. Payment (VNPay)

This workflow manages online payment processing through VNPay integration, providing secure electronic payment with real-time status updates and comprehensive error handling.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer | Select VNPay payment | During checkout | VNPay option chosen | Bank selection required |
| 2 | Customer | Choose bank | Select preferred bank | Bank option selected | Available banks listed |
| 3 | Customer | Initiate payment | Click "Pay Now" button | Payment request sent | Secure connection established |
| 4 | System | Generate payment URL | Valid order and bank selected | VNPay payment URL created | Includes order details and security hash |
| 5 | System | Redirect to VNPay | Payment URL generated | Customer redirected to VNPay gateway | Session maintained |
| 6 | VNPay | Process payment | Customer enters bank credentials | Payment processed by bank | Secure transaction processing |
| 7 | VNPay | Send callback | Payment completed or failed | Callback sent to system | Includes transaction status |
| 8 | System | Validate callback | IF callback signature valid | Callback accepted | Security hash verified |
| 9 | System | Update order status | IF payment successful | Order status set to "CONFIRMED" | Payment status updated |
| 10 | System | Redirect customer | Back to order confirmation | Success/failure page displayed | Clear status indication |
| 11 | Customer | View payment result | Returned to system | Payment confirmation shown | Order details displayed |
| 12 | System | Handle timeout | IF no callback within time limit | Order status updated | Payment marked as expired |
| 13 | System | Process refunds | IF payment failed after processing | Refund initiated | Customer notified |

**Swimlane Summary:**
- **Customer**: Select VNPay, choose bank, enter payment details, view result
- **VNPay**: Process payment, send callback with status
- **System**: Generate payment URL, validate callback, update order, handle errors

## 6. Order Management (Staff)

This workflow enables staff members to manage order processing from confirmation through delivery, ensuring smooth operational handling of customer orders.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Staff | View new orders | Login to staff dashboard | Order list displayed | Status filter available |
| 2 | Staff | Review order details | Click on specific order | Complete order information shown | Customer details, items, payment |
| 3 | Staff | Confirm order | Verify stock and details | IF order valid | Status changed to "CONFIRMED" |
| 4 | System | Update inventory | Confirmed order | Stock quantities adjusted | Prevents overselling |
| 5 | Staff | Prepare order | Gather items for packing | Status updated to "PREPARING" | Preparation time tracked |
| 6 | Staff | Mark ready for delivery | Order packed and ready | Status changed to "READY_FOR_DELIVERY" | Delivery partner notified |
| 7 | System | Assign delivery | Based on location and availability | Delivery scheduled | Tracking number generated |
| 8 | Staff | Update to shipped | Order handed to delivery | Status changed to "SHIPPED" | Customer notified via email |
| 9 | System | Track delivery | Integration with delivery partner | Status updates received | Real-time tracking available |
| 10 | System | Mark delivered | Delivery confirmation received | Status changed to "DELIVERED" | Customer satisfaction survey sent |
| 11 | Staff | Handle issues | IF delivery problems occur | Status updated appropriately | Customer service involved |

**Swimlane Summary:**
- **Staff**: Review orders, confirm, prepare, ship, handle issues
- **System**: Update statuses, manage inventory, coordinate delivery

## 7. Order Management (Manager)

This workflow provides managers with enhanced order oversight capabilities including cancellation authority and performance monitoring across the order fulfillment process.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Manager | View all orders | Access management dashboard | Complete order list displayed | Advanced filtering available |
| 2 | Manager | Monitor performance | Review order metrics | Statistics and KPIs shown | Delivery times, success rates |
| 3 | Manager | Override order status | IF operational issues | Status manually adjusted | Audit trail maintained |
| 4 | Manager | Cancel orders | IF customer requests OR stock issues | IF order not shipped | Status changed to "CANCELLED" |
| 5 | System | Process cancellation | Order cancelled | Inventory restored | Refund initiated if paid |
| 6 | Manager | Create discount codes | For promotions or compensation | Discount rules defined | Usage limits and expiry set |
| 7 | System | Validate discounts | During order processing | Discount applied correctly | Business rules enforced |
| 8 | Manager | Handle escalations | Complex customer issues | Order status managed | Customer service coordination |
| 9 | Manager | Generate reports | Order fulfillment analytics | Performance reports created | Data for business decisions |

**Swimlane Summary:**
- **Manager**: Monitor orders, override statuses, cancel orders, create discounts, handle escalations
- **System**: Process cancellations, validate discounts, generate reports

## 8. Order Management (Admin)

This workflow gives administrators complete system control over orders, users, and operational parameters, enabling comprehensive system management and user administration.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Admin | Access admin dashboard | Login as administrator | Full system overview displayed | All modules accessible |
| 2 | Admin | Manage user accounts | Create, edit, deactivate users | User database updated | Role assignments controlled |
| 3 | Admin | Override any order | IF system issues OR legal requests | Order status forcibly changed | Full audit trail required |
| 4 | Admin | Configure system settings | Update business rules | System parameters modified | Requires system restart if needed |
| 5 | Admin | View system logs | Monitor system activity | Audit logs and error reports | Security and compliance |
| 6 | Admin | Handle system issues | IF technical problems | Orders and users affected | Emergency response procedures |
| 7 | Admin | Generate system reports | Comprehensive analytics | Business and technical reports | For stakeholders and compliance |

**Swimlane Summary:**
- **Admin**: Manage users, override orders, configure system, monitor logs, handle issues
- **System**: Enforce admin actions, maintain audit trails, update configurations

## 9. Order Tracking (Customer)

This workflow allows customers to monitor their order progress throughout the fulfillment lifecycle, providing transparency and enabling order management actions when appropriate.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer | View order history | Login to account | List of customer's orders shown | Chronological order, status indicated |
| 2 | Customer | Select specific order | Click on order from list | Order detail page displayed | Complete order information |
| 3 | Customer | Check order status | View current status | Status badge and description shown | Color-coded for clarity |
| 4 | Customer | View tracking information | IF order shipped | Tracking number and updates displayed | Integration with delivery partner |
| 5 | Customer | Cancel order | IF status allows cancellation | Cancellation option available | Time limits apply |
| 6 | System | Process cancellation | IF cancellation approved | Order status changed to "CANCELLED" | Inventory restored, refund processed |
| 7 | Customer | Contact support | IF issues with order | Support contact information provided | Multiple channels available |
| 8 | System | Send notifications | Status changes occur | Email/SMS notifications sent | Configurable preferences |
| 9 | Customer | Leave feedback | After delivery | Rating and review options | Improves service quality |

**Swimlane Summary:**
- **Customer**: View orders, check status, track delivery, cancel if allowed, provide feedback
- **System**: Display order info, send notifications, process cancellations

## 10. Authentication & Authorization for 4 Roles

This workflow demonstrates how the authentication and authorization system works across all four user roles, ensuring appropriate access to system features based on assigned permissions.

| Step No. | Actor/Role | Action | Input/Condition | System Reaction / Output | Notes |
|----------|------------|--------|-----------------|---------------------------|-------|
| 1 | Customer/Staff/Manager/Admin | Attempt system access | User navigates to protected area | Authentication challenge issued | Public pages accessible without login |
| 2 | System | Verify user identity | Credentials provided | IF valid user | Role and permissions retrieved |
| 3 | System | Assign role permissions | Based on user role in database | Permission set applied | Customer: basic access, Staff: order management, Manager: extended management, Admin: full access |
| 4 | Customer | Access customer features | Login successful | Shop catalog, cart, orders accessible | Personal data protected |
| 5 | Staff | Access staff features | Login successful | Order management, customer service tools | Cannot modify prices or create discounts |
| 6 | Manager | Access manager features | Login successful | All staff features plus discount management | Can cancel orders, create promotions |
| 7 | Admin | Access admin features | Login successful | Full system control, user management | Can modify any data, system configuration |
| 8 | System | Enforce authorization | Any action attempted | IF user has permission | Action allowed | ELSE access denied |
| 9 | System | Log security events | Permission checks occur | Audit trail maintained | Compliance and security monitoring |
| 10 | Customer/Staff/Manager/Admin | Logout | End session | All permissions revoked | Secure session termination |

**Swimlane Summary:**
- **Customer**: Access personal shopping features
- **Staff**: Manage orders and customer service
- **Manager**: Oversee operations and create promotions
- **Admin**: Control system and user management
- **System**: Enforce permissions, maintain security, log activities