package io.github.oliviercailloux.compromise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

class ProfileTests {

	@Test
	void testSingleton() {
		final Profile profile = new Profile(ImmutableList.of("a"), ImmutableList.of("a"));
		assertEquals("a", profile.fb());
		assertEquals("a", profile.ms());
	}

	@Test
	void testDualton() {
		final Profile profile = new Profile(ImmutableList.of("a", "b"), ImmutableList.of("a", "b"));
		assertEquals("a", profile.fb());
		assertEquals("a", profile.ms());
	}

	@Test
	void testOppositeDualton() {
		final Profile profile = new Profile(ImmutableList.of("a", "b"), ImmutableList.of("b", "a"));
		assertEquals(ImmutableSet.of("a", "b"), profile.fbs());
		assertEquals(ImmutableSet.of("a", "b"), profile.mses());
		assertThrows(IllegalStateException.class, () -> profile.fb());
		assertThrows(IllegalStateException.class, () -> profile.ms());
	}

	@Test
	void testWrongProfile() {
		final ImmutableList<String> l = ProfileGenerator.firstLetters(13);
		assertThrows(IllegalArgumentException.class, () -> new Profile(l, ImmutableList.of("a", "b")));
	}

	@Test
	void test13() {
		final Profile profile = new Profile(ProfileGenerator.firstLetters(13),
				ImmutableList.of("i", "j", "h", "k", "l", "m", "a", "b", "c", "d", "e", "f", "g"));
		assertEquals("a", profile.fb());
		assertEquals("h", profile.ms());
		assertEquals("a", profile.b());
	}

	@Test
	void test13kjihgblmcdefa() {
		final Profile profile = ProfileTestsHelper.p13kjihgblmcdefa();
		assertEquals("b", profile.fb());
		assertEquals("g", profile.ms());
		assertEquals("b", profile.b());
	}

	@Test
	void test13kjihgbalmcdef() {
		final Profile profile = ProfileTestsHelper.p13kjihgbalmcdef();
		assertEquals("b", profile.fb());
		assertEquals("g", profile.ms());
		assertEquals(ImmutableSet.of("a", "b"), profile.bs());
	}

}
