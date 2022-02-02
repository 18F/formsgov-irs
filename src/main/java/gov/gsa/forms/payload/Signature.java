package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Signature {

    private String idType;
    private String id;
    private String name;
    private String email;
    private String signedTimestamp;
    private String ipAddress;
    private String userAgent;
    private String sessionId;
    private String authenticationType;
    private String authenticationLevel;
    private int authenticationId;
    private String clientApp;
    private String transType;
    private String intentId;

    public Signature() {}

    public Signature(
        String idType,
        String id,
        String name,
        String email,
        String signedTimestamp,
        String ipAddress,
        String userAgent,
        String sessionId,
        String authenticationType,
        String authenticationLevel,
        int authenticationId,
        String clientApp,
        String transType,
        String intentId
    ) {
        this.idType = idType;
        this.id = id;
        this.name = name;
        this.email = email;
        this.signedTimestamp = signedTimestamp;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.sessionId = sessionId;
        this.authenticationType = authenticationType;
        this.authenticationLevel = authenticationLevel;
        this.authenticationId = authenticationId;
        this.clientApp = clientApp;
        this.transType = transType;
        this.intentId = intentId;
    }
}
