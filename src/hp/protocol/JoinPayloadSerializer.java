/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Serializer and builder for join upload logical payloads
 */
package hp.protocol;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public final class JoinPayloadSerializer {
    private JoinPayloadSerializer() {
    }

    public static byte[] writeLogicalPayload(JoinUploadPacket packet) {
        SimplePayloadBuffer buffer = new SimplePayloadBuffer();
        writeLogicalPayload(buffer, packet);
        return buffer.toByteArray();
    }

    public static void writeLogicalPayload(PayloadWriter writer, JoinUploadPacket packet) {
        Objects.requireNonNull(writer, "writer");
        Objects.requireNonNull(packet, "packet");
        writeCommonHeader(writer, packet.commonHeader());
        switch (packet.action()) {
            case SPRINT -> writeSprintBody(writer, packet.sprintBody());
            case SNEAK -> writeSneakBody(writer, packet.sneakBody());
            case SWIM -> writeSwimBody(writer, packet.swimBody());
            case ATTACK -> writeAttackBody(writer, packet.attackBody());
            case NULL, ASD -> {
            }
        }
    }

    public static JoinUploadPacket readLogicalPayload(byte[] bytes) {
        return readLogicalPayload(new SimplePayloadBuffer(bytes));
    }

    public static JoinUploadPacket readLogicalPayload(PayloadReader reader) {
        CommonHeader header = readCommonHeader(reader);
        return switch (header.action()) {
            case SPRINT -> JoinUploadPacket.fromDecodedBody(header, readSprintBody(reader));
            case SNEAK -> JoinUploadPacket.fromDecodedBody(header, readSneakBody(reader));
            case SWIM -> JoinUploadPacket.fromDecodedBody(header, readSwimBody(reader));
            case ATTACK -> JoinUploadPacket.fromDecodedBody(header, readAttackBody(reader));
            case NULL, ASD -> JoinUploadPacket.fromDecodedBody(header, new HeaderOnlyBody());
        };
    }

    public static void writeCommonHeader(PayloadWriter writer, CommonHeader header) {
        writer.writeString(header.sessionUuidString());
        writer.writeByte(header.action().networkByte());
        writer.writeString(header.packetUuidString());
        writer.writeLong(header.packetTimestamp());
    }

    public static CommonHeader readCommonHeader(PayloadReader reader) {
        return new CommonHeader(
            reader.readString(),
            reader.readString(),
            reader.readLong(),
            Action.decode(reader.readByte() & 0xFF)
        );
    }

    public static void writeSprintBody(PayloadWriter writer, SprintBody body) {
        writer.writeInt(body.loadedModFiles().size());
        for (SprintBody.LoadedModFileRecord entry : body.loadedModFiles()) {
            writer.writeString(entry.moduleName());
            writer.writeString(entry.filePath());
            writer.writeString(entry.transformedFilePath());
        }
        writer.writeString(body.workingDirectory());
        writer.writeString(body.javaHomeDirectory());
        writer.writeString(body.fixedBlocks().cpuBlock());
        writer.writeString(body.fixedBlocks().machineBlock());
        writer.writeString(body.fixedBlocks().nicBlock());
        writer.writeString(body.fixedBlocks().diskBlock());
        writer.writeString(body.fixedBlocks().siblingInstanceBlock());
        writer.writeString(body.fixedBlocks().userIdBlock());
        writer.writeInt(body.modsDirectoryJarFiles().size());
        for (SprintBody.ModsDirectoryJarRecord entry : body.modsDirectoryJarFiles()) {
            writer.writeString(entry.transformedPathToString());
            writer.writeString(entry.transformedPathValueOf());
        }
    }

    public static SprintBody readSprintBody(PayloadReader reader) {
        int firstSize = reader.readInt();
        List<SprintBody.LoadedModFileRecord> firstEntries = new ArrayList<>(firstSize);
        for (int i = 0; i < firstSize; i++) {
            firstEntries.add(new SprintBody.LoadedModFileRecord(reader.readString(), reader.readString(), reader.readString()));
        }
        String workingDirectory = reader.readString();
        String javaHomeDirectory = reader.readString();
        FixedBlocks fixedBlocks = new FixedBlocks(
            reader.readString(),
            reader.readString(),
            reader.readString(),
            reader.readString(),
            reader.readString(),
            reader.readString()
        );
        int secondSize = reader.readInt();
        List<SprintBody.ModsDirectoryJarRecord> secondEntries = new ArrayList<>(secondSize);
        for (int i = 0; i < secondSize; i++) {
            secondEntries.add(new SprintBody.ModsDirectoryJarRecord(reader.readString(), reader.readString()));
        }
        return new SprintBody(firstEntries, workingDirectory, javaHomeDirectory, fixedBlocks, secondEntries);
    }

    public static void writeSneakBody(PayloadWriter writer, SneakBody body) {
        writer.writeInt(body.classAggregateTotal());
        writer.writeInt(body.classAggregatePreviewCount());
        writer.writeInt(body.classAggregatePreviewItems().size());
        for (String item : body.classAggregatePreviewItems()) {
            writer.writeString(item);
        }
    }

    public static SneakBody readSneakBody(PayloadReader reader) {
        int classAggregateTotal = reader.readInt();
        int previewCount = reader.readInt();
        int previewSize = reader.readInt();
        List<String> items = new ArrayList<>(previewSize);
        for (int i = 0; i < previewSize; i++) {
            items.add(reader.readString());
        }
        return new SneakBody(classAggregateTotal, previewCount, items);
    }

    public static void writeSwimBody(PayloadWriter writer, SwimBody body) {
        writer.writeInt(body.snapshotHashCode());
        writer.writeString(body.snapshotStringDigest());
    }

    public static SwimBody readSwimBody(PayloadReader reader) {
        return new SwimBody(reader.readInt(), reader.readString());
    }

    public static void writeAttackBody(PayloadWriter writer, AttackBody body) {
        writer.writeInt(body.loadedModulePathCount());
        writer.writeInt(body.loadedModuleDigestCount());
        writer.writeInt(body.loadedModuleDigestPreviewItems().size());
        for (String item : body.loadedModuleDigestPreviewItems()) {
            writer.writeString(item);
        }
    }

    public static AttackBody readAttackBody(PayloadReader reader) {
        int pathCount = reader.readInt();
        int digestCount = reader.readInt();
        int previewSize = reader.readInt();
        List<String> items = new ArrayList<>(previewSize);
        for (int i = 0; i < previewSize; i++) {
            items.add(reader.readString());
        }
        return new AttackBody(pathCount, digestCount, items);
    }

    public static SprintBody buildSprintBody(List<LoadedModFileEntry> loadedModFiles, Path workingDirectory, Path javaHomeDirectory, FixedBlocks fixedBlocks, List<Path> modsDirectoryJarFiles, StringTransform transform) {
        List<SprintBody.LoadedModFileRecord> firstEntries = new ArrayList<>(loadedModFiles.size());
        for (LoadedModFileEntry entry : loadedModFiles) {
            String filePath = entry.filePath().toString();
            firstEntries.add(new SprintBody.LoadedModFileRecord(entry.moduleName(), filePath, transform.apply(filePath)));
        }
        List<SprintBody.ModsDirectoryJarRecord> secondEntries = new ArrayList<>(modsDirectoryJarFiles.size());
        for (Path path : modsDirectoryJarFiles) {
            String text = path.toString();
            secondEntries.add(new SprintBody.ModsDirectoryJarRecord(transform.apply(text), transform.apply(String.valueOf(path))));
        }
        return new SprintBody(firstEntries, workingDirectory.toString(), javaHomeDirectory.toString(), fixedBlocks, secondEntries);
    }

    public static SneakBody buildSneakBody(RuntimeScanSnapshot snapshot, Random random) {
        List<String> previewItems = new ArrayList<>(snapshot.classAggregatePreviewList());
        Collections.shuffle(previewItems, random);
        if (previewItems.size() > ProtocolConstants.PREVIEW_LIMIT) {
            previewItems = new ArrayList<>(previewItems.subList(0, ProtocolConstants.PREVIEW_LIMIT));
        }
        return new SneakBody(snapshot.classAggregateTotal(), snapshot.classAggregatePreviewSet().size(), previewItems);
    }

    public static SwimBody buildSwimBody(RuntimeScanSnapshot snapshot, StringTransform transform) {
        return new SwimBody(snapshot.hashCode(), transform.apply(snapshot.toString()));
    }

    public static AttackBody buildAttackBody(RuntimeScanSnapshot snapshot, Random random) {
        List<String> keys = new ArrayList<>(snapshot.loadedModuleDigestMap().keySet());
        Collections.shuffle(keys, random);
        if (keys.size() > ProtocolConstants.PREVIEW_LIMIT) {
            keys = new ArrayList<>(keys.subList(0, ProtocolConstants.PREVIEW_LIMIT));
        }
        List<String> previewItems = new ArrayList<>(keys.size());
        for (String key : keys) {
            previewItems.add(key + ":" + snapshot.loadedModuleDigestMap().get(key));
        }
        return new AttackBody(snapshot.loadedModulePathSet().size(), snapshot.loadedModuleDigestMap().size(), previewItems);
    }

    public static List<Path> selectModsDirectoryJarFiles(List<Path> files) {
        List<Path> result = new ArrayList<>();
        for (Path file : files) {
            if (file != null && file.getFileName() != null && file.getFileName().toString().endsWith(".jar")) {
                result.add(file);
            }
        }
        return result;
    }

    public static RuntimeScanSnapshot snapshotOf(List<String> classAggregatePreviewItems, Map<String, String> loadedModuleDigestMap, List<String> loadedModulePathItems, int classAggregateTotal) {
        return new RuntimeScanSnapshot(Set.copyOf(classAggregatePreviewItems), loadedModuleDigestMap, Set.copyOf(loadedModulePathItems), classAggregateTotal);
    }
}
