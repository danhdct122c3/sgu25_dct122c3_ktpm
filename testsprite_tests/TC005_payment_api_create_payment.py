import requests

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_payment_api_create_payment():
    url = f"{BASE_URL}/payment"
    headers = {
        "Content-Type": "application/json"
    }
    # Example payload based on typical payment creation data for VNPay integration
    payload = {
        "orderId": "test_order_123",
        "amount": 100000,  # Amount in smallest currency unit (e.g. cents or equivalent)
        "orderInfo": "Test payment creation via VNPay",
        "returnUrl": "http://localhost:8080/payment-return",
        "ipAddr": "127.0.0.1",
        "bankCode": "NCB"  # Example bank code used in VNPay integrations
    }

    try:
        response = requests.post(url, json=payload, headers=headers, timeout=TIMEOUT)
    except requests.RequestException as e:
        assert False, f"Request to create payment failed: {e}"

    # Validate response status code
    assert response.status_code == 200, f"Expected status code 200, got {response.status_code}"

    try:
        data = response.json()
    except ValueError:
        assert False, "Response is not valid JSON"

    # Validate response contains paymentUrl or payment token for VNPay
    # Common keys might be 'paymentUrl', 'payUrl', 'vnpayUrl', 'token', or similar
    payment_url_keys = ["paymentUrl", "payUrl", "vnpayUrl", "token", "paymentToken"]
    has_payment_url = any(key in data and isinstance(data[key], str) and data[key].startswith("http") for key in payment_url_keys)
    has_token = any(key in data and isinstance(data[key], str) and len(data[key]) > 0 for key in payment_url_keys)

    assert has_payment_url or has_token, "Response JSON does not contain a valid payment URL or token"

test_payment_api_create_payment()
