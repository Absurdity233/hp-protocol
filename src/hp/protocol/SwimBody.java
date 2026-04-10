/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Swim branch payload body for snapshot hash and string digest
 */
package hp.protocol;

import java.util.Objects;

public record SwimBody(int snapshotHashCode, String snapshotStringDigest) implements PacketBody {
    public SwimBody {
        Objects.requireNonNull(snapshotStringDigest, "snapshotStringDigest");
    }
}
