package acceptance;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features={"src/test/specs/API-v2", "src/test/specs/API-v3"}
        , glue={"steps"})

public class AcceptanceTests {
}
