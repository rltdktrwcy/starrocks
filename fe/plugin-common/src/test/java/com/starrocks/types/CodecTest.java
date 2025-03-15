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
    public void testEncodeDecodeZero() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        Codec.encodeVarint64(0, dos);

        byte[] bytes = baos.toByteArray();
        assertEquals(1, bytes.length); // Zero should be encoded as single byte

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(dis);
        assertEquals(0, decoded);
    }

    @Test
    public void testEncodeDecodeLargeValues() throws IOException {
        long[] largeValues = {
            Integer.MAX_VALUE,
            Integer.MAX_VALUE + 1L,
            Long.MAX_VALUE - 1,
            Long.MAX_VALUE
        };

        for (long value : largeValues) {
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
        long[] boundaryValues = {
            127,  // Requires 1 byte
            128,  // Requires 2 bytes
            16383,
            16384,
            2097151,
            2097152
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
