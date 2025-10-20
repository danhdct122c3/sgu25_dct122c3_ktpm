package fpl.sd.backend.service;

import fpl.sd.backend.dto.request.PaymentCallbackRequest;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
public class PaymentValidationService {
    public void validatePaymentSignature(PaymentCallbackRequest request, String secretKey) {
        Map<String, String> params = new HashMap<>(request.getParameters());
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String calculatedHash = calculateHash(params, secretKey);
        
        log.info("Payment signature validation - Expected: {}, Actual: {}", calculatedHash, request.getSecureHash());

        if (!calculatedHash.equals(request.getSecureHash())) {
            log.error("Invalid payment signature. Expected: {}, Actual: {}", calculatedHash, request.getSecureHash());
            throw new AppException(ErrorCode.PAYMENT_SIGNATURE_INVALID);
        }
        
        log.info("Payment signature validated successfully");
    }

    public boolean validatePaymentStatus(String responseCode, String transactionStatus) {
        boolean isValid = "00".equals(responseCode) && "00".equals(transactionStatus);
        
        if (isValid) {
            log.info("Payment status validation successful - Response code: {}, Transaction status: {}", 
                    responseCode, transactionStatus);
        } else {
            log.warn("Payment status validation failed - Response code: {}, Transaction status: {}", 
                    responseCode, transactionStatus);
        }
        
        return isValid;
    }

    private String calculateHash(Map<String, String> params, String secretKey) {
        try {
            // Sort parameters và loại bỏ các tham số null hoặc rỗng
            Map<String, String> sortedParams = new TreeMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    sortedParams.put(entry.getKey(), entry.getValue());
                }
            }

            // Create hash data theo đúng format VNPay
            StringBuilder hashData = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append("&");
                }
                hashData.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue());
            }

            log.debug("Hash data for validation: {}", hashData.toString());

            // Calculate HMAC-SHA512
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes("UTF-8"),
                    "HmacSHA512"
            );
            hmacSha512.init(secretKeySpec);
            byte[] hmacData = hmacSha512.doFinal(hashData.toString().getBytes("UTF-8"));
            return Hex.encodeHexString(hmacData);

        } catch (Exception e) {
            log.error("Error calculating hash for payment validation", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
