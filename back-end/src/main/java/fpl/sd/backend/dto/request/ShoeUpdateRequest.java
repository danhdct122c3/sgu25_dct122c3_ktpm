package fpl.sd.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoeUpdateRequest {
    String name;
    double price;
    boolean status;
    double fakePrice;
    String gender;
    String category;
    String description;
    List<VariantUpdateRequest> variants;
    List<ImageRequest> images; // Add this field for image updates
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageRequest {
        private String url;
    }
}
