/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Common upload packet header fields
 */
package hp.protocol;

import java.util.Objects;

public record CommonHeader(String sessionUuidString, String packetUuidString, long packetTimestamp, Action action) {
    public CommonHeader {
        Objects.requireNonNull(sessionUuidString, "sessionUuidString");
        Objects.requireNonNull(packetUuidString, "packetUuidString");
        Objects.requireNonNull(action, "action");
    }
}
