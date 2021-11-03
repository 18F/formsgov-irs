package gov.gsa.forms.service.dto;

import com.nimbusds.openid.connect.sdk.claims.Address;
import gov.gsa.forms.config.Constants;
import java.util.Set;
import javax.validation.constraints.*;
import lombok.Data;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.SessionScope;

/**
 * A DTO representing a user, with his authorities.
 */
@Data
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AdminUserDTO {

    private String id;

    @NotBlank
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Size(max = 50)
    private String ssn;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String imageUrl;

    private boolean activated = false;

    @Size(min = 2, max = 10)
    private String langKey;

    @Size(max = 50)
    private String formatted;

    private Set<String> authorities;

    private UserAddress address;
}
