package io.github.oliviercailloux.compromise;

import java.util.List;
import java.util.stream.Collectors;

public class LatexWriter {

	public static String line(List<String> v, String x, String y) {
		return v.stream().map(s -> s.equals(x) ? "\\bm{%s}".formatted(s) : s)
				.map(s -> s.equals(y) ? "\\boxed{%s}".formatted(s) : s).collect(Collectors.joining("&"));
	}

	public static String line(List<String> v) {
		return v.stream().collect(Collectors.joining("&"));
	}

	private String innerTable(Profile profile, boolean highlight) {
		if (highlight) {
			return line(profile.v1(), profile.fb(), profile.ms()) + "\\\\\n"
					+ line(profile.v2(), profile.fb(), profile.ms());
		}
		return line(profile.v1()) + "\\\\\n" + line(profile.v2());
	}

	public String equation(Profile profile, boolean highlight) {
		final String equation = """
				\\begin{equation}
				  \\begin{array}{*{13}c}
				%s\
				  \\end{array}
				\\end{equation}""".formatted(innerTable(profile, highlight).indent(4));
		return equation;
	}

	public String example(Profile profile, ExampleKind kind) {
		final String equation = equation(profile, true);
		final String x = profile.fb();
		final String y = profile.ms();
		final LossPair lx = profile.losses(x);
		final LossPair ly = profile.losses(y);
		final String header = "$\\lprof(x) = \\{%s, %s\\}$; $\\lprof(y) = \\{%s, %s\\}$; %s".formatted(lx.loss1(),
				lx.loss2(), ly.loss2(), ly.loss1(), kind);
		final String label = "ex:%s%s%s%s%s".formatted(lx.loss1(), lx.loss2(), ly.loss2(), ly.loss1(), kind);
		final String outer = """
				\\begin{example}[%s]
				  \\label{%s}
				%s\
				\\end{example}
				""".formatted(header, label, equation.indent(2));
		return outer;
	}

}
