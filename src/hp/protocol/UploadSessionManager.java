/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Session state holder for upload packet building and transforms
 */
package hp.protocol;

import java.util.Objects;
import java.util.UUID;

public final class UploadSessionManager {
    private JoinUploadPacket currentPacketTemplate;
    private RuntimeScanSnapshot runtimeScanSnapshot;
    private ProviderCryptoTransform providerTransform;
    private StringTransform secondaryTransform;
    private OuterPayloadFramer outerPayloadTransform;
    private UUID sessionUuid;
    private long lastUploadTimestamp;
    private int uploadStateCode;

    public UploadSessionManager(
        JoinUploadPacket currentPacketTemplate,
        RuntimeScanSnapshot runtimeScanSnapshot,
        ProviderCryptoTransform providerTransform,
        StringTransform secondaryTransform,
        OuterPayloadFramer outerPayloadTransform,
        UUID sessionUuid,
        long lastUploadTimestamp,
        int uploadStateCode
    ) {
        this.currentPacketTemplate = currentPacketTemplate;
        this.runtimeScanSnapshot = Objects.requireNonNull(runtimeScanSnapshot, "runtimeScanSnapshot");
        this.providerTransform = Objects.requireNonNull(providerTransform, "providerTransform");
        this.secondaryTransform = Objects.requireNonNull(secondaryTransform, "secondaryTransform");
        this.outerPayloadTransform = Objects.requireNonNull(outerPayloadTransform, "outerPayloadTransform");
        this.sessionUuid = Objects.requireNonNull(sessionUuid, "sessionUuid");
        this.lastUploadTimestamp = lastUploadTimestamp;
        this.uploadStateCode = uploadStateCode;
    }

    public JoinUploadPacket currentPacketTemplate() {
        return this.currentPacketTemplate;
    }

    public void currentPacketTemplate(JoinUploadPacket currentPacketTemplate) {
        this.currentPacketTemplate = currentPacketTemplate;
    }

    public RuntimeScanSnapshot runtimeScanSnapshot() {
        return this.runtimeScanSnapshot;
    }

    public ProviderCryptoTransform providerTransform() {
        return this.providerTransform;
    }

    public StringTransform secondaryTransform() {
        return this.secondaryTransform;
    }

    public OuterPayloadFramer outerPayloadTransform() {
        return this.outerPayloadTransform;
    }

    public UUID sessionUuid() {
        return this.sessionUuid;
    }

    public long lastUploadTimestamp() {
        return this.lastUploadTimestamp;
    }

    public void lastUploadTimestamp(long lastUploadTimestamp) {
        this.lastUploadTimestamp = lastUploadTimestamp;
    }

    public int uploadStateCode() {
        return this.uploadStateCode;
    }

    public String transformString(String value) {
        return this.secondaryTransform.apply(value);
    }
}
