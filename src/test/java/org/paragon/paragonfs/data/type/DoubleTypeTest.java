package org.paragon.paragonfs.data.type;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;

/**
 * Tests for {@link DoubleType}.
 */
public class DoubleTypeTest
{
	private static final double DOUBLE_VALUE = 1234l;

	private DoubleType type;

	@Before
	public void setUp()
	{
		type = new DoubleType();
	}

	@Test
	public void testSetValueWithNull()
	{
		type.setValue(null);

		assertThat(type.getValue(), equalTo(null));
	}

	@Test
	public void testSetValueWithBigDecimal()
	{
		type.setValue(new BigDecimal(DOUBLE_VALUE));

		assertThat(type.getValue(), not(equalTo(null)));
		assertThat((double) type.getValue(), equalTo(DOUBLE_VALUE));
	}

	@Test
	public void testSetValueWithString()
	{
		type.setValue(Double.toString(DOUBLE_VALUE));

		assertThat(type.getValue(), not(equalTo(null)));
		assertThat((double) type.getValue(), equalTo(DOUBLE_VALUE));
	}

	@Test
	public void testSetValueWithInvalidString()
	{
		type.setValue(Double.toString(DOUBLE_VALUE));

		assertThat(type.getValue(), not(equalTo(null)));
		assertThat((double) type.getValue(), equalTo(DOUBLE_VALUE));

		type.setValue("NotADouble");

		assertThat(type.getValue(), equalTo(null));
	}
}
