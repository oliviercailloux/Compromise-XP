package io.github.oliviercailloux.compromise;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

class ProfileGenerationTests {

	@Test
	void test13() {
		final Profile profile = ProfileGenerator.sized(13).generateB(new LossPair(1, 5), new LossPair(6, 4));
		final Profile expected = new Profile(ProfileGenerator.firstLetters(13),
				ImmutableList.of("k", "j", "i", "h", "g", "b", "l", "m", "c", "d", "e", "f", "a"));
		assertEquals(expected, profile);
	}

}
