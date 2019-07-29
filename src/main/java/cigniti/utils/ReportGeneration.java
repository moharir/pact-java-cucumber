package cigniti.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;
import net.masterthought.cucumber.presentation.PresentationMode;

public class ReportGeneration {
	public void generateSummaryReport() {
		File reportOutputDirectory = new File("target");
		List<String> jsonFiles = new ArrayList<>();
		File inputFiles = new File(System.getProperty("user.dir") + "/target/cucumber-report/");

		for (File file : inputFiles.listFiles()) {
			if (file.toString().endsWith(".json")) {
				jsonFiles.add(file.toString());
			}
		}

		String buildNumber = "01";
		String projectName = "PACT Automation";

		Configuration configuration = new Configuration(reportOutputDirectory, projectName);
		configuration.addPresentationModes(PresentationMode.RUN_WITH_JENKINS);
		configuration.setBuildNumber(buildNumber);
		configuration.addClassifications("Platform", System.getProperty("os.name"));
		configuration.addClassifications("Platform", System.getProperty("user.name"));
		configuration.addClassifications("Branch", "Demo/1.0");

		ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
		Reportable result = reportBuilder.generateReports();
	}
}
