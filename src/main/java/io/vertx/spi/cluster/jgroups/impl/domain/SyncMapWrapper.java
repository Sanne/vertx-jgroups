/*
 * Copyright (c) 2011-2013 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.spi.cluster.jgroups.impl.domain;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.spi.cluster.jgroups.impl.services.RpcExecutorService;
import io.vertx.spi.cluster.jgroups.impl.support.LambdaLogger;
import io.vertx.spi.cluster.jgroups.impl.services.RpcServerObjDelegate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SyncMapWrapper<K, V> implements Map<K, V>, LambdaLogger {

  private final static Logger LOG = LoggerFactory.getLogger(SyncMapWrapper.class);
  public static final int TIMEOUT = 1000;

  private final String name;
  private final Map<K, V> map;
  private final RpcExecutorService executorService;

  public SyncMapWrapper(String name, Map<K, V> map, RpcExecutorService executorService) {
    this.name = name;
    this.map = map;
    this.executorService = executorService;
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return map.get(key);
  }

  @Override
  public V put(K key, V value) {
    logTrace(() -> "SyncMap.put name [" + name + "], k = [" + key + "], v = [" + value + "]");
    return executorService.remoteExecute(RpcServerObjDelegate.CALL_MAP_PUT.method(name, key, value), TIMEOUT);
  }

  @Override
  public V remove(Object key) {
    logTrace(() -> "SyncMap.remove name [" + name + "], k = [" + key + "]");
    return executorService.remoteExecute(RpcServerObjDelegate.CALL_MAP_REMOVE.method(name, key), TIMEOUT);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> data) {
    logTrace(() -> "SyncMap.putAll name [" + name + "], data = [" + data + "]");
    executorService.remoteExecute(RpcServerObjDelegate.CALL_MAP_PUTALL.method(name, data), TIMEOUT);
  }

  @Override
  public void clear() {
    logTrace(() -> "SyncMap.clear name [" + name + "]");
    executorService.remoteExecute(RpcServerObjDelegate.CALL_MAP_CLEAR.method(name), TIMEOUT);
  }

  @Override
  public Set<K> keySet() {
    return Collections.unmodifiableSet(map.keySet());
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(map.values());
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return Collections.unmodifiableSet(map.entrySet());
  }

  @Override
  public Logger log() {
    return LOG;
  }
}
