/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Attack branch payload body for module digest preview data
 */
package hp.protocol;

import java.util.List;

public record AttackBody(int loadedModulePathCount, int loadedModuleDigestCount, List<String> loadedModuleDigestPreviewItems) implements PacketBody {
    public AttackBody {
        loadedModuleDigestPreviewItems = List.copyOf(loadedModuleDigestPreviewItems);
    }
}
