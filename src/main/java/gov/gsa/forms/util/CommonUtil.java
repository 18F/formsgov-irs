package gov.gsa.forms.util;

import gov.gsa.forms.service.dto.AdminUserDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {

    private CommonUtil() {}

    public static byte[] encodePdfToByte(URL url) {
        try (InputStream is = url.openStream(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] byteChunk = new byte[4096];
            int n;
            while ((n = is.read(byteChunk)) > 0) {
                baos.write(byteChunk, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("Error occurred executing SignRequest", e);
        }
        return new byte[0];
    }
}
