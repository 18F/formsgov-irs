package gov.gsa.forms.web.rest.vm;

import gov.gsa.forms.payload.SignRequestDocumentResponse;
import gov.gsa.forms.service.IRSAPIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("send-irs-payload")
@Slf4j
public class IRSPayloadResource {

    public static final String SIGNED = "signed";
    private final IRSAPIService irsAPIService;

    public IRSPayloadResource(IRSAPIService irsAPIService) {
        this.irsAPIService = irsAPIService;
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> sendIrsPayload(@RequestBody SignRequestDocumentResponse documentResponse) {
        log.info("Sign request event call back response: {}", documentResponse);
        if (StringUtils.equals(SIGNED, documentResponse.getEventType())) {
            boolean response = irsAPIService.sendPayload(documentResponse);
            return new ResponseEntity<>(String.valueOf(response), HttpStatus.OK);
        }
        return new ResponseEntity<>("false", HttpStatus.OK);
    }
}
