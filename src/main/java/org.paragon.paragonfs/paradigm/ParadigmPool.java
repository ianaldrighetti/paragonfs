package org.paragon.paragonfs.paradigm;

import org.paragon.paragonfs.archetype.Archetype;
import org.paragon.paragonfs.exception.ParagonFSException;

import java.io.File;
import java.lang.ref.WeakReference;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The Paradigm Pool is an object which every {@link Archetype} has to ensure that there is only one instance of a
 * {@link Paradigm}.<br />
 * <br />
 * The Paradigm Pool takes care of creating and loading {@link Paradigm}'s.
 */
public class ParadigmPool
{
	/**
	 * The {@link Archetype} this Paradigm Pool is for.
	 */
	private final Archetype archetype;

	/**
	 * A map, where the key is the Paradigm's ID and the value is the Paradigm itself.
	 */
	private final Map<String, WeakReference<Paradigm>> paradigms;

	private final Object _lock = new Object();

	/**
	 * Initializes the Paradigm Pool.
	 *
	 * @param archetype The {@link Archetype} this Paradigm Pool is for.
	 */
	public ParadigmPool(final Archetype archetype)
	{
		this.archetype = archetype;
		this.paradigms = new LinkedHashMap<>();
	}

	/**
	 * Adds a Paradigm to the Paradigm Pool.<br />
	 * <br /><strong>Note:</strong> This method must only be invoked within a synchronized block.
	 *
	 * @param paradigm The Paradigm to add to the pool
	 */
	private void add(final Paradigm paradigm)
	{
		paradigms.put(paradigm.getId().toLowerCase(), new WeakReference<>(paradigm));
	}

	/**
	 * Creates a new {@link Paradigm} within the {@link Archetype}.
	 *
	 * @return Returns the created {@link Paradigm}.
	 * @throws ParagonFSException Thrown if an error occurs while attempting to create the Paradigm.
	 */
	public Paradigm create() throws ParagonFSException
	{
		synchronized (_lock)
		{
			final String paradigmId = archetype.getParagonFS().getParadigmIdPool().getNextParadigmId();
			final Paradigm paradigm = archetype.getParagonFS().getParadigmUtil().create(archetype, paradigmId);

			// Index it in our pool.
			add(paradigm);

			return paradigm;
		}
	}

	/**
	 * Returns the {@link Paradigm}, located based on it's unique identifier.
	 *
	 * @param paradigmId The {@link Paradigm}'s unique identifier.
	 * @return Returns the {@link Paradigm} or null if the Paradigm does not exist.
	 */
	public Paradigm get(final String paradigmId)
	{
		if (paradigmId == null)
		{
			throw new IllegalArgumentException("The paradigm ID must not be null.");
		}

		synchronized (_lock)
		{
			final WeakReference<Paradigm> paradigm = paradigms.get(paradigmId);

			if (paradigm == null || paradigm.get() == null)
			{
				return getAndIndex(paradigmId);
			}

			return paradigm.get();
		}
	}

	/**
	 * This method will check to see if the {@link Paradigm} exists and if it does it will load it into the pool.
	 * <br /><br /><strong>Note:</strong> This method should only be invoked within a synchronized block.
	 *
	 * @param paradigmId The Paradigm's unique identifier.
	 * @return Returns the {@link Paradigm}, null if it does not exist.
	 */
	private Paradigm getAndIndex(final String paradigmId)
	{
		if (!archetype.exists(paradigmId))
		{
			return null;
		}

		// Build the Paradigm object.
		final File paradigmFile = new File(Paths.get(
				archetype.getDir().getAbsolutePath(),
				archetype.getParagonFS().getParadigmUtil().getParadigmPath(paradigmId)).toString()
		);
		final Paradigm paradigm = new Paradigm(archetype, paradigmFile, paradigmId);

		// Then add it to the index.
		add(paradigm);

		return paradigm;
	}
}
