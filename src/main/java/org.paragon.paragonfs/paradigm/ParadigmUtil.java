package org.paragon.paragonfs.paradigm;

import org.apache.commons.lang3.StringUtils;
import org.paragon.paragonfs.ParagonFS;
import org.paragon.paragonfs.archetype.Archetype;
import org.paragon.paragonfs.exception.ParagonFSException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * A class with some utilities for {@link Paradigm}'s.
 */
public class ParadigmUtil
{
	/**
	 * A reference to the {@link ParagonFS} this utility instance is for.
	 */
	private final ParagonFS paragonFS;

	/**
	 * Initializes the {@link ParadigmUtil}.
	 *
	 * @param paragonFS The {@link ParagonFS}.
	 */
	public ParadigmUtil(final ParagonFS paragonFS)
	{
		this.paragonFS = paragonFS;
	}

	/**
	 * Determines whether the specified {@link Paradigm}, based on it's unique identifier, exists.
	 *
	 * @param paradigmId The {@link Paradigm}'s unique identifier.
	 * @return Returns true if the {@link Paradigm} exists, false otherwise.
	 */
	public boolean exists(final String paradigmId)
	{
		for (final Archetype archetype : paragonFS.list())
		{
			if (!archetype.exists(paradigmId))
			{
				continue;
			}

			return true;
		}

		return false;
	}

	/**
	 * Returns a relative path to a {@link Paradigm}. This needs to be used in conjunction with an {@link Archetype}
	 * path.
	 *
	 * @param paradigmId The {@link Paradigm}'s unique identifier.
	 * @return Returns the relative path to the {@link Paradigm}.
	 * @see {@link ParadigmUtil#exists(String)}
	 */
	public String getParadigmPath(final String paradigmId)
	{
		return Paths.get(paradigmId.substring(0, 3),
							paradigmId.substring(3, 6),
							paradigmId.substring(6, 9),
							paradigmId + ".json").toString();
	}

	/**
	 * Creates a new {@link Paradigm} within the {@link Archetype}.
	 *
	 * @param archetype The {@link Archetype} to create the {@link Paradigm} under.
	 * @param paradigmId The ID of the {@link Paradigm} to create.
	 * @return The created {@link Paradigm}.
	 * @throws ParagonFSException Thrown if an exception occurs while attempting to create the Paradigm.
	 */
	public Paradigm create(final Archetype archetype, final String paradigmId) throws ParagonFSException
	{
		if (archetype == null) {
			throw new IllegalArgumentException("The archetype must not be null.");
		}

		if (StringUtils.isBlank(paradigmId)) {
			throw new IllegalArgumentException("The paradigm ID must not be blank.");
		}

		final String paradigmPath = Paths.get(paragonFS.getDir().getAbsolutePath(), getParadigmPath(paradigmId)).toString();
		final File paradigmFile = new File(paradigmPath);

		if (paradigmFile.exists()) {
			throw new IllegalArgumentException("The paradigm already exists.");
		}

		if (!paradigmFile.mkdirs()) {
			throw new IllegalArgumentException("The paradigm could not be created as the parent directories do not exist.");
		}

		try
		{
			if (!paradigmFile.createNewFile()) {
				throw new ParagonFSException("The paradigm could not be created.");
			}
		}
		catch (final IOException e)
		{
			throw new ParagonFSException("The paradigm could not be created.", e);
		}

		return new Paradigm(archetype, paradigmFile, paradigmId);
	}
}
