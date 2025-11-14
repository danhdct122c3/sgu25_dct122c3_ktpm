import hmac
import hashlib

secret = "ANAIDKYHLTU5HVKRMUM4NVRMLCN0SJU1"

# Raw hash data must match the server's non-URL-encoded sorted parameter string
hash_data = (
    "vnp_Amount=247000000&vnp_Command=pay&vnp_CreateDate=20251114211709&"
    "vnp_CurrCode=VND&vnp_ExpireDate=20251114213209&vnp_IpAddr=42.118.191.137&"
    "vnp_Locale=vn&vnp_OrderInfo=a5a17609-6583-44b9-a13f-cdb1774425ad&vnp_OrderType=other&"
    "vnp_ReturnUrl=http://localhost:3000/checkout/payment-callback&vnp_TmnCode=8VWZT7KJ&"
    "vnp_TxnRef=31591564&vnp_Version=2.1.0"
)

vnp_secure_hash_from_url = "26031EA52D549FB9AFB787727035A4DC32BE7CC3AD1BFEE5375001565A7C334D4CB61331A5743A90F1C06D1CA572B2FFCC81204993B34A059628C022760723F5"

computed = hmac.new(secret.encode('utf-8'), hash_data.encode('utf-8'), hashlib.sha512).hexdigest().upper()

print('Raw hash data:')
print(hash_data)
print()
print('Computed HMAC-SHA512 (UPPER):')
print(computed)
print()
print('vnp_SecureHash from URL:')
print(vnp_secure_hash_from_url)
print()
print('Match (case-insensitive):', computed.lower() == vnp_secure_hash_from_url.lower())

