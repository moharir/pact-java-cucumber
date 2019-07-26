package cigniti.runner;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.runner.RunWith;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import cigniti.utils.CignitiProperties;
import au.com.dius.pact.provider.junit.PactRunner;

@RunWith(PactRunner.class)
@Provider("ReqRes")
@au.com.dius.pact.provider.junit.loader.PactFolder("target/pacts")

/**
 * This will verify the pact created by a consumer This can be executed all
 * alone by calling JUnit runner (right click and run as JUnit) But we are
 * currently calling this from a BDD step definition to give a touch of BDD
 */
public class CignitiContractVerifierTestSuite {

	static String endPoint = new CignitiProperties("ReqRes").getProperty("URI");

	@TestTarget
	public static Target target = null;

	static{
		try {
			target = new HttpTarget(new URL(endPoint), true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	// Verify the pact contract by their states
	@State(value = "POST_user_details")
	public void verifyState() {
		//This verifies the state of post api
	}
}
