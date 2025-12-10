import requests

BASE_URL = "http://localhost:8080"
TIMEOUT = 30

def test_cart_api_add_to_cart():
    session = requests.Session()
    headers = {
        "Content-Type": "application/json"
    }
    # Step 1: Get product variants to add to cart (assuming at least one variant exists)
    try:
        variants_resp = session.get(f"{BASE_URL}/variants", headers=headers, timeout=TIMEOUT)
        variants_resp.raise_for_status()
        variants = variants_resp.json()
        assert isinstance(variants, list), "Variants response should be a list"
        assert len(variants) > 0, "No variants found to add to cart"
        variant = variants[0]
        variant_id = variant.get("id")
        assert variant_id is not None, "Variant ID must be present"
        
        # Step 2: Add item to cart
        add_payload = {
            "variantId": variant_id,
            "quantity": 2
        }
        add_resp = session.post(f"{BASE_URL}/cart/add", json=add_payload, headers=headers, timeout=TIMEOUT)
        add_resp.raise_for_status()
        add_result = add_resp.json()
        # We expect some indication of success, check at least the variantId is in the cart state
        assert add_result is not None, "Add to cart response is empty"
        
        # Step 3: Get cart and verify item added with correct quantity
        cart_resp = session.get(f"{BASE_URL}/cart", headers=headers, timeout=TIMEOUT)
        cart_resp.raise_for_status()
        cart = cart_resp.json()
        assert "items" in cart, "Cart should contain items field"
        items = cart["items"]
        assert any(item.get("variantId") == variant_id and item.get("quantity") == 2 for item in items), "Added variant with correct quantity not found in cart"

    except requests.exceptions.RequestException as e:
        assert False, f"Request failed: {str(e)}"


test_cart_api_add_to_cart()