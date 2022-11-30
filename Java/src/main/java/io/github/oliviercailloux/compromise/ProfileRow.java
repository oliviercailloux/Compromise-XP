package io.github.oliviercailloux.compromise;

import com.univocity.parsers.annotations.Parsed;

class ProfileRow {

	@Parsed
	public int dlPx;
	@Parsed
	public int dlPy;
	@Parsed(field = "max lPx")
	public int maxLPx;
	@Parsed(field = "max lPy")
	public int maxLPy;
	@Parsed(field = "min lPx")
	public int minLPx;
	@Parsed(field = "avg lPx")
	public double avgLPx;
	@Parsed(field = "min lPy")
	public int minLPy;
	@Parsed(field = "avg lPy")
	public double avgLPy;
	public int e;
	public boolean top;
	public int dmin;
	public double davg;
	public int delta;
	@Parsed(field = "cl coarse")
	public int clCoarse;
	public int subclass;

	public LossPair xl() {
		return new LossPair(minLPx, maxLPx);
	}

	public LossPair yl() {
		return new LossPair(maxLPy, minLPy);
	}
}