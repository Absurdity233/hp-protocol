/**
 * @author Absurdity
 * @date 2026/4/11
 * @description Reader interface for protocol primitive values
 */
package hp.protocol;

public interface PayloadReader {
    String readString();

    byte readByte();

    int readInt();

    long readLong();
}
