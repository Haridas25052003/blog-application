package BlogApplication.BlogApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private int id;
    private String username;
    private String email;
    private String role;
}