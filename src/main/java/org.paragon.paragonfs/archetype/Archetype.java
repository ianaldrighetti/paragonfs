package org.paragon.paragonfs.archetype;

import org.paragon.paragonfs.paradigm.Paradigm;

import java.io.File;

/**
 * An Archetype is a directory within ParagonFS, which can contain many {@link Paradigm}'s.
 */
public class Archetype
{
	private final File dir;

	public Archetype(final File dir)
	{
		this.dir = dir;
	}

}
