package io.github.oliviercailloux.compromise;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.stream.IntStream;
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
		LOGGER.info("Generating for {}, {}.", xl, yl);
		final ImmutableList<String> v1 = firstLetters(m);
		checkArgument(xl.loss1() == xl.min());
		checkArgument(yl.loss1() == yl.max());
		checkArgument(xl.loss1() < yl.loss1());
		final String x = v1.get(xl.loss1());
		final String y = v1.get(yl.loss1());
		final ImmutableList<String> worstThanYFor1 = v1.subList(yl.loss1() + 1, m);
		LOGGER.info("Worst than {} (for v1): {}.", y, worstThanYFor1);
		final ImmutableList<String> beforeYIn2 = worstThanYFor1.subList(0, yl.loss2());
		LOGGER.info("Before {} (for v2): {}.", y, beforeYIn2);
		final ImmutableList<String> inBetweenFor2 = worstThanYFor1.subList(yl.loss2(), xl.loss2() - 1);
		LOGGER.info("In between (for v2): {}.", inBetweenFor2);
		final ImmutableList<String> betterThanYFor1 = v1.subList(0, yl.loss1());
		LOGGER.info("Better than {} (for v1): {}.", y, betterThanYFor1);
		final ImmutableList<String> justAfterXFor2;
		{
			final ArrayList<String> arrayList = new ArrayList<>(betterThanYFor1);
			arrayList.remove(x);
			justAfterXFor2 = ImmutableList.copyOf(arrayList);
		}
		LOGGER.info("Just after {} (for v2): {}.", x, justAfterXFor2);
		final ImmutableList<String> remainingWorstThanYFor1 = worstThanYFor1.subList(xl.loss2() - 1,
				worstThanYFor1.size());
		LOGGER.info("Remaining, worst than {} (for v1): {}.", y, remainingWorstThanYFor1);
		checkArgument(remainingWorstThanYFor1.size() % 2 == 0);
		final ImmutableList<String> remainingOnesFor2FirstHalf = remainingWorstThanYFor1
				.subList(remainingWorstThanYFor1.size() / 2, remainingWorstThanYFor1.size());
		final ImmutableList<String> remainingOnesFor2SecondHalf = remainingWorstThanYFor1.subList(0,
				remainingWorstThanYFor1.size() / 2);
		final ImmutableList.Builder<String> builder = ImmutableList.builder();
		builder.addAll(beforeYIn2);
		builder.add(y);
		builder.addAll(inBetweenFor2);
		builder.add(x);
		builder.addAll(justAfterXFor2);
		builder.addAll(remainingOnesFor2FirstHalf);
		builder.addAll(remainingOnesFor2SecondHalf);
		final ImmutableList<String> v2 = builder.build();
		final Profile profile = new Profile(v1, v2);
		verify(x.equals(profile.fb()));
		verify(y.equals(profile.ms()));
		return profile;
	}
}
