package io.github.oliviercailloux.compromise;

public record FbMsK (LossPair fb, LossPair ms, ExampleKind k) {
  public static FbMsK canonical(int minX, int maxX, int minY, int maxY, ExampleKind k) {
    return new FbMsK(new LossPair(minX, maxX), new LossPair(maxY, minY), k);
  }

  public static FbMsK b(int minX, int maxX, int minY, int maxY) {
    return new FbMsK(new LossPair(minX, maxX), new LossPair(maxY, minY), ExampleKind.B);
  }

  public static FbMsK d(int minX, int maxX, int minY, int maxY) {
    return new FbMsK(new LossPair(minX, maxX), new LossPair(maxY, minY), ExampleKind.D);
  }

  public FbMs fbMs() {
    return FbMs.canonical(fb.min(), fb.max(), ms.min(), ms.max());
  }
}
