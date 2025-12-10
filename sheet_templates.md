# Templates cho các Sheet trong Báo Cáo Kiểm Thử

## Bug Report Sheet

| Bug ID | Tiêu đề | Mô tả | Severity (Critical/Major/Minor) | Status (New/Open/Fixed/Closed) | Assigned To | Reported By | Date Reported | Date Fixed | Test Case ID | Steps to Reproduce | Expected Result | Actual Result | Environment | Comments |
|--------|---------|--------|-------------------------------|-------------------------------|-------------|-------------|----------------|------------|-------------|-------------------|-----------------|--------------|-------------|----------|
| BUG-001 | Thanh toán VNPay thất bại | Khi chọn VNPay, không chuyển hướng | Critical | Open | Dev Team | QC | 2025-11-26 |  | TC-005 | 1. Thêm sản phẩm vào giỏ<br>2. Checkout<br>3. Chọn VNPay | Chuyển đến cổng VNPay | Lỗi 500 | Staging | Cần fix ngay |
| ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... | ... |

## Test Report Sheet

| Test Run ID | Test Case ID | Test Case Name | Status (Pass/Fail) | Executed By | Execution Date | Environment | Notes | Bug ID (if failed) |
|-------------|--------------|----------------|-------------------|-------------|----------------|-------------|-------|-------------------|
| TR-001 | TC-001 | Đăng nhập hợp lệ | Pass | QC1 | 2025-11-26 | Staging | OK |  |
| TR-002 | TC-002 | Thêm sản phẩm vào giỏ | Fail | QC1 | 2025-11-26 | Staging | Lỗi tính toán | BUG-002 |
| ... | ... | ... | ... | ... | ... | ... | ... | ... |

### Tóm Tắt Test Report
- Tổng Test Case: 100
- Pass: 95
- Fail: 5
- Tỷ Lệ Pass: 95%
- Coverage: 85%
- Lỗi Critical: 1
- Lỗi Major: 2
- Lỗi Minor: 2

## QA Checklist Sheet

| Checklist ID | Item | Description | Status (Pass/Fail/NA) | Comments | Reviewed By | Review Date |
|--------------|------|-------------|-----------------------|----------|-------------|-------------|
| QA-001 | SRS Compliance | Hệ thống đáp ứng SRS | Pass | Tất cả yêu cầu FR được cover | QA Manager | 2025-11-26 |
| QA-002 | Test Coverage | Coverage > 80% | Pass | 85% đạt yêu cầu | QA Manager | 2025-11-26 |
| QA-003 | Bug Resolution | Tất cả critical bugs fixed | Pass | 1 critical fixed | QA Manager | 2025-11-26 |
| QA-004 | Automation | Scripts chạy ổn định | Pass | Selenium OK | QA Manager | 2025-11-26 |
| QA-005 | Performance | Thời gian phản hồi < 2s | Pass | Trung bình 1.5s | QA Manager | 2025-11-26 |
| ... | ... | ... | ... | ... | ... | ... |

### Nhận Xét QA Manager
Hệ thống sẵn sàng triển khai. Khuyến nghị: Cải thiện automation cho thanh toán, thêm monitoring cho phân quyền.

## Automation Scripts Sheet

| Script ID | Script Name | Type (Selenium/JUnit) | Description | Test Case Mapped | Status (Active/Inactive) | Last Run | Result | Notes |
|-----------|-------------|----------------------|-------------|------------------|--------------------------|----------|--------|-------|
| AS-001 | LoginTest.java | JUnit | Kiểm thử đăng nhập | TC-001 | Active | 2025-11-26 | Pass | OK |
| AS-002 | AddToCartTest.java | Selenium | Thêm sản phẩm vào giỏ | TC-002 | Active | 2025-11-26 | Fail | Lỗi element |
| AS-003 | CheckoutTest.java | Selenium | Quy trình thanh toán | TC-003, TC-004, TC-005 | Active | 2025-11-26 | Pass | OK |
| ... | ... | ... | ... | ... | ... | ... | ... | ... |

### Tóm Tắt Automation
- Tổng Scripts: 20
- Active: 18
- Success Rate: 90%
- Công cụ: Selenium 4.0, JUnit 5, TestNG