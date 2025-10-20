package fpl.sd.backend.dto.request;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCallbackRequest {
    String secureHash;
    Map<String, String> parameters;
    String responseCode;
    String transactionStatus;
    String orderId;
    Long amount;
    String bankCode;
    String transactionNo;
    String paymentDate;
    String cardType;

    public static PaymentCallbackRequest from(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> {
            if (value != null && value.length > 0) {
                params.put(key, value[0]);
            }
        });

        // Validate required parameters
        String orderId = params.get("vnp_OrderInfo");
        String amountStr = params.get("vnp_Amount");
        String transactionNo = params.get("vnp_TransactionNo");
        
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID is required in payment callback");
        }
        
        if (transactionNo == null || transactionNo.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction number is required in payment callback");
        }
        
        Long amount = null;
        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                amount = Long.parseLong(amountStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount format in payment callback: " + amountStr);
            }
        }

        return PaymentCallbackRequest.builder()
                .secureHash(params.get("vnp_SecureHash"))
                .parameters(params)
                .responseCode(params.get("vnp_ResponseCode"))
                .transactionStatus(params.get("vnp_TransactionStatus"))
                .orderId(orderId)
                .amount(amount)
                .bankCode(params.get("vnp_BankCode"))
                .transactionNo(transactionNo)
                .paymentDate(params.get("vnp_PayDate"))
                .cardType(params.get("vnp_CardType"))
                .build();
    }
}
