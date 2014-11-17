/**
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.hadoop.hive.ql.exec.spark;

import org.apache.hadoop.hive.ql.io.HiveKey;
import org.apache.hadoop.io.BytesWritable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.storage.StorageLevel;

public class ShuffleTran implements SparkTran<HiveKey, BytesWritable, HiveKey, Iterable<BytesWritable>> {
  private final SparkShuffler shuffler;
  private final int numOfPartitions;
  private final StorageLevel storageLevel;

  public ShuffleTran(SparkShuffler sf, int n) {
    this(sf, n, null);
  }

  public ShuffleTran(SparkShuffler sf, int n, StorageLevel level) {
    shuffler = sf;
    numOfPartitions = n;
    storageLevel = level;
  }

  @Override
  public JavaPairRDD<HiveKey, Iterable<BytesWritable>> transform(JavaPairRDD<HiveKey, BytesWritable> input) {
    JavaPairRDD<HiveKey, Iterable<BytesWritable>> result = shuffler.shuffle(input, numOfPartitions);
    return storageLevel == null || storageLevel.equals(StorageLevel.NONE()) ? result : result.persist(storageLevel);
  }
}