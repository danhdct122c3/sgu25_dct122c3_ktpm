# Test Report Automation Template - Multi-Sheet Excel System

## Tổng quan hệ thống báo cáo

### 🎯 **Mục tiêu:**
Tạo hệ thống báo cáo test tự động với multiple Excel sheets liên kết, mapping đầy đủ từ Use Case → Test Cases → Test Results → Reports.

### 📊 **Cấu trúc Multi-Sheet:**
1. **Use Cases** - Danh sách tất cả UC
2. **Test Cases** - Chi tiết test cases
3. **Test Execution** - Kết quả chạy test
4. **Test Reports** - Báo cáo tổng hợp
5. **Traceability Matrix** - Mapping UC ↔ Test
6. **Defect Reports** - Lỗi phát hiện

---

## Cách 1: AUTOMATED TEST REPORTING (Khuyến nghị)

### 🚀 **Maven Surefire Reports + Excel Export**

#### **1. Cấu hình Maven Surefire Plugin**
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
    <configuration>
        <testFailureIgnore>false</testFailureIgnore>
        <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory>
        <includes>
            <include>**/*IntegrationTest.java</include>
        </includes>
    </configuration>
</plugin>
```

#### **2. Chạy test và generate reports**
```bash
# Chạy test và tạo XML reports
mvnw.cmd test -Dtest=*IntegrationTest

# Chạy với custom reports
mvnw.cmd surefire-report:report
```

#### **3. Convert XML → Excel tự động**
```bash
# Sử dụng script Python để convert
python convert_test_reports.py
```

### 📋 **Sheet 1: Test Execution Results (Auto-generated)**

| Test Class | Test Method | Status | Time | Error | Use Case |
|------------|-------------|--------|------|-------|----------|
| CartManagementIntegrationTest | testSuccessfulCartManagementAndCheckout | ✅ PASS | 3.2s | - | UC2 |
| CartManagementIntegrationTest | testAddOutOfStockProduct | ✅ PASS | 2.8s | - | UC2-E1 |
| CartManagementIntegrationTest | testUpdateQuantityExceedingStock | ✅ PASS | 3.1s | - | UC2-E1 |
| CartManagementIntegrationTest | testApplyInvalidDiscountCode | ✅ PASS | 2.9s | - | UC2-E2 |

---

## Cách 2: HYBRID APPROACH (Manual + Auto)

### 📝 **Sheet 2: Test Cases Detail (Template)**

| ID | Title | UC Mapping | Steps | Expected | Status | Actual | Notes |
|----|-------|------------|-------|----------|--------|--------|-------|
| TC_INT_CM_001 | Successful Cart Management | UC2-1→13 | 1. Setup data<br>2. POST /cart/add<br>3. GET /cart<br>4. PUT /cart/update<br>5. POST /apply-discount<br>6. GET /cart final | HTTP 200<br>Correct totals<br>Discount applied | ✅ PASS | HTTP 200<br>Total: 270.0<br>Discount: 30.0 | Auto-updated |

### 🔄 **Workflow Automation:**

#### **Step 1: Run Tests**
```bash
mvnw.cmd test -Dtest=CartManagementIntegrationTest -Dspring.profiles.active=test
```

#### **Step 2: Parse Results (Automated Script)**
```python
# parse_test_results.py
import xml.etree.ElementTree as ET
import pandas as pd

def parse_surefire_report():
    tree = ET.parse('target/surefire-reports/TEST-CartManagementIntegrationTest.xml')
    root = tree.getroot()

    results = []
    for testcase in root.findall('.//testcase'):
        result = {
            'class': root.find('.//testsuite').get('name'),
            'method': testcase.get('name'),
            'time': testcase.get('time'),
            'status': 'PASS'
        }

        # Check for failures
        failure = testcase.find('failure')
        if failure is not None:
            result['status'] = 'FAIL'
            result['error'] = failure.text

        results.append(result)

    # Export to Excel
    df = pd.DataFrame(results)
    df.to_excel('test_results.xlsx', sheet_name='Execution_Results', index=False)

    return results

if __name__ == "__main__":
    parse_surefire_report()
```

#### **Step 3: Update Excel với VLOOKUP**
```excel
=VLOOKUP(A2, Execution_Results!A:D, 4, FALSE)  # Status
=VLOOKUP(A2, Execution_Results!A:E, 5, FALSE)  # Error
```

---

## Cách 3: FULLY AUTOMATED DASHBOARD

### 📊 **Sheet 3: Test Dashboard (Auto-calculated)**

| Metric | Value | Target | Status |
|--------|-------|--------|--------|
| Total Test Cases | 4 | - | ✅ |
| Passed | 4 | 100% | ✅ |
| Failed | 0 | 0% | ✅ |
| Coverage % | 100% | 100% | ✅ |
| Avg Execution Time | 3.0s | <5s | ✅ |
| Use Case Coverage | 13/13 | 100% | ✅ |

### 📈 **Sheet 4: Trend Analysis**

| Date | Run # | Total | Passed | Failed | Coverage | Avg Time |
|------|-------|-------|--------|--------|----------|----------|
| 2025-12-10 | 1 | 4 | 4 | 0 | 100% | 3.0s |
| 2025-12-11 | 2 | 4 | 4 | 0 | 100% | 2.8s |

---

## Cách 4: JENKINS CI/CD INTEGRATION

### 🔧 **Jenkins Pipeline Configuration**

```groovy
// Jenkinsfile
pipeline {
    agent any

    stages {
        stage('Run Integration Tests') {
            steps {
                sh 'mvnw.cmd test -Dtest=*IntegrationTest -Dspring.profiles.active=test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishTestNGResults 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Generate Test Report') {
            steps {
                sh 'python3 scripts/generate_test_report.py'
            }
        }

        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'test-reports/*.xlsx', fingerprint: true
            }
        }
    }

    post {
        always {
            emailext attachmentsPattern: 'test-reports/*.xlsx',
                     body: 'Test results attached',
                     subject: 'Integration Test Results',
                     to: 'qa-team@company.com'
        }
    }
}
```

---

## 🎯 **RECOMMENDED APPROACH: Hybrid Auto + Manual**

### **Workflow đề xuất:**

#### **1. Automated Execution**
```bash
# Chạy tất cả integration tests
mvnw.cmd test -Dtest=*IntegrationTest -Dspring.profiles.active=test

# Tự động parse results
python scripts/parse_results.py > test_execution.log
```

#### **2. Excel Template với Auto-update**

**Sheet 1: Test Cases (Manual setup)**
| ID | Title | Steps | Expected | Status | Actual |
|----|-------|-------|----------|--------|--------|
| TC_INT_CM_001 | Cart Management Success | 6 steps | HTTP 200 | =VLOOKUP() | =VLOOKUP() |

**Sheet 2: Execution Results (Auto-import)**
- Import từ `target/surefire-reports/*.xml`
- Hoặc từ CSV output của script Python

**Sheet 3: Dashboard (Auto-calculate)**
```excel
=COUNTIF(Sheet1!E:E, "PASS")  # Passed count
=COUNTIF(Sheet1!E:E, "FAIL")  # Failed count
=AVERAGE(Sheet2!C:C)          # Avg execution time
```

#### **3. Mapping Relationships**

**Use Case → Test Cases**
```
UC2-1  → TC_INT_CM_001-Step1
UC2-2  → TC_INT_CM_001-Step2
UC2-E1 → TC_INT_CM_002, TC_INT_CM_003
UC2-E2 → TC_INT_CM_004
```

**Test Cases → Execution Results**
```
TC_INT_CM_001 → testSuccessfulCartManagementAndCheckout
TC_INT_CM_002 → testAddOutOfStockProduct
```

---

## 📋 **IMPLEMENTATION STEPS**

### **Step 1: Setup Automation Scripts**
```bash
# Tạo thư mục scripts
mkdir scripts

# Tạo Python script để parse results
touch scripts/parse_test_results.py
```

### **Step 2: Configure Excel Template**
1. **Sheet 1:** Test Cases (manual entry)
2. **Sheet 2:** Execution Results (auto-import)
3. **Sheet 3:** Dashboard (formulas)
4. **Sheet 4:** Traceability Matrix (VLOOKUP)

### **Step 3: CI/CD Integration**
```yaml
# .github/workflows/integration-tests.yml
name: Integration Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Run tests
        run: mvnw.cmd test -Dtest=*IntegrationTest
      - name: Generate report
        run: python scripts/generate_report.py
      - name: Upload report
        uses: actions/upload-artifact@v3
        with:
          name: test-report
          path: test-reports/
```

---

## 🎉 **KẾT LUẬN**

### **Cách tốt nhất: HYBRID APPROACH**

1. **Automated Test Execution** ✅
2. **Semi-automated Report Generation** ✅  
3. **Manual QA Review & Updates** ✅
4. **Excel Inter-sheet Linking** ✅

### **Benefits:**
- ✅ **Minimal Manual Work** - Auto-update results
- ✅ **Full Traceability** - UC → Test → Results
- ✅ **Professional Reports** - Excel dashboard
- ✅ **CI/CD Ready** - Jenkins/GitHub Actions
- ✅ **Team Collaboration** - Shared Excel sheets

**Bạn muốn tôi tạo script Python để auto-generate reports không?** 🚀</result>