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
    public void testEncodeDecodeVarint64_Zero() throws IOException {
        testEncodeDecodeVarint64(0);
    }

    @Test
    public void testEncodeDecodeVarint64_Small() throws IOException {
        testEncodeDecodeVarint64(127);
    }

    @Test
    public void testEncodeDecodeVarint64_Medium() throws IOException {
        testEncodeDecodeVarint64(128);
        testEncodeDecodeVarint64(16383);
    }

    @Test
    public void testEncodeDecodeVarint64_Large() throws IOException {
        testEncodeDecodeVarint64(16384);
        testEncodeDecodeVarint64(2097151);
    }

    @Test
    public void testEncodeDecodeVarint64_VeryLarge() throws IOException {
        testEncodeDecodeVarint64(2097152);
        testEncodeDecodeVarint64(268435455);
        testEncodeDecodeVarint64(Long.MAX_VALUE);
    }

    @Test
    public void testEncodeDecodeVarint64_Sequential() throws IOException {
        // Test sequential numbers to catch edge cases
        for (long i = 0; i < 1000; i++) {
            testEncodeDecodeVarint64(i);
        }
    }

    @Test
    public void testEncodeDecodeVarint64_PowersOfTwo() throws IOException {
        for (int i = 0; i < 63; i++) {
            testEncodeDecodeVarint64(1L << i);
        }
    }

    @Test(expected = AssertionError.class)
    public void testEncodeVarint64_Negative() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(-1, out);
    }

    private void testEncodeDecodeVarint64(long value) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        Codec.encodeVarint64(value, out);

        byte[] bytes = baos.toByteArray();
        DataInput in = new DataInputStream(new ByteArrayInputStream(bytes));

        long decoded = Codec.decodeVarint64(in);
        assertEquals(value, decoded);
    }
}
