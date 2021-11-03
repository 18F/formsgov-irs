package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Attachment {

    private String id;
    private String storageLocation;
    private String storageId;
    private String name;
    private String data;
    private String hash;
}
