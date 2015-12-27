package org.paragon.paragonfs.archetype;

import org.paragon.paragonfs.ParagonFS;
import org.paragon.paragonfs.paradigm.Paradigm;
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
	 * Initializes the {@link Archetype} with the path to the directory.
	 *
	 * @param paragonFS The {@link ParagonFS} this {@link Archetype} belongs to.
	 * @param dir The path to the directory on the file system.
	 */
	public Archetype(final ParagonFS paragonFS, final File dir)
	{
		this.paragonFS = paragonFS;
		this.dir = dir;
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
}
