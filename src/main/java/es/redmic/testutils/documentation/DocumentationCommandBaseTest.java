package es.redmic.testutils.documentation;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.testutils.oauth.IntegrationTestBase;

@ActiveProfiles("test")
public abstract class DocumentationCommandBaseTest extends IntegrationTestBase {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	protected ObjectMapper mapper;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	protected FilterChainProxy springSecurityFilterChain;

	protected RestDocumentationResultHandler document = document("{class-name}/{method-name}",
			preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));

	protected MockMvc mockMvc;

	@Value("${controller.mapping.EDIT_SCHEMA}")
	protected String editSchemaPath;

	protected final String SCHEME = "https";

	protected final Integer PORT = 443;
}
