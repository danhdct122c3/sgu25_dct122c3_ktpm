package fpl.sd.backend.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class VNPayUtils {
    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            // Use UTF-8 for the key bytes to ensure consistent HMAC across platforms
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff)); // lowercase hex to match fastfood project
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getRandomNumber(int length) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Generate query string for VNPay.
     * If encodeForUrl == true: encode both key and value with UTF-8 for redirect URL.
     * If encodeForUrl == false: generate raw key=value pairs (no URL encoding) for secure hash data.
     */
    public static String generateQueryUrl(Map<String, String> paramsMap, boolean encodeForUrl) {
        return paramsMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (encodeForUrl) {
                        return urlEncode(key) + "=" + urlEncode(value);
                    } else {
                        // raw key=value used to build hash data (no URL encoding)
                        return key + "=" + value;
                    }
                })
                .collect(Collectors.joining("&"));
    }

    private static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20")
                    .replace("%7E", "~");
        } catch (Exception ex) {
            return s;
        }
    }

    public static String getCreateDate() {
        return getCreateDate("GMT+7");
    }

    public static String getCreateDate(String timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        return formatter.format(cal.getTime());
    }

    public static String getExpireDate() {
        return getExpireDate("GMT+7");
    }

    public static String getExpireDate(String timezone) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));

        // Add 15 minutes to the current date and time
        cal.add(Calendar.MINUTE, 15);

        return formatter.format(cal.getTime());
    }
}
