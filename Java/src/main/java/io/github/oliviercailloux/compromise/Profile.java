package io.github.oliviercailloux.compromise;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Profile {
  private final ImmutableList<String> v1;
  private final ImmutableList<String> v2;

  public Profile(List<String> v1, List<String> v2) {
    this.v1 = ImmutableList.copyOf(v1);
    this.v2 = ImmutableList.copyOf(v2);
    final ImmutableSet<String> v1Set = ImmutableSet.copyOf(v1);
    final ImmutableSet<String> v2Set = ImmutableSet.copyOf(v2);
    checkArgument(v1.size() == v1Set.size());
    checkArgument(v1Set.equals(v2Set), this.v1.toString());
  }

  public ImmutableList<String> v1() {
    return v1;
  }

  public ImmutableList<String> v2() {
    return v2;
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

  public ImmutableSet<String> bs() {
    return smallests(sumLosses());
  }

  public String b() {
    final ImmutableSet<String> winners = bs();
    checkState(winners.size() == 1);
    return Iterables.getOnlyElement(winners);
  }

  public ExampleKind kind() {
    final boolean equalBFb = fbs().equals(bs());
    return equalBFb ? ExampleKind.B : ExampleKind.D;
  }

  public ImmutableSet<String> efficients() {
    return alternatives().stream().filter(a -> dominating(a).isEmpty())
        .collect(ImmutableSet.toImmutableSet());
  }

  public ImmutableSet<String> dominating(String alternative) {
    final ImmutableSet<String> betterThanFor1 =
        ImmutableSet.copyOf(v1.subList(0, v1.indexOf(alternative)));
    final ImmutableSet<String> betterThanFor2 =
        ImmutableSet.copyOf(v2.subList(0, v2.indexOf(alternative)));
    return Sets.intersection(betterThanFor1, betterThanFor2).immutableCopy();

  }

  public ImmutableSet<String> msWides() {
    return smallests(distLosses());
  }

  public ImmutableSet<String> mses() {
    final ImmutableMap<String, Integer> distLosses = distLosses();
    final ImmutableSet<String> efficients = efficients();
    final Map<String, Integer> distLossesAmongEfficients =
        Maps.filterKeys(distLosses, a -> efficients.contains(a));
    return smallests(distLossesAmongEfficients);
  }

  public String ms() {
    final ImmutableSet<String> winners = mses();
    checkState(winners.size() == 1);
    return Iterables.getOnlyElement(winners);
  }

  private ImmutableSet<String> smallests(Map<String, Integer> values) {
    // final ImmutableSortedSet<String> sortedValues =
    // ImmutableSortedSet.copyOf(Comparator.comparing(values::get),
    // alternatives());
    // final String smallest = sortedValues.first();
    // final int smallestValue = values.get(smallest);
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

  private ImmutableMap<String, Integer> sumLosses() {
    return Maps.toMap(alternatives(), a -> losses(a).sum());
  }

  private ImmutableMap<String, Integer> maxLosses() {
    return Maps.toMap(alternatives(), a -> losses(a).max());
  }

  public LossPair losses(String alternative) {
    return new LossPair(v1.indexOf(alternative), v2.indexOf(alternative));
  }

  @Override
  public boolean equals(Object o2) {
    if (!(o2 instanceof Profile)) {
      return false;
    }
    final Profile t2 = (Profile) o2;
    return v1.equals(t2.v1) && v2.equals(t2.v2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(v1, v2);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("v1", v1).add("v2", v2).toString();
  }
}
