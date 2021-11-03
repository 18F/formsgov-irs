package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SignRequest {

    public String from_email;
    public String from_email_name;
    public boolean is_being_prepared;
    public String redirect_url;
    public String redirect_url_declined;
    //    public List<Object> required_attachments;
    public List<Signers> signers;
    public String uuid;
}
