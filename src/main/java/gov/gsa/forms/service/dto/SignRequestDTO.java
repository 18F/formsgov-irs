package gov.gsa.forms.service.dto;

import java.io.Serializable;
import lombok.Data;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.SessionScope;

@Data
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SignRequestDTO implements Serializable {

    private static final long serialVersionUID = 8223397747949987738L;
    private String signRequestDocumentUrl;

    public SignRequestDTO() {}

    public SignRequestDTO(String signRequestDocumentUrl) {
        this.signRequestDocumentUrl = signRequestDocumentUrl;
    }
}
