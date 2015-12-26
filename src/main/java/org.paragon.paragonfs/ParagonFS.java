package org.paragon.paragonfs;

import org.apache.commons.lang3.ArrayUtils;
import org.paragon.paragonfs.archetype.Archetype;
import org.paragon.paragonfs.paradigm.Paradigm;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link ParagonFS} represents the Paragon file system.
 */
public class ParagonFS
{
	/**
	 * The directory which is where the instance of this ParagonFS stores all data.
	 */
	private final File dir;

	/**
	 * The archetype lock used when interacting with archetypes.
	 */
	private final Object archetypeLock = new Object();

	/**
	 * A map of {@link Archetype}'s, where the key is the archetype name, lowercase.
	 */
	private final Map<String, Archetype> archetypes = new LinkedHashMap<>();

	/**
	 * Initializes the ParagonFS with the path to where it stores and retrieves data.
	 *
	 * @param path The path to where ParagonFS will store and retrieve it's data.
	 * @throws IllegalArgumentException Thrown if the file path does not exist or is not a directory.
	 */
	public ParagonFS(final String path)
	{
		if (path == null) {
			throw new IllegalArgumentException("The path must not be null.");
		}

		this.dir = new File(path);
		validateAndInitialize();
	}

	/**
	 * Initializes the ParagonFS with the path to where it stores and retrieves data.
	 *
	 * @param dir The path to where ParagonFS will store and retrieve it's data.
	 * @throws IllegalArgumentException Thrown if the file path does not exist or is not a directory.
	 */
	public ParagonFS(final File dir)
	{
		this.dir = dir;

		validateAndInitialize();
	}

	/**
	 * Validates the dir field, ensuring it isn't null and points to a directory. This will also load up all known
	 * archetypes as well.
	 */
	private void validateAndInitialize()
	{
		if (dir == null) {
			throw new IllegalArgumentException("The directory must not be null.");
		}
		else if (!dir.exists()) {
			throw new IllegalArgumentException("The path does not exist.");
		}
		else if (!dir.isDirectory()) {
			throw new IllegalArgumentException("The path does not resolve to a directory.");
		}

		final File[] files = dir.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return;
		}

		for (final File file : files)
		{
			if (!file.isDirectory()) {
				continue;
			}

			archetypes.put(file.getName().toLowerCase(), new Archetype(file));
		}
	}

	/**
	 * Creates a new {@link Archetype}, which is the equivalent to a directory, which contains {@link Paradigm}'s.
	 *
	 * @param name The name of the archetype to create, names are case-insensitive.
	 * @return Returns the created {@link Archetype}.
	 */
	public Archetype create(final String name)
	{
		if (name == null) {
			throw new IllegalArgumentException("The archetype name must not be null.");
		}
		else if (name.contains("/") || name.contains("\\")) {
			throw new IllegalArgumentException("The archetype name must not contain a forward or backward slash.");
		}

		synchronized (archetypeLock)
		{
			final File archetypeDir = new File(this.dir, name);

			if (archetypeDir.exists()) {
				throw new IllegalArgumentException("The archetype already exists.");
			}
			else if (!archetypeDir.exists() && !archetypeDir.mkdir()) {
				throw new IllegalArgumentException("The archetype could not be created.");
			}

			final Archetype archetype = new Archetype(archetypeDir);
			archetypes.put(archetypeDir.getName().toLowerCase(), archetype);

			return archetype;
		}
	}

	/**
	 * Returns the {@link Archetype} if it exists.
	 *
	 * @param name The name of the archetype, case-insensitive.
	 * @return The {@link Archetype} or null if it does not exist.
	 */
	public Archetype get(final String name)
	{
		if (name == null) {
			throw new IllegalArgumentException("The archetype name must not be null.");
		}

		synchronized (archetypeLock)
		{
			return archetypes.get(name.toLowerCase());
		}
	}

	/**
	 * Deletes the {@link Archetype} if it exists and it's empty.
	 *
	 * @param name The name of the {@link Archetype} to remove.
	 */
	public void delete(final String name)
	{
		if (name == null) {
			throw new IllegalArgumentException("The archetype name must not be null.");
		}

		synchronized (archetypeLock)
		{
			final Archetype archetype = archetypes.get(name.toLowerCase());

			if (archetype == null) {
				throw new IllegalArgumentException("The archetype does not exist.");
			}

			// TODO delete and remove archetype from map.
		}
	}
}
