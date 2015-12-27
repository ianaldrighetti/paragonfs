package org.paragon.paragonfs.paradigm;

import org.paragon.paragonfs.ParagonFS;
import org.paragon.paragonfs.archetype.Archetype;

import java.io.File;
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
}
