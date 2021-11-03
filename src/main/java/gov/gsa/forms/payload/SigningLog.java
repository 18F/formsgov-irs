package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SigningLog {

    private String id;
    private String name;
    private String data;
    private String hash;
    private String declinedTimestamp;
    private String message;
    private String pdf;

    public SigningLog() {}

    public SigningLog(String id, String name, String data, String hash, String declinedTimestamp, String message) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.hash = hash;
        this.declinedTimestamp = declinedTimestamp;
        this.message = message;
    }

    @JsonProperty("hash")
    public String getHash() {
        return hash;
    }

    @JsonProperty("security_hash")
    public void setHash(String hash) {
        this.hash = hash;
    }
}
