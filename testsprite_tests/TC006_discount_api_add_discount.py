import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_discount_api_add_discount():
    url = f"{BASE_URL}/discounts"
    headers = {"Content-Type": "application/json"}

    # Prepare a unique discount code to avoid conflicts
    discount_code = f"TESTCODE-{uuid.uuid4().hex[:8]}"
    payload = {
        "code": discount_code,
        "description": "Test discount code created by automated test",
        "percentage": 15,
        "active": True,
        "max_uses": 100,
        "used": 0,
        "valid_from": "2024-01-01T00:00:00Z",
        "valid_to": "2025-01-01T00:00:00Z"
    }

    discount_id = None
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=TIMEOUT)
        # Assert we got a success response code indicating creation
        assert response.status_code == 201 or response.status_code == 200, f"Unexpected status code: {response.status_code}"
        response_data = response.json()
        # Verify response contains expected fields and a valid discount ID
        assert "id" in response_data, "Response missing discount id"
        discount_id = response_data["id"]
        assert response_data.get("code") == discount_code, "Returned discount code mismatch"
        assert response_data.get("percentage") == 15, "Returned discount percentage mismatch"
        assert response_data.get("active") is True, "Returned discount active status mismatch"
        # Optionally verify dates and usage fields if returned
    finally:
        # Cleanup: delete the created discount if created
        if discount_id:
            delete_url = f"{BASE_URL}/discounts/{discount_id}"
            try:
                del_resp = requests.delete(delete_url, headers=headers, timeout=TIMEOUT)
                # Deletion may return 204 No Content or 200 OK on success
                assert del_resp.status_code in (200, 204), f"Failed to delete discount during cleanup, status code: {del_resp.status_code}"
            except Exception:
                # Ignore errors during cleanup
                pass

test_discount_api_add_discount()