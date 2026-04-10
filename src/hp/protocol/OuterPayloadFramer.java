/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Outer framing writer for packet id and varint length
 */
package hp.protocol;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public final class OuterPayloadFramer {
    public byte[] writeFramedPayload(byte[] transformedPayload) {
        Objects.requireNonNull(transformedPayload, "transformedPayload");
        ByteArrayOutputStream out = new ByteArrayOutputStream(transformedPayload.length + 8);
        writeUnsignedVarInt(out, transformedPayload.length + 1);
        writeUnsignedVarInt(out, ProtocolConstants.JOIN_UPLOAD_PACKET_ID);
        out.writeBytes(transformedPayload);
        return out.toByteArray();
    }

    private static void writeUnsignedVarInt(ByteArrayOutputStream out, int value) {
        int current = value;
        while ((current & -128) != 0) {
            out.write(current & 127 | 128);
            current >>>= 7;
        }
        out.write(current);
    }
}
