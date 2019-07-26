package cigniti.utils;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class MyListener implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		}

	@Override
	public void onTestFailure(ITestResult result) {
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		}

	@Override
	public void onStart(ITestContext context) {
			System.out.println("\n*************** " + context.getSuite().getName() + " Started ***************\n");
	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("\n*************** " + context.getSuite().getName() + " Finished ***************\n");
	}

}
