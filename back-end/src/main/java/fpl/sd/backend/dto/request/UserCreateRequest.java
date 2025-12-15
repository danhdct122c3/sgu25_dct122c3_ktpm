package fpl.sd.backend.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @NotBlank(message = "Username is mandatory")
    String username;
    @NotBlank(message = "Password is mandatory")
    String password;
    @NotBlank(message = "Email is mandatory")
    String email;

}
