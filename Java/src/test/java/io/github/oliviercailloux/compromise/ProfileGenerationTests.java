package io.github.oliviercailloux.compromise;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ProfileGenerationTests {

	@Test
	void test13B() {
		final FbMs fbMs = new FbMs(new LossPair(1, 5), new LossPair(6, 4));
		final Profile profile = ProfileGenerator.sized(13).generateB(fbMs);
		final Profile expected = ProfileTestsHelper.p13kjihgblmcdefa();
		assertEquals(expected, profile);
	}

	@Test
	void test13D() {
		final FbMs fbMs = new FbMs(new LossPair(1, 5), new LossPair(6, 4));
		final Profile profile = ProfileGenerator.sized(13).generateD(fbMs);
		final Profile expected = ProfileTestsHelper.p13kjihgbalmcdef();
		assertEquals(expected, profile);
	}

}
