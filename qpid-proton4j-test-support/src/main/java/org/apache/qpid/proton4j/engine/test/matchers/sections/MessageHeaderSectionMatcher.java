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
package org.apache.qpid.proton4j.engine.test.matchers.sections;

import java.util.HashMap;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.hamcrest.Matcher;

public class MessageHeaderSectionMatcher extends MessageListSectionMatcher {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:header:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000070L);

    /**
     * Note that the ordinals of the Field enums match the order specified in
     * the AMQP spec
     */
    public enum Field {
        DURABLE, PRIORITY, TTL, FIRST_ACQUIRER, DELIVERY_COUNT,
    }

    public MessageHeaderSectionMatcher(boolean expectTrailingBytes) {
        super(DESCRIPTOR_CODE, DESCRIPTOR_SYMBOL, new HashMap<Object, Matcher<?>>(), expectTrailingBytes);
    }

    public MessageHeaderSectionMatcher withDurable(Matcher<?> m) {
        getMatchers().put(Field.DURABLE, m);
        return this;
    }

    public MessageHeaderSectionMatcher withPriority(Matcher<?> m) {
        getMatchers().put(Field.PRIORITY, m);
        return this;
    }

    public MessageHeaderSectionMatcher withTtl(Matcher<?> m) {
        getMatchers().put(Field.TTL, m);
        return this;
    }

    public MessageHeaderSectionMatcher withFirstAcquirer(Matcher<?> m) {
        getMatchers().put(Field.FIRST_ACQUIRER, m);
        return this;
    }

    public MessageHeaderSectionMatcher withDeliveryCount(Matcher<?> m) {
        getMatchers().put(Field.DELIVERY_COUNT, m);
        return this;
    }

    public Object getReceivedDurable() {
        return getReceivedFields().get(Field.DURABLE);
    }

    public Object getReceivedPriority() {
        return getReceivedFields().get(Field.PRIORITY);
    }

    public Object getReceivedTtl() {
        return getReceivedFields().get(Field.TTL);
    }

    public Object getReceivedFirstAcquirer() {
        return getReceivedFields().get(Field.FIRST_ACQUIRER);
    }

    public Object getReceivedDeliveryCount() {
        return getReceivedFields().get(Field.DELIVERY_COUNT);
    }

    @Override
    protected Enum<?> getField(int fieldIndex) {
        return Field.values()[fieldIndex];
    }
}
