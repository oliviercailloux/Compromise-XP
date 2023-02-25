package io.github.oliviercailloux.compromise;

public record LossPair(int loss1, int loss2) {
	public int min() {
		return Math.min(loss1, loss2);
	}

	public int max() {
		return Math.max(loss1, loss2);
	}

	public int sum() {
		return loss1 + loss2;
	}

	public int dist() {
		return Math.absExact(loss1 - loss2);
	}
}
