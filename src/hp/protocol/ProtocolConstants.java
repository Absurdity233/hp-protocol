/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Protocol constants for channel id packet id throttle and limits
 */
package hp.protocol;

public final class ProtocolConstants {
    public static final String UPLOAD_CHANNEL = "heypixel:s2cevent";
    public static final int JOIN_UPLOAD_PACKET_ID = 1;
    public static final int JOIN_THROTTLE_MILLIS = 30000;
    public static final int PREVIEW_LIMIT = 40;

    private ProtocolConstants() {
    }
}
