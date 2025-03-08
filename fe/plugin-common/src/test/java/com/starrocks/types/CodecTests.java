package com.starrocks.types;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class CodecTests {

    @Test
    public void testEncodeDecodeZero() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(0, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);
        assertEquals(0, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(127, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);
        assertEquals(127, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeMultiBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(128, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);
        assertEquals(128, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeLargeNumber() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        long value = Long.MAX_VALUE;
        Codec.encodeVarint64(value, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);
        assertEquals(value, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeSequentialNumbers() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        // Encode multiple numbers sequentially
        long[] values = {1, 127, 128, 16383, 16384, Integer.MAX_VALUE, Long.MAX_VALUE};
        for (long value : values) {
            Codec.encodeVarint64(value, out);
        }

        // Decode and verify
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);
        for (long expected : values) {
            assertEquals(expected, Codec.decodeVarint64(in));
        }
    }

    @Test(expected = AssertionError.class)
    public void testEncodeNegativeValue() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(-1, out);
    }

    @Test
    public void testEncodePowerOfTwo() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        for (int i = 0; i < 63; i++) {
            long value = 1L << i;
            Codec.encodeVarint64(value, out);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        for (int i = 0; i < 63; i++) {
            long expected = 1L << i;
            assertEquals(expected, Codec.decodeVarint64(in));
        }
    }
}
