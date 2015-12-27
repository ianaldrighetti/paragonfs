package org.paragon.paragonfs.paradigm;

import org.paragon.paragonfs.ParagonFS;
import org.paragon.paragonfs.archetype.Archetype;

import java.io.File;

/**
 * A Paradigm is as close to a file as you can get with ParagonFS. It represents a collection of key/value pairs.
 */
public class Paradigm
{
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
	 * Initializes the {@link Paradigm}.
	 *
	 * @param archetype The archetype this Paradigm belongs to.
	 * @param file The file which this Paradigm represents.
	 * @param paradigmId The ID of the Paradigm.
	 */
	public Paradigm(final Archetype archetype, final File file, final String paradigmId)
	{
		this.archetype = archetype;
		this.file = file;
		this.id = paradigmId;
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
}
