package io.github.oliviercailloux.compromise;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableList;

public class PartitionOverM {
	public static PartitionOverM fbMs(FbMs fbMs, int m) {
		final LossPair xl = fbMs.fb();
		final LossPair yl = fbMs.ms();
		ProfileGenerator.LOGGER.debug("Generating for {}, {}.", xl, yl);
		final int t1 = xl.loss1();
		final int t2 = yl.loss2();
		final int t3 = xl.loss2();
		final int t4 = yl.loss1();
		checkArgument(t1 == xl.min());
		checkArgument(t2 == yl.min());
		checkArgument(t3 == xl.max());
		checkArgument(t4 == yl.max());
		return new PartitionOverM(t1, t2, t3, t4, m);
	}

	private final ImmutableList<String> firstLetters;
	private final int t1;
	private final int t2;
	private final int t3;
	private final int t4;

	private PartitionOverM(int t1, int t2, int t3, int t4, int m) {
		checkArgument(0 <= t1);
		checkArgument(t1 < t2);
		checkArgument(t2 < t3);
		checkArgument(t3 < t4);
		checkArgument(t4 <= m - 1);
		checkArgument(t1 + t4 + 1 <= t2 + t3);
		checkArgument(t3 + t4 <= m);
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.t4 = t4;
		firstLetters = ProfileGenerator.firstLetters(m);
	}

	public ImmutableList<String> whole() {
		return firstLetters;
	}

	public ImmutableList<String> a1() {
		return firstLetters.subList(t4 + 1, t2 + t4 + 1);
	}

	public ImmutableList<String> a1inv() {
		return a1().reverse();
	}

	public ImmutableList<String> a2() {
		return firstLetters.subList(t2 + t4 + 1, t3 + t4);
	}

	public ImmutableList<String> a3() {
		return firstLetters.subList(t3 + t4, firstLetters.size());
	}

	public ImmutableList<String> a4() {
		return firstLetters.subList(0, t1);
	}

	public ImmutableList<String> a4inv() {
		return a4().reverse();
	}

	public ImmutableList<String> a5() {
		return firstLetters.subList(t1 + 1, t4);
	}
}