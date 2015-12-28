package org.paragon.paragonfs.data.type;

/**
 * An interface that must be implemented by all data types used for storing data.
 */
public interface DataTypeIF
{
	/**
	 * Set's the value of the object.
	 *
	 * @param value The value to set.
	 */
	void setValue(final Object value);

	/**
	 * Returns the value of the object.
	 *
	 * @return The object's value.
	 */
	Object getValue();
}
