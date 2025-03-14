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

public class CodecTest {

    @Test
    public void testEncodeDecodeVarint64_Zero() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(0, out);

        DataInput in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(0, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeVarint64_SmallPositive() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(127, out);

        DataInput in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(127, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeVarint64_LargePositive() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(Integer.MAX_VALUE, out);

        DataInput in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Integer.MAX_VALUE, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeVarint64_MaxLong() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(Long.MAX_VALUE, out);

        DataInput in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(Long.MAX_VALUE, Codec.decodeVarint64(in));
    }

    @Test
    public void testEncodeDecodeVarint64_MultipleValues() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);

        long[] values = {0, 127, 128, 16383, 16384, Integer.MAX_VALUE, Long.MAX_VALUE};
        for (long value : values) {
            Codec.encodeVarint64(value, out);
        }

        DataInput in = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
        for (long expected : values) {
            assertEquals(expected, Codec.decodeVarint64(in));
        }
    }

    @Test(expected = AssertionError.class)
    public void testEncodeVarint64_NegativeValue() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(baos);
        Codec.encodeVarint64(-1, out);
    }

    @Test(expected = IOException.class)
    public void testDecodeVarint64_InvalidInput() throws IOException {
        byte[] invalidData = new byte[]{(byte)0x80}; // Incomplete varint
        DataInput in = new DataInputStream(new ByteArrayInputStream(invalidData));
        Codec.decodeVarint64(in);
    }
}
