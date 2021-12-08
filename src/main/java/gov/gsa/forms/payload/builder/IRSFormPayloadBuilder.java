package gov.gsa.forms.payload.builder;

import gov.gsa.forms.payload.*;
import java.util.List;

public class IRSFormPayloadBuilder {

    private final IRSFormPayload payload;

    private IRSFormPayloadBuilder() {
        payload = new IRSFormPayload();
    }

    public static IRSFormPayloadBuilder builder() {
        return new IRSFormPayloadBuilder();
    }

    public IRSFormPayload build() {
        return payload;
    }

    public IRSFormPayloadBuilder token(String token) {
        payload.setToken(token);
        return this;
    }

    public IRSFormPayloadBuilder signatures(List<Signature> signatures) {
        payload.setSignatures(signatures);
        return this;
    }

    public IRSFormPayloadBuilder form(Form form) {
        payload.setForm(form);
        return this;
    }

    public IRSFormPayloadBuilder signingLog(SigningLogPayload signingLog) {
        payload.setSigningLog(signingLog);
        return this;
    }

    public IRSFormPayloadBuilder attachments(List<Attachment> attachments) {
        payload.setAttachments(attachments);
        return this;
    }
}
