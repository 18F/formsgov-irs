package gov.gsa.forms.service.dto;

import lombok.Data;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.annotation.SessionScope;

@Data
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SignRequestDTO {

    private String signRequestDocumentUrl;

    public SignRequestDTO() {}

    public SignRequestDTO(String signRequestDocumentUrl) {
        this.signRequestDocumentUrl = signRequestDocumentUrl;
    }
}
