import requests
import uuid

BASE_URL = "http://localhost:8080"
REGISTER_ENDPOINT = "/users/register"
HEADERS = {"Content-Type": "application/json"}
TIMEOUT = 30

def test_user_management_api_register_user():
    unique_suffix = str(uuid.uuid4())[:8]
    new_user_payload = {
        "username": f"testuser_{unique_suffix}",
        "password": "TestPassword123!",
        "email": f"testuser_{unique_suffix}@example.com",
        "fullName": "Test User"
    }

    response = None
    try:
        response = requests.post(
            f"{BASE_URL}{REGISTER_ENDPOINT}",
            json=new_user_payload,
            headers=HEADERS,
            timeout=TIMEOUT
        )
        # Validate HTTP response status code for successful registration
        assert response.status_code == 201 or response.status_code == 200, f"Unexpected status code: {response.status_code}"

        # Validate response content (assuming JSON with at least user ID or confirmation message)
        resp_json = response.json()
        # The response must contain a user identifier or confirmation attribute
        assert "id" in resp_json or "userId" in resp_json or "message" in resp_json, "Response missing confirmation attributes"
        if "id" in resp_json:
            user_id = resp_json["id"]
        elif "userId" in resp_json:
            user_id = resp_json["userId"]
        else:
            user_id = None
        # Optional: if user_id is present, it should be non-empty string or positive int
        if user_id is not None:
            assert (isinstance(user_id, str) and user_id) or (isinstance(user_id, int) and user_id > 0), "Invalid user id returned"
    except requests.exceptions.RequestException as e:
        assert False, f"HTTP request failed: {e}"
    except ValueError as e:
        assert False, f"Response JSON parsing failed: {e}"

test_user_management_api_register_user()