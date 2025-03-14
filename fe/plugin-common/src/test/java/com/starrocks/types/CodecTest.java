package com.starrocks.types;

import org.junit.Test;
import java.io.*;

import static org.junit.Assert.assertEquals;

public class CodecTest {

    @Test
    public void testEncodeDecodeVarint64() throws IOException {
        // Test basic numbers
        assertEncodeDecode(0);
        assertEncodeDecode(1);
        assertEncodeDecode(127);
        assertEncodeDecode(128);
        assertEncodeDecode(16383);
        assertEncodeDecode(16384);
        assertEncodeDecode(2097151);
        assertEncodeDecode(2097152);
        assertEncodeDecode(268435455);
        assertEncodeDecode(268435456);
        assertEncodeDecode(Long.MAX_VALUE);
    }

    @Test
    public void testEncodePowerOfTwo() throws IOException {
        // Test powers of 2
        for (int i = 0; i < 63; i++) {
            assertEncodeDecode(1L << i);
        }
    }

    @Test(expected = AssertionError.class)
    public void testEncodeNegative() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        Codec.encodeVarint64(-1, out);
    }

    @Test
    public void testEncodeDecodeSequential() throws IOException {
        // Test sequential numbers
        for (long i = 0; i < 1000; i++) {
            assertEncodeDecode(i);
        }
    }

    private void assertEncodeDecode(long value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        Codec.encodeVarint64(value, out);

        byte[] bytes = baos.toByteArray();
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

        long decoded = Codec.decodeVarint64(in);
        assertEquals("Value " + value + " was not correctly encoded/decoded", value, decoded);
    }
}
