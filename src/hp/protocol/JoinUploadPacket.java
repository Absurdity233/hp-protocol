/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Concrete join upload packet with action-specific body building
 */
package hp.protocol;

import java.util.Objects;
import java.util.UUID;

public final class JoinUploadPacket extends AbstractClientUploadPacket {
    private final UploadSessionManager sessionManager;
    private String actionName;
    private String cachedActionTransform;
    private UUID packetUuid;
    private long packetTimestamp;
    private PacketBody packetBody;

    public JoinUploadPacket(UploadSessionManager sessionManager) {
        this(sessionManager, Action.SPRINT.name(), UUID.randomUUID(), System.currentTimeMillis());
    }

    public JoinUploadPacket(UploadSessionManager sessionManager, String actionName, UUID packetUuid, long packetTimestamp) {
        this.sessionManager = Objects.requireNonNull(sessionManager, "sessionManager");
        this.actionName = Objects.requireNonNull(actionName, "actionName");
        this.cachedActionTransform = sessionManager.transformString(actionName);
        this.packetUuid = Objects.requireNonNull(packetUuid, "packetUuid");
        this.packetTimestamp = packetTimestamp;
        this.packetBody = buildBody(action());
    }

    public static JoinUploadPacket fromDecodedBody(CommonHeader header, PacketBody body) {
        UploadSessionManager manager = new UploadSessionManager(null, new RuntimeScanSnapshot(java.util.Set.of(), java.util.Map.of(), java.util.Set.of(), 0), ProviderCryptoTransform.identity(), value -> value, new OuterPayloadFramer(), UUID.fromString(header.sessionUuidString()), header.packetTimestamp(), 0);
        JoinUploadPacket packet = new JoinUploadPacket(manager, header.action().name(), UUID.fromString(header.packetUuidString()), header.packetTimestamp());
        packet.packetBody = Objects.requireNonNull(body, "body");
        return packet;
    }

    public UploadSessionManager sessionManager() {
        return this.sessionManager;
    }

    public String actionName() {
        return this.actionName;
    }

    public String cachedActionTransform() {
        return this.cachedActionTransform;
    }

    public UUID packetUuid() {
        return this.packetUuid;
    }

    public long packetTimestamp() {
        return this.packetTimestamp;
    }

    public Action action() {
        return Action.valueOf(this.actionName);
    }

    public void setActionName(String actionName) {
        this.actionName = Objects.requireNonNull(actionName, "actionName");
        this.cachedActionTransform = this.sessionManager.transformString(actionName);
        this.packetBody = buildBody(action());
    }

    public PacketBody packetBody() {
        return this.packetBody;
    }

    public CommonHeader commonHeader() {
        return new CommonHeader(this.sessionManager.sessionUuid().toString(), this.packetUuid.toString(), this.packetTimestamp, action());
    }

    public SprintBody sprintBody() {
        return (SprintBody) this.packetBody;
    }

    public SneakBody sneakBody() {
        return (SneakBody) this.packetBody;
    }

    public SwimBody swimBody() {
        return (SwimBody) this.packetBody;
    }

    public AttackBody attackBody() {
        return (AttackBody) this.packetBody;
    }

    public byte[] applyProviderTransform(byte[] logicalPayload) {
        return this.sessionManager.providerTransform().encryptLogicalPayload(logicalPayload);
    }

    @Override
    protected JoinUploadPacket asJoinUploadPacket() {
        return this;
    }

    private PacketBody buildBody(Action action) {
        return switch (action) {
            case SPRINT -> this.sessionManager.currentPacketTemplate() != null && this.sessionManager.currentPacketTemplate().packetBody() instanceof SprintBody sprintBody
                ? sprintBody
                : new SprintBody(java.util.List.of(), "", "", new FixedBlocks("", "", "", "", "", ""), java.util.List.of());
            case SNEAK -> JoinPayloadSerializer.buildSneakBody(this.sessionManager.runtimeScanSnapshot(), new java.util.Random(this.packetTimestamp));
            case SWIM -> JoinPayloadSerializer.buildSwimBody(this.sessionManager.runtimeScanSnapshot(), this.sessionManager::transformString);
            case ATTACK -> JoinPayloadSerializer.buildAttackBody(this.sessionManager.runtimeScanSnapshot(), new java.util.Random(this.packetTimestamp));
            case NULL, ASD -> new HeaderOnlyBody();
        };
    }
}
