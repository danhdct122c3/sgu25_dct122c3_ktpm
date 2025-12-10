#!/usr/bin/env python3
"""
Script để tạo báo cáo chi tiết từ kết quả JUnit test
Sử dụng: python generate_test_report.py [report_directory]

Convention đặt tên test để có báo cáo đẹp:
1. Cách 1 (Khuyên dùng): Sử dụng @DisplayName
   @DisplayName("Thêm vào giỏ | user='testuser', variantId='v001', qty=2 | Nên lưu CartItem mới")
   @Test
   void addToCart_newItem_shouldSaveCartItem() { ... }

2. Cách 2: Convention tên test chi tiết
   methodName_inputData_scenario_shouldExpectedResult
   Ví dụ: addToCart_userTestVariantV001Qty2_newItem_shouldSaveCartItem
"""

import xml.etree.ElementTree as ET
import glob
import os
import sys
from datetime import datetime
import re

def parse_test_name(name):
    """
    Parse test name theo convention: methodName_scenario_shouldExpectedResult
    Cố gắng extract dữ liệu nhập thực từ tên test
    """
    parts = name.split('_')

    # Pattern 1: methodName_inputData_scenario_shouldExpectedResult
    # Ví dụ: addToCart_userTestVariantV001Qty2_newItem_shouldSaveCartItem
    if len(parts) >= 4:
        method = parts[0]
        # Phần thứ 2 có thể là input data
        potential_input = parts[1]
        scenario = '_'.join(parts[2:-1])
        expected = parts[-1]

        # Tạo mô tả từ scenario
        desc = scenario.replace('_', ' ').capitalize()

        # Parse dữ liệu nhập từ potential_input
        data_input = parse_input_data(potential_input)

        # Tạo kết quả mong đợi từ expected
        expected_result = format_expected_result(expected)

    elif len(parts) >= 3:
        method = parts[0]
        scenario = '_'.join(parts[1:-1])
        expected = parts[-1]

        # Tạo mô tả từ scenario
        desc = scenario.replace('_', ' ').capitalize()

        # Cố gắng parse input từ scenario
        data_input = parse_input_from_scenario(scenario)

        # Tạo kết quả mong đợi từ expected
        expected_result = format_expected_result(expected)

    elif len(parts) == 2:
        method = parts[0]
        scenario = parts[1]
        desc = scenario.replace('_', ' ').capitalize()
        data_input = parse_input_from_scenario(scenario)
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

def parse_input_data(input_str):
    """
    Parse input data từ string
    Ví dụ: 'userTestVariantV001Qty2' -> 'user=test, variant=v001, qty=2'
    """
    # Tìm các pattern như: userXxx, variantXxx, qtyXxx, idXxx
    patterns = {
        r'user([A-Z][a-zA-Z0-9]+)': 'user',
        r'variant([A-Z][a-zA-Z0-9]+)': 'variant',
        r'qty(\d+)': 'qty',
        r'quantity(\d+)': 'quantity',
        r'id([A-Z0-9]+)': 'id',
        r'token([A-Z][a-zA-Z0-9]+)': 'token',
        r'password([A-Z][a-zA-Z0-9]+)': 'password',
    }

    results = []
    for pattern, name in patterns.items():
        matches = re.finditer(pattern, input_str, re.IGNORECASE)
        for match in matches:
            value = match.group(1).lower()
            results.append(f"{name}='{value}'")

    if results:
        return ', '.join(results)
    else:
        # Fallback: chỉ format lại string
        return format_camel_case(input_str)

