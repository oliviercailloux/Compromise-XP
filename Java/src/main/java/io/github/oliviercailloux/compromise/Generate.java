package io.github.oliviercailloux.compromise;

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
		final ProfileGenerator generator = ProfileGenerator.sized(13);
		final ImmutableSet<Profile> profiles = beans.stream().map(r -> generator.generate(r.xl(), r.yl()))
				.collect(ImmutableSet.toImmutableSet());
		LOGGER.info("Generated {} profiles.", profiles.size());
		final LatexWriter writer = new LatexWriter();
		final String latex = "%Generated – please do not edit.\n\n"
				+ profiles.stream().map(writer::example).collect(Collectors.joining("\n"));
		Files.writeString(Path.of("../examples.tex"), latex);

		final ImmutableSet<FbMs> chosenOnes = ImmutableSet.of(FbMs.canonical(0, 3, 2, 4), FbMs.canonical(0, 5, 2, 6),
				FbMs.canonical(0, 6, 5, 7), FbMs.canonical(1, 4, 3, 5), FbMs.canonical(3, 6, 5, 7),
				FbMs.canonical(0, 5, 4, 6), FbMs.canonical(0, 6, 4, 7));
		final ImmutableSet<Profile> shuffledProfiles = chosenOnes.stream().map(generator::generate)
				.map(generator::shuffle).collect(ImmutableSet.toImmutableSet());

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
