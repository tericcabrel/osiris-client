package osirisclient;

/**
 *
 * @author ZEGEEK
 */
public class Utils {
    public static String byteArrayToString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append((char) byteArray[i]);
        }
        return hexStringBuffer.toString();
    }
}
