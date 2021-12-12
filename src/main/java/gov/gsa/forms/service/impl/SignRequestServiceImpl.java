package gov.gsa.forms.service.impl;

import gov.gsa.forms.payload.SignRequestPayload;
import gov.gsa.forms.payload.Signers;
import gov.gsa.forms.payload.builder.SignRequestPayloadBuilder;
import gov.gsa.forms.service.IRSAPIService;
import gov.gsa.forms.service.SignRequestService;
import gov.gsa.forms.service.dto.AdminUserDTO;
import gov.gsa.forms.util.CommonUtil;
import gov.gsa.forms.util.ObjectMapperUtil;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author Wube Kifle
 *
 * <p>
 * A service class to send a Sign request using Non Blocking HTTP Request
 * </p>
 */
@Named
@Slf4j
public class SignRequestServiceImpl implements SignRequestService {

    private static final String REDIRECT_URL_SIGNED = "/sign-success";
    private static final String REDIRECT_URL_NOT_SIGNED = "/sign-unsuccessful";
    private static final String DOCUMENTS = "documents/";
    private static final String USER = "user";
    private static final String FORM_DATA = "form-data";
    public static final String YES = "yes";

    @Value("${sign-request-token}")
    private String signRequestToken;

    @Value("${sign-request-create-url}")
    private String signRequestCreateUrl;

    @Value("${sign-request-base-url}")
    private String signRequestBaseUrl;

    @Value("${sign-request-redirect-url}")
    private String redirectUrl;

    @Value("${sign-event-callback-url}")
    private String eventCallBackUrl;

    private final WebClient webClient;

    private final HttpServletRequest request;

    private final IRSAPIService irsAPIService;

    public SignRequestServiceImpl(WebClient webClient, HttpServletRequest request, IRSAPIService irsAPIService) {
        this.webClient = webClient;
        this.request = request;
        this.irsAPIService = irsAPIService;
    }

    @Override
    public String executeSignRequest(
        String pdfUrl,
        String pdfName,
        Principal principal,
        String firstName,
        String lastName,
        String email,
        String jointRequest
    ) {
        try {
            URL url = new URL(pdfUrl);
            byte[] encoded = CommonUtil.encodePdfToByte(url);
            AdminUserDTO user = (AdminUserDTO) request.getSession().getAttribute(USER);
            SignRequestPayload signRequestPayload = buildRequest(encoded, pdfName, user, firstName, lastName, email, jointRequest);
            String jsonString = ObjectMapperUtil.writeToJsonString(signRequestPayload);
            log.info("Sign Request JSON :{}", jsonString);
            String signRequestResponse = postRequest(jsonString).block();
            log.info("****** Sign Request Response :{}", signRequestResponse);
            return signRequestResponse;
        } catch (Exception e) {
            log.error("Error occurred executing SignRequest", e);
        }
        return "";
    }

    //    @Override
    //    public boolean getSignedDocumentData(String docUUID) {
    //        String signRequestUrl = signRequestBaseUrl + DOCUMENTS + docUUID + "/";
    //        log.info("****** signRequestUrl :getSignedDocumentData: {} ", signRequestUrl);
    //        try {
    //            if (docUUID == null) {
    //                log.error("****** docUUID is null");
    //                return false;
    //            }
    //            String signedDocumentJSON = getRequestDocument(signRequestUrl).block();
    //            log.info("****** Signed Document Data JSON , {}", signedDocumentJSON);
    //            SignRequestDocumentResponse signRequestDocumentResponse = ObjectMapperUtil.readFromJson(
    //                signedDocumentJSON,
    //                SignRequestDocumentResponse.class
    //            );
    //            if (signRequestDocumentResponse.getSigningLog() == null) {
    //                signedDocumentJSON = getRequestDocument(signRequestUrl).block();
    //                log.info("****** signed Document JSON , {}", signedDocumentJSON);
    //                signRequestDocumentResponse = ObjectMapperUtil.readFromJson(signedDocumentJSON, SignRequestDocumentResponse.class);
    //            }
    //            irsAPIService.sendPayload(signRequestDocumentResponse);
    //        } catch (IOException e) {
    //            log.error("****** Error occurred converting signedDocumentJSON");
    //        }
    //        return true;
    //    }

    private SignRequestPayload buildRequest(
        byte[] encodedContent,
        String pdfName,
        AdminUserDTO user,
        String taxPayer2FirstName,
        String taxPayer2LastName,
        String taxPayer2Email,
        String jointRequest
    ) {
        log.info("****** Re-direct Url****** :{}", redirectUrl);
        String urlToRedirectOnceSigned = redirectUrl + REDIRECT_URL_SIGNED;
        String urlToRedirectIfNotSigned = redirectUrl + REDIRECT_URL_NOT_SIGNED;
        List<Signers> signers = new ArrayList<>();
        buildSigners(signers, jointRequest, user, taxPayer2FirstName, taxPayer2Email);
        return SignRequestPayloadBuilder
            .builder()
            .fromEmail(user.getEmail())
            .fromEmailName(user.getFirstName())
            .redirectUrl(urlToRedirectOnceSigned)
            .redirectUrlDeclined(urlToRedirectIfNotSigned)
            .fileFromContent(encodedContent)
            .eventsCallbackUrl(eventCallBackUrl)
            .fileFromContentName(pdfName)
            .disableDate(false)
            .signers(signers)
            .build();
    }

    private void buildSigners(
        List<Signers> signers,
        String jointRequest,
        AdminUserDTO user,
        String taxPayer2FirstName,
        String taxPayer2Email
    ) {
        Signers firstSigner = new Signers(user.getEmail(), user.getFirstName(), user.getLastName(), user.getEmail(), 0);
        Signers secondSigner = new Signers(taxPayer2Email, taxPayer2FirstName, "", 1);
        if (StringUtils.equals(jointRequest, YES)) {
            signers.add(firstSigner);
            signers.add(secondSigner);
        } else {
            signers.add(firstSigner);
        }
    }

    private Mono<String> postRequest(String reqBody) {
        String signRequestUrl = signRequestBaseUrl + signRequestCreateUrl;
        return webClient
            .post()
            .uri(signRequestUrl)
            .headers(httpHeaders -> {
                httpHeaders.add("Authorization", "TOKEN " + signRequestToken);
                httpHeaders.add("Content-Type", "application/json");
            })
            .body(Mono.just(reqBody), String.class)
            .retrieve()
            .bodyToMono(String.class);
    }

    private Mono<String> getRequestDocument(String getDocumentUrl) {
        return webClient
            .get()
            .uri(getDocumentUrl)
            .headers(httpHeaders -> {
                httpHeaders.add("Authorization", "TOKEN " + signRequestToken);
                httpHeaders.add("Content-Type", "application/json");
            })
            .retrieve()
            .bodyToMono(String.class);
    }
}
