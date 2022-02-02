package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SignRequestDocumentResponse {

    //    private String document;
    private String url;
    private String uuid;
    private String user;
    private String name;

    @JsonProperty("external_id")
    private String externalId;

    private String file;
    private String pdf;
    private String status;
    private SignRequest signrequest;
    private SigningLog signingLog;

    @JsonProperty("event_type")
    private String eventType;

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

        private String name;
        private Team team;
        private String uuid;
        private SignRequest signrequest;

        @JsonProperty("signing_log")
        private SigningLog signingLog;

        @JsonProperty("security_hash")
        private String securityHash;

        @JsonProperty("file_as_pdf")
        private String fileAsPdf;

        @JsonProperty("short_id")
        private String documentId;

        private String pdf;
    }
    // private List<Object> attachments;
}
