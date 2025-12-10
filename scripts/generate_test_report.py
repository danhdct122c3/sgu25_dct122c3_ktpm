#!/usr/bin/env python3
"""
Test Report Generator for Integration Tests
Generates Excel reports with multiple sheets from Surefire XML reports
"""

import os
import xml.etree.ElementTree as ET
from datetime import datetime
import pandas as pd
from pathlib import Path

class TestReportGenerator:
    def __init__(self, surefire_dir="back-end/target/surefire-reports", output_dir="back-end/test-reports"):
        self.surefire_dir = Path(surefire_dir)
        self.output_dir = Path(output_dir)
        self.output_dir.mkdir(exist_ok=True)

    def parse_surefire_reports(self):
        """Parse all Surefire XML reports"""
        if not self.surefire_dir.exists():
            print(f"Surefire directory not found: {self.surefire_dir}")
            return []

        results = []
        xml_files = list(self.surefire_dir.glob("TEST-*.xml"))

        for xml_file in xml_files:
            try:
                tree = ET.parse(xml_file)
                root = tree.getroot()

                # Parse test suite info
                testsuite = root
                test_class = testsuite.get('name', 'Unknown')

                # Parse individual test cases
                for testcase in testsuite.findall('.//testcase'):
                    result = {
                        'test_class': test_class,
                        'test_method': testcase.get('name', 'Unknown'),
                        'execution_time': float(testcase.get('time', 0)),
                        'status': 'PASS',
                        'error_message': None,
                        'error_type': None,
                        'use_case': self.map_to_use_case(test_class, testcase.get('name')),
                        'timestamp': datetime.now().isoformat()
                    }

                    # Check for failures
                    failure = testcase.find('failure')
                    error = testcase.find('error')

                    if failure is not None:
                        result['status'] = 'FAIL'
                        result['error_message'] = failure.text[:500] if failure.text else 'No error message'
                        result['error_type'] = failure.get('type', 'Unknown')
                    elif error is not None:
                        result['status'] = 'ERROR'
                        result['error_message'] = error.text[:500] if error.text else 'No error message'
                        result['error_type'] = error.get('type', 'Unknown')

                    results.append(result)

            except Exception as e:
                print(f"Error parsing {xml_file}: {e}")
                continue

        return results

    def map_to_use_case(self, test_class, test_method):
        """Map test methods to use cases"""
        mapping = {
            'CartManagementIntegrationTest': {
                'testSuccessfulCartManagementAndCheckout': 'UC2-Basic',
                'testAddOutOfStockProduct': 'UC2-E1',
                'testUpdateQuantityExceedingStock': 'UC2-E1',
                'testApplyInvalidDiscountCode': 'UC2-E2'
            }
        }

        class_mapping = mapping.get(test_class, {})
        return class_mapping.get(test_method, 'Unknown')

    def calculate_metrics(self, results):
        """Calculate test metrics"""
        if not results:
            return {}

        total_tests = len(results)
        passed = len([r for r in results if r['status'] == 'PASS'])
        failed = len([r for r in results if r['status'] == 'FAIL'])
        errors = len([r for r in results if r['status'] == 'ERROR'])

        avg_time = sum(r['execution_time'] for r in results) / total_tests if total_tests > 0 else 0

        # Use case coverage
        use_cases = set(r['use_case'] for r in results if r['use_case'] != 'Unknown')
        coverage = len(use_cases)

        return {
            'Metric': ['Total Tests', 'Passed', 'Failed', 'Errors', 'Success Rate', 'Avg Time', 'UC Coverage'],
            'Value': [total_tests, passed, failed, errors, f"{(passed/total_tests*100):.1f}%" if total_tests > 0 else "0%", f"{avg_time:.2f}s", coverage],
            'Status': ['Info', '✅' if passed == total_tests else '❌', '❌' if failed > 0 else '✅', '❌' if errors > 0 else '✅', 'Info', 'Info', 'Info']
        }

    def create_traceability_matrix(self, results):
        """Create traceability matrix"""
        traceability = []

        # UC2 mappings
        uc2_mappings = {
            'UC2-Basic': ['testSuccessfulCartManagementAndCheckout'],
            'UC2-E1': ['testAddOutOfStockProduct', 'testUpdateQuantityExceedingStock'],
            'UC2-E2': ['testApplyInvalidDiscountCode']
        }

        for uc, test_methods in uc2_mappings.items():
            for test_method in test_methods:
                test_result = next((r for r in results if r['test_method'] == test_method), None)
                traceability.append({
                    'Use_Case': uc,
                    'Test_Method': test_method,
                    'Status': test_result['status'] if test_result else 'Not Found',
                    'Execution_Time': f"{test_result['execution_time']:.2f}s" if test_result else 'N/A',
                    'Coverage': '✅' if test_result and test_result['status'] == 'PASS' else '❌'
                })

        return traceability

    def generate_excel_report(self, results):
        """Generate Excel report with multiple sheets"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"integration_test_report_{timestamp}.xlsx"
        filepath = self.output_dir / filename

        with pd.ExcelWriter(filepath, engine='openpyxl') as writer:
            # Sheet 1: Test Execution Results
            if results:
                df_results = pd.DataFrame(results)
                df_results.to_excel(writer, sheet_name='Execution_Results', index=False)

                # Format the sheet
                worksheet = writer.sheets['Execution_Results']
                worksheet.column_dimensions['A'].width = 25
                worksheet.column_dimensions['B'].width = 40
                worksheet.column_dimensions['C'].width = 15
                worksheet.column_dimensions['D'].width = 10
                worksheet.column_dimensions['E'].width = 50

            # Sheet 2: Dashboard
            metrics = self.calculate_metrics(results)
            if metrics:
                df_metrics = pd.DataFrame(metrics)
                df_metrics.to_excel(writer, sheet_name='Dashboard', index=False)

                # Format dashboard
                worksheet = writer.sheets['Dashboard']
                worksheet.column_dimensions['A'].width = 20
                worksheet.column_dimensions['B'].width = 15
                worksheet.column_dimensions['C'].width = 10

            # Sheet 3: Traceability Matrix
            traceability = self.create_traceability_matrix(results)
            if traceability:
                df_traceability = pd.DataFrame(traceability)
                df_traceability.to_excel(writer, sheet_name='Traceability', index=False)

                # Format traceability
                worksheet = writer.sheets['Traceability']
                worksheet.column_dimensions['A'].width = 15
                worksheet.column_dimensions['B'].width = 35
                worksheet.column_dimensions['C'].width = 10
                worksheet.column_dimensions['D'].width = 15
                worksheet.column_dimensions['E'].width = 10

            # Sheet 4: Summary Report
            summary_data = {
                'Report_Generated': [datetime.now().strftime("%Y-%m-%d %H:%M:%S")],
                'Total_Test_Classes': [len(set(r['test_class'] for r in results))],
                'Total_Test_Methods': [len(results)],
                'Passed_Tests': [len([r for r in results if r['status'] == 'PASS'])],
                'Failed_Tests': [len([r for r in results if r['status'] == 'FAIL'])],
                'Error_Tests': [len([r for r in results if r['status'] == 'ERROR'])],
                'Success_Rate': [f"{len([r for r in results if r['status'] == 'PASS'])/len(results)*100:.1f}%" if results else "0%"],
                'Average_Execution_Time': [f"{sum(r['execution_time'] for r in results)/len(results):.2f}s" if results else "0s"],
                'Use_Case_Coverage': [len(set(r['use_case'] for r in results if r['use_case'] != 'Unknown'))],
                'CI_Status': ['✅ PASSED' if all(r['status'] == 'PASS' for r in results) else '❌ FAILED']
            }

            df_summary = pd.DataFrame(summary_data)
            df_summary.to_excel(writer, sheet_name='Summary', index=False)

            # Format summary
            worksheet = writer.sheets['Summary']
            worksheet.column_dimensions['A'].width = 25
            worksheet.column_dimensions['B'].width = 20

        print(f"Test report generated: {filepath}")
        return filepath

    def generate_markdown_report(self, results):
        """Generate Markdown summary report"""
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"test_summary_{timestamp}.md"
        filepath = self.output_dir / filename

        total_tests = len(results)
        passed = len([r for r in results if r['status'] == 'PASS'])
        failed = len([r for r in results if r['status'] == 'FAIL'])
        errors = len([r for r in results if r['status'] == 'ERROR'])

        with open(filepath, 'w', encoding='utf-8') as f:
            f.write("# Integration Test Report\n\n")
            f.write(f"**Generated:** {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n\n")

            f.write("## Test Summary\n\n")
            f.write(f"- **Total Tests:** {total_tests}\n")
            f.write(f"- **Passed:** {passed}\n")
            f.write(f"- **Failed:** {failed}\n")
            f.write(f"- **Errors:** {errors}\n")
            f.write(f"- **Success Rate:** {passed/total_tests*100:.1f}%\n\n" if total_tests > 0 else "- **Success Rate:** 0%\n\n")

            f.write("## Test Results\n\n")
            f.write("| Test Class | Test Method | Status | Time |\n")
            f.write("|------------|-------------|--------|------|\n")

            for result in results:
                status_emoji = "✅" if result['status'] == 'PASS' else "❌"
                f.write(f"| {result['test_class']} | {result['test_method']} | {status_emoji} {result['status']} | {result['execution_time']:.2f}s |\n")

            f.write("\n## CI Status\n\n")
            if failed == 0 and errors == 0:
                f.write("✅ **All tests passed!**\n\n")
                f.write("### Next Steps:\n")
                f.write("- [ ] Review detailed Excel report\n")
                f.write("- [ ] Merge PR if approved\n")
                f.write("- [ ] Update test documentation\n")
            else:
                f.write("❌ **Test failures detected!**\n\n")
                f.write("### Failed Tests:\n")
                for result in results:
                    if result['status'] != 'PASS':
                        f.write(f"- {result['test_class']}.{result['test_method']}: {result['error_message'][:100]}...\n")

        print(f"Markdown summary generated: {filepath}")
        return filepath

def main():
    print("🔄 Generating Integration Test Report...")

    generator = TestReportGenerator()

    # Parse test results
    results = generator.parse_surefire_reports()

    if not results:
        print("❌ No test results found!")
        return

    print(f"📊 Found {len(results)} test results")

    # Generate Excel report
    excel_file = generator.generate_excel_report(results)

    # Generate Markdown summary
    md_file = generator.generate_markdown_report(results)

    print("✅ Test report generation completed!")
    print(f"📁 Excel Report: {excel_file}")
    print(f"📄 Markdown Summary: {md_file}")

    # Check overall status
    failed_tests = [r for r in results if r['status'] != 'PASS']
    if failed_tests:
        print(f"❌ {len(failed_tests)} tests failed")
        for test in failed_tests:
            print(f"   - {test['test_class']}.{test['test_method']}: {test['status']}")
        exit(1)
    else:
        print("✅ All tests passed!")
        exit(0)

if __name__ == "__main__":
    main()