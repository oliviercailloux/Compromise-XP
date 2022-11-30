package io.github.oliviercailloux.compromise;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

class ProfileGenerationTests {

	@Test
	void test13() {
		final Profile profile = ProfileGenerator.sized(13).generate(new LossPair(0, 6), new LossPair(7, 2));
		final Profile expected = new Profile(ProfileGenerator.firstLetters(13),
				ImmutableList.of("i", "j", "h", "k", "l", "m", "a", "b", "c", "d", "e", "f", "g"));
		assertEquals(expected, profile);
	}

}
