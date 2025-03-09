package com.starrocks.load.loadv2.dpp;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class DppColumnsTest {

    @Test
    public void testConstructorWithList() {
        List<Object> keys = Arrays.asList("a", 1, true);
        DppColumns columns = new DppColumns(keys);
        assertEquals(keys, columns.columns);
    }

    @Test
    public void testConstructorWithIndexes() {
        List<Object> keys = Arrays.asList("a", 1, true, "b");
        DppColumns original = new DppColumns(keys);
        List<Integer> indexes = Arrays.asList(0, 2);

        DppColumns columns = new DppColumns(original, indexes);
        assertEquals(Arrays.asList("a", true), columns.columns);
    }

    @Test
    public void testCompareToWithIntegers() {
        DppColumns col1 = new DppColumns(Arrays.asList(1, 2));
        DppColumns col2 = new DppColumns(Arrays.asList(1, 3));
        DppColumns col3 = new DppColumns(Arrays.asList(1, 2));

        assertTrue(col1.compareTo(col2) < 0);
        assertTrue(col2.compareTo(col1) > 0);
        assertEquals(0, col1.compareTo(col3));
    }

    @Test
    public void testCompareToWithDifferentTypes() {
        DppColumns col1 = new DppColumns(Arrays.asList(1L, 1.5f, true, "test"));
        DppColumns col2 = new DppColumns(Arrays.asList(2L, 1.5f, true, "test"));

        assertTrue(col1.compareTo(col2) < 0);
    }

    @Test
    public void testCompareToWithNulls() {
        DppColumns col1 = new DppColumns(Arrays.asList(null, "test"));
        DppColumns col2 = new DppColumns(Arrays.asList("value", "test"));
        DppColumns col3 = new DppColumns(Arrays.asList(null, "test"));

        assertTrue(col1.compareTo(col2) < 0);
        assertTrue(col2.compareTo(col1) > 0);
        assertEquals(0, col1.compareTo(col3));
    }

    @Test
    public void testCompareToWithDates() {
        Date date1 = new Date(1000);
        Date date2 = new Date(2000);
        DppColumns col1 = new DppColumns(Arrays.asList(date1));
        DppColumns col2 = new DppColumns(Arrays.asList(date2));

        assertTrue(col1.compareTo(col2) < 0);
    }

    @Test
    public void testCompareToWithTimestamps() {
        Timestamp ts1 = new Timestamp(1000);
        Timestamp ts2 = new Timestamp(2000);
        DppColumns col1 = new DppColumns(Arrays.asList(ts1));
        DppColumns col2 = new DppColumns(Arrays.asList(ts2));

        assertTrue(col1.compareTo(col2) < 0);
    }

    @Test
    public void testEquals() {
        List<Object> keys1 = Arrays.asList(1, "test", true);
        List<Object> keys2 = Arrays.asList(1, "test", true);
        List<Object> keys3 = Arrays.asList(2, "test", true);

        DppColumns col1 = new DppColumns(keys1);
        DppColumns col2 = new DppColumns(keys2);
        DppColumns col3 = new DppColumns(keys3);

        assertEquals(col1, col2);
        assertNotEquals(col1, col3);
        assertNotEquals(col1, null);
        assertNotEquals(col1, "not a DppColumns");
        assertEquals(col1, col1);
    }

    @Test
    public void testHashCode() {
        List<Object> keys1 = Arrays.asList(1, "test", true);
        List<Object> keys2 = Arrays.asList(1, "test", true);

        DppColumns col1 = new DppColumns(keys1);
        DppColumns col2 = new DppColumns(keys2);

        assertEquals(col1.hashCode(), col2.hashCode());
    }

    @Test
    public void testToString() {
        List<Object> keys = Arrays.asList(1, "test", true);
        DppColumns columns = new DppColumns(keys);

        String expected = "dppColumns{columns=[1, test, true]}";
        assertEquals(expected, columns.toString());
    }

    @Test
    public void testDppColumnsComparator() {
        DppColumns col1 = new DppColumns(Arrays.asList(1, "test"));
        DppColumns col2 = new DppColumns(Arrays.asList(2, "test"));

        DppColumnsComparator comparator = new DppColumnsComparator();
        assertTrue(comparator.compare(col1, col2) < 0);
        assertTrue(comparator.compare(col2, col1) > 0);
        assertEquals(0, comparator.compare(col1, col1));
    }
}
