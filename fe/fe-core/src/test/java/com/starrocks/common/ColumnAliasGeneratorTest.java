// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.starrocks.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ColumnAliasGeneratorTest {

    @Test
    public void testConstructorWithNullPrefix() {
        List<String> existingLabels = new ArrayList<>();
        ColumnAliasGenerator generator = new ColumnAliasGenerator(existingLabels, null);
        String alias = generator.getNextAlias();
        assertNotNull(alias);
        assertEquals("$c$1", alias);
    }

    @Test
    public void testConstructorWithCustomPrefix() {
        List<String> existingLabels = new ArrayList<>();
        ColumnAliasGenerator generator = new ColumnAliasGenerator(existingLabels, "test_");
        String alias = generator.getNextAlias();
        assertNotNull(alias);
        assertEquals("test_1", alias);
    }

    @Test
    public void testGetNextAliasWithExistingLabels() {
        List<String> existingLabels = Arrays.asList("$c$1", "$c$2");
        ColumnAliasGenerator generator = new ColumnAliasGenerator(existingLabels, null);
        String alias = generator.getNextAlias();
        assertNotNull(alias);
        assertEquals("$c$3", alias);
    }

    @Test
    public void testGetNextAliasSequential() {
        List<String> existingLabels = new ArrayList<>();
        ColumnAliasGenerator generator = new ColumnAliasGenerator(existingLabels, null);
        assertEquals("$c$1", generator.getNextAlias());
        assertEquals("$c$2", generator.getNextAlias());
        assertEquals("$c$3", generator.getNextAlias());
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorWithNullExistingLabels() {
        new ColumnAliasGenerator(null, null);
    }

    @Test
    public void testGetNextAliasWithConflict() {
        List<String> existingLabels = Arrays.asList("$c$1", "$c$2", "$c$3");
        ColumnAliasGenerator generator = new ColumnAliasGenerator(existingLabels, null);
        assertEquals("$c$4", generator.getNextAlias());
    }
}
