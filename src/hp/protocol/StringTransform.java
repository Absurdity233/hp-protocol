/**
 * @author Absurdity
 * @date 2026/4/11
 * @description String transform abstraction used by protocol field encoding
 */
package hp.protocol;

@FunctionalInterface
public interface StringTransform {
    String apply(String value);
}
