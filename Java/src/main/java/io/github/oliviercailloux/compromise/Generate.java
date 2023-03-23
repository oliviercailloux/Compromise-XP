package io.github.oliviercailloux.compromise;

import static com.google.common.base.Verify.verify;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generate {
  @SuppressWarnings("unused")
  private static final Logger LOGGER = LoggerFactory.getLogger(Generate.class);

  public static void main(String[] args) throws Exception {
    new Generate().proceed();
  }

  public void proceed() throws IOException {
    final List<ProfileRow> beans = read();
    // final ImmutableBiMap<Integer, ProfileRow> indexedBeans = IntStream.range(0,
    // beans.size()).boxed()
    // .collect(ImmutableBiMap.toImmutableBiMap(i -> i, beans::get));

    final ProfileGenerator generator = ProfileGenerator.sized(13, 0);

    {
      final String ct = "ct:profclassid";
      final String newCt = "\\newcounter{%s}".formatted(ct);
      final String labels = beans.stream()
          .map(p -> "\\refstepcounter{%s} \\label{profclassid:%s%s%s%s}".formatted(ct,
              p.xl().loss1(), p.xl().loss2(), p.yl().loss2(), p.yl().loss1()))
          .collect(Collectors.joining("\n"));
      final String latex = "%Generated – please do not edit.\n" + newCt + "\n" + labels;
      Files.writeString(Path.of("../profclassids.tex"), latex);
    }

    final LatexWriter writer = new LatexWriter();
    {
      final ImmutableSet<Profile> profilesB =
          beans.stream().map(r -> generator.generateB(new FbMs(r.xl(), r.yl())))
              .collect(ImmutableSet.toImmutableSet());
      LOGGER.info("Generated {} B profiles.", profilesB.size());
      final String latex = "%Generated – please do not edit.\n\n"
          + profilesB.stream().map(p -> writer.example(p, true)).collect(Collectors.joining("\n"));
      Files.writeString(Path.of("../examplesB.tex"), latex);
    }
    {
      final ImmutableSet<Profile> profilesD = beans.stream().filter(r -> r.xl().min() >= 1)
          .map(r -> generator.generateD(new FbMs(r.xl(), r.yl())))
          .collect(ImmutableSet.toImmutableSet());
      LOGGER.info("Generated {} D profiles.", profilesD.size());
      final String latex = "%Generated – please do not edit.\n\n"
          + profilesD.stream().map(p -> writer.example(p, true)).collect(Collectors.joining("\n"));
      Files.writeString(Path.of("../examplesD.tex"), latex);
    }

    final ImmutableList.Builder<FbMsK> builder = ImmutableList.builder();
    builder.add(FbMsK.b(0, 3, 2, 4));
    builder.add(FbMsK.b(0, 6, 5, 7));
    builder.add(FbMsK.d(1, 4, 3, 5));
    builder.add(FbMsK.b(3, 6, 5, 7));
    builder.add(FbMsK.d(3, 6, 5, 7));
    builder.add(FbMsK.b(0, 6, 2, 7));
    final ImmutableList<FbMsK> chosenOnes = builder.build();
    final ImmutableSet<Profile> chosenProfiles =
        chosenOnes.stream().map(generator::generate).collect(ImmutableSet.toImmutableSet());
    {
      final ImmutableSet<Profile> shuffledProfiles =
          chosenProfiles.stream().map(generator::shuffle).collect(ImmutableSet.toImmutableSet());
      verify(shuffledProfiles.size() == chosenOnes.size());
      final String latex = "%Generated – please do not edit.\n\n" + shuffledProfiles.stream()
          .map(p -> writer.equation(p, false)).collect(Collectors.joining("\n"));
      Files.writeString(Path.of("../run.tex"), latex);
    }
    {
      final String latex = "%Generated – please do not edit.\n\n" + chosenProfiles.stream()
          .map(p -> bunchOfShuffling(writer, generator, p)).collect(Collectors.joining("\n\n"));
      Files.writeString(Path.of("../multiple.tex"), latex);
    }

  }

  private String bunchOfShuffling(LatexWriter writer, ProfileGenerator generator, Profile p) {
    return Stream.iterate(p, generator::shuffle).limit(30).map(s -> writer.example(s, false))
        .collect(Collectors.joining("\n"));
  }

  public List<ProfileRow> read() throws IOException {
    final StringReader reader = new StringReader(Files.readString(Path.of("../Profiles.csv")));

    final BeanListProcessor<ProfileRow> rowProcessor = new BeanListProcessor<>(ProfileRow.class);

    final CsvParserSettings parserSettings = new CsvParserSettings();
    parserSettings.getFormat().setLineSeparator("\n");
    parserSettings.setProcessor(rowProcessor);
    parserSettings.setHeaderExtractionEnabled(true);

    final CsvParser parser = new CsvParser(parserSettings);
    parser.parse(reader);

    return rowProcessor.getBeans();
  }
}
