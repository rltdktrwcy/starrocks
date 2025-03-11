// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.common.util;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UnionFindTest {

    private UnionFind<Integer> unionFind;

    @Before
    public void setUp() {
        unionFind = new UnionFind<>();
    }

    @Test
    public void testAdd() {
        unionFind.add(1, 2, 3);
        Assert.assertTrue(unionFind.find(1));
        Assert.assertTrue(unionFind.find(2));
        Assert.assertTrue(unionFind.find(3));
        Assert.assertFalse(unionFind.find(4));
    }

    @Test
    public void testUnion() {
        unionFind.add(1, 2, 3, 4);
        unionFind.union(1, 2);
        unionFind.union(2, 3);

        Set<Integer> group = unionFind.getEquivGroup(1);
        Assert.assertEquals(3, group.size());
        Assert.assertTrue(group.containsAll(Sets.newHashSet(1, 2, 3)));
        Assert.assertFalse(group.contains(4));
    }

    @Test
    public void testGetEquivGroups() {
        unionFind.add(1, 2, 3, 4, 5);
        unionFind.union(1, 2);
        unionFind.union(3, 4);

        Map<Integer, Set<Integer>> groups = unionFind.getEquivGroups(Sets.newHashSet(1, 2, 3, 4, 5));
        Assert.assertEquals(4, groups.size());
        Assert.assertTrue(groups.get(1).containsAll(Sets.newHashSet(1, 2)));
        Assert.assertTrue(groups.get(2).containsAll(Sets.newHashSet(1, 2)));
        Assert.assertTrue(groups.get(3).containsAll(Sets.newHashSet(3, 4)));
        Assert.assertTrue(groups.get(4).containsAll(Sets.newHashSet(3, 4)));
    }

    @Test
    public void testGetEquivGroup() {
        unionFind.add(1, 2, 3);
        unionFind.union(1, 2);

        Set<Integer> group = unionFind.getEquivGroup(1);
        Assert.assertEquals(2, group.size());
        Assert.assertTrue(group.containsAll(Sets.newHashSet(1, 2)));

        Set<Integer> emptyGroup = unionFind.getEquivGroup(4);
        Assert.assertTrue(emptyGroup.isEmpty());
    }

    @Test
    public void testGetGroupIdOrAdd() {
        int id1 = unionFind.getGroupIdOrAdd(1);
        int id2 = unionFind.getGroupIdOrAdd(1);
        Assert.assertEquals(id1, id2);

        Assert.assertTrue(unionFind.find(1));
    }

    @Test
    public void testGetGroupId() {
        unionFind.add(1);
        Optional<Integer> id = unionFind.getGroupId(1);
        Assert.assertTrue(id.isPresent());
        Assert.assertEquals(0, (int) id.get());

        Optional<Integer> nonExistId = unionFind.getGroupId(2);
        Assert.assertFalse(nonExistId.isPresent());
    }

    @Test
    public void testGetGroup() {
        unionFind.add(1, 2);
        unionFind.union(1, 2);

        Set<Integer> group = unionFind.getGroup(0);
        Assert.assertEquals(2, group.size());
        Assert.assertTrue(group.containsAll(Sets.newHashSet(1, 2)));

        Set<Integer> emptyGroup = unionFind.getGroup(99);
        Assert.assertTrue(emptyGroup.isEmpty());
    }

    @Test
    public void testGetAllGroups() {
        unionFind.add(1, 2, 3, 4);
        unionFind.union(1, 2);
        unionFind.union(3, 4);

        Assert.assertEquals(2, unionFind.getAllGroups().size());
    }

    @Test
    public void testContainsAll() {
        unionFind.add(1, 2, 3);
        Assert.assertTrue(unionFind.containsAll(Sets.newHashSet(1, 2, 3)));
        Assert.assertFalse(unionFind.containsAll(Sets.newHashSet(1, 2, 3, 4)));
    }

    @Test
    public void testRemovesAll() {
        unionFind.add(1, 2, 3);
        unionFind.union(1, 2);
        unionFind.removesAll(Collections.singletonList(1));

        Assert.assertFalse(unionFind.find(1));
        Assert.assertTrue(unionFind.find(2));
        Assert.assertTrue(unionFind.find(3));
    }

    @Test
    public void testClear() {
        unionFind.add(1, 2, 3);
        unionFind.clear();
        Assert.assertFalse(unionFind.find(1));
        Assert.assertFalse(unionFind.find(2));
        Assert.assertFalse(unionFind.find(3));
    }

    @Test
    public void testCopy() {
        unionFind.add(1, 2, 3);
        unionFind.union(1, 2);

        UnionFind<Integer> copy = unionFind.copy();
        Assert.assertEquals(unionFind.getEquivGroup(1), copy.getEquivGroup(1));
        Assert.assertEquals(unionFind.getEquivGroup(3), copy.getEquivGroup(3));
    }
}
