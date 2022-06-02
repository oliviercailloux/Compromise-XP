package io.github.oliviercailloux.compromise;

import java.util.List;
import java.util.stream.Collectors;

public class LatexWriter {

	public static String line(List<String> v, String x, String y, int indent) {
		final String ind = "  ".repeat(indent);
		return ind + v.stream().map(s -> s.equals(x) ? "\\bm{%s}".formatted(s) : s)
				.map(s -> s.equals(y) ? "\\boxed{%s}".formatted(s) : s).collect(Collectors.joining("&"));
	}

	private String innerTable(Profile profile, int indent) {
		return line(profile.v1(), profile.fb(), profile.ms(), indent) + "\\\\\n"
				+ line(profile.v2(), profile.fb(), profile.ms(), indent);
	}

	public String example(Profile profile) {
		final String x = profile.fb();
		final String y = profile.ms();
		final LossPair lx = profile.losses(x);
		final LossPair ly = profile.losses(y);
		final String header = "$\\lprof(x) = \\{%s, %s\\}$; $\\lprof(y) = \\{%s, %s\\}$".formatted(lx.loss1(),
				lx.loss2(), ly.loss2(), ly.loss1());
		final String label = "ex:%s%s%s%s".formatted(lx.loss1(), lx.loss2(), ly.loss2(), ly.loss1());
		final String outer = """
				\\begin{example}[%s]
				  \\label{%s}
				  \\begin{equation}
				    \\begin{array}{*{13}c}
				%s
				    \\end{array}
				  \\end{equation}
				\\end{example}
				""".formatted(header, label, innerTable(profile, 3));
		return outer;
	}

}
