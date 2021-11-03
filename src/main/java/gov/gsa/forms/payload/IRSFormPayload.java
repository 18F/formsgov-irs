package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class IRSFormPayload {

    private String token;
    private List<Signature> signatures;
    private Form form;
    private SigningLog signingLog;
    private List<Attachment> attachments;
}
