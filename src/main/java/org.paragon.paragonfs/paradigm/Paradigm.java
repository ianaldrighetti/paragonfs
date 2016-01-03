package org.paragon.paragonfs.paradigm;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.paragon.paragonfs.archetype.Archetype;
import org.paragon.paragonfs.data.type.DataType;
import org.paragon.paragonfs.data.type.DataTypeIF;
import org.paragon.paragonfs.exception.ParagonFSException;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A Paradigm is as close to a file as you can get with ParagonFS. It represents a collection of key/value pairs.
 */
public class Paradigm
{
	public static final String PARADIGM_KEY_TYPE = "type";
	public static final String PARADIGM_KEY_VALUE = "value";
	public static final String DATA = "data";

	/**
	 * The {@link Archetype} this Paradigm belongs to.
	 */
	private final Archetype archetype;

	/**
	 * The file which indicates where this Paradigm resides on disk.
	 */
	private final File file;

	/**
	 * The unique identifier for this Paradigm.
	 */
	private final String id;

	/**
	 * A {@link ReadWriteLock} used to prevent concurrency issues.
	 */
	private final ReadWriteLock lock;

	/**
	 * This is the object that this Paradigm represents.
	 */
	private Map<String, Object> object;

	/**
	 * Initializes the {@link Paradigm}.
	 *
	 * @param archetype  The archetype this Paradigm belongs to.
	 * @param file       The file which this Paradigm represents.
	 * @param paradigmId The ID of the Paradigm.
	 */
	public Paradigm(final Archetype archetype, final File file, final String paradigmId)
	{
		this.archetype = archetype;
		this.file = file;
		this.id = paradigmId;
		this.lock = new ReentrantReadWriteLock(true);
	}

	/**
	 * Returns the {@link Archetype} in which this {@link Paradigm} resides.
	 *
	 * @return The {@link Archetype} in which this Paradigm resides.
	 */
	public Archetype getArchetype()
	{
		return archetype;
	}

	/**
	 * Returns the {@link File} this {@link Paradigm} represents.
	 *
	 * @return The instance of {@link File}.
	 */
	public File getFile()
	{
		return new File(file.getAbsolutePath());
	}

	/**
	 * Returns the unique identifier for this {@link Paradigm}.
	 *
	 * @return The Paradigm's unique identifier.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the value of a single key on the Paradigm.
	 *
	 * @param key   The key to set the value on.
	 * @param value The value to set the key to.
	 */
	public void setValue(final String key, final DataTypeIF value) throws ParagonFSException
	{
		final Map<String, DataTypeIF> values = new LinkedHashMap<>();
		values.put(key, value);

		setValues(values);
	}

	/**
	 * Sets the values of multiple keys on the Paradigm.
	 *
	 * @param values A map of keys and their values to set.
	 */
	public void setValues(final Map<String, DataTypeIF> values) throws ParagonFSException
	{
		// TODO validate keys are not empty, then write lock, update values, flush/sync.
		validate(values);

		// Obtain a write lock.
		lock.writeLock().lock();

		try
		{
			refresh();

			if (object == null)
			{
				object = getEmptyObject();
			}

			@SuppressWarnings("unchecked")
			final Map<String, Object> data = (Map<String, Object>) object.get(DATA);
			for (final Map.Entry<String, DataTypeIF> entry : values.entrySet())
			{
				object.put(entry.getKey(), entry.getValue());
			}

			updateMetaData();
			flush();
		}
		catch (final IOException e)
		{
			throw new ParagonFSException("An error occurred while refreshing the object.", e);
		}
		finally
		{
			lock.writeLock().unlock();
		}
	}

	/**
	 * Ensures that the map is not null or empty, along with ensuring there are no empty keys.
	 *
	 * @param values The map to validate.
	 */
	private void validate(final Map<String, DataTypeIF> values)
	{
		if (values == null)
		{
			throw new IllegalArgumentException("The values map must not be null.");
		}
		else if (values.isEmpty())
		{
			throw new IllegalArgumentException("The values map must not be empty.");
		}

		int index = 0;
		for (final Map.Entry<String, DataTypeIF> entry : values.entrySet())
		{
			if (StringUtils.isNotBlank(entry.getKey()))
			{
				index++;
				continue;
			}

			throw new IllegalArgumentException("The " + (index + 1) + " entry of the map has an empty key.");
		}
	}

	/**
	 * Returns an empty object, to be used when the file has not been created yet.
	 *
	 * @return An empty object with the version, timestamp and data maps.
	 */
	private Map<String, Object> getEmptyObject()
	{
		final Map<String, Object> object = new LinkedHashMap<>();

		// A version of 0, it will be incremented anyways.
		object.put("version", 0);

		// Add the map with the created timestamp only -- updated will be overwritten as well.
		final Map<String, Object> timestamp = new LinkedHashMap<>();
		timestamp.put("created", Calendar.getInstance());
		object.put("timestamp", timestamp);

		// Then an empty map for data.
		object.put("data", new LinkedHashMap<>());

		return object;
	}

