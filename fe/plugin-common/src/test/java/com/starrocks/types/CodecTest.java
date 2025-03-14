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
    public void testEncodeDecodeVarint64Basic() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        long value = 127;
        Codec.encodeVarint64(value, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(in);
        assertEquals(value, decoded);
    }

    @Test
    public void testEncodeDecodeVarint64Large() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        long value = Long.MAX_VALUE;
        Codec.encodeVarint64(value, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(in);
        assertEquals(value, decoded);
    }

    @Test
    public void testEncodeDecodeVarint64Zero() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        long value = 0;
        Codec.encodeVarint64(value, out);

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        long decoded = Codec.decodeVarint64(in);
        assertEquals(value, decoded);
    }

    @Test
    public void testEncodeDecodeVarint64MultipleValues() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        long[] values = {0, 127, 128, 16383, 16384, Long.MAX_VALUE};

        for (long value : values) {
            Codec.encodeVarint64(value, out);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        for (long expected : values) {
            long decoded = Codec.decodeVarint64(in);
            assertEquals(expected, decoded);
        }
    }

    @Test
    public void testEncodeDecodeVarint64BoundaryValues() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        long[] values = {
            1L << 7,  // 128
            1L << 14, // 16384
            1L << 21, // 2097152
            1L << 28, // 268435456
            1L << 35, // 34359738368
            1L << 42, // 4398046511104
            1L << 49, // 562949953421312
            1L << 56  // 72057594037927936
        };

        for (long value : values) {
            Codec.encodeVarint64(value, out);
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        DataInput in = new DataInputStream(bais);

        for (long expected : values) {
            long decoded = Codec.decodeVarint64(in);
            assertEquals(expected, decoded);
        }
    }
}
