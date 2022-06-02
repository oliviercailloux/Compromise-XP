package io.github.oliviercailloux.compromise;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Profile {
	private ImmutableList<String> v1;
	private ImmutableList<String> v2;

	public static String line(ImmutableList<String> v, String x, String y) {
		return v.stream().map(s -> s.equals(x) ? "\\bm{%s}".formatted(s) : s)
				.map(s -> s.equals(y) ? "\\emph{%s}".formatted(s) : s).collect(Collectors.joining("&"));
	}

	public Profile(List<String> v1, List<String> v2) {
		this.v1 = ImmutableList.copyOf(v1);
		this.v2 = ImmutableList.copyOf(v2);
		final ImmutableSet<String> v1Set = ImmutableSet.copyOf(v1);
		final ImmutableSet<String> v2Set = ImmutableSet.copyOf(v2);
		checkArgument(v1.size() == v1Set.size());
		checkArgument(v1Set.equals(v2Set), this.v1.toString());
	}

	public ImmutableSet<String> alternatives() {
		return ImmutableSet.copyOf(this.v1);
	}

	public ImmutableSet<String> fbs() {
		return smallests(maxLosses());
	}

	public String fb() {
		final ImmutableSet<String> winners = fbs();
		checkState(winners.size() == 1);
		return Iterables.getOnlyElement(winners);
	}

	public ImmutableSet<String> efficients() {
		return alternatives().stream().filter(a -> dominating(a).isEmpty()).collect(ImmutableSet.toImmutableSet());
	}

	public ImmutableSet<String> dominating(String alternative) {
		final ImmutableSet<String> betterThanFor1 = ImmutableSet.copyOf(v1.subList(0, v1.indexOf(alternative)));
		final ImmutableSet<String> betterThanFor2 = ImmutableSet.copyOf(v2.subList(0, v2.indexOf(alternative)));
		return Sets.intersection(betterThanFor1, betterThanFor2).immutableCopy();

	}

	@SuppressWarnings("unused")
	private ImmutableSet<String> msWides() {
		return smallests(distLosses());
	}

	public ImmutableSet<String> mses() {
		final ImmutableMap<String, Integer> distLosses = distLosses();
		final ImmutableSet<String> efficients = efficients();
		final Map<String, Integer> distLossesAmongEfficients = Maps.filterKeys(distLosses, a -> efficients.contains(a));
		return smallests(distLossesAmongEfficients);
	}

	public String ms() {
		final ImmutableSet<String> winners = mses();
		checkState(winners.size() == 1);
		return Iterables.getOnlyElement(winners);
	}

	private ImmutableSet<String> smallests(Map<String, Integer> values) {
//		final ImmutableSortedSet<String> sortedValues = ImmutableSortedSet.copyOf(Comparator.comparing(values::get),
//				alternatives());
//		final String smallest = sortedValues.first();
//		final int smallestValue = values.get(smallest);
		final int smallestValue = Collections.min(values.values());
		return values.keySet().stream().filter(a -> values.get(a).equals(smallestValue))
				.collect(ImmutableSet.toImmutableSet());
	}

	public ImmutableMap<String, LossPair> allLosses() {
		return Maps.toMap(alternatives(), this::losses);
	}

	private ImmutableMap<String, Integer> distLosses() {
		return Maps.toMap(alternatives(), a -> losses(a).dist());
	}

	private ImmutableMap<String, Integer> maxLosses() {
		return Maps.toMap(alternatives(), a -> losses(a).max());
	}

	public LossPair losses(String alternative) {
		return new LossPair(v1.indexOf(alternative), v2.indexOf(alternative));
	}

	private String innerTable(String x, String y) {
		return line(v1, x, y) + "\\" + line(v2, x, y);
	}

	public String example() {
		final String x = fb();
		final String y = ms();
		final String outer = """
				\\begin{example}[$\\lprof(x) = (%s, %s)$; $\\lprof(y) = (%s, %s)$]
					\\label{ex1.1}
					\\begin{equation}
						\\begin{array}{*{13}l}
							%s
						\\end{array}
					\\end{equation}
				\\end{example}
				""".formatted(losses(x).loss1(), losses(x).loss2(), losses(y).loss2(), losses(y).loss1(),
				innerTable(x, y));
		return outer;
	}
}
