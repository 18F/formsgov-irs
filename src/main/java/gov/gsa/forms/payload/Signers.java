package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Signers implements Serializable {

    private static final long serialVersionUID = 5808167462123295388L;

    @JsonProperty("email")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("embed_url_user_id")
    private String embedUrlUserId;

    @JsonProperty("approve_only")
    private boolean approveOnly = false;

    @JsonProperty("notify_only")
    private boolean notifyOnly = false;

    private int order;

    public Signers(String email, String firstName, String lastName, String embedUrlUserId, int order) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.embedUrlUserId = embedUrlUserId;
        this.order = order;
    }

    public Signers(String email, String firstName, String lastName, int order) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.order = order;
    }

    public Signers() {}
}