def parse_input_from_scenario(scenario):
    """
    Parse dữ liệu nhập từ scenario name
    Ví dụ: 'userNotFound' -> "user='notFound'"
           'validCredentials' -> "credentials='valid'"
    """
    # Các pattern thông dụng
    if 'notfound' in scenario.lower() or 'notexist' in scenario.lower():
        entity = scenario.lower().replace('notfound', '').replace('notexist', '').replace('not', '')
        return f"{entity}='không tồn tại'"
    elif 'invalid' in scenario.lower():
        entity = scenario.lower().replace('invalid', '')
        return f"{entity}='invalid'"
    elif 'valid' in scenario.lower():
        entity = scenario.lower().replace('valid', '')
        return f"{entity}='valid'" if entity else "input='valid'"
    elif 'exist' in scenario.lower():
        entity = scenario.lower().replace('existing', '').replace('exist', '')
        return f"{entity}='đã tồn tại'"
    elif 'exceed' in scenario.lower():
        return "quantity='vượt quá stock'"
    elif 'new' in scenario.lower():
        return "item='mới'"
    else:
        return format_camel_case(scenario)

def format_camel_case(text):
    """Chuyển camelCase thành readable format"""
    # Insert space before uppercase letters
    result = re.sub(r'([A-Z])', r' \1', text)
    return result.strip().lower()

def format_expected_result(expected):
    """Format expected result từ test name"""
    result = expected.replace('should', '').replace('Should', '')
    result = result.replace('Throw', 'Ném exception ')
    result = result.replace('Return', 'Trả về ')
    result = result.replace('Save', 'Lưu ')
    result = result.replace('Update', 'Cập nhật ')
    result = result.replace('Delete', 'Xóa ')
    result = result.replace('Success', 'thành công')
    result = result.replace('_', ' ')
    return result.strip()

