package es.redmic.testutils.documentation;

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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.redmic.models.es.common.query.dto.MetadataQueryDTO;
import es.redmic.models.es.common.query.dto.MgetDTO;
import es.redmic.models.es.common.query.dto.SimpleQueryDTO;
import es.redmic.testutils.oauth.IntegrationTestBase;

@ActiveProfiles("test")
public abstract class DocumentationViewBaseTest extends IntegrationTestBase {

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

	@Value("${controller.mapping.FILTER_SCHEMA}")
	protected String filterSchemaPath;

	protected final String SCHEME = "https";

	protected final Integer PORT = 443;

	protected static final Map<String, String> parametersDescription = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put("ids", "Identifiers of items that you wish retrieve");
			put("fields", "Fields to return in each item");
			put("text", "Text to search");
			put("from", "Pagination start");
			put("size", "Num of items to return");
			put("terms", "Terms for create query in server side");
			put("sorts", "Fields and type of sort");
			put("returnFields", "Fields to return in each item");
			put("suggest", "Terms for create a suggest query");
			put("suggest.text", "Text to search");
			put("suggest.searchFields", "Fields to search");
			put("suggest.size", "Num of items to return");
			put("regexp", "Regexp which apply to search");
			put("postFilter", "Terms for create facets queries");
			put("aggs", "Terms for create aggregations");
			put("dateLimits", "Date interval query");
			put("bbox", "Bounding box query");
			put("precision", "Precision radio of a point");
			put("qFlags", "Quality flag of a data");
			put("vFlags", "Value type");
			put("interval", "Query by time interval of a timeserie");
			put("z", "Query by z field");
			put("value", "Query by value");
		}
	};

	protected RestDocumentationResultHandler getSuggestParametersDescription() {
		return document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(parameterWithName("fields").description(parametersDescription.get("fields")),
						parameterWithName("text").description(parametersDescription.get("text")),
						parameterWithName("size").description(parametersDescription.get("size"))));
	}

	protected RestDocumentationResultHandler getSearchSimpleParametersDescription() {
		return document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestParameters(parameterWithName("fields").description(parametersDescription.get("fields")),
						parameterWithName("text").description(parametersDescription.get("text")),
						parameterWithName("from").description(parametersDescription.get("from")),
						parameterWithName("size").description(parametersDescription.get("size"))));
	}

	protected RestDocumentationResultHandler getMgetRequestDescription() {

		ConstrainedFields fields = new ConstrainedFields(MgetDTO.class);

		FieldDescriptor[] mget = new FieldDescriptor[] {
				fields.withPath("ids").description(parametersDescription.get("ids")),
				fields.withPath("fields").description(parametersDescription.get("fields")) };

		return document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()), requestFields(mget));
	}

	protected RestDocumentationResultHandler getSimpleQueryFieldsDescriptor() {

		ConstrainedFields fields = new ConstrainedFields(SimpleQueryDTO.class);

		return document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()), requestFields(getSimpleQueryFieldsDescriptor(fields)));
	}

	protected RestDocumentationResultHandler getMetadataQueryFieldsDescriptor() {

		ConstrainedFields fields = new ConstrainedFields(MetadataQueryDTO.class);

		return document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()), requestFields(getMetadataQueryFieldsDescriptor(fields)));
	}

	protected RestDocumentationResultHandler getDataQueryFieldsDescriptor() {

		ConstrainedFields fields = new ConstrainedFields(MetadataQueryDTO.class);

		return document("{class-name}/{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()), requestFields(getDataQueryFieldsDescriptor(fields)));
	}

	private List<FieldDescriptor> getDataQueryFieldsDescriptor(ConstrainedFields fields) {

		List<FieldDescriptor> fieldDescriptor = getMetadataQueryFieldsDescriptor(fields);

		fieldDescriptor.add(fields.withPath("dateLimits").description(parametersDescription.get("dateLimits")));
		fieldDescriptor.add(fields.withPath("bbox").description(parametersDescription.get("bbox")));
		fieldDescriptor.add(fields.withPath("precision").description(parametersDescription.get("precision")));
		fieldDescriptor.add(fields.withPath("qFlags").description(parametersDescription.get("qFlags")));
		fieldDescriptor.add(fields.withPath("vFlags").description(parametersDescription.get("vFlags")));
		fieldDescriptor.add(fields.withPath("interval").description(parametersDescription.get("interval")));
		fieldDescriptor.add(fields.withPath("z").description(parametersDescription.get("z")));
		fieldDescriptor.add(fields.withPath("value").description(parametersDescription.get("value")));

		return fieldDescriptor;
	}

	private List<FieldDescriptor> getMetadataQueryFieldsDescriptor(ConstrainedFields fields) {

		List<FieldDescriptor> fieldDescriptor = getSimpleQueryFieldsDescriptor(fields);

		fieldDescriptor.add(fields.withPath("postFilter").description(parametersDescription.get("postFilter")));
		fieldDescriptor.add(fields.withPath("aggs").description(parametersDescription.get("aggs")));

		return fieldDescriptor;
	}

	private List<FieldDescriptor> getSimpleQueryFieldsDescriptor(ConstrainedFields fields) {

		List<FieldDescriptor> fieldDescriptor = new ArrayList<FieldDescriptor>();
		fieldDescriptor.add(fields.withPath("from").description(parametersDescription.get("from")));
		fieldDescriptor.add(fields.withPath("size").description(parametersDescription.get("size")));
		fieldDescriptor.add(fields.withPath("terms").description(parametersDescription.get("terms")));
		fieldDescriptor.add(fields.withPath("sorts").description(parametersDescription.get("sorts")));
		fieldDescriptor.add(fields.withPath("returnFields").description(parametersDescription.get("returnFields")));
		fieldDescriptor.add(fields.withPath("text").description(parametersDescription.get("text")));
		fieldDescriptor.add(fields.withPath("suggest").description(parametersDescription.get("suggest")));
		fieldDescriptor.add(fields.withPath("suggest.text").ignored().optional());
		fieldDescriptor.add(fields.withPath("suggest.searchFields").ignored().optional());
		fieldDescriptor.add(fields.withPath("suggest.size").ignored().optional());
		fieldDescriptor.add(fields.withPath("regexp").description(parametersDescription.get("regexp")));
		return fieldDescriptor;
	}

	protected static class ConstrainedFields {

		private final ConstraintDescriptions constraintDescriptions;

		ConstrainedFields(Class<?> input) {
			this.constraintDescriptions = new ConstraintDescriptions(input);
		}

		protected FieldDescriptor withPath(String path) {
			return fieldWithPath(path).attributes(key("constraints").value(StringUtils
					.collectionToDelimitedString(this.constraintDescriptions.descriptionsForProperty(path), ". ")));
		}
	}
}
