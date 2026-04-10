/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Controller that throttles and submits join upload packets
 */
package hp.protocol;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class JoinUploadController {
    private static final Map<String, String> uploadKeyMapping = new ConcurrentHashMap<>();
    private static volatile long lastJoinUploadMillis;
    private static volatile boolean joinUploadScheduled;
    private static volatile boolean controllerEnabled = true;

    private final Map<String, String> pathRewriteCache = new ConcurrentHashMap<>();
    private final Map<String, String> uiStateCache = new ConcurrentHashMap<>();
    private final Map<Integer, Long> decodedLongSeeds = new ConcurrentHashMap<>();

    public JoinUploadPacket submitJoinUpload(UploadSessionManager manager) {
        long now = System.currentTimeMillis();
        if (!controllerEnabled || joinUploadScheduled || now - lastJoinUploadMillis < decodeThrottleMillis(0, 0L)) {
            return null;
        }
        joinUploadScheduled = true;
        try {
            JoinUploadPacket packet = new JoinUploadPacket(Objects.requireNonNull(manager, "manager"), Action.SPRINT.name(), UUID.randomUUID(), now);
            manager.currentPacketTemplate(packet);
            manager.lastUploadTimestamp(now);
            lastJoinUploadMillis = now;
            return packet;
        } finally {
            joinUploadScheduled = false;
        }
    }

    public void onEntityJoinLevel(UploadSessionManager manager) {
        submitJoinUpload(manager);
    }

    public void onClientLoggingIn() {
    }

    public void onClientLoggingOut() {
        this.pathRewriteCache.clear();
        this.uiStateCache.clear();
    }

    public void onKeyInput(String key, String value) {
        uploadKeyMapping.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
    }

    public void onCommonSetup(String key, String value) {
        this.pathRewriteCache.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
    }

    public void onClientSetup(String key, Supplier<String> supplier) {
        this.uiStateCache.put(Objects.requireNonNull(key, "key"), submitNamedTask(key, supplier));
    }

    public void writeNamedStringPayload(String name, PayloadWriter writer) {
        writer.writeString(name);
    }

    public String readNamedStringPayload(PayloadReader reader) {
        return reader.readString();
    }

    public String submitNamedTask(String name, Supplier<String> supplier) {
        String value = Objects.requireNonNull(supplier, "supplier").get();
        this.uiStateCache.put(Objects.requireNonNull(name, "name"), value);
        return value;
    }

    public void setControllerEnabled(boolean controllerEnabled) {
        JoinUploadController.controllerEnabled = controllerEnabled;
    }

    public boolean isControllerEnabled() {
        return controllerEnabled;
    }

    public boolean isControllerDisabled() {
        return !controllerEnabled;
    }

    public long decodeThrottleMillis(int ignored, long seed) {
        this.decodedLongSeeds.put(ignored, seed);
        return ProtocolConstants.JOIN_THROTTLE_MILLIS;
    }
}
