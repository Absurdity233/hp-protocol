/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Fixed environment block bundle used by the sprint branch
 */
package hp.protocol;

import java.util.Objects;

public record FixedBlocks(String cpuBlock, String machineBlock, String nicBlock, String diskBlock, String siblingInstanceBlock, String userIdBlock) {
    public FixedBlocks {
        Objects.requireNonNull(cpuBlock, "cpuBlock");
        Objects.requireNonNull(machineBlock, "machineBlock");
        Objects.requireNonNull(nicBlock, "nicBlock");
        Objects.requireNonNull(diskBlock, "diskBlock");
        Objects.requireNonNull(siblingInstanceBlock, "siblingInstanceBlock");
        Objects.requireNonNull(userIdBlock, "userIdBlock");
    }
}
