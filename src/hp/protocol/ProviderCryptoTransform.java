/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Provider transform abstraction for logical payload encryption
 */
package hp.protocol;

@FunctionalInterface
public interface ProviderCryptoTransform {
    byte[] encryptLogicalPayload(byte[] logicalPayload);

    static ProviderCryptoTransform identity() {
        return logicalPayload -> logicalPayload;
    }
}
