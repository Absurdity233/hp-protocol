/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Base class for client upload packets with registry and dispatch flow
 */
package hp.protocol;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractClientUploadPacket {
    private static final Map<Integer, Function<byte[], AbstractClientUploadPacket>> packetDecoderRegistry = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Integer> packetIdRegistry = new ConcurrentHashMap<>();
    private static final OuterPayloadFramer outerPayloadFramer = new OuterPayloadFramer();
    private static volatile boolean debugFlag;

    public static void registerPacketDecoder(int packetId, Function<byte[], AbstractClientUploadPacket> decoder) {
        packetDecoderRegistry.put(packetId, Objects.requireNonNull(decoder, "decoder"));
    }

    public static void registerPacketClass(int packetId, Class<?> packetClass) {
        packetIdRegistry.put(Objects.requireNonNull(packetClass, "packetClass"), packetId);
    }

    public static void registerPacket(int packetId, Function<byte[], AbstractClientUploadPacket> decoder, Class<?> packetClass) {
        registerPacketDecoder(packetId, decoder);
        registerPacketClass(packetId, packetClass);
    }

    public static Function<byte[], AbstractClientUploadPacket> getPacketDecoder(int packetId) {
        return packetDecoderRegistry.get(packetId);
    }

    public static OuterPayloadFramer getOuterPayloadFramer() {
        return outerPayloadFramer;
    }

    public static void setDebugFlag(boolean value) {
        debugFlag = value;
    }

    public static boolean isDebugFlagEnabled() {
        return debugFlag;
    }

    public static boolean isDebugFlagDisabled() {
        return !debugFlag;
    }

    public final int getPacketId() {
        Integer packetId = packetIdRegistry.get(getClass());
        if (packetId == null) {
            throw new IllegalStateException("packet class is not registered: " + getClass().getName());
        }
        return packetId;
    }

    public final byte[] dispatchVanillaUpload() {
        return JoinPayloadSerializer.writeLogicalPayload(asJoinUploadPacket());
    }

    public final byte[] dispatchEncryptedUpload() {
        JoinUploadPacket packet = asJoinUploadPacket();
        byte[] logicalPayload = JoinPayloadSerializer.writeLogicalPayload(packet);
        byte[] transformedPayload = packet.applyProviderTransform(logicalPayload);
        return transformOuterPayload(transformedPayload);
    }

    protected byte[] transformOuterPayload(byte[] transformedPayload) {
        return outerPayloadFramer.writeFramedPayload(transformedPayload);
    }

    protected abstract JoinUploadPacket asJoinUploadPacket();
}
