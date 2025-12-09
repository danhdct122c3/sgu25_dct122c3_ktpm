#!/usr/bin/env python3
"""
Script để tạo báo cáo chi tiết từ kết quả JUnit test
Sử dụng: python generate_test_report.py [report_directory]
"""

import xml.etree.ElementTree as ET
import glob
import os
import sys
from datetime import datetime

def parse_test_name(name):
    """
    Parse test name theo convention: methodName_scenario_shouldExpectedResult
    Example: addToCart_userNotFound_shouldThrowUserNotExisted
    """
    parts = name.split('_')

    if len(parts) >= 3:
        method = parts[0]
        scenario = '_'.join(parts[1:-1])
        expected = parts[-1]

        # Tạo mô tả từ scenario
        desc = scenario.replace('_', ' ').capitalize()

        # Tạo dữ liệu nhập từ scenario
        data_input = scenario

        # Tạo kết quả mong đợi từ expected
        expected_result = expected.replace('should', '').replace('Throw', 'Ném exception ').replace('Return', 'Trả về ')
        expected_result = expected_result.replace('_', ' ')

    elif len(parts) == 2:
        method = parts[0]
        scenario = parts[1]
        desc = scenario.replace('_', ' ').capitalize()
        data_input = scenario
        expected_result = "Xem mô tả test"
    else:
        method = name
        desc = name.replace('_', ' ').replace('test', '').strip().capitalize()
        data_input = "N/A"
        expected_result = "Xem mô tả test"

    return {
        'method': method,
        'description': desc,
        'data_input': data_input,
        'expected_result': expected_result
    }

def generate_report(report_dir):
    """Tạo báo cáo Markdown từ XML test reports"""

    if not os.path.exists(report_dir):
        print(f" Không tìm thấy thư mục: {report_dir}")
        return

    xml_files = glob.glob(f"{report_dir}/TEST-*.xml")

    if not xml_files:
        print(f" Không tìm thấy file XML test report trong: {report_dir}")
        return

    print(f"\n{'='*80}")
    print(f" BÁO CÁO KẾT QUẢ TEST")
    print(f"{'='*80}")
    print(f"Thời gian: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"Số file XML: {len(xml_files)}")
    print(f"{'='*80}\n")

    # Thống kê tổng
    total_tests = 0
    total_passed = 0
    total_failed = 0
    total_errors = 0
    total_skipped = 0

    # Danh sách test cases
    test_results = []

    for xml_file in sorted(xml_files):
        try:
            tree = ET.parse(xml_file)
            root = tree.getroot()

            suite_name = root.get('name', '')

            for testcase in root.findall('testcase'):
                total_tests += 1

                classname = testcase.get('classname', '')
                name = testcase.get('name', '')
                time = float(testcase.get('time', '0'))

                # Parse test name
                parsed = parse_test_name(name)

                # Check status
                failure = testcase.find('failure')
                error = testcase.find('error')
                skipped = testcase.find('skipped')

                if skipped is not None:
                    status = " SKIPPED"
                    total_skipped += 1
                    result_msg = "Test bị bỏ qua"
                    error_detail = skipped.get('message', '')
                elif failure is not None:
                    status = "FAILED"
                    total_failed += 1
                    error_msg = failure.get('message', '')
                    error_type = failure.get('type', '')
                    result_msg = f"Exception: {error_type}"
                    error_detail = error_msg
                elif error is not None:
                    status = "️ ERROR"
                    total_errors += 1
                    error_msg = error.get('message', '')
                    error_type = error.get('type', '')
                    result_msg = f"Error: {error_type}"
                    error_detail = error_msg
                else:
                    status = "PASS"
                    total_passed += 1
                    result_msg = "Test passed successfully"
                    error_detail = ""

                test_results.append({
                    'class': classname.split('.')[-1],
                    'name': name,
                    'description': parsed['description'],
                    'data_input': parsed['data_input'],
                    'expected': parsed['expected_result'],
                    'result': result_msg,
                    'status': status,
                    'time': time,
                    'error_detail': error_detail
                })

        except Exception as e:
            print(f"Lỗi khi parse {xml_file}: {e}")

    # In bảng kết quả
    print("\n##  CHI TIẾT KẾT QUẢ TEST\n")
    print("| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/Pass |")
    print("|--------------|-------|--------------|------------------|--------------|-------------|")

    for test in test_results:
        # Escape pipe characters in data
        name = test['name'].replace('|', '\\|')
        desc = test['description'].replace('|', '\\|')[:50]
        data_input = test['data_input'].replace('|', '\\|')[:30]
        expected = test['expected'].replace('|', '\\|')[:40]
        result = test['result'].replace('|', '\\|')[:50]

        print(f"| `{name}` | {desc} | `{data_input}` | {expected} | {result} | {test['status']} |")

    # In thống kê
    print(f"\n{'='*80}")
    print("##  TỔNG KẾT")
    print(f"{'='*80}")
    print(f" Tổng số test:        {total_tests}")
    print(f"Passed:              {total_passed}")
    print(f" Failed:              {total_failed}")
    print(f" Errors:              {total_errors}")
    print(f"  Skipped:             {total_skipped}")

    if total_tests > 0:
        success_rate = (total_passed / total_tests) * 100
        print(f" Tỷ lệ thành công:    {success_rate:.2f}%")

    print(f"{'='*80}\n")

    # In chi tiết các test failed
    if total_failed > 0 or total_errors > 0:
        print("\n## CHI TIẾT CÁC TEST FAILED/ERROR\n")
        for test in test_results:
            if test['status'] in [' FAILED', ' ERROR']:
                print(f"### {test['status']} {test['name']}")
                print(f"- **Class:** {test['class']}")
                print(f"- **Mô tả:** {test['description']}")
                print(f"- **Dữ liệu nhập:** {test['data_input']}")
                print(f"- **Kết quả mong đợi:** {test['expected']}")
                print(f"- **Lỗi:**")
                print(f"```")
                print(test['error_detail'][:500])
                print(f"```")
                print()

if __name__ == "__main__":
    # Lấy report directory từ argument hoặc dùng default
    if len(sys.argv) > 1:
        report_dir = sys.argv[1]
    else:
        report_dir = "back-end/target/surefire-reports"

    generate_report(report_dir)

