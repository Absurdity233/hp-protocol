/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Runtime snapshot for class aggregates and loaded module digests
 */
package hp.protocol;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class RuntimeScanSnapshot {
    private final Set<String> classAggregatePreviewSet;
    private final Map<String, String> loadedModuleDigestMap;
    private final Set<String> loadedModulePathSet;
    private final int classAggregateTotal;

    public RuntimeScanSnapshot(Set<String> classAggregatePreviewSet, Map<String, String> loadedModuleDigestMap, Set<String> loadedModulePathSet, int classAggregateTotal) {
        this.classAggregatePreviewSet = Set.copyOf(Objects.requireNonNull(classAggregatePreviewSet, "classAggregatePreviewSet"));
        this.loadedModuleDigestMap = Map.copyOf(Objects.requireNonNull(loadedModuleDigestMap, "loadedModuleDigestMap"));
        this.loadedModulePathSet = Set.copyOf(Objects.requireNonNull(loadedModulePathSet, "loadedModulePathSet"));
        this.classAggregateTotal = classAggregateTotal;
    }

    public Set<String> classAggregatePreviewSet() {
        return this.classAggregatePreviewSet;
    }

    public Map<String, String> loadedModuleDigestMap() {
        return this.loadedModuleDigestMap;
    }

    public Set<String> loadedModulePathSet() {
        return this.loadedModulePathSet;
    }

    public int classAggregateTotal() {
        return this.classAggregateTotal;
    }

    public List<String> classAggregatePreviewList() {
        return List.copyOf(this.classAggregatePreviewSet);
    }

    public LinkedHashMap<String, String> loadedModuleDigestPreviewMap() {
        return new LinkedHashMap<>(this.loadedModuleDigestMap);
    }

    public LinkedHashSet<String> loadedModulePathPreviewSet() {
        return new LinkedHashSet<>(this.loadedModulePathSet);
    }
}
