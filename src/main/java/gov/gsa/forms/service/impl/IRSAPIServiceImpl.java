package gov.gsa.forms.service.impl;

import gov.gsa.forms.payload.*;
import gov.gsa.forms.payload.builder.IRSFormPayloadBuilder;
import gov.gsa.forms.service.IRSAPIService;
import gov.gsa.forms.service.dto.AdminUserDTO;
import gov.gsa.forms.util.CommonUtil;
import gov.gsa.forms.util.ObjectMapperUtil;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Named
@Slf4j
public class IRSAPIServiceImpl implements IRSAPIService {

    private static final String ID_TYPE = "LoginGovUUID";

    private static final String AUTHENTICATION_TYPE = "eAuth";

    private static final String AUTHENTICATION_LEVEL = "IAL2";

    private static final int AUTHENTICATION_ID = 909080550;

    private static final String CLIENT_APP = "GSA_FormsGov";

    private static final String TRANS_TYPE = "GSA_DocId";

    private static final String INTENT_ID = "0";

    @Value("${irs-api-token}")
    private String apiToken;

    @Value("${irs-api-url}")
    private String apiEndPoint;

    private final WebClient webClient;

    private final HttpServletRequest request;

    private String sampleJson =
        "{\n" +
        "    \"token\": \"TLLANK29fUrzkXUjsnuDbEaB\",\n" +
        "    \"signatures\": [\n" +
        "        {\n" +
        "            \"idType\": \"LoginGovUUID\",\n" +
        "            \"id\": \"952657505ab011f2\",\n" +
        "            \"name\": \"Valeria Williamson\",\n" +
        "            \"email\": \"Valeria.Williamson@fakeEmail.com\",\n" +
        "            \"signedTimestamp\": \"2021-10-25T16:51:10Z\",\n" +
        "            \"ipAddress\": \"169.249.181.130\",\n" +
        "            \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36\",\n" +
        "            \"sessionId\": \"2012-49a7-bedf-f13c\",\n" +
        "            \"authenticationType\": \"eAuth\",\n" +
        "            \"authenticationLevel\": \"IAL2\",\n" +
        "            \"authenticationId\": 909080550,\n" +
        "            \"clientApp\": \"GSA_FormsGov\",\n" +
        "            \"transType\": \"GSA_DocId\",\n" +
        "            \"intentId\": \"0\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"idType\": \"LoginGovUUID\",\n" +
        "            \"id\": \"abbea0812647bdc2\",\n" +
        "            \"name\": \"Celeste McGee\",\n" +
        "            \"email\": \"Celeste.McGee@fakeEmail.com\",\n" +
        "            \"signedTimestamp\": \"2021-10-25T16:51:10Z\",\n" +
        "            \"ipAddress\": \"230.65.244.172\",\n" +
        "            \"userAgent\": \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36\",\n" +
        "            \"sessionId\": \"302e-4c9d-a568-2460\",\n" +
        "            \"authenticationType\": \"eAuth\",\n" +
        "            \"authenticationLevel\": \"IAL2\",\n" +
        "            \"authenticationId\": 617162810,\n" +
        "            \"clientApp\": \"GSA_FormsGov\",\n" +
        "            \"transType\": \"GSA_DocId\",\n" +
        "            \"intentId\": \"0\"\n" +
        "        }\n" +
        "    ],\n" +
        "    \"form\": {\n" +
        "        \"id\": \"ad6ec70130b7\",\n" +
        "        \"name\": \"Form 1\",\n" +
        "        \"data\": \"data:application/octet-stream;base64,IyBUaHltZWxlYWYNCnNwcmluZy50aHltZWxlYWYucHJlZml4PWNsYXNzcGF0aDovdGVtcGxhdGVzLw0Kc3ByaW5nLnRoeW1lbGVhZi5zdWZmaXg9Lmh0bWwNCg0KIyBBcHBsaWNhdGlvbiBJbmZvcm1hdGlvbiANCmluZm8uYXBwLm5hbWU9ZVNpZ25hdHVyZQ0KaW5mby5hcHAuZ3JvdXBJZD1nb3YuaXJzLmlhbS5lc2lnbmF0dXJlDQppbmZvLmFwcC5hcnRpZmFjdElkPWVTaWduYXR1cmUNCmluZm8uYXBwLnZlcnNpb249MjAuMS1TTkFQU0hPVA0KDQojIEFjdHVhdG9yDQojIFRPRE86IE5vdGU6IFdlJ3ZlIGVuYWJsZWQgYWN0dWF0b3IgYXJlIHNob3dpbmcgaW4gZGVwdGggZGV0YWlscw0KbWFuYWdlbWVudC5lbmRwb2ludHMud2ViLmV4cG9zdXJlLmluY2x1ZGU9Kg0KIyBUT0RPOiBJZiBzaG93LWRldGFpbHMgd2FzIHNldCB0byBuZXZlciB3ZSB3b3VsZCBvbmx5IGRpc3BsYXkgYW4gVXAgaW5kaWNhdG9yIG9uIHRoZSBoZWFsdGggcGFnZQ0KbWFuYWdlbWVudC5lbmRwb2ludC5oZWFsdGguc2hvdy1kZXRhaWxzPWFsd2F5cw0KbWFuYWdlbWVudC5oZWFsdGguZGIuZW5hYmxlZD10cnVlDQptYW5hZ2VtZW50LmhlYWx0aC5kZWZhdWx0cy5lbmFibGVkPXRydWUNCm1hbmFnZW1lbnQuaGVhbHRoLmRpc2tzcGFjZS5lbmFibGVkPXRydWUNCg0KDQoNCiMgSGlrYXJpQ1Agc2V0dGluZ3MNCiMgc3ByaW5nLmRhdGFzb3VyY2UuaGlrYXJpLioNCg0KI3NwcmluZy5kYXRhc291cmNlLmhpa2FyaS5jb25uZWN0aW9uLXRpbWVvdXQ9NjAwMDANCiNzcHJpbmcuZGF0YXNvdXJjZS5oaWthcmkubWF4aW11bS1wb29sLXNpemU9NQ0KDQoNCiMgY3JlYXRlIGFuZCBkcm9wIHRhYmxlcyBhbmQgc2VxdWVuY2VzLCBsb2FkcyBpbXBvcnQuc3FsDQojc3ByaW5nLmpwYS5oaWJlcm5hdGUuZGRsLWF1dG89Y3JlYXRlLWRyb3ANCg0KIyBIMg0KI3NwcmluZy5oMi5jb25zb2xlLmVuYWJsZWQ9dHJ1ZQ0KI3NwcmluZy5kYXRhc291cmNlLmRyaXZlckNsYXNzTmFtZT1vcmcuaDIuRHJpdmVyDQojc3ByaW5nLmgyLmNvbnNvbGUucGF0aD0vaDINCg0KIyBEYXRhc291cmNlDQojc3ByaW5nLmRhdGFzb3VyY2UudXJsPWpkYmM6aDI6bWVtOnRlc3RkYg0KI3NwcmluZy5qcGEuZGF0YWJhc2UtcGxhdGZvcm09b3JnLmhpYmVybmF0ZS5kaWFsZWN0LkgyRGlhbGVjdA0KI3NwcmluZy5qcGEucHJvcGVydGllcy5oaWJlcm5hdGUuZGlhbGVjdD1vcmcuaGliZXJuYXRlLmRpYWxlY3QuSDJEaWFsZWN0DQojc3ByaW5nLmpwYS5wcm9wZXJ0aWVzLmhpYmVybmF0ZS5pZC5uZXdfZ2VuZXJhdG9yX21hcHBpbmdzPWZhbHNlDQojc3ByaW5nLmpwYS5wcm9wZXJ0aWVzLmhpYmVybmF0ZS5mb3JtYXRfc3FsPXRydWUNCiNzcHJpbmcuZGF0YXNvdXJjZS51c2VybmFtZT1zYQ0KI3NwcmluZy5kYXRhc291cmNlLnBhc3N3b3JkPQ0KDQoNCiMgY3JlYXRlIGFuZCBkcm9wIHRhYmxlcyBhbmQgc2VxdWVuY2VzLCBsb2FkcyBpbXBvcnQuc3FsDQojc3ByaW5nLmpwYS5oaWJlcm5hdGUuZGRsLWF1dG89Y3JlYXRlLWRyb3ANCg0KDQoNCg0KIyBPcmFjbGUgc2V0dGluZ3MNCnNwcmluZy5kYXRhc291cmNlLnVybD1qZGJjOm9yYWNsZTp0aGluOkB2eHpzbWVtb3JhZXNkMDEudGNjLmlycy5nb3Y6MTcwMDplc2lnbg0Kc3ByaW5nLmRhdGFzb3VyY2UudXNlcm5hbWU9ZXNpZ25kYmFwcGRldnN2Yw0KI3NwcmluZy5kYXRhc291cmNlLnBhc3N3b3JkPVRwaWdfcDIjeW5UaXIyDQpzcHJpbmcuZGF0YXNvdXJjZS5wYXNzd29yZD1KY21keDBPVG9tZmU3Qk5IVmNRY1RnPT0NCnNwcmluZy5kYXRhc291cmNlLmRyaXZlci1jbGFzcy1uYW1lPW9yYWNsZS5qZGJjLmRyaXZlci5PcmFjbGVEcml2ZXINCg0KDQojc3ByaW5nLmpwYS5wcm9wZXJ0aWVzLmhpYmVybmF0ZS5pZC5uZXdfZ2VuZXJhdG9yX21hcHBpbmdzPWZhbHNlDQojc3ByaW5nLmpwYS5wcm9wZXJ0aWVzLmhpYmVybmF0ZS5mb3JtYXRfc3FsPXRydWUNCg0KIyBsb2dnaW5nDQpsb2dnaW5nLmxldmVsLnJvb3Q9REVCVUcNCmxvZ2dpbmcuZmlsZT1DOi9JQU0vZVNpZ25hdHVyZS5sb2cNCmxvZ2dpbmcucGF0dGVybi5jb25zb2xlPSVke3l5eXktTU0tZGQgSEg6bW06c3N9ICUtNWxldmVsICVsb2dnZXJ7MzZ9IC0gJW1zZyVuDQpsb2dnaW5nLmxldmVsLm9yZy5oaWJlcm5hdGUuU1FMPWRlYnVnDQpsb2dnaW5nLmxldmVsLm9yZy5oaWJlcm5hdGUudHlwZS5kZXNjcmlwdG9yLnNxbD1lcnJvcg0KbG9nZ2luZy5sZXZlbC5vcmcuc3ByaW5nZnJhbWV3b3JrLj1lcnJvcg0KbG9nZ2luZy5sZXZlbC5nb3YuaXJzLj10cmFjZQ0KbG9nZ2luZy5sZXZlbC49ZXJyb3I=\",\n" +
        "        \"hash\": \"=92b6cc582a13a77dd791c80dfc3f36\"\n" +
        "    },\n" +
        "    \"signingLog\": {\n" +
        "        \"id\": \"f5684c44b8c5\",\n" +
        "        \"name\": \"Signing Log 1\",\n" +
        "        \"data\": \"long text\",\n" +
        "        \"hash\": \"=31783f642d19b54628a48843dce290\",\n" +
        "        \"declinedTimestamp\": \"\",\n" +
        "        \"message\": \"\"\n" +
        "    },\n" +
        "    \"attachments\": [\n" +
        "        {\n" +
        "            \"id\": \"af941162c692\",\n" +
        "            \"name\": \"Attachment 1\",\n" +
        "            \"data\": \"long text\",\n" +
        "            \"hash\": \"=24c0b1eba05166ad51c473d02ce3f9\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"id\": \"c6b25af2d324\",\n" +
        "            \"name\": \"Attachment 2\",\n" +
        "            \"data\": \"long text\",\n" +
        "            \"hash\": \"=6c4d100e69b4eba694ebceb9275928\"\n" +
        "        }\n" +
        "    ]\n" +
        "}";