def generate_report(report_dir):
    """Tạo báo cáo Markdown từ XML test reports"""

    if not os.path.exists(report_dir):
        print(f"❌ Không tìm thấy thư mục: {report_dir}")
        return

    xml_files = glob.glob(f"{report_dir}/TEST-*.xml")

    if not xml_files:
        print(f"❌ Không tìm thấy file XML test report trong: {report_dir}")
        return

    print(f"\n{'='*100}")
    print(f"📊 BÁO CÁO KẾT QUẢ TEST")
    print(f"{'='*100}")
    print(f"Thời gian: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"Số file XML: {len(xml_files)}")
    print(f"{'='*100}\n")

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

            for testcase in root.findall('testcase'):
                total_tests += 1

                classname = testcase.get('classname', '')
                name = testcase.get('name', '')
                time = float(testcase.get('time', '0'))

                # Check status
                failure = testcase.find('failure')
                error = testcase.find('error')
                skipped = testcase.find('skipped')

                if skipped is not None:
                    status = "⏭️ SKIPPED"
                    total_skipped += 1
                    result_msg = "Test bị bỏ qua"
                    error_detail = skipped.get('message', '')
                elif failure is not None:
                    status = "❌ FAILED"
                    total_failed += 1
                    error_msg = failure.get('message', '')
                    error_type = failure.get('type', '')
                    result_msg = f"Exception: {error_type}"
                    error_detail = error_msg
                elif error is not None:
                    status = "⚠️ ERROR"
                    total_errors += 1
                    error_msg = error.get('message', '')
                    error_type = error.get('type', '')
                    result_msg = f"Error: {error_type}"
                    error_detail = error_msg
                else:
                    status = "✅ PASS"
                    total_passed += 1
                    result_msg = "Đạt yêu cầu"
                    error_detail = ""

                # Parse @DisplayName format: "Mô tả | dữ liệu nhập | kết quả mong đợi"
                # hoặc "methodName | Mô tả | dữ liệu nhập | kết quả mong đợi"
                method_name = name  # Default to full name

                if ' | ' in name:
                    parts = name.split(' | ')

                    # Check if first part looks like a method name (contains underscore and no spaces)
                    if '_' in parts[0] and ' ' not in parts[0]:
                        # Format: "methodName | description | input | expected"
                        method_name = parts[0].strip()
                        if len(parts) == 4:
                            description = parts[1].strip()
                            data_input = parts[2].strip()
                            expected = parts[3].strip()
                        elif len(parts) == 3:
                            description = parts[1].strip()
                            data_input = parts[2].strip()
                            expected = "Xem test"
                        elif len(parts) == 2:
                            description = parts[1].strip()
                            data_input = "Xem test"
                            expected = "Xem test"
                        else:
                            description = parts[1] if len(parts) > 1 else name
                            data_input = "Xem test"
                            expected = "Xem test"
                    else:
                        # Format: "description | input | expected" (no method name)
                        if len(parts) == 3:
                            description = parts[0].strip()
                            data_input = parts[1].strip()
                            expected = parts[2].strip()
                        elif len(parts) == 2:
                            description = parts[0].strip()
                            data_input = "Xem test"
                            expected = parts[1].strip()
                        else:
                            description = name
                            data_input = "Xem test"
                            expected = "Xem test"
                else:
                    # Fallback: parse từ tên method cũ
                    parts = name.split('_')
                    if len(parts) >= 3:
                        description = '_'.join(parts[1:-1]).replace('_', ' ').capitalize()
                        data_input = parts[1].replace('_', ' ')
                        expected = parts[-1].replace('should', '').replace('_', ' ')
                    else:
                        description = name.replace('_', ' ').capitalize()
                        data_input = "N/A"
                        expected = "Xem test"

                test_results.append({
                    'class': classname.split('.')[-1],
                    'name': method_name,
                    'description': description,
                    'data_input': data_input,
                    'expected': expected,
                    'result': result_msg,
                    'status': status,
                    'time': time,
                    'error_detail': error_detail
                })

        except Exception as e:
            print(f"⚠️ Lỗi khi parse {xml_file}: {e}")

    # In bảng kết quả
    print("\n## 📋 CHI TIẾT KẾT QUẢ TEST\n")
    print("| Tên hàm test | Mô tả | Dữ liệu nhập | Kết quả mong đợi | Kết quả chạy | Failed/Pass |")
    print("|--------------|-------|--------------|------------------|--------------|-------------|")

    for test in test_results:
        # Escape pipe characters and limit length
        name = test['name'][:80].replace('|', '\\|')
        desc = test['description'][:50].replace('|', '\\|')
        data_input = test['data_input'][:60].replace('|', '\\|')
        expected = test['expected'][:60].replace('|', '\\|')
        result = test['result'][:50].replace('|', '\\|')

        print(f"| `{name}` | {desc} | {data_input} | {expected} | {result} | {test['status']} |")

    # In thống kê
    print(f"\n{'='*100}")
    print("## 📈 TỔNG KẾT")
    print(f"{'='*100}")
    print(f"📊 Tổng số test:        {total_tests}")
    print(f"✅ Passed:              {total_passed}")
    print(f"❌ Failed:              {total_failed}")
    print(f"⚠️  Errors:              {total_errors}")
    print(f"⏭️  Skipped:             {total_skipped}")

    if total_tests > 0:
        success_rate = (total_passed / total_tests) * 100
        print(f"📊 Tỷ lệ thành công:    {success_rate:.2f}%")

    print(f"{'='*100}\n")

    # In chi tiết các test failed
    if total_failed > 0 or total_errors > 0:
        print("\n## ❌ CHI TIẾT CÁC TEST FAILED/ERROR\n")
        for test in test_results:
            if test['status'] in ['❌ FAILED', '⚠️ ERROR']:
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

    # Hướng dẫn
    print("\n" + "="*100)
    print("💡 LƯU Ý: Format @DisplayName để có báo cáo đẹp:")
    print("="*100)
    print('@DisplayName("Mô tả ngắn gọn | dữ liệu nhập chi tiết | kết quả mong đợi")')
    print("VD: @DisplayName(\"Thêm item mới | username='test', variantId='v001', qty=2 | Lưu CartItem thành công\")")
    print("="*100 + "\n")

if __name__ == "__main__":
    # Lấy report directory từ argument hoặc dùng default
    if len(sys.argv) > 1:
        report_dir = sys.argv[1]
    else:
        report_dir = "back-end/target/surefire-reports"

    generate_report(report_dir)
