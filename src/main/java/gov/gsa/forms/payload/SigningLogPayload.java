package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SigningLogPayload {

    private String id;
    private String name; //form-name-signing_log
    private String data;
    private String hash;
    private String declinedTimestamp;
    private String message;
    private String pdf;

    public SigningLogPayload() {}

    public SigningLogPayload(String id, String name, String data, String hash, String declinedTimestamp, String message) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.hash = hash;
        this.declinedTimestamp = declinedTimestamp;
        this.message = message;
    }
}
