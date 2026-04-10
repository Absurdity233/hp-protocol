/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Sneak branch payload body for class aggregate preview data
 */
package hp.protocol;

import java.util.List;

public record SneakBody(int classAggregateTotal, int classAggregatePreviewCount, List<String> classAggregatePreviewItems) implements PacketBody {
    public SneakBody {
        classAggregatePreviewItems = List.copyOf(classAggregatePreviewItems);
    }
}
