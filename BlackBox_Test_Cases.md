# Black-Box Testing Design for Adding Items to Cart and Viewing Product Details

Based on the requirements in 'BT06_01_KiemThuBangQuyetDinh_BaiSua.pdf', this document outlines detailed black-box test cases for two functions: Viewing Product Details and Adding Items to Cart. Test cases use equivalence partitioning, boundary value analysis, and decision table techniques.

## 1. Viewing Product Details

### Function Description
When a user enters a Product Number, the system displays product details if the product exists and is active. If not, it shows an error message.

### Inputs
- Product Number: 10-digit string, numeric, leading zeros allowed.

### Outputs
- Valid product: Description, Unit Price, WAC, Taxable indicator (* if taxable).
- Invalid: "Item not found" message.

### Equivalence Classes and Boundary Values
- Valid format: Exactly 10 digits, numeric.
- Invalid format: <10 digits, >10 digits, non-numeric, empty.
- Valid product: Exists and active.
- Invalid product: Not exists or inactive.
- Boundaries: 9 digits, 10 digits, 11 digits, all zeros, leading zeros.

### Test Cases

| Test Case ID | Description | Input (Product Number) | Expected Output |
|--------------|-------------|-------------------------|-----------------|
| VPD-001 | Valid product, taxable | 1234567890 (exists, active, taxable) | Display: Description, Unit Price, WAC, * |
| VPD-002 | Valid product, non-taxable | 0987654321 (exists, active, non-taxable) | Display: Description, Unit Price, WAC, no * |
| VPD-003 | Valid format, product inactive | 1111111111 (exists, inactive) | "Item not found" |
| VPD-004 | Valid format, product not exists | 9999999999 (not exists) | "Item not found" |
| VPD-005 | Invalid: 9 digits | 123456789 | "Item not found" |
| VPD-006 | Invalid: 11 digits | 12345678901 | "Item not found" |
| VPD-007 | Invalid: non-numeric | ABCDEFGHIJ | "Item not found" |
| VPD-008 | Invalid: empty | (empty) | "Item not found" |
| VPD-009 | Boundary: all zeros | 0000000000 (exists, active) | Display details |
| VPD-010 | Boundary: leading zeros | 0012345678 (exists, active) | Display details |

## 2. Adding Items to Cart

### Function Description
After viewing product details, user can set quantity, modify pricing, and add to cart. Tax is calculated based on product, customer, and address.

### Inputs
- Product Number (from viewing)
- Quantity: Integer >0
- New Price: 0.00 - 999999.99
- Profit Margin %: >=30
- Discount %: 0-100
- Customer: Optional for retail, mandatory for non-retail
- Customer Address: Domestic/Overseas

### Outputs
- Cart item added with calculated prices, tax.
- Errors for invalid inputs.

### Equivalence Classes and Boundary Values
- Quantity: Valid (1-999), Invalid (0, negative, non-integer, >999)
- New Price: Valid (0.00-999999.99), Invalid (negative, >999999.99, non-numeric)
- Profit Margin: Valid (>=30), Invalid (<30)
- Discount: Valid (0-100), Invalid (<0, >100)

### Decision Table for Tax Calculation
Based on the document's simplified decision table:

| Rule | Taxable Product | Retail Customer | Taxable Customer | Customer Address | 10% Tax |
|------|-----------------|-----------------|------------------|------------------|---------|
| R1   | N               | DC              | DC               | DC               | N       |
| R10  | Y               | N               | N                | DC               | N       |
| R13  | Y               | N               | Y                | U                | ?       |
| R14  | Y               | N               | Y                | D                | Y       |
| R15  | Y               | N               | Y                | O                | N       |
| R19  | Y               | Y               | DC               | U                | ?       |
| R22  | Y               | Y               | DC               | DC               | ?       |

Note: ? indicates unclear requirement, need clarification.

### Test Cases

#### Equivalence and Boundary Test Cases

| Test Case ID | Description | Inputs | Expected Output |
|--------------|-------------|--------|-----------------|
| ATC-001 | Valid add to cart | Product: valid, Qty: 1, Price: 100.00, Margin: 30%, Discount: 0%, Customer: retail | Item added, no tax |
| ATC-002 | Quantity boundary: 0 | Qty: 0 | Error: Invalid quantity |
| ATC-003 | Quantity boundary: -1 | Qty: -1 | Error: Invalid quantity |
| ATC-004 | Quantity boundary: 1000 | Qty: 1000 | Error: Quantity too large |
| ATC-005 | Quantity non-integer | Qty: 1.5 | Error: Invalid quantity |
| ATC-006 | Price boundary: 0.00 | Price: 0.00 | Item added |
| ATC-007 | Price boundary: 999999.99 | Price: 999999.99 | Item added |
| ATC-008 | Price invalid: -1.00 | Price: -1.00 | Error: Invalid price |
| ATC-009 | Price invalid: 1000000.00 | Price: 1000000.00 | Error: Invalid price |
| ATC-010 | Margin boundary: 29.99 | Margin: 29.99 | Error: Margin too low |
| ATC-011 | Margin valid: 30.00 | Margin: 30.00 | Item added |
| ATC-012 | Discount boundary: -1 | Discount: -1 | Error: Invalid discount |
| ATC-013 | Discount boundary: 101 | Discount: 101 | Error: Invalid discount |
| ATC-014 | No customer for non-retail | Customer: none, Type: non-retail | Error: Customer required |

#### Decision Table Test Cases

| Test Case ID | Description | Taxable Product | Retail Customer | Taxable Customer | Customer Address | Expected Tax |
|--------------|-------------|-----------------|-----------------|------------------|------------------|--------------|
| ATC-DT-001 | Rule R1 | N | N/A | N/A | N/A | No |
| ATC-DT-002 | Rule R10 | Y | N | N | N/A | No |
| ATC-DT-003 | Rule R13 | Y | N | Y | U | ? (Clarify) |
| ATC-DT-004 | Rule R14 | Y | N | Y | D | Yes |
| ATC-DT-005 | Rule R15 | Y | N | Y | O | No |
| ATC-DT-006 | Rule R19 | Y | Y | N/A | U | ? (Clarify) |
| ATC-DT-007 | Rule R22 | Y | Y | N/A | N/A | ? (Clarify) |

For each DT test case, assume valid other inputs, check if 10% tax is applied to the total.