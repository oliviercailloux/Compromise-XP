package io.github.oliviercailloux.compromise;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;

import com.google.common.base.VerifyException;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileGenerator {
  @SuppressWarnings("unused")
  static final Logger LOGGER = LoggerFactory.getLogger(ProfileGenerator.class);

  public static ProfileGenerator sized(int size, long seed) {
    return new ProfileGenerator(size, seed);
  }

  public static ImmutableList<String> firstLetters(int size) {
    return IntStream.rangeClosed('a', 'z').limit(size).boxed()
        .map(i -> String.valueOf((char) i.intValue())).collect(ImmutableList.toImmutableList());
  }

  private final int m;
  private final Random random;

  private ProfileGenerator(int size, long seed) {
    this.m = size;
    random = new Random(seed);
  }

  public Profile generateB(LossPair xl, LossPair yl) {
    return generateB(new FbMs(xl, yl));
  }

  public Profile generate(FbMsK fbMsK) {
    switch (fbMsK.k()) {
      case B:
        return generateB(fbMsK.fbMs());
      case D:
        return generateD(fbMsK.fbMs());
      default:
        throw new VerifyException();
    }
  }

  public Profile generateB(FbMs fbMs) {
    final PartitionOverM partition = PartitionOverM.fbMs(fbMs, m);
    final ImmutableList<String> v1 = partition.whole();
    final String x = v1.get(fbMs.fb().loss1());
    final String y = v1.get(fbMs.ms().loss1());
    final List<String> v1Check = new ArrayList<>();
    v1Check.addAll(partition.a4());
    v1Check.add(x);
    v1Check.addAll(partition.a5());
    v1Check.add(y);
    v1Check.addAll(partition.a1());
    v1Check.addAll(partition.a2());
    v1Check.addAll(partition.a3());
    verify(v1Check.equals(v1));

    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    builder.addAll(partition.a1inv());
    builder.add(y);
    builder.addAll(partition.a2());
    builder.add(x);
    builder.addAll(partition.a3());
    builder.addAll(partition.a5());
    builder.addAll(partition.a4inv());
    final ImmutableList<String> v2 = builder.build();
    final Profile profile = new Profile(v1, v2);
    verify(x.equals(profile.fb()));
    verify(x.equals(profile.b()));
    verify(y.equals(profile.ms()));
    verify(profile.msWides().equals(profile.mses()));
    verify(profile.kind() == ExampleKind.B);
    return profile;
  }

  public Profile generateD(FbMs fbMs) {
    checkArgument(fbMs.fb().min() >= 1);
    final PartitionOverM partition = PartitionOverM.fbMs(fbMs, m);
    final ImmutableList<String> v1 = partition.whole();
    final String x = v1.get(fbMs.fb().loss1());
    final String y = v1.get(fbMs.ms().loss1());
    final List<String> v1Check = new ArrayList<>();
    v1Check.addAll(partition.a4());
    v1Check.add(x);
    v1Check.addAll(partition.a5());
    v1Check.add(y);
    v1Check.addAll(partition.a1());
    v1Check.addAll(partition.a2());
    v1Check.addAll(partition.a3());
    verify(v1Check.equals(v1));

    final ImmutableList.Builder<String> builder = ImmutableList.builder();
    builder.addAll(partition.a1inv());
    builder.add(y);
    builder.addAll(partition.a2());
    builder.add(x);
    builder.addAll(partition.a4());
    builder.addAll(partition.a3());
    builder.addAll(partition.a5());
    final ImmutableList<String> v2 = builder.build();
    final Profile profile = new Profile(v1, v2);
    verify(x.equals(profile.fb()));
    switch (fbMs.fb().loss1()) {
      case 0:
        verify(profile.equals(generateB(fbMs)));
        break;
      case 1:
        verify(profile.bs().contains(x) && !profile.bs().equals(profile.fbs()));
        break;
      default:
        verify(fbMs.fb().loss1() >= 2);
        verify(!profile.bs().contains(x));
        break;
    }
    verify(y.equals(profile.ms()));
    verify(profile.msWides().equals(profile.mses()));
    verify(profile.kind() == ExampleKind.D, "Char %s, profile %s, kind %s, fbs %s, bs %s."
        .formatted(fbMs, profile, profile.kind(), profile.fbs(), profile.bs()));
    return profile;
  }

  public Profile shuffle(Profile source) {
    final ArrayList<String> v1Shuffled = new ArrayList<>(source.alternatives());
    Collections.shuffle(v1Shuffled, random);
    final Map<String, String> permutation = IntStream.range(0, source.v1().size()).boxed()
        .collect(Collectors.toMap(source.v1()::get, v1Shuffled::get));
    final ImmutableList<String> v2Permuted =
        source.v2().stream().map(permutation::get).collect(ImmutableList.toImmutableList());
    return new Profile(v1Shuffled, v2Permuted);
  }
}
