package org.paragon.paragonfs;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Tests (unit and integration) for {@link ParagonFS}.
 */
public class ParagonFSTest
{
	@InjectMocks
	private ParagonFS paragonFS;

	@Before
	public void setUp() throws Exception
	{
		final Path path = Files.createTempDirectory(null);
		paragonFS = new ParagonFS(path.toFile());
	}

	@Test
	public void testPathNullException()
	{
		try
		{
			final ParagonFS paragonFS = new ParagonFS(StringUtils.EMPTY);

			fail("Expected an exception.");
		}
		catch (final Exception e)
		{
			assertThat(e, instanceOf(IllegalArgumentException.class));
			assertThat(e.getMessage(), equalTo(ParagonFS.THE_PATH_MUST_NOT_BE_EMPTY));
		}
	}
}
