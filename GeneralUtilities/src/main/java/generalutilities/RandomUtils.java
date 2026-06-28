package generalutilities;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.security.SecureRandom;
import java.util.Random;

/**
 * This class perform operation related to random utils.
 * This class contain methods such as getRandomNumberAsString, generateRandomString etc .
 */
public class RandomUtils {
    Random random = new SecureRandom();

    /**
     * This method is used to get random number as string.
     *
     * @param length - length in integer to get random number as string
     * @return sb    - string
     */
    public static String getRandomNumberAsString(int length) {
        var sb = new StringBuilder(length);
        for (var i = 0; i < length; ++i) {
            sb.append((char) (48 + new RandomUtils().random.nextInt(10)));
        }
        return sb.toString();
    }

    /**
     * This method is used to get random number in the given integer range.
     *
     * @param min     - min integer of range to get random number in the given integer range
     * @param max     - max integer of range to get random number in the given integer range
     * @return random - Integer
     */
    public static int getRandomIntegerInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        } else {
            return new RandomUtils().random.nextInt(max - min + 1) + min;
        }
    }

    /**
     * This method is used to get random string.
     *
     * @param length  - length to get random string
     * @return        - random string
     */
    public static String getRandomString(int length) {
        return generateRandomString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", length);
    }

    /**
     * This method is used to generate random string.
     *
     * @param candidateChars - all the characters to generate random string
     * @param length         - length to generate random string
     * @return               - random string
     */
    public static String generateRandomString(String candidateChars, int length) {
        if (candidateChars == null || candidateChars.isEmpty()) {
            candidateChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        }
        var stringBuilder = new StringBuilder();
        for (var i = 0; i < length; ++i) {
            stringBuilder.append(candidateChars.charAt(new RandomUtils().random.nextInt(candidateChars.length())));
        }
        return stringBuilder.toString();
    }

    /**
     * This method is used to get random hex number as string.
     *
     * @return String - random hex number
     */
    public static String getRandomForXB3TraceId() {
        return getRandomHexString(16);
    }

    /**
     * This method is used to generate random hex number as String.
     *
     * @param length  - to generate random hex number as String
     * @return String - random hex number
     */
    public static String getRandomHexString(int length) {
        var sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(Integer.toHexString(new RandomUtils().random.nextInt()));
        }
        return sb.toString().substring(0, length) + Thread.currentThread().getId();
    }

    /**
     * This method converts gson to json.
     *
     * @param body              - to convert gson pretty.
     * @return prettyJsonString - String
     */
    public static String gsonPretty(String body) {
        var gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = null;
        assert false;
        var jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        var je = jsonObject.get(body);
        return gson.toJson(je);
    }

    /**
     * This method generates random number.
     *
     * @param digit   - to generates random number
     * @return subStr - long
     */
    public long randomNumberGenerator(int digit) {
        long timeSeed = System.nanoTime();
        double randSeed = random.nextDouble() * 1000;
        long midSeed = (long) (timeSeed * randSeed);
        String s = midSeed + "";
        var subStr = s.substring(0, digit);
        return Integer.parseInt(subStr);
    }
}