    public IRSAPIServiceImpl(WebClient webClient, HttpServletRequest request) {
        this.webClient = webClient;
        this.request = request;
    }

    @Override
    public boolean sendPayload(SignRequestDocumentResponse signRequestDocumentResponse) {
        try {
            URL urlPdf = new URL(signRequestDocumentResponse.getFileAsPdf());
            URL urlSigningLog = new URL(signRequestDocumentResponse.getSigningLog().getPdf());
            byte[] encodedPdf = CommonUtil.encodePdfToByte(urlPdf);
            String stringPdf = Base64.getEncoder().encodeToString(encodedPdf);
            byte[] encodedSigningLogPdf = CommonUtil.encodePdfToByte(urlSigningLog);
            String stringSigningLogPdf = Base64.getEncoder().encodeToString(encodedSigningLogPdf);
            IRSFormPayload irsFormPayload = buildPayload(signRequestDocumentResponse, stringPdf, stringSigningLogPdf);
            String jsonString = ObjectMapperUtil.writeToJsonString(irsFormPayload);
            log.info("****** IRS outbound payload ****** :{}", jsonString);
            String irsResponse = postRequest(sampleJson).block();
            log.info("#IRS API post response , {}  ", irsResponse);
        } catch (IOException e) {
            log.error("#IRSAPIServiceImpl.sendPayload error occurred converting SignRequestDocumentResponse : ", e);
        }
        return true;
    }

