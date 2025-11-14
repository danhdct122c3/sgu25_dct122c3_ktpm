import hmac
import hashlib

# Config (from your application.yml)
secret = "ANAIDKYHLTU5HVKRMUM4NVRMLCN0SJU1"

# Raw hash data (must match exactly the string used by the server to compute HMAC)
hash_data = (
    "vnp_Amount=115000000&vnp_Command=pay&vnp_CreateDate=20251114210621&"
    "vnp_CurrCode=VND&vnp_ExpireDate=20251114212121&vnp_IpAddr=42.118.191.137&"
    "vnp_Locale=vn&vnp_OrderInfo=05b04433-15bb-4127-ba3b-99acaf0db87a&vnp_OrderType=other&"
    "vnp_ReturnUrl=http://localhost:3000/checkout/payment-callback&vnp_TmnCode=8VWZT7KJ&"
    "vnp_TxnRef=43548551&vnp_Version=2.1.0"
)

# vnp_SecureHash from the request URL you provided
vnp_secure_hash = "285e840ffa939dc72dd2f8bce462a322ded53f80f1716b1468b33edcb9bc81b06592183e193e2abef620830a805e048fa3ab61759d208b868a9b00ded1cf7e47"

# Compute HMAC-SHA512
computed = hmac.new(secret.encode('utf-8'), hash_data.encode('utf-8'), hashlib.sha512).hexdigest()

print("Raw hash data:")
print(hash_data)
print()
print("Computed HMAC-SHA512:")
print(computed)
print()
print("vnp_SecureHash from URL:")
print(vnp_secure_hash)
print()
print("Match (case-insensitive):", computed.lower() == vnp_secure_hash.lower())

