package com.starrocks.types;

import org.junit.Test;
import java.io.*;

import static org.junit.Assert.assertEquals;

public class CodecTest {

    @Test
    public void testEncodeDecodeVarint64() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        // Test case 1: Encode/decode 0
        Codec.encodeVarint64(0, out);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(0, Codec.decodeVarint64(in));

        // Test case 2: Encode/decode small positive number
        baos.reset();
        Codec.encodeVarint64(127, out);
        in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(127, Codec.decodeVarint64(in));

        // Test case 3: Encode/decode medium number
        baos.reset();
        Codec.encodeVarint64(16383, out); // 2^14 - 1
        in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(16383, Codec.decodeVarint64(in));

        // Test case 4: Encode/decode large number
        baos.reset();
        Codec.encodeVarint64(Integer.MAX_VALUE, out);
        in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Integer.MAX_VALUE, Codec.decodeVarint64(in));

        // Test case 5: Encode/decode max long value
        baos.reset();
        Codec.encodeVarint64(Long.MAX_VALUE, out);
        in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Long.MAX_VALUE, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeBoundaryValues() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        // Test powers of 2
        long[] testValues = {
            1L,
            128L,  // 2^7
            16384L, // 2^14
            2097152L, // 2^21
            268435456L, // 2^28
            34359738368L, // 2^35
            4398046511104L, // 2^42
            562949953421312L, // 2^49
            72057594037927936L // 2^56
        };

        for (long value : testValues) {
            baos.reset();
            Codec.encodeVarint64(value, out);
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
            assertEquals(value, Codec.decodeVarint64(in));
        }
    }

    @Test
    public void testConsecutiveEncodeDecode() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        // Encode multiple values consecutively
        long[] values = {0L, 127L, 128L, 16383L, 16384L, 1000000L};
        for (long value : values) {
            Codec.encodeVarint64(value, out);
        }

        // Decode them back in order
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        for (long expected : values) {
            assertEquals(expected, Codec.decodeVarint64(in));
        }
    }
}
