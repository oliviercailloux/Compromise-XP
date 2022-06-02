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

public class App {
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) throws Exception {
		new App().proceed();
	}

	public void proceed() throws IOException {
		LOGGER.info("Hello World!");

		final List<ProfileRow> beans = read();
		final ImmutableSet<Profile> profiles = beans.stream()
				.map(r -> ProfileGenerator.sized(13).generate(r.xl(), r.yl())).collect(ImmutableSet.toImmutableSet());
		LOGGER.info("Generated {} profiles.", profiles.size());
		final String latex = "%Generated – please do not edit.\n\n"
				+ profiles.stream().map(Profile::example).collect(Collectors.joining("\n"));
		Files.writeString(Path.of("../examples.tex"), latex);
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
