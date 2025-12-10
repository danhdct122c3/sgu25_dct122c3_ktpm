# User Acceptance Test (UAT) Document for ShoeShop Store

## UAT Summary

The User Acceptance Test validates that the ShoeShop Store system meets business requirements and provides the expected value to end users before production deployment. UAT focuses on real-world business scenarios to ensure the system supports customer purchasing journeys and internal order management processes effectively. Key participants include Product Owner, business stakeholders, end users (customers), and operational staff to confirm the system delivers the promised functionality and user experience.

## UAT Scope

| Feature | Included in UAT | Notes |
|---------|-----------------|-------|
| Login/Logout | Yes | Verify secure access for all user types |
| View Catalog | Yes | Ensure customers can easily find and view products |
| Checkout | Yes | Validate complete purchase process |
| Payment COD | Yes | Confirm cash on delivery option works |
| Payment VNPay | Yes | Verify online payment integration |
| Order Tracking | Yes | Allow customers to monitor order progress |
| Order Management | Yes | Enable staff to process orders efficiently |
| Role-based Access | Yes | Confirm appropriate permissions for each user type |

## UAT Roles & Responsibilities

| Role | Responsibilities in UAT | Notes |
|------|--------------------------|-------|
| Customer (Business User) | Execute customer-facing scenarios, provide feedback on shopping experience | Represents actual end users |
| Staff Representative | Test order processing workflows, validate operational procedures | Ensures staff can manage orders effectively |
| Manager | Review management dashboards, approve system capabilities | Confirms business oversight features |
| Admin | Validate user management and system administration features | Ensures administrative controls work |
| QA | Facilitate UAT execution, document issues, support stakeholders | Provides testing expertise and coordination |
| Product Owner (PO) | Review results, make acceptance decisions, prioritize fixes | Final authority on business requirements |

## UAT Test Scenarios

| Scenario ID | Business Scenario Title | Role | Pre-condition | Steps | Expected Result | Acceptance Criteria |
|-------------|-------------------------|------|---------------|--------|-----------------|---------------------|
| UAT_001 | Complete Purchase with Cash on Delivery | Customer | Customer account exists | 1. Login to account<br>2. Browse and select products<br>3. Add items to cart<br>4. Proceed to checkout<br>5. Enter delivery details<br>6. Select cash on delivery<br>7. Confirm order | Order placed successfully, confirmation received, order appears in order history | Customer can complete full purchase journey without issues, receives order confirmation, payment method clearly communicated |
| UAT_002 | Complete Purchase with Online Payment | Customer | Customer account exists, bank account available | 1. Login to account<br>2. Browse and select products<br>3. Add items to cart<br>4. Proceed to checkout<br>5. Enter delivery details<br>6. Select VNPay option<br>7. Choose bank and complete payment<br>8. Return to confirmation | Payment processed successfully, order confirmed, payment confirmation received | Customer can pay securely online, receives immediate confirmation, payment method integrates smoothly |
| UAT_003 | Handle Failed Online Payment | Customer | Customer account exists | 1. Login to account<br>2. Add items to cart<br>3. Proceed to checkout<br>4. Select VNPay payment<br>5. Intentionally fail payment at bank<br>6. Return to store | Clear error message displayed, order not completed, can retry payment | Customer understands what happened, no money charged, can easily attempt payment again |
| UAT_004 | Track Order from Placement to Delivery | Customer | Order placed and being processed | 1. Login to account<br>2. View order history<br>3. Select recent order<br>4. Monitor status updates<br>5. Receive delivery notification | Order status updates accurately, delivery completed successfully | Customer stays informed throughout process, receives items as expected, communication is clear |
| UAT_005 | Cancel Order Before Processing | Customer | Order placed but not yet processed | 1. Login to account<br>2. View order details<br>3. Select cancel option<br>4. Confirm cancellation<br>5. Check refund status | Order cancelled successfully, refund processed if paid | Customer can cancel when appropriate, refund handled correctly, clear communication provided |
| UAT_006 | Process Order from Receipt to Delivery | Staff Representative | New order received | 1. Login to staff account<br>2. View new orders<br>3. Confirm order details<br>4. Update status to preparing<br>5. Mark as ready for delivery<br>6. Confirm delivery completion | Order status updates correctly, customer notified at each step | Staff can efficiently manage order workflow, customer receives appropriate updates, process runs smoothly |
| UAT_007 | Handle Customer Order Issues | Staff Representative | Order with delivery problem | 1. Login to staff account<br>2. Identify problem order<br>3. Contact customer if needed<br>4. Update order status appropriately<br>5. Process any refunds or replacements | Issue resolved satisfactorily, customer satisfied | Staff can handle problems effectively, communication with customer works, appropriate resolution achieved |
| UAT_008 | Review Business Performance | Manager | System operational with orders | 1. Login to manager account<br>2. View dashboard<br>3. Review order statistics<br>4. Check payment summaries<br>5. Monitor staff performance | Clear business metrics displayed, actionable insights available | Manager can make informed decisions, reports are accurate and useful, performance tracking works |
| UAT_009 | Manage User Accounts and Permissions | Admin | System operational | 1. Login to admin account<br>2. Access user management<br>3. Create new staff account<br>4. Modify user permissions<br>5. Deactivate old account | User accounts managed correctly, permissions applied properly | Admin can control system access, changes take effect immediately, security maintained |
| UAT_010 | Verify Role-Based Access Controls | All Roles | Various user accounts exist | 1. Login as customer - verify customer features only<br>2. Login as staff - verify staff features only<br>3. Login as manager - verify manager features only<br>4. Login as admin - verify all features accessible | Each role sees appropriate interface and functions | Users can only access what they should, no unauthorized access possible, interface adapts to role |

## UAT Entry Criteria

| Criteria | Description |
|----------|-------------|
| System Deployed | ShoeShop Store deployed to UAT environment and accessible |
| Major Bugs Fixed | All critical and high-priority defects from system testing resolved |
| Test Data Prepared | Realistic customer accounts, products, and order history available |
| Key Stakeholders Available | Product Owner, business users, and operational staff ready to participate |
| Training Completed | UAT participants understand how to use the system and execute scenarios |

## UAT Exit Criteria

| Criteria | Description |
|----------|-------------|
| Critical Scenarios Passed | All high-priority UAT scenarios completed successfully |
| No Blocker Defects | No business-critical issues preventing core functionality |
| Business Requirements Met | System meets all stated business needs and user expectations |
| Performance Acceptable | System responds quickly enough for business operations |
| Product Owner Approval | PO confirms system is ready for production deployment |

## UAT Sign-off

| Stakeholder | Role | Decision (Approved/Rejected) | Comments |
|-------------|------|------------------------------|----------|
| Product Owner | Business Sponsor | Approved | System meets all business requirements, ready for launch |
| Customer Representative | End User | Approved | Shopping experience works well, easy to use |
| Staff Manager | Operations | Approved | Order management features support our workflow |
| QA Lead | Testing | Approved | UAT completed successfully, no critical issues |
| Development Lead | Technical | Approved | System stable and ready for production |