/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.proton4j.amqp.messaging;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;

public final class Footer implements Section {

    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000078L);
    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:footer:map");

    private final Map<Object, Object> value;

    public Footer(Map<Object, Object> value) {
        this.value = value;
    }

    public Footer copy() {
        return new Footer(value == null ? null : new LinkedHashMap<>(value));
    }

    public Map<Object, Object> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Footer{ " + value + " }";
    }

    @Override
    public SectionType getType() {
        return SectionType.Footer;
    }
}