	/**
	 * When invoked this method will increment the version number of the object and update the updated timestamp to now.
	 */
	private void updateMetaData()
	{
		final Integer version = (Integer) object.get("version");

		// Increment the version as appropriate.
		if (version == null) {
			object.put("version", 1);
		}
		else {
			object.put("version", version + 1);
		}

		@SuppressWarnings("unchecked")
		final Map<String, Object> timestamp = (Map<String, Object>) object.get("timestamp");
		timestamp.put("updated", Calendar.getInstance());
	}

	/**
	 * This will flush the current state of the object onto disk. This method uses {@link FileOutputStream} and then
	 * invokes {@link java.nio.channels.FileChannel#force(boolean)} and {@link FileDescriptor#sync()} to ensure the
	 * file has been written to disk.
	 *
	 * @throws IOException
	 */
	private void flush() throws IOException
	{
		FileOutputStream stream = null;
		try
		{
			// Open the file stream and write the object.
			stream = new FileOutputStream(file, false);
			stream.write(getObjectMapper().writeValueAsBytes(object));

			stream.getChannel().force(true);
			stream.getFD().sync();
		}
		finally {
			if (stream == null) {
				//noinspection ReturnInsideFinallyBlock
				return;
			}

			stream.close();
		}
	}

	/**
	 * Returns a set of all the keys in the Paradigm.
	 *
	 * @return Returns a set of all keys in the Paradigm.
	 *
	 * @throws ParagonFSException
	 */
	public Set<String> keySet() throws ParagonFSException
	{
		lock.readLock().lock();

		try
		{
			refresh();

			return new LinkedHashSet<>(object.keySet());
		}
		catch (final IOException e)
		{
			throw new ParagonFSException("An error occurred while refreshing the object.", e);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * Returns the value of the key within the Paradigm.
	 *
	 * @param key The key to retrieve, case-insensitive.
	 * @return The value of the key, or null if the key does not exist in this Paradigm. <strong>Note:</strong> The
	 * value returned may not be null but the {@link DataTypeIF#getValue()} may also return null. This means
	 * that the key was defined, but no value was set.
	 *
	 * @throws ParagonFSException Thrown if an error occurs while trying to read from the file system.
	 */
	public DataTypeIF getValue(final String key) throws ParagonFSException
	{
		if (key == null)
		{
			throw new IllegalArgumentException("The key must not be null.");
		}

		lock.readLock().lock();

		try
		{
			// Ensure the object is loaded.
			refresh();

			// Make sure the object isn't empty.
			if (object == null || !object.containsKey(DATA))
			{
				return null;
			}

			// Now get the value for the key, if any.
			@SuppressWarnings("unchecked")
			final Map<String, Object> value = (Map<String, Object>) ((Map<String, Object>) object.get(DATA)).get(key.toLowerCase());
			if (value == null || !value.containsKey(PARADIGM_KEY_TYPE))
			{
				return null;
			}

			final DataTypeIF dataTypeIF = getNewTypeInstance(value.get(PARADIGM_KEY_TYPE).toString());
			dataTypeIF.setValue(value.get(PARADIGM_KEY_VALUE));

			return dataTypeIF;
		}
		catch (final IOException e)
		{
			throw new ParagonFSException("An error occurred while refreshing the object.", e);
		}
		finally
		{
			lock.readLock().unlock();
		}
	}

	/**
	 * This will load the object from the file into the map if the object is null.
	 *
	 * @throws IOException
	 */
	private void refresh() throws IOException
	{
		if (object != null)
		{
			return;
		}
		else if (!file.exists())
		{
			return;
		}

		object = getObjectMapper().readValue(file, getMapTypeReference());
	}

	/**
	 * Returns an instance of an {@link ObjectMapper}.
	 *
	 * @return An object mapper.
	 */
	private ObjectMapper getObjectMapper()
	{
		final JsonFactory jsonFactory = new JsonFactory();

		return new ObjectMapper(jsonFactory);
	}

	/**
	 * Returns a {@link TypeReference} to be used with an {@link ObjectMapper}.
	 *
	 * @return {@link TypeReference}
	 */
	private TypeReference<Map<String, Object>> getMapTypeReference()
	{
		return new TypeReference<Map<String, Object>>()
		{
		};
	}

	/**
	 * Returns a new instance of the specified class {@link DataTypeIF}.
	 *
	 * @param className The name of the class to create a new instance of.
	 * @return The {@link DataTypeIF} implementation.
	 */
	private DataTypeIF getNewTypeInstance(final String className) throws ParagonFSException
	{
		try
		{
			return (DataTypeIF) Class.forName(className).newInstance();
		}
		catch (IllegalAccessException e)
		{
			throw new ParagonFSException("IllegalAccessException", e);
		}
		catch (InstantiationException e)
		{
			throw new ParagonFSException("Unable to create an instance of the class.", e);
		}
		catch (ClassNotFoundException e)
		{
			throw new ParagonFSException("The class was not found.", e);
		}
	}
}
