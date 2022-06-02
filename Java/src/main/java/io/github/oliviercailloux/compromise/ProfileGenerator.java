package io.github.oliviercailloux.compromise;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileGenerator {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(ProfileGenerator.class);

	public static ImmutableList<String> firstLetters(int size) {
		return IntStream.rangeClosed('a', 'z').limit(size).boxed().map(i -> String.valueOf((char) i.intValue()))
				.collect(ImmutableList.toImmutableList());
	}

	public static ProfileGenerator sized(int size) {
		return new ProfileGenerator(size);
	}

	private final int m;

	private ProfileGenerator(int size) {
		this.m = size;
	}

	public Profile generate(LossPair xl, LossPair yl) {
		return generate(new FbMs(xl, yl));
	}

	public Profile generate(FbMs fbMs) {
		final LossPair xl = fbMs.fb();
		final LossPair yl = fbMs.ms();
		LOGGER.debug("Generating for {}, {}.", xl, yl);
		final ImmutableList<String> v1 = firstLetters(m);
		checkArgument(xl.loss1() == xl.min());
		checkArgument(yl.loss1() == yl.max());
		checkArgument(xl.loss1() < yl.loss1());
		final String x = v1.get(xl.loss1());
		final String y = v1.get(yl.loss1());
		final ImmutableList<String> worstThanYFor1 = v1.subList(yl.loss1() + 1, m);
		LOGGER.debug("Worst than {} (for v1): {}.", y, worstThanYFor1);
		final ImmutableList<String> betterThanYFor1 = v1.subList(0, yl.loss1());
		LOGGER.debug("Better than {} (for v1): {}.", y, betterThanYFor1);
		final ImmutableList<String> remainingBetterThanYFor1;
		{
			final ArrayList<String> arrayList = new ArrayList<>(betterThanYFor1);
			arrayList.remove(x);
			remainingBetterThanYFor1 = ImmutableList.copyOf(arrayList);
		}
		final ImmutableList<String> availables = Stream
				.concat(worstThanYFor1.stream(), remainingBetterThanYFor1.stream())
				.collect(ImmutableList.toImmutableList());

		final ImmutableList<String> beforeYIn2 = availables.subList(0, yl.loss2());
		LOGGER.debug("Before {} (for v2): {}.", y, beforeYIn2);
		final ImmutableList<String> inBetweenFor2 = availables.subList(yl.loss2(), xl.loss2() - 1);
		LOGGER.debug("In between (for v2): {}.", inBetweenFor2);
		final ImmutableList<String> afterXFor2 = availables.subList(xl.loss2() - 1, availables.size());
		LOGGER.debug("After {} (for v2): {}.", x, afterXFor2);
		final ImmutableList.Builder<String> builder = ImmutableList.builder();
		builder.addAll(beforeYIn2);
		builder.add(y);
		builder.addAll(inBetweenFor2);
		builder.add(x);
		builder.addAll(afterXFor2);
		final ImmutableList<String> v2 = builder.build();
		final Profile profile = new Profile(v1, v2);
		verify(x.equals(profile.fb()));
		verify(y.equals(profile.ms()));
		verify(profile.msWides().contains(y));
		return profile;
	}

	public Profile shuffle(Profile source) {
		final Random r = new Random();
		final ArrayList<String> v1Shuffled = new ArrayList<>(source.alternatives());
		Collections.shuffle(v1Shuffled, r);
		final Map<String, String> permutation = IntStream.range(0, source.v1().size()).boxed()
				.collect(Collectors.toMap(source.v1()::get, v1Shuffled::get));
		final ImmutableList<String> v2Permuted = source.v2().stream().map(permutation::get)
				.collect(ImmutableList.toImmutableList());
		return new Profile(v1Shuffled, v2Permuted);
	}
}
