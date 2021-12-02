package gov.gsa.forms.service.impl;

import gov.gsa.forms.payload.SignRequestDocumentResponse;
import gov.gsa.forms.payload.SignRequestPayload;
import gov.gsa.forms.payload.Signers;
import gov.gsa.forms.payload.builder.SignRequestPayloadBuilder;
import gov.gsa.forms.service.IRSAPIService;
import gov.gsa.forms.service.SignRequestService;
import gov.gsa.forms.service.dto.AdminUserDTO;
import gov.gsa.forms.service.dto.SignRequestDTO;
import gov.gsa.forms.util.CommonUtil;
import gov.gsa.forms.util.ObjectMapperUtil;
import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.util.List;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
    public String executeSignRequest(String pdfUrl, String pdfName, Principal principal, String firstName, String lastName, String email) {
        try {
            URL url = new URL(pdfUrl);
            byte[] encoded = CommonUtil.encodePdfToByte(url);
            AdminUserDTO user = (AdminUserDTO) request.getSession().getAttribute(USER);
            user.setFormName(pdfName);
            user.setTaxpayer2FirstName(firstName);
            user.setTaxpayer2LastName(lastName);
            user.setTaxpayer2Email(email);
            request.getSession().setAttribute("updated-user", user);
            SignRequestPayload signRequestPayload = buildRequest(encoded, pdfName, user, firstName, lastName, email);
            String jsonString = ObjectMapperUtil.writeToJsonString(signRequestPayload);
            log.info("Sign Request JSON :{}", jsonString);
            String signRequestResponse = postRequest(jsonString).block();
            log.info("****** Sign Request Response :{}", signRequestResponse);
            //            SignRequestDocumentResponse response = ObjectMapperUtil.readFromJson(signRequestResponse, SignRequestDocumentResponse.class);
            //            Assert.assertNotNull(response);
            //            SignRequestDTO signRequestDTO = new SignRequestDTO(response.getDocument());
            //            request.getSession().setAttribute("signRequestDTO", signRequestDTO);
            return signRequestResponse;
        } catch (Exception e) {
            log.error("Error occurred executing SignRequest", e);
        }
        return "";
    }

    @Override
    public boolean getSignedDocumentData(String docUUID) {
        String signRequestUrl = signRequestBaseUrl + DOCUMENTS + docUUID + "/";
        log.error("****** signRequestUrl :getSignedDocumentData: {} ", signRequestUrl);
        //        SignRequestDTO signRequestDTO = (SignRequestDTO) request.getSession().getAttribute("signRequestDTO");
        try {
            if (docUUID == null) {
                log.error("****** docUUID is null");
                return false;
            }
            String signedDocumentJSON = getRequestDocument(signRequestUrl).block();
            log.info("****** signed Document JSON , {}", signedDocumentJSON);
            SignRequestDocumentResponse signRequestDocumentResponse = ObjectMapperUtil.readFromJson(
                signedDocumentJSON,
                SignRequestDocumentResponse.class
            );
            if (signRequestDocumentResponse.getSigningLog() == null) {
                signedDocumentJSON = getRequestDocument(signRequestDTO.getSignRequestDocumentUrl()).block();
                log.info("****** signed Document JSON , {}", signedDocumentJSON);
                signRequestDocumentResponse = ObjectMapperUtil.readFromJson(signedDocumentJSON, SignRequestDocumentResponse.class);
            }
            irsAPIService.sendPayload(signRequestDocumentResponse);
        } catch (IOException e) {
            log.error("****** Error occurred converting signedDocumentJSON");
        }
        return true;
    }

    private SignRequestPayload buildRequest(
        byte[] encodedContent,
        String pdfName,
        AdminUserDTO user,
        String firstName,
        String lastName,
        String email
    ) {
        log.info("****** Re-direct Url****** :{}", redirectUrl);
        String urlToRedirectOnceSigned = redirectUrl + REDIRECT_URL_SIGNED;
        String urlToRedirectIfNotSigned = redirectUrl + REDIRECT_URL_NOT_SIGNED;
        Signers firstSigner = new Signers(user.getEmail(), user.getFirstName(), user.getLastName(), user.getEmail(), 0);
        Signers secondSigner = new Signers(email, firstName, "", 1);
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
            .signers(List.of(firstSigner, secondSigner))
            //            .signers(List.of(firstSigner))
            .build();
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
