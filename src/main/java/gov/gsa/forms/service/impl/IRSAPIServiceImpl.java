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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
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
    public static final String USER_AGENT = "User-Agent";
    public static final String USER = "updated-user";

    @Value("${irs-api-token}")
    private String apiToken;

    @Value("${irs-api-url}")
    private String apiEndPoint;

    private final WebClient webClient;

    private final HttpServletRequest request;

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
            String irsResponse = postRequest(jsonString).block();
            log.info("****** IRS API post response ******: {}", irsResponse);
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
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader(USER_AGENT);
        String sessionId = request.getSession().getId();
        AdminUserDTO user = (AdminUserDTO) request.getSession().getAttribute(USER);
        Signature signature1 = new Signature(
            ID_TYPE,
            signRequestDocumentResponse.getSignrequest().getUuid(),
            user.getFirstName() + " " + user.getLastName(),
            user.getEmail(),
            ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
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
        Signature signature2 = new Signature(
            ID_TYPE,
            signRequestDocumentResponse.getSignrequest().getUuid(),
            user.getTaxpayer2FirstName() + " " + user.getTaxpayer2LastName(),
            user.getTaxpayer2Email(),
            ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT),
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
        Form form = new Form(
            signRequestDocumentResponse.getDocumentId(),
            user.getFormName(),
            encodedPdf,
            signRequestDocumentResponse.getSecurityHash()
        );
        SigningLog signingLog = new SigningLog(
            signRequestDocumentResponse.getUuid(),
            user.getFormName() + "_signing_log",
            encodedSigningLogPdf,
            signRequestDocumentResponse.getSigningLog().getHash(),
            "",
            ""
        );
        return IRSFormPayloadBuilder
            .builder()
            .token(apiToken)
            .signatures(List.of(signature1, signature2))
            .form(form)
            .signingLog(signingLog)
            .attachments(Collections.emptyList())
            .build();
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
