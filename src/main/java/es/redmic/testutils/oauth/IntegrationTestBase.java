package es.redmic.testutils.oauth;

/*-
 * #%L
 * Test utils
 * %%
 * Copyright (C) 2019 REDMIC Project / Server
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

import es.redmic.testutils.kafka.KafkaBaseIntegrationTest;
import es.redmic.utils.httpclient.HttpClient;

@ActiveProfiles("test")
public abstract class IntegrationTestBase extends KafkaBaseIntegrationTest {

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
