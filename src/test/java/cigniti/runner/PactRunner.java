package cigniti.runner;

import org.testng.annotations.AfterSuite;
import cigniti.utils.ReportGeneration;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = "src/test/java/cigniti/features", tags = { "not @ignore" }, monochrome = true, plugin = {
		"pretty", "html:target/cucumber-report/pactresult", "json:target/cucumber-report/pactresult.json",
		"rerun:target/pactrerun.txt" }, glue = { "/cigniti/steps" }

)
public class PactRunner extends AbstractTestNGCucumberTests {
	
	@AfterSuite
	public void afterSuite() {
		ReportGeneration generateReport = new ReportGeneration();

		generateReport.generateSummaryReport();
	}

}
