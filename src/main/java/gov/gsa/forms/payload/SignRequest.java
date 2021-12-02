package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SignRequest {

    private String from_email;
    private String from_email_name;
    private boolean is_being_prepared;
    private String redirect_url;
    private String redirect_url_declined;
    //    public List<Object> required_attachments;
    private List<Signers> signers;
    private String uuid;
}
