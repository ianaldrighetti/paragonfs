package org.paragon.paragonfs.data.type;

/**
 * A simple integer type.
 */
public class IntegerType extends DataType
{
	private Integer value;

	public void setValue(Object value)
	{
		if (value instanceof Number)
		{
			this.value = ((Number) value).intValue();
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
