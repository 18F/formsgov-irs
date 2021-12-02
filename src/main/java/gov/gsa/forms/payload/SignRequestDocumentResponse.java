package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SignRequestDocumentResponse {

    private String document;
    private String url;
    private String uuid;
    private String user;

    @JsonProperty("file_as_pdf")
    private String fileAsPdf;

    private String name;

    @JsonProperty("external_id")
    private String externalId;

    private String file;
    private String pdf;
    private String status;
    private SignRequest signrequest;

    @JsonProperty("signing_log")
    private SigningLog signingLog;

    @JsonProperty("security_hash")
    private String securityHash;

    @JsonProperty("short_id")
    private String documentId;
    // private List<Object> attachments;
}
