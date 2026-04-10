/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Loaded mod file entry used to build the sprint branch payload
 */
package hp.protocol;

import java.nio.file.Path;
import java.util.Objects;

public record LoadedModFileEntry(String moduleName, Path filePath) {
    public LoadedModFileEntry {
        Objects.requireNonNull(moduleName, "moduleName");
        Objects.requireNonNull(filePath, "filePath");
    }
}
