package utils;

public enum HttpMethod {
  GET("GET"),
  POST("POST"),
  POSTAUTH("POSTAUTH"),
  PUT("PUT"),
  PATCH("PATCH"),
  DELETE("DELETE");

  private final String value;

  HttpMethod(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
