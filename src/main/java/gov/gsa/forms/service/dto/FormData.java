package gov.gsa.forms.service.dto;

import lombok.Data;
import org.springframework.web.context.annotation.SessionScope;

@SessionScope
@Data
public class FormData {

    private String formName;
    private String taxpayer2FirstName;
    private String taxpayer2LastName;
    private String taxpayer2Email;
}
