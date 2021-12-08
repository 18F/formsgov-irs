package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SigningLog {

    private String pdf;

    @JsonProperty("security_hash")
    private String hash;
}
