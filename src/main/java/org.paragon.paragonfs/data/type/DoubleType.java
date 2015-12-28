package org.paragon.paragonfs.data.type;

/**
 * A simple double type.
 */
public class DoubleType extends DataType
{
	private Double value;

	public void setValue(Object value)
	{
		if (value instanceof Number)
		{
			this.value = ((Number) value).doubleValue();
		}
		else
		{
			this.value = null;
		}
	}

	public Object getValue()
	{
		return value;
	}
}
