package gov.gsa.forms.service.impl;

import gov.gsa.forms.payload.*;
import gov.gsa.forms.payload.builder.IRSFormPayloadBuilder;
import gov.gsa.forms.service.IRSAPIService;
import gov.gsa.forms.util.CommonUtil;
import gov.gsa.forms.util.ObjectMapperUtil;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
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

    public static final String ENCODED_PDF_PREFIX = "data:application/pdf;base64,";

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
            URL urlPdf = new URL(signRequestDocumentResponse.getDocument().getPdf());
            log.info("****** urlPdf ****** :{}", urlPdf);
            URL urlSigningLog = new URL(signRequestDocumentResponse.getDocument().getSigningLog().getPdf());
            byte[] encodedPdf = CommonUtil.encodePdfToByte(urlPdf);
            String base64stringPdf = Base64.getEncoder().encodeToString(encodedPdf);

            StringBuilder base64stringPdfBuilder = new StringBuilder(ENCODED_PDF_PREFIX);
            base64stringPdfBuilder.append(base64stringPdf);
            log.info("****** Base64 ****** :{}", base64stringPdfBuilder);

            StringBuilder base64stringLogPdfBuilder = new StringBuilder(ENCODED_PDF_PREFIX);
            byte[] encodedSigningLogPdf = CommonUtil.encodePdfToByte(urlSigningLog);
            String stringSigningLogPdf = Base64.getEncoder().encodeToString(encodedSigningLogPdf);
            base64stringLogPdfBuilder.append(stringSigningLogPdf);

            IRSFormPayload irsFormPayload = buildPayload(
                signRequestDocumentResponse,
                base64stringPdfBuilder.toString(),
                base64stringLogPdfBuilder.toString()
            );
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
        //first signer
        String firstSignerName = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().get(0).getFirstName();
        String firstSignerLastName = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().get(0).getLastName();
        String firstSignerEmail = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().get(0).getEmail();
        List<Signature> signatures = new ArrayList<>();
        Signature signature1 = new Signature(
            ID_TYPE,
            signRequestDocumentResponse.getDocument().getSignrequest().getUuid(),
            firstSignerName + " " + firstSignerLastName,
            firstSignerEmail,
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
        signatures.add(signature1);
        //second signer
        int size = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().size();
        if (size > 1) {
            String secondSignerName = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().get(1).getFirstName();
            String secondSignerLastName = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().get(1).getLastName();
            String secondSignerEmail = signRequestDocumentResponse.getDocument().getSignrequest().getSigners().get(1).getEmail();
            Signature signature2 = new Signature(
                ID_TYPE,
                signRequestDocumentResponse.getDocument().getSignrequest().getUuid(),
                secondSignerName + " " + secondSignerLastName,
                secondSignerEmail,
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
            signatures.add(signature2);
        }

        Form form = new Form(
            signRequestDocumentResponse.getDocument().getDocumentId(),
            signRequestDocumentResponse.getDocument().getName(),
            encodedPdf,
            signRequestDocumentResponse.getDocument().getSecurityHash()
        );
        SigningLogPayload signingLog = new SigningLogPayload(
            signRequestDocumentResponse.getUuid(),
            "signing_log_" + signRequestDocumentResponse.getDocument(),
            encodedSigningLogPdf,
            signRequestDocumentResponse.getDocument().getSigningLog().getHash(),
            "",
            ""
        );
        return IRSFormPayloadBuilder
            .builder()
            .token(apiToken)
            .signatures(signatures)
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
