package org.paragon.paragonfs.data.type;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests for {@link StringType}.
 */
public class StringTypeTest
{
	private static final String STRING_VALUE = "STRING VALUE";

	private StringType type;

	@Before
	public void setUp()
	{
		type = new StringType();
	}

	@Test
	public void testSetValue()
	{
		type.setValue(STRING_VALUE);

		assertThat(type.getValue(), not(equalTo(null)));
		assertThat(type.getValue().toString(), equalTo(STRING_VALUE));
	}
}
