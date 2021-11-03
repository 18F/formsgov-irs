package gov.gsa.forms.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Form {

    private String id;
    private String name;
    private String data;
    private String hash;

    public Form(String id, String name, String data, String hash) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.hash = hash;
    }
}
