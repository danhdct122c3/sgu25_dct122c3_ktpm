package fpl.sd.backend.configuration;

import fpl.sd.backend.utils.VNPayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class VNPayConfig {

    @Value("${vnPay.version}")
    String vnpVersion;

    @Value("${vnPay.command}")
    String vnpCommand;

    @Value("${vnPay.orderType}")
    String vnpOrderType;

    @Value("${vnPay.currCode}")
    String vnpCurrCode;

    @Getter
    @Value("${vnPay.hash-secret}")
    String vnpHashSecret;

    @Value("${vnPay.tmn-code}")
    String vnpTmnCode;

    @Getter
    @Value("${vnPay.payment-url}")
    String vnpPayUrl;

    @Value("${vnPay.return-url}")
    String vnpReturnUrl;

    @Getter
    @Value("${vnPay.demo:false}")
    boolean vnpDemo;

    @Getter
    @Value("${vnPay.timezone:GMT+7}")
    String vnpTimezone;

    // New optional flags to try alternate hashing behavior when debugging signature issues
    @Getter
    @Value("${vnPay.hash-upper:false}")
    boolean vnpHashUpper;

    @Getter
    @Value("${vnPay.hash-encode:false}")
    boolean vnpHashEncode;

    @Getter
    @Value("${vnPay.send-hash-type:false}")
    boolean vnpSendHashType;

    @PostConstruct
    public void postConstruct() {
        try {
            String masked = "<not-set>";
            if (vnpHashSecret != null) {
                int len = vnpHashSecret.length();
                if (len > 4) {
                    masked = "****" + vnpHashSecret.substring(len - 4);
                } else {
                    masked = "****" + vnpHashSecret;
                }
            }
            log.info("VNPay config loaded: tmn-code={}, payment-url={}, hash-secret(masked)={}, demo={}, hashUpper={}, hashEncode={}, sendHashType={}",
                    vnpTmnCode, vnpPayUrl, masked, vnpDemo, vnpHashUpper, vnpHashEncode, vnpSendHashType);
        } catch (Exception ex) {
            log.warn("Error logging VNPayConfig at startup", ex);
        }
    }

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParams = new HashMap<>();
        // normalize values (remove possible surrounding quotes and whitespace)
        vnpParams.put("vnp_Version", normalize(vnpVersion));
        vnpParams.put("vnp_Command", normalize(vnpCommand));
        vnpParams.put("vnp_TmnCode", normalize(vnpTmnCode));
        vnpParams.put("vnp_CurrCode", normalize(vnpCurrCode));
        vnpParams.put("vnp_TxnRef", VNPayUtils.getRandomNumber(8));
        vnpParams.put("vnp_OrderType", normalize(vnpOrderType));
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_ReturnUrl", normalize(vnpReturnUrl));
        vnpParams.put("vnp_CreateDate", VNPayUtils.getCreateDate(vnpTimezone));
        vnpParams.put("vnp_ExpireDate", VNPayUtils.getExpireDate(vnpTimezone));
        return vnpParams;
    }

    private String normalize(String s) {
        if (s == null) return "";
        return s.replace("\"", "").trim();
    }
}
