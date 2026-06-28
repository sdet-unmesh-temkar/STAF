package com.solstice.staf.interframework.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import de.kabeldeutschland.jaxws.client.endpoint.EndpointState;
import de.kabeldeutschland.wss.testsuitedatareport.ObjectMapperProvider;
import de.kabeldeutschland.wss.testsuitedatareport.client.TestSuiteDataReportClient;
import de.kabeldeutschland.wss.testsuitedatareport.client.TestSuiteDataReportClientImpl;
import generalutilities.EnvironmentDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mediatype.MessageResolver;
import org.springframework.hateoas.mediatype.hal.CurieProvider;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * This class is used to test suite data report client configuration.
 */
@Configuration
public class TestSuiteDataReportClientConfiguration {

    private final Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    private static final Logger LOG = LoggerFactory.getLogger(TestSuiteDataReportClientConfiguration.class);
    private final String baseUrl = environment.get("TestSuiteDataReportClient_Service_Credentials/URI");
    private final String username = environment.get("TestSuiteDataReportClient_Service_Credentials/Username");
    private final String password = environment.get("TestSuiteDataReportClient_Service_Credentials/Password");
    private static final String BEAN_PREFIX = "de.kabeldeutschland.wss.testsuitedatareport.TestSuiteDataReport";
    private static final int TIMEOUTMS = 60000;

    @Bean(name = BEAN_PREFIX + "ClientHttpRequestFactory")
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(TIMEOUTMS);
        factory.setConnectTimeout(TIMEOUTMS);
        return factory;
    }

    @Bean(name = BEAN_PREFIX + "jsonObjectMapper")
    public ObjectMapper jsonObjectMapper() {
        ObjectMapper objectMapper = ObjectMapperProvider.getObjectMapper();

        Jackson2ObjectMapperBuilder
                .json()
                .modules(new Jackson2HalModule())
                .handlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(new AnnotationLinkRelationProvider(), CurieProvider.NONE, MessageResolver.DEFAULTS_ONLY))
                .configure(objectMapper);

        return objectMapper;
    }

    @Bean(name = BEAN_PREFIX + "ClientRestTemplate")
    public RestTemplate restTemplate(
            @Qualifier(BEAN_PREFIX + "ClientHttpRequestFactory") ClientHttpRequestFactory clientHttpRequestFactory,
            @Qualifier(BEAN_PREFIX + "jsonObjectMapper") ObjectMapper jsonObjectMapper) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        MappingJackson2HttpMessageConverter jacksonConverter = getJacksonConverterFromRestTemplate(restTemplate);
        jacksonConverter.setObjectMapper(jsonObjectMapper);

        Preconditions.checkArgument(!(Strings.isNullOrEmpty(username) ^ Strings.isNullOrEmpty(password)),
                "Either provide no username/password or both!");
        Preconditions.checkArgument((!"PROVIDED_LOCALLY".equalsIgnoreCase(password) && !"PROVIDED-LOCALLY".equalsIgnoreCase(password)),
                "Password not yet replaced. Check configuration! - Aborting to prevent account lockout.");

        LOG.info("restTemplate() - configured: {} with timeout: {}ms (default: {}ms) for user: {}", baseUrl, TIMEOUTMS,
                "6000", username);

        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        return restTemplate;
    }


    @Bean(name = BEAN_PREFIX + "Client")
    public TestSuiteDataReportClient clientInstance(@Qualifier(BEAN_PREFIX + "ClientRestTemplate") RestTemplate restTemplate,
                                                    @Qualifier(BEAN_PREFIX + "ClientEndpointState") EndpointState endpointState) {
        return new TestSuiteDataReportClientImpl(baseUrl, restTemplate, endpointState);
    }

    @Bean(name = BEAN_PREFIX + "ClientEndpointState")
    public EndpointState endpointState() {
        return new EndpointState(baseUrl, username);
    }


    private MappingJackson2HttpMessageConverter getJacksonConverterFromRestTemplate(RestTemplate restTemplate) {
        return restTemplate
                .getMessageConverters()
                .stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .map(MappingJackson2HttpMessageConverter.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find MappingJackson2HttpMessageConverter in RestTemplate"));
    }
}