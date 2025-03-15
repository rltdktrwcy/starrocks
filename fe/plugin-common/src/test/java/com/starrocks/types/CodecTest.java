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
        long[] testValues = {0, 1, 127, 128, 16383, 16384, 2097151, 2097152, Long.MAX_VALUE};

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
    public void testEncodeDecodeSmallNumbers() throws IOException {
        // Test encoding/decoding small numbers 0-100
        for (long i = 0; i < 100; i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            Codec.encodeVarint64(i, dos);

            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            DataInputStream dis = new DataInputStream(bais);

            long decoded = Codec.decodeVarint64(dis);
            assertEquals(i, decoded);
        }
    }

    @Test
    public void testEncodePowerOfTwo() throws IOException {
        // Test powers of 2
        for (int i = 0; i < 63; i++) {
            long value = 1L << i;

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
    public void testEncodeBoundaryValues() throws IOException {
        // Test boundary values
        long[] boundaryValues = {
            0x7F,           // 127
            0x80,           // 128
            0x3FFF,         // 16383
            0x4000,         // 16384
            0x1FFFFF,       // 2097151
            0x200000,       // 2097152
            Long.MAX_VALUE  // 2^63-1
        };

        for (long value : boundaryValues) {
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
}
