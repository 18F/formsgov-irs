package gov.gsa.forms;

import gov.gsa.forms.FormsGovApp;
import gov.gsa.forms.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { FormsGovApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
