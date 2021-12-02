package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SignRequestEventCallbackResponse {

    private String uuid;
    private String status;

    @JsonProperty("event_type")
    private String eventType;

    private Date timestamp;
    private Team team;
    private Document document;

    @Data
    private static class Team {

        private String name;
        private String subdomain;
        private String url;
    }

    @Data
    public static class Document {

        private Team team;
        private String uuid;
    }
}
