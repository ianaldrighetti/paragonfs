package org.paragon.paragonfs.data.type;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests for {@link IntegerType}.
 */
public class IntegerTypeTest
{
	private final int INTEGER_VALUE = 2345;

	private IntegerType type;

	@Before
	public void setUp()
	{
		type = new IntegerType();
	}

	@Test
	public void testSetValueWithNumber()
	{
		type.setValue(new BigDecimal(INTEGER_VALUE));

		assertThat((int) type.getValue(), equalTo(INTEGER_VALUE));
	}

	@Test
	public void testSetValueWithString()
	{
		type.setValue(Integer.toString(INTEGER_VALUE));

		assertThat((int) type.getValue(), equalTo(INTEGER_VALUE));
	}

	@Test
	public void testSetValueWithInvalidString()
	{
		type.setValue("NotAnInteger");

		assertThat(type.getValue(), equalTo(null));
	}
}
