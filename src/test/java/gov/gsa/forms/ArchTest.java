package gov.gsa.forms;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("gov.gsa.forms");

        noClasses()
            .that()
            .resideInAnyPackage("gov.gsa.forms.service..")
            .or()
            .resideInAnyPackage("gov.gsa.forms.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..gov.gsa.forms.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
