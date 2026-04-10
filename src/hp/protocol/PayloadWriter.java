/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Writer interface for protocol primitive values
 */
package hp.protocol;

public interface PayloadWriter {
    void writeString(String value);

    void writeByte(int value);

    void writeInt(int value);

    void writeLong(long value);
}
