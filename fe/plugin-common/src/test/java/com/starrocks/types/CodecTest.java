package com.starrocks.types;

import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
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
            DataOutputStream dos = new DataOutputStream(baos);

            Codec.encodeVarint64(value, dos);

            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bais);

            long decoded = Codec.decodeVarint64(dis);
            assertEquals(value, decoded);
        }
    }

    @Test
    public void testEncodeSingleByte() throws IOException {
        // Test encoding values that fit in single byte (0-127)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        Codec.encodeVarint64(123L, dos);

        byte[] bytes = baos.toByteArray();
        assertEquals(1, bytes.length);
        assertEquals(123, bytes[0]);
    }

    @Test
    public void testEncodeMultipleBytes() throws IOException {
        // Test encoding values that require multiple bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        Codec.encodeVarint64(130L, dos);

        byte[] bytes = baos.toByteArray();
        assertEquals(2, bytes.length);
    }

    @Test
    public void testDecodeMultipleBytes() throws IOException {
        // Test decoding multi-byte values
        byte[] encoded = new byte[]{(byte)0x82, 0x01}; // Represents 130
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInputStream dis = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(dis);
        assertEquals(130L, decoded);
    }

    @Test
    public void testEncodeZero() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        Codec.encodeVarint64(0L, dos);

        byte[] bytes = baos.toByteArray();
        assertEquals(1, bytes.length);
        assertEquals(0, bytes[0]);
    }

    @Test
    public void testDecodeZero() throws IOException {
        byte[] encoded = new byte[]{0x00};
        ByteArrayInputStream bais = new ByteArrayInputStream(encoded);
        DataInputStream dis = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(dis);
        assertEquals(0L, decoded);
    }
}
