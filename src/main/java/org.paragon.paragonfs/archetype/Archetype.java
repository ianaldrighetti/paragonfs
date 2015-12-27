package org.paragon.paragonfs.archetype;

import org.paragon.paragonfs.ParagonFS;
import org.paragon.paragonfs.exception.ParagonFSException;
import org.paragon.paragonfs.paradigm.Paradigm;
import org.paragon.paragonfs.paradigm.ParadigmPool;
import org.paragon.paragonfs.paradigm.ParadigmUtil;

import java.io.File;
import java.nio.file.Paths;

/**
 * An Archetype is a directory within ParagonFS, which can contain many {@link Paradigm}'s.
 */
public class Archetype
{
	/**
	 * The {@link ParagonFS} this {@link Archetype} belongs to.
	 */
	private final ParagonFS paragonFS;

	/**
	 * The path to the directory which this archetype represents.
	 */
	private final File dir;

	/**
	 * The Paradigm Pool for this Archetype.
	 */
	private final ParadigmPool paradigmPool;

	/**
	 * Initializes the {@link Archetype} with the path to the directory.
	 *
	 * @param paragonFS The {@link ParagonFS} this {@link Archetype} belongs to.
	 * @param dir The path to the directory on the file system.
	 */
	public Archetype(final ParagonFS paragonFS, final File dir)
	{
		this.paragonFS = paragonFS;
		this.dir = dir;
		this.paradigmPool = new ParadigmPool(this);
	}

	/**
	 * Determines whether the {@link Paradigm} exists within this {@link Archetype}. This is different from
	 * {@link ParadigmUtil#exists(String)} which checks if a {@link Paradigm} exists in any {@link Archetype}.
	 *
	 * @param paradigmId The {@link Paradigm} ID.
	 * @return Returns true if the {@link Paradigm} exists within this {@link Archetype} false otherwise.
	 */
	public boolean exists(final String paradigmId)
	{
		return (new File(Paths.get(this.dir.getAbsolutePath(), paragonFS.getParadigmUtil().getParadigmPath(paradigmId)).toString())).exists();
	}

	/**
	 * Returns the {@link File} which represents where this {@link Archetype} resides.
	 *
	 * @return The absolute path to where this {@link Archetype} resides.
	 */
	public File getDir()
	{
		return dir;
	}

	/**
	 * Returns the {@link ParagonFS} this {@link Archetype} belongs to.
	 *
	 * @return The {@link ParagonFS} for this {@link Archetype}.
	 */
	public ParagonFS getParagonFS()
	{
		return paragonFS;
	}

	/**
	 * Returns the {@link ParadigmPool} for this {@link Archetype}.
	 *
	 * @return The {@link ParadigmPool} for this {@link Archetype}.
	 */
	public ParadigmPool getParadigmPool()
	{
		return paradigmPool;
	}

	/**
	 * Returns the {@link Paradigm} within this {@link Archetype}.
	 *
	 * @param paradigmId The Paradigm's unique identifier.
	 * @return Returns the Paradigm, or null if it does not exist.
	 */
	public Paradigm get(final String paradigmId)
	{
		return paradigmPool.get(paradigmId);
	}

	/**
	 * Creates a new {@link Paradigm} within this {@link Archetype}.
	 *
	 * @return The newly created {@link Paradigm}.
	 * @throws ParagonFSException Thrown if an error occurs while attempting to create the new Paradigm.
	 */
	public Paradigm create() throws ParagonFSException
	{
		return paradigmPool.create();
	}
}
