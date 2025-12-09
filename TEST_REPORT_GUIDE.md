# 📊 Hướng dẫn sử dụng Test Report

## Tổng quan

Khi bạn push code lên GitHub, hệ thống sẽ tự động:
1. ✅ Chạy tất cả unit tests và integration tests
2. 📊 Tạo báo cáo chi tiết theo format bảng
3. 📈 Hiển thị kết quả từng test case
4. 💾 Lưu artifacts để xem sau

## Cách xem kết quả test trên GitHub

### 1. Xem tại GitHub Actions tab

1. Vào repository của bạn trên GitHub
2. Click vào tab **Actions**
3. Chọn workflow run mới nhất (workflow "backend-unit-tests" hoặc "Backend Integration Tests")
4. Xem kết quả:
   - **Summary**: Tổng quan về test results
   - **Test Results**: Báo cáo chi tiết từng test case (từ dorny/test-reporter)
   - **Generate Detailed Test Report**: Bảng markdown chi tiết theo format bạn yêu cầu

### 2. Format báo cáo

Báo cáo sẽ hiển thị theo format:

```markdown
## 📊 Chi tiết kết quả Unit Test

| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/Pass |
|--------------|-------|--------------|------------------|--------------|-------------|
| addToCart_userNotFound_shouldThrowUserNotExisted | user not found | userNotFound | Throw UserNotExisted | Test passed (0.5s) | ✅ PASS |
| addToCart_variantNotFound_shouldThrowVariantNotFound | variant not found | variantNotFound | Throw VariantNotFound | Test passed (0.3s) | ✅ PASS |

### 📈 Tổng kết

- **Tổng số test:** 10
- **✅ Passed:** 9
- **❌ Failed:** 1
- **⚠️ Errors:** 0
- **Tỷ lệ thành công:** 90.0%
```

## Chạy báo cáo test locally

### Cách 1: Chạy test và xem kết quả trong Maven

```bash
cd back-end
mvn clean test
```

### Cách 2: Sử dụng script Python để tạo báo cáo đẹp

```bash
# Chạy test trước
cd back-end
mvn clean test
cd ..

# Tạo báo cáo
python generate_test_report.py back-end/target/surefire-reports
```

### Cách 3: Chỉ chạy unit tests (không cần database)

```bash
cd back-end
mvn clean test -Dtest=*UnitTest -Dsurefire.failIfNoSpecifiedTests=false
cd ..
python generate_test_report.py back-end/target/surefire-reports
```

## Convention đặt tên test để báo cáo hiển thị đẹp

Để báo cáo tự động parse và hiển thị đẹp, hãy đặt tên test theo format:

```
methodName_scenario_shouldExpectedResult
```

**Ví dụ:**

```java
@Test
void addToCart_userNotFound_shouldThrowUserNotExisted() {
    // Test khi user không tồn tại
}

@Test
void addToCart_variantNotFound_shouldThrowVariantNotFound() {
    // Test khi variant không tồn tại
}

@Test
void addToCart_validInput_shouldReturnCartItem() {
    // Test với input hợp lệ
}
```

Script sẽ tự động parse tên test thành:
- **Tên hàm test**: `addToCart_userNotFound_shouldThrowUserNotExisted`
- **Mô tả**: `user not found`
- **Dữ liệu nhập**: `userNotFound`
- **Kết quả mong đợi**: `Throw UserNotExisted`

## Các workflow được cấu hình

### 1. Unit Tests (`ci-unit-tests.yml`)
- Chạy khi push vào branches: `main`, `develop`, `junit-integration-tests`, `unit-integration-test`, `shoeUnitIntergrationTest`
- Chỉ chạy test classes kết thúc bằng `*UnitTest`
- Không cần database
- Nhanh hơn integration tests

### 2. Integration Tests (`ci-integration-tests.yml`)
- Chạy khi push vào branches: `main`, `develop`, `junit-integration-tests`, `unit-integration-test`, `shoeUnitIntergrationTest`
- Chạy tất cả tests với MySQL database
- Mất thời gian hơn nhưng test đầy đủ hơn

## Xem artifacts

Nếu bạn muốn download báo cáo XML gốc:

1. Vào Actions tab
2. Chọn workflow run
3. Scroll xuống phần **Artifacts**
4. Download `unit-surefire-reports` hoặc `integration-surefire-reports`
5. Giải nén và xem XML reports

## Troubleshooting

### Không thấy báo cáo chi tiết?

1. Kiểm tra xem step "Generate Detailed Test Report" có chạy không
2. Kiểm tra log của step đó
3. Đảm bảo có test reports trong `back-end/target/surefire-reports`

### Báo cáo không hiển thị đúng format?

1. Kiểm tra tên test method có theo convention không
2. Sử dụng format: `methodName_scenario_shouldExpectedResult`
3. Tránh ký tự đặc biệt trong tên test

### Test không chạy?

1. Kiểm tra Maven configuration
2. Đảm bảo `maven-surefire-plugin` được cấu hình đúng trong `pom.xml`
3. Kiểm tra test class có annotation `@Test` không

## Cấu hình đã thêm

### 1. `pom.xml`
- Thêm `maven-surefire-plugin` với cấu hình chi tiết
- Tạo XML reports cho CI/CD

### 2. GitHub Actions workflows
- Thêm `dorny/test-reporter` để hiển thị test results dạng bảng
- Thêm script Python inline để tạo markdown report
- Upload artifacts để download sau

### 3. Script Python độc lập
- File `generate_test_report.py` để chạy locally
- Parse XML reports và tạo báo cáo đẹp

## Ví dụ output khi push

Khi bạn push code lên, GitHub Actions sẽ hiển thị:

```
✅ Run backend unit tests - Success
📊 Publish Test Report - 10 tests run, 9 passed, 1 failed
📋 Generate Detailed Test Report - Markdown table with details
💾 Upload surefire reports - Artifacts available
```

Click vào từng step để xem chi tiết!

## Lưu ý quan trọng

1. **Đặt tên test có ý nghĩa** để báo cáo dễ đọc
2. **Chạy test locally trước khi push** để tránh fail trên CI
3. **Xem log chi tiết** nếu test fail để debug
4. **Convention đặt tên** giúp báo cáo tự động đẹp hơn

---

🎉 **Hoàn tất!** Bây giờ mỗi lần push, bạn sẽ nhận được báo cáo chi tiết từng test case!

