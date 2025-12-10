import requests

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_product_management_api_create_shoe():
    url = f"{BASE_URL}/shoes"
    headers = {
        "Content-Type": "application/json"
    }
    # Example valid shoe data
    payload = {
        "name": "Test Shoe Model X",
        "description": "A stylish test shoe for automated testing",
        "brand": "TestBrand",
        "category": "Sneakers",
        "price": 99.99,
        "sizes": [38, 39, 40, 41, 42],
        "color": "Red",
        "stock": 50
    }

    shoe_id = None
    try:
        response = requests.post(url, json=payload, headers=headers, timeout=TIMEOUT)
        # Validate that the response status code is 201 Created or 200 OK
        assert response.status_code in (200, 201), f"Unexpected status code: {response.status_code}"
        data = response.json()
        # Validate the response contains an ID and correct fields
        shoe_id = data.get("id")
        assert shoe_id is not None, "Response JSON missing shoe id"
        assert data.get("name") == payload["name"], "Shoe name mismatch"
        assert data.get("brand") == payload["brand"], "Shoe brand mismatch"
        assert float(data.get("price")) == payload["price"], "Shoe price mismatch"
        assert data.get("category") == payload["category"], "Shoe category mismatch"
        assert data.get("color") == payload["color"], "Shoe color mismatch"
        assert isinstance(data.get("sizes"), list), "Shoe sizes is not a list"
        # Optionally confirm sizes match
        assert sorted(data.get("sizes")) == sorted(payload["sizes"]), "Shoe sizes mismatch"
        assert int(data.get("stock")) == payload["stock"], "Shoe stock mismatch"
    finally:
        # Cleanup: delete the created shoe if it was created
        if shoe_id:
            delete_url = f"{BASE_URL}/shoes/{shoe_id}"
            try:
                del_resp = requests.delete(delete_url, timeout=TIMEOUT)
                assert del_resp.status_code in (200, 204), f"Failed to delete shoe with id {shoe_id}"
            except Exception:
                pass

test_product_management_api_create_shoe()