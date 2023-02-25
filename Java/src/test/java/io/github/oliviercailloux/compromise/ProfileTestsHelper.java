package io.github.oliviercailloux.compromise;

import com.google.common.collect.ImmutableList;

public class ProfileTestsHelper {

	public static Profile p13kjihgblmcdefa() {
		final Profile profile = new Profile(ProfileGenerator.firstLetters(13),
				ImmutableList.of("k", "j", "i", "h", "g", "b", "l", "m", "c", "d", "e", "f", "a"));
		return profile;
	}

	public static Profile p13kjihgbalmcdef() {
		final Profile profile = new Profile(ProfileGenerator.firstLetters(13),
				ImmutableList.of("k", "j", "i", "h", "g", "b", "a", "l", "m", "c", "d", "e", "f"));
		return profile;
	}

}