    private IRSFormPayload buildPayload(
        SignRequestDocumentResponse signRequestDocumentResponse,
        String encodedPdf,
        String encodedSigningLogPdf
    ) {
        String ipAddress = "78.36.30.1";
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession().getId();
        AdminUserDTO user = (AdminUserDTO) request.getSession().getAttribute("user");
        Signers signers = new Signers(user.getEmail(), user.getFirstName(), user.getLastName(), user.getEmail());
        Signature signature = new Signature(
            ID_TYPE,
            signRequestDocumentResponse.getSignrequest().getUuid(),
            user.getFirstName() + user.getLastName(),
            user.getEmail(),
            new Date().toString(),
            ipAddress,
            userAgent,
            sessionId,
            AUTHENTICATION_TYPE,
            AUTHENTICATION_LEVEL,
            AUTHENTICATION_ID,
            CLIENT_APP,
            TRANS_TYPE,
            INTENT_ID
        );
        Form form = new Form("irs-4506T", "4506-T", encodedPdf, signRequestDocumentResponse.getSecurityHash());
        SigningLog signingLog = new SigningLog(
            signRequestDocumentResponse.getUuid(),
            "4506-T_signing_log",
            encodedSigningLogPdf,
            "",
            "",
            ""
        );
        return IRSFormPayloadBuilder
            .builder()
            .token(apiToken)
            .signatures(List.of(signature))
            .form(form)
            .signingLog(signingLog)
            .attachments(Collections.emptyList())
            .build();
    }

    private Mono<String> getRequestDocument(String getDocumentUrl) {
        return webClient
            .get()
            .uri(getDocumentUrl)
            .headers(httpHeaders -> {
                httpHeaders.add("Authorization", "TOKEN " + apiToken);
                httpHeaders.add("Content-Type", "application/json");
            })
            .retrieve()
            .bodyToMono(String.class);
    }

    @Override
    public boolean verifyPayload(String docId) {
        throw new NotImplementedException("TODO");
    }

    private Mono<String> postRequest(String reqBody) {
        return webClient
            .post()
            .uri(apiEndPoint)
            .headers(httpHeaders -> {
                httpHeaders.add("Authorization", "TOKEN " + apiToken);
                httpHeaders.add("Content-Type", "application/json");
            })
            .body(Mono.just(reqBody), String.class)
            .retrieve()
            .bodyToMono(String.class);
    }
}
