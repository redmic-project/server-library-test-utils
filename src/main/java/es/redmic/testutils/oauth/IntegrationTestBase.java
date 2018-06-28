package es.redmic.testutils.oauth;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import es.redmic.utils.httpclient.HttpClient;

@ActiveProfiles("test")
public abstract class IntegrationTestBase {

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	protected MockMvc mockMvc;

	HttpClient client = new HttpClient();

	@Value("${oauth.server}")
	private String OAUTH_SERVER_PATH;

	// TEST USERS

	@Value("${test.user.ADMINISTRATOR}")
	private String ADMINISTRATOR_USER;

	@Value("${test.user.OAG}")
	private String OAG_USER;

	@Value("${test.user.COLLABORATOR}")
	private String COLLABORATOR_USER;

	@Value("${test.user.USER}")
	private String USER;

	@Value("${test.user.PASSWORD}")
	private String PASSWORD;

	@Before
	public void setUp() {

		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain)
				.build();
	}

	protected String getTokenAdministratorUser() {

		return obtainAccessToken(ADMINISTRATOR_USER, PASSWORD);
	}

	protected String getTokenOAGUser() {

		return obtainAccessToken(OAG_USER, PASSWORD);
	}

	protected String getTokenCollaboratorUser() {

		return obtainAccessToken(COLLABORATOR_USER, PASSWORD);
	}

	protected String getTokenUser() {

		return obtainAccessToken(USER, PASSWORD);
	}

	@SuppressWarnings("unchecked")
	private String obtainAccessToken(String username, String password) {

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("username", username);
		params.add("password", password);
		params.add("scope", "write");

		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", "Basic YXBwOnNlY3JldEtleQ==");

		Map<String, String> result = (Map<String, String>) client.post(OAUTH_SERVER_PATH + "/api/oauth/token", params,
				headers, java.util.HashMap.class);

		return result.get("access_token");
	}
}
