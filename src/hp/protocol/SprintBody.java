/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Sprint branch payload body for mod files paths and fixed blocks
 */
package hp.protocol;

import java.util.List;
import java.util.Objects;

public record SprintBody(
    List<LoadedModFileRecord> loadedModFiles,
    String workingDirectory,
    String javaHomeDirectory,
    FixedBlocks fixedBlocks,
    List<ModsDirectoryJarRecord> modsDirectoryJarFiles
) implements PacketBody {
    public SprintBody {
        loadedModFiles = List.copyOf(loadedModFiles);
        modsDirectoryJarFiles = List.copyOf(modsDirectoryJarFiles);
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(javaHomeDirectory, "javaHomeDirectory");
        Objects.requireNonNull(fixedBlocks, "fixedBlocks");
    }

    public record LoadedModFileRecord(String moduleName, String filePath, String transformedFilePath) {
        public LoadedModFileRecord {
            Objects.requireNonNull(moduleName, "moduleName");
            Objects.requireNonNull(filePath, "filePath");
            Objects.requireNonNull(transformedFilePath, "transformedFilePath");
        }
    }

    public record ModsDirectoryJarRecord(String transformedPathToString, String transformedPathValueOf) {
        public ModsDirectoryJarRecord {
            Objects.requireNonNull(transformedPathToString, "transformedPathToString");
            Objects.requireNonNull(transformedPathValueOf, "transformedPathValueOf");
        }
    }
}
