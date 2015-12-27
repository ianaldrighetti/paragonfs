package org.paragon.paragonfs.paradigm;

import org.apache.commons.lang3.RandomStringUtils;
import org.paragon.paragonfs.ParagonFS;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This keeps a pool of available unique identifiers to use for new {@link Paradigm}'s.
 */
public class ParadigmIdPool
{
	/**
	 * The minimum number of unique identifiers in the pool until the pool needs to be refilled.
	 */
	private static final int MIN_POOL_SIZE = 10;

	/**
	 * The maximum number of unique identifiers, or the number the pool will be filled to when the pool is too small.
	 */
	private static final int MAX_POOL_SIZE = 50;

	private static final int PARADIGM_ID_LENGTH = 60;

	/**
	 * The {@link ParagonFS} this Paradigm ID pool is for.
	 */
	private final ParagonFS paragonFS;

	/**
	 * A queue of unique identifiers which are available to be assigned to new {@link Paradigm}'s.
	 */
	private final Queue<String> uuids;

	private final Object _lock = new Object();

	/**
	 * Initializes the {@link ParadigmIdPool}.
	 *
	 * @param paragonFS The {@link ParagonFS} object this pool belongs to.
	 */
	public ParadigmIdPool(final ParagonFS paragonFS)
	{
		this.paragonFS = paragonFS;
		uuids = new LinkedList<>();

		refill();
	}

	/**
	 * This will refill the unique identifier queue.<br />
	 * <br /><strong>Note:</strong> This method should only be invoked within a synchronized block.
	 */
	private void refill()
	{
		if (MIN_POOL_SIZE < uuids.size()) {
			return;
		}

		while (MAX_POOL_SIZE < uuids.size())
		{
			uuids.add(getNextUuid());
		}
	}

	/**
	 * Returns the next available unique identifier for a Paradigm.
	 *
	 * @return A string, which is a unique identifier which can be used for a {@link Paradigm}.
	 */
	private String getNextUuid()
	{
		String paradigmId;
		do {
			paradigmId = RandomStringUtils.randomAlphanumeric(PARADIGM_ID_LENGTH);
		}
		while (paragonFS.getParadigmUtil().exists(paradigmId));

		return paradigmId;
	}

	/**
	 * Returns the next available ID to be used for a {@link Paradigm}.
	 *
	 * @return The next available {@link Paradigm}.
	 */
	public String getNextParadigmId()
	{
		synchronized (_lock)
		{
			// Get the next UUID then invoke refill, which won't do so unless necessary.
			final String nextId = uuids.poll();
			refill();

			return nextId;
		}
	}
}
