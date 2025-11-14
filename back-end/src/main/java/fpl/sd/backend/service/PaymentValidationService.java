package fpl.sd.backend.service;

import fpl.sd.backend.configuration.VNPayConfig;
import fpl.sd.backend.dto.request.PaymentCallbackRequest;
import fpl.sd.backend.exception.AppException;
import fpl.sd.backend.exception.ErrorCode;
import fpl.sd.backend.utils.VNPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentValidationService {
    private final VNPayConfig vnPayConfig;

    public void validatePaymentSignature(PaymentCallbackRequest request) {
        Map<String, String> params = new HashMap<>(request.getParameters());
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String calculatedHash = calculateHash(params, vnPayConfig);

        log.info("Payment signature validation - Expected: {}, Actual: {}", calculatedHash, request.getSecureHash());

        // Compare case-insensitively to avoid simple case mismatch issues; this will pass whether hash was sent upper/lower
        if (request.getSecureHash() == null || !calculatedHash.equalsIgnoreCase(request.getSecureHash())) {
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

    private String calculateHash(Map<String, String> params, VNPayConfig config) {
        try {
            // Sort parameters và loại bỏ các tham số null hoặc rỗng
            Map<String, String> sortedParams = new TreeMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    sortedParams.put(entry.getKey(), entry.getValue());
                }
            }

            // Build hash data depending on config: encoded values or raw
            String hashData;
            if (config.isVnpHashEncode()) {
                // encode keys and values for hash
                hashData = VNPayUtils.generateQueryUrl(sortedParams, true);
            } else {
                // raw key=value pairs
                hashData = VNPayUtils.generateQueryUrl(sortedParams, false);
            }

            log.debug("Hash data for validation: {}", hashData);

            // Use VNPayUtils hmac to calculate HMAC-SHA512
            String hmac = VNPayUtils.hmacSHA512(config.getVnpHashSecret(), hashData);

            // If config expects uppercase when sending, still compare case-insensitively (handled in caller)
            if (config.isVnpHashUpper()) {
                return hmac.toUpperCase();
            }
            return hmac;

        } catch (Exception e) {
            log.error("Error calculating hash for payment validation", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
