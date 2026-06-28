package generalutilities;

import java.util.Base64;

/**
 * This class perform operation such as encrypt or decrypt the data.
 */
public class EncryptDecrypt {

    /**
     * Private constructor to prevent instantiation in other class.
     */
    private EncryptDecrypt() {
    }

    /**
     * This method is used for encrypting the data.
     *
     * @param password - for encrypting the data
     * @return         - encrypted type of data
     */
    public static String encryption(String password) {
        byte[] encry = password.getBytes();
        return Base64.getEncoder().encodeToString(encry);
    }

    /**
     * This method is used for decrypting the data.
     *
     * @param password - for decrypting the data
     * @return         - decrypted type of data
     */
    public static String decrypted(String password) {
        byte[] decry = Base64.getDecoder().decode(password);
        return new String(decry);
    }
}
