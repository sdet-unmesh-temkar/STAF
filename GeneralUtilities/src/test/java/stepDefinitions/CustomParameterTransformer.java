package stepDefinitions;

import generalutilities.StringInterpolation;
import io.cucumber.java.ParameterType;

public class CustomParameterTransformer {

    private final StringInterpolation stringInterpolation = new StringInterpolation();

    @ParameterType("(.*\\$\\{\\{(.*?)\\}\\}.*)")
    public Object interpolatedString(String originalString) {
        return stringInterpolation.stringInterpolation(originalString);
    }

}