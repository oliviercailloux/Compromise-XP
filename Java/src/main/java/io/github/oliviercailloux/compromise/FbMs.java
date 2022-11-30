package io.github.oliviercailloux.compromise;

public record FbMs(LossPair fb, LossPair ms) {
	public static FbMs canonical(int minX, int maxX, int minY, int maxY) {
		return new FbMs(new LossPair(minX, maxX), new LossPair(maxY, minY));
	}
}
