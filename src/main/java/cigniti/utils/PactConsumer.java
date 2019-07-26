package cigniti.utils;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslRequestWithPath;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import java.util.Map;
import java.util.Set;
import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;


public class PactConsumer {
    RequestResponsePact pact;
    Object globalBody = null;
    Map<String, String> globalHeaders;
    PactDslRequestWithPath partialPact;
    PactDslResponse partialPactResponse;
    Map<String, String> headers;
    Map<String, String> currentMessage = null;
    String method = "method";
    
    /**
     * A generic method To build the PACT for the request, headers and the response that are required
     *
     * @param requestBody    -> Request Body
     * @param requestHeaders -> Request Headers
     * @param params         -> Params that are passed
     * @param responseBody   -> Response Body that is obtained
     * @param status         -> Status of the response
     */
    public void buildPactForAService(Object requestBody, Map<String, String> requestHeaders, Map<String,
            String> params, String path, Map<String, String> responseHeaders, PactDslJsonBody responseBody, int status) {
        globalBody = requestBody;
        globalHeaders = requestHeaders;

        if (params.get(method).equalsIgnoreCase("GET") || params.get(method).equalsIgnoreCase("DELETE")) {
            globalBody = null;
        }

        partialPact = ConsumerPactBuilder
                .consumer((params.get("consumerName")))
                .hasPactWith(params.get("providerName"))
                .given(params.get("interactionName"))
                .uponReceiving(params.get("interactionName"))
                .method(params.get(method))
                .path(path);

        if (requestHeaders != null)
            partialPact = partialPact.headers(requestHeaders);

        if (requestBody != null)
            partialPact = partialPact.body(requestBody.toString());

        partialPactResponse = partialPact.willRespondWith().status(status);

        if (responseHeaders != null)
            partialPactResponse = partialPactResponse.headers(responseHeaders);

        if (responseBody != null)
            partialPactResponse = partialPactResponse.body(responseBody);

        pact = partialPactResponse.toPact();
        PactState.contractIdentifier = null;
    }

    /**
     * A generic method To publish the PACT for the request, headers and the response that are required
     * This uses the core java libraries provided by PACT JVM for consumer
     * @param fullPath       -> Request Body
     * @param headers        -> Request Headers
     * @param pactParameters -> Params that are passed
     */

    public void publishPactAndVerifyForService(String fullPath, Map<String, String> headers, Map<String, String> pactParameters) {
        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, mockServer -> {
            String uri;
            if (fullPath == null) {
                uri = mockServer.getUrl();
            } else {
                uri = mockServer.getUrl() + "/" + fullPath;
            }

            Request req =
                    (pactParameters.get(method).equals("GET") ? Request.Get(uri) :
                            (pactParameters.get(method).equals("POST") ? Request.Post(uri) :
                                    (pactParameters.get(method).equals("PUT") ? Request.Put(uri) : Request.Delete(uri))));

            if (headers != null) {
                Set<String> headerKeys = headers.keySet();
                for (String headerKey : headerKeys)
                    req = req.setHeader(headerKey, headers.get(headerKey));
            }

            if (globalBody != null)
                req = req.bodyString(globalBody.toString(), APPLICATION_JSON);
            req
                    .execute()
                    .handleResponse(httpResponse -> {
                        String content = EntityUtils.toString(httpResponse.getEntity());
                        if (httpResponse.getStatusLine().getStatusCode() == 500) {
                            Map map = new ObjectMapper().readValue(content, Map.class);
                            Assert.fail((String) map.get("error"));
                        }
                        return null;
                    });
        });
        if (result instanceof PactVerificationResult.Error) {
            Throwable error = ((PactVerificationResult.Error) result).getError();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
        currentMessage = null;
        Assert.assertEquals(PactVerificationResult.Ok.INSTANCE, result);
    }
}
