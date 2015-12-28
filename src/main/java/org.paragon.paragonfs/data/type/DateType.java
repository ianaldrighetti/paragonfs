package org.paragon.paragonfs.data.type;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple date type.
 */
public class DateType extends DataType
{
	private Date value;

	public void setValue(final Object value)
	{
		if (value instanceof Date)
		{
			this.value = new Date(((Date) value).getTime());
		}
		else if (value instanceof Calendar)
		{
			this.value = ((Calendar) value).getTime();
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
