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
		else if (value != null)
		{
			try {
				this.value = Double.parseDouble(value.toString());
			}
			catch (final Exception e) {
				this.value = null;
			}
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
