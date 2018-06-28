package es.redmic.testutils.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;

public abstract class DTOBaseTest<TDTO extends Object> {

	protected static final String NOT_NULL_MESSAGE_TEMPLATE = "{javax.validation.constraints.NotNull.message}",
			SIZE_MESSAGE_TEMPLATE = "{javax.validation.constraints.Size.message}",
			DATE_RANGE_MESSAGE_TEMPLATE = "{redmic.validation.constraints.ValidateDateRange.message}",
			PHONE_NUMBER_MESSAGE_TEMPLATE = "{redmic.validation.constraints.ValidatePhoneNumber.message}",
			EMAIL_MESSAGE_TEMPLATE = "{org.hibernate.validator.constraints.Email.message}",
			URL_MESSAGE_TEMPLATE = "{redmic.validation.constraints.ValidateUrl.message}",
			MIN_MESSAGE_TEMPLATE = "{javax.validation.constraints.Min.message}",
			MAX_MESSAGE_TEMPLATE = "{javax.validation.constraints.Max.message}",
			LANGUAGE_MESSAGE_TEMPLATE = "{redmic.validation.constraints.ValidateLanguage.message}";

	private Map<String, String> ignoreErrors = new HashMap<String, String>();

	protected static Validator validator;

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	protected void checkDTOHasNoError(TDTO dto) {

		Set<ConstraintViolation<TDTO>> set = validator.validate(dto);

		assertEquals(ignoreErrors.size(), set.size());
	}

	protected void checkDTOHasError(TDTO dto, String messageTemplate) {
		checkDTOHasError(dto, Arrays.asList(messageTemplate));
	}

	protected void checkDTOHasError(TDTO dto, List<String> messageTemplate) {

		Set<ConstraintViolation<TDTO>> set = validator.validate(dto);

		assertEquals(ignoreErrors.size() + messageTemplate.size(), set.size());

		List<ConstraintViolation<TDTO>> constraintViolations = getConstraintViolation(set);

		assertNotNull(constraintViolations);

		for (ConstraintViolation<TDTO> violation : constraintViolations)
			assertTrue(messageTemplate.contains(violation.getMessageTemplate()));
	}

	protected List<ConstraintViolation<TDTO>> getConstraintViolation(Set<ConstraintViolation<TDTO>> set) {

		List<ConstraintViolation<TDTO>> violantions = new ArrayList<>();

		for (ConstraintViolation<TDTO> violation : set) {

			if (!(ignoreErrors.containsKey(violation.getPropertyPath().toString())
					&& ignoreErrors.containsValue(violation.getMessageTemplate().toString()))) {
				violantions.add(violation);
			}
		}
		return violantions;
	}

	public Map<String, String> getIgnoreErrors() {
		return ignoreErrors;
	}

	public void setIgnoreErrors(Map<String, String> ignoreErrors) {
		this.ignoreErrors = ignoreErrors;
	}

	@SuppressWarnings("serial")
	public void addIgnoreError(String error, String message) {
		this.ignoreErrors = new HashMap<String, String>() {
			{
				put(error, message);
			}
		};
	}

	protected String generateString(Integer length) {

		String stringGenerated = null;
		for (int i = 0; i <= length; i++)
			stringGenerated += "a";

		return stringGenerated;
	}
}
