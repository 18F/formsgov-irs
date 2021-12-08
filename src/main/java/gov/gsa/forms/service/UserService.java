package gov.gsa.forms.service;

import com.nimbusds.jose.shaded.json.JSONObject;
import gov.gsa.forms.config.Constants;
import gov.gsa.forms.service.dto.AdminUserDTO;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service class for managing users.
 */
@Service
@Slf4j
public class UserService {

    /**
     * Returns the user from an OAuth 2.0 login or resource server with JWT.
     *
     * @param authToken the authentication token.
     * @return the user from the authentication.
     */
    public AdminUserDTO getUserFromAuthentication(AbstractAuthenticationToken authToken) {
        Map<String, Object> attributes;
        if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }
        AdminUserDTO user = getUser(attributes);
        user.setAuthorities(authToken.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));

        return user;
    }

    private static AdminUserDTO getUser(Map<String, Object> details) {
        log.info("User details scope  :{}", details);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        AdminUserDTO user = new AdminUserDTO();
        Boolean activated = Boolean.TRUE;
        // handle resource server JWT, where sub claim is email and uid is ID
        if (details.get("uid") != null) {
            user.setId((String) details.get("uid"));
            user.setLogin((String) details.get("sub"));
        } else {
            user.setId((String) details.get("sub"));
        }
        if (details.get("preferred_username") != null) {
            user.setLogin(((String) details.get("preferred_username")).toLowerCase());
        } else if (user.getLogin() == null) {
            user.setLogin(user.getId());
        }
        if (details.get("given_name") != null) {
            user.setFirstName((String) details.get("given_name"));
        } else if (details.get("name") != null) {
            user.setFirstName((String) details.get("name"));
        }
        if (details.get("family_name") != null) {
            user.setLastName((String) details.get("family_name"));
        }
        if (details.get("email_verified") != null) {
            activated = (Boolean) details.get("email_verified");
        }
        if (details.get("email") != null) {
            user.setEmail(((String) details.get("email")).toLowerCase());
        } else {
            user.setEmail((String) details.get("sub"));
        }
        if (details.get("langKey") != null) {
            user.setLangKey((String) details.get("langKey"));
        } else if (details.get("locale") != null) {
            // trim off country code if it exists
            String locale = (String) details.get("locale");
            if (locale.contains("_")) {
                locale = locale.substring(0, locale.indexOf('_'));
            } else if (locale.contains("-")) {
                locale = locale.substring(0, locale.indexOf('-'));
            }
            user.setLangKey(locale.toLowerCase());
        } else {
            // set langKey to default if not specified by IdP
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        if (details.get("picture") != null) {
            user.setImageUrl((String) details.get("picture"));
        }
        if (details.get("social_security_number") != null) {
            user.setSsn((String) details.get("social_security_number"));
        }
        if (details.get("address") != null) {
            JSONObject addressAttr = (JSONObject) details.get("address");
            Object formatted = addressAttr.get("formatted");
            user.setFormatted((String) formatted);
        }
        user.setActivated(activated);
        request.getSession().setAttribute("user", user);
        return user;
    }
}
