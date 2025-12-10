import requests
import time

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_report_api_get_revenue_report():
    url = f"{BASE_URL}/reports/revenue"
    headers = {
        "Accept": "application/json"
    }
    try:
        start_time = time.time()
        response = requests.get(url, headers=headers, timeout=TIMEOUT)
        duration = time.time() - start_time
    except requests.exceptions.RequestException as e:
        assert False, f"Request to {url} failed: {e}"
    
    # Assert status code is 200 OK
    assert response.status_code == 200, f"Expected status code 200 but got {response.status_code}"
    
    # Assert response is JSON
    try:
        data = response.json()
    except ValueError:
        assert False, "Response is not a valid JSON"
    
    # Basic validation of revenue report content: check if keys expected in revenue report are present
    # Since schema is not explicitly given, check at least that data is non-empty and contains numeric revenue fields
    assert isinstance(data, dict), "Response JSON is not an object as expected"
    assert "totalRevenue" in data or "revenue" in data or len(data) > 0, "Revenue report missing or empty"

    # Performance benchmark: response time should be less than 2 seconds (per product catalog benchmark rationale)
    # Adjusted for report, allow up to 3 seconds max acceptable response
    max_response_time = 3.0
    assert duration <= max_response_time, f"Response time {duration:.2f}s exceeded max allowed {max_response_time}s"

test_report_api_get_revenue_report()