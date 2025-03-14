package com.starrocks.types;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CodecTest {

    @Test
    public void testEncodeDecodeVarint64() throws IOException {
        // Test cases with different values
        long[] testValues = {
            0L,
            1L,
            127L,
            128L,
            16383L,
            16384L,
            2097151L,
            2097152L,
            268435455L,
            268435456L,
            Long.MAX_VALUE
        };

        for (long value : testValues) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutput out = new DataOutputStream(baos);
            Codec.encodeVarint64(value, out);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            DataInput in = new DataInputStream(bais);
            long decoded = Codec.decodeVarint64(in);

            assertEquals("Value " + value + " should be encoded and decoded correctly", value, decoded);
        }
    }

    @Test
    public void testEncodeVarint64SingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        // Test encoding single byte value (0-127)
        Codec.encodeVarint64(123L, out);

        byte[] bytes = baos.toByteArray();
        assertEquals("Single byte value should be encoded as one byte", 1, bytes.length);
        assertEquals("Encoded byte should match expected value", 123, bytes[0] & 0xFF);
    }

    @Test
    public void testEncodeVarint64MultiByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        // Test encoding multi-byte value
        long value = 130L; // Requires 2 bytes
        Codec.encodeVarint64(value, out);

        byte[] bytes = baos.toByteArray();
        assertEquals("Multi-byte value should be encoded correctly", 2, bytes.length);
    }

    @Test
    public void testDecodeVarint64SingleByte() throws IOException {
        byte[] encoded = new byte[]{0x42}; // Single byte value
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInput in = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(in);
        assertEquals("Single byte value should be decoded correctly", 66L, decoded);
    }

    @Test
    public void testDecodeVarint64MultiByte() throws IOException {
        // Encode 130 as two bytes: 10000010 00000001
        byte[] encoded = new byte[]{(byte)0x82, 0x01};
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInput in = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(in);
        assertEquals("Multi-byte value should be decoded correctly", 130L, decoded);
    }

    @Test
    public void testEncodeDecodeZero() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        Codec.encodeVarint64(0L, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(in);
        assertEquals("Zero should be encoded and decoded correctly", 0L, decoded);
    }
}
