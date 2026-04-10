/**
 * @author Absurdity
 * @date 2026/4/11
 * @description In-memory payload buffer implementing both reader and writer
 */
package hp.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class SimplePayloadBuffer implements PayloadWriter, PayloadReader {
    private final ByteArrayOutputStream output;
    private final ByteArrayInputStream input;

    public SimplePayloadBuffer() {
        this.output = new ByteArrayOutputStream();
        this.input = null;
    }

    public SimplePayloadBuffer(byte[] bytes) {
        this.output = null;
        this.input = new ByteArrayInputStream(Objects.requireNonNull(bytes, "bytes"));
    }

    public byte[] toByteArray() {
        if (this.output == null) {
            throw new IllegalStateException("buffer is read-only");
        }
        return this.output.toByteArray();
    }

    @Override
    public void writeString(String value) {
        byte[] bytes = Objects.requireNonNull(value, "value").getBytes(StandardCharsets.UTF_8);
        writeUnsignedVarInt(bytes.length);
        this.output.writeBytes(bytes);
    }

    @Override
    public void writeByte(int value) {
        this.output.write(value);
    }

    @Override
    public void writeInt(int value) {
        this.output.write((value >>> 24) & 0xFF);
        this.output.write((value >>> 16) & 0xFF);
        this.output.write((value >>> 8) & 0xFF);
        this.output.write(value & 0xFF);
    }

    @Override
    public void writeLong(long value) {
        this.output.write((int) ((value >>> 56) & 0xFF));
        this.output.write((int) ((value >>> 48) & 0xFF));
        this.output.write((int) ((value >>> 40) & 0xFF));
        this.output.write((int) ((value >>> 32) & 0xFF));
        this.output.write((int) ((value >>> 24) & 0xFF));
        this.output.write((int) ((value >>> 16) & 0xFF));
        this.output.write((int) ((value >>> 8) & 0xFF));
        this.output.write((int) (value & 0xFF));
    }

    @Override
    public String readString() {
        int length = readUnsignedVarInt();
        byte[] bytes = readBytes(length);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte readByte() {
        return (byte) readRequired();
    }

    @Override
    public int readInt() {
        return (readRequired() << 24)
            | (readRequired() << 16)
            | (readRequired() << 8)
            | readRequired();
    }

    @Override
    public long readLong() {
        return ((long) readRequired() << 56)
            | ((long) readRequired() << 48)
            | ((long) readRequired() << 40)
            | ((long) readRequired() << 32)
            | ((long) readRequired() << 24)
            | ((long) readRequired() << 16)
            | ((long) readRequired() << 8)
            | readRequired();
    }

    public int readUnsignedVarInt() {
        int value = 0;
        int position = 0;
        int current;
        do {
            current = readRequired();
            value |= (current & 0x7F) << position;
            position += 7;
            if (position > 35) {
                throw new IllegalArgumentException("varint is too large");
            }
        } while ((current & 0x80) != 0);
        return value;
    }

    public void writeUnsignedVarInt(int value) {
        int current = value;
        while ((current & -128) != 0) {
            this.output.write(current & 127 | 128);
            current >>>= 7;
        }
        this.output.write(current);
    }

    private byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        int read = this.input.read(bytes, 0, length);
        if (read != length) {
            throw new IllegalStateException("unexpected end of buffer");
        }
        return bytes;
    }

    private int readRequired() {
        int value = this.input.read();
        if (value < 0) {
            throw new IllegalStateException("unexpected end of buffer");
        }
        return value;
    }
}
