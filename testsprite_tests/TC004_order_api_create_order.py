import requests
import uuid

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_order_api_create_order():
    # Step 1: Create a test user (register)
    user_data = {
        "username": f"testuser_{uuid.uuid4().hex[:8]}",
        "password": "TestPass123!",
        "email": f"testuser_{uuid.uuid4().hex[:8]}@example.com"
    }
    try:
        resp_user_register = requests.post(
            f"{BASE_URL}/users/register",
            json=user_data,
            timeout=TIMEOUT
        )
        assert resp_user_register.status_code == 201 or resp_user_register.status_code == 200, f"User registration failed: {resp_user_register.text}"
        user_id = resp_user_register.json().get("id")
        assert user_id is not None, "No user ID returned on registration"

        # Step 2: Authenticate user to get token
        auth_data = {
            "username": user_data["username"],
            "password": user_data["password"]
        }
        resp_auth = requests.post(
            f"{BASE_URL}/auth/token",
            json=auth_data,
            timeout=TIMEOUT
        )
        assert resp_auth.status_code == 200, f"Authentication failed: {resp_auth.text}"
        auth_json = resp_auth.json()
        token = auth_json.get("token") or auth_json.get("accessToken") or auth_json.get("access_token")
        assert token, "No token returned on authentication"

        headers = {
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json"
        }

        # Step 3: Setup product and variant to add to cart
        # Create brand
        brand_data = {"name": f"TestBrand_{uuid.uuid4().hex[:6]}"}
        resp_brand = requests.post(f"{BASE_URL}/brands", json=brand_data, headers=headers, timeout=TIMEOUT)
        assert resp_brand.status_code in [200,201], f"Failed to create brand: {resp_brand.text}"
        brand_id = resp_brand.json().get("id")
        assert brand_id, "Brand ID missing"

        # Create shoe
        shoe_data = {
            "name": f"TestShoe_{uuid.uuid4().hex[:6]}",
            "brandId": brand_id,
            "description": "A test shoe",
            "price": 100,
            "categories": []
        }
        resp_shoe = requests.post(f"{BASE_URL}/shoes", json=shoe_data, headers=headers, timeout=TIMEOUT)
        assert resp_shoe.status_code in [200,201], f"Failed to create shoe: {resp_shoe.text}"
        shoe_id = resp_shoe.json().get("id")
        assert shoe_id, "Shoe ID missing"

        # Create variant
        variant_data = {
            "shoeId": shoe_id,
            "size": 42,
            "color": "Black",
            "stock": 10,
            "price": 100
        }
        resp_variant = requests.post(f"{BASE_URL}/variants", json=variant_data, headers=headers, timeout=TIMEOUT)
        assert resp_variant.status_code in [200,201], f"Failed to create variant: {resp_variant.text}"
        variant_id = resp_variant.json().get("id")
        assert variant_id, "Variant ID missing"

        # Step 4: Add variant to user's cart
        add_cart_data = {
            "variantId": variant_id,
            "quantity": 1
        }
        resp_cart_add = requests.post(f"{BASE_URL}/cart/add", json=add_cart_data, headers=headers, timeout=TIMEOUT)
        assert resp_cart_add.status_code in [200,201], f"Failed to add item to cart: {resp_cart_add.text}"

        # Step 5: Create order
        order_data = {
            "userId": user_id,
            "paymentMethod": "COD"  # Cash on delivery; assuming required field
        }

        resp_order = requests.post(f"{BASE_URL}/orders", json=order_data, headers=headers, timeout=TIMEOUT)
        assert resp_order.status_code in [200,201], f"Order creation failed: {resp_order.text}"
        order_resp_json = resp_order.json()
        order_id = order_resp_json.get("id")
        assert order_id, "Order ID missing in response"
        assert order_resp_json.get("status") == "CREATED", f"Initial order status is not CREATED but {order_resp_json.get('status')}"

    finally:
        # Cleanup: Delete order, variant, shoe, brand, and user if possible
        if 'order_id' in locals():
            try:
                requests.delete(f"{BASE_URL}/orders/{order_id}", headers=headers, timeout=TIMEOUT)
            except Exception:
                pass
        if 'variant_id' in locals():
            try:
                requests.delete(f"{BASE_URL}/variants/{variant_id}", headers=headers, timeout=TIMEOUT)
            except Exception:
                pass
        if 'shoe_id' in locals():
            try:
                requests.delete(f"{BASE_URL}/shoes/{shoe_id}", headers=headers, timeout=TIMEOUT)
            except Exception:
                pass
        if 'brand_id' in locals():
            try:
                requests.delete(f"{BASE_URL}/brands/{brand_id}", headers=headers, timeout=TIMEOUT)
            except Exception:
                pass
        if 'token' in locals():
            try:
                requests.post(f"{BASE_URL}/auth/logout", headers=headers, timeout=TIMEOUT)
            except Exception:
                pass
        if 'user_id' in locals():
            try:
                requests.delete(f"{BASE_URL}/users/{user_id}", headers=headers, timeout=TIMEOUT)
            except Exception:
                pass

test_order_api_create_order()
