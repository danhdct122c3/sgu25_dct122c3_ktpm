# Test Automation Scripts

This directory contains scripts for automated test reporting and CI/CD integration.

## 📋 Files Overview

| File | Purpose | Language |
|------|---------|----------|
| `generate_test_report.py` | Parse Surefire XML and generate Excel reports | Python |
| `requirements.txt` | Python dependencies | - |
| `README.md` | This documentation | Markdown |

## 🚀 Quick Start

### 1. Install Dependencies
```bash
pip install -r scripts/requirements.txt
```

### 2. Run Tests
```bash
cd back-end
mvnw.cmd test -Dtest=*IntegrationTest -Dspring.profiles.active=test
```

### 3. Generate Report
```bash
python scripts/generate_test_report.py
```

## 📊 Generated Reports

### Excel Report (`integration_test_report_YYYYMMDD_HHMMSS.xlsx`)

#### Sheet 1: Execution_Results
| Column | Description |
|--------|-------------|
| test_class | Java test class name |
| test_method | Individual test method |
| execution_time | Time in seconds |
| status | PASS/FAIL/ERROR |
| error_message | Failure details (if any) |
| use_case | Mapped use case (UC2-Basic, UC2-E1, etc.) |

#### Sheet 2: Dashboard
| Metric | Description |
|--------|-------------|
| Total Tests | Total number of tests run |
| Passed | Number of passed tests |
| Failed | Number of failed tests |
| Errors | Number of error tests |
| Success Rate | Percentage of passed tests |
| Avg Time | Average execution time |
| UC Coverage | Number of use cases covered |

#### Sheet 3: Traceability
| Column | Description |
|--------|-------------|
| Use_Case | Use case identifier (UC2-Basic, UC2-E1, etc.) |
| Test_Method | Corresponding test method |
| Status | Test execution status |
| Execution_Time | Time taken |
| Coverage | ✅ Covered / ❌ Not covered |

#### Sheet 4: Summary
High-level summary with CI status and key metrics.

### Markdown Report (`test_summary_YYYYMMDD_HHMMSS.md`)
Human-readable summary for quick review.

## 🔧 CI/CD Integration

### GitHub Actions Workflow
The `.github/workflows/integration-tests.yml` provides:

#### ✅ On Success:
- Generates Excel and Markdown reports
- Uploads reports as artifacts
- Comments on PR with test results
- Publishes test results dashboard

#### ❌ On Failure:
- Creates GitHub issue with failure details
- Includes error logs and recent commits
- Assigns appropriate labels (`bug`, `high-priority`)
- Prevents merging until tests pass

### Jenkins Integration
```groovy
pipeline {
    stages {
        stage('Run Tests') {
            steps {
                sh 'mvnw.cmd test -Dtest=*IntegrationTest'
            }
        }
        stage('Generate Report') {
            steps {
                sh 'python scripts/generate_test_report.py'
            }
        }
        stage('Archive Reports') {
            steps {
                archiveArtifacts artifacts: 'back-end/test-reports/*.xlsx'
            }
        }
    }
    post {
        always {
            publishTestNG testResultsPattern: 'back-end/target/surefire-reports/*.xml'
        }
        failure {
            script {
                // Create Jira issue or send notifications
            }
        }
    }
}
```

## 🎯 Use Case Mapping

The script automatically maps test methods to use cases:

```python
mapping = {
    'CartManagementIntegrationTest': {
        'testSuccessfulCartManagementAndCheckout': 'UC2-Basic',
        'testAddOutOfStockProduct': 'UC2-E1',
        'testUpdateQuantityExceedingStock': 'UC2-E1',
        'testApplyInvalidDiscountCode': 'UC2-E2'
    }
}
```

## 📈 Customization

### Adding New Test Classes
1. Update the `map_to_use_case()` method in `generate_test_report.py`
2. Add new mappings for your test classes

### Custom Metrics
Modify `calculate_metrics()` to add custom KPIs.

### Report Formats
Extend the script to generate:
- PDF reports
- HTML dashboards
- JSON APIs
- Slack/Teams notifications

## 🐛 Troubleshooting

### Common Issues

#### 1. "No test results found"
- Ensure tests ran successfully
- Check `back-end/target/surefire-reports/` exists
- Verify XML files are generated

#### 2. "pandas not found"
```bash
pip install pandas openpyxl lxml
```

#### 3. Permission errors
- Ensure write permissions to `back-end/test-reports/`
- Check file system permissions

#### 4. Encoding issues
- Use UTF-8 encoding for all files
- Handle special characters in error messages

### Debug Mode
```bash
python scripts/generate_test_report.py --debug
```

## 📞 Support

For issues with the test automation:
1. Check the generated log files
2. Review GitHub Actions workflow logs
3. Verify test execution in local environment
4. Check dependency versions

## 🎉 Success Metrics

### Target Achievements
- ✅ **100% Test Automation** - No manual intervention required
- ✅ **Instant Feedback** - Results within minutes of commit
- ✅ **Full Traceability** - UC → Test → Results mapping
- ✅ **Professional Reports** - Excel dashboards for stakeholders
- ✅ **CI/CD Integration** - Automated quality gates

### Quality Indicators
- **Test Execution Time** < 5 minutes
- **Report Generation** < 30 seconds
- **Success Rate** > 95%
- **False Positives** = 0

---

**This automation system ensures quality code reaches production with comprehensive testing and reporting!** 🚀