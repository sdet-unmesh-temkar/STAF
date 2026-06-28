package utils;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

// Custom TypeAdapter for Enum serialization and deserialization using toString() value
public class EnumToStringAdapter<T extends Enum<T>> extends TypeAdapter<T> {
  private final Class<T> enumClass;

  public EnumToStringAdapter(Class<T> enumClass) {
    this.enumClass = enumClass;
  }

  @Override
  public void write(JsonWriter out, T value) throws IOException {
    if (value == null) {
      out.nullValue();
    } else {
      out.value(value.toString());  // Use the toString() method for serialization
    }
  }

  @Override
  public T read(JsonReader in) throws IOException {
    String stringValue = in.nextString();

    // Iterate over enum constants to find the one that matches the toString() value
    for (T enumConstant : enumClass.getEnumConstants()) {
      if (enumConstant.toString().equals(stringValue)) {
        return enumConstant;
      }
    }

    throw new JsonParseException("Unknown value for enum " + enumClass.getSimpleName() + ": " + stringValue);
  }
}
