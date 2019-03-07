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
package org.apache.qpid.proton4j.engine.test.describedtypes;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.engine.test.peer.ListDescribedType;

public class FlowFrame extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:flow:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000013L);

    private static final int FIELD_NEXT_INCOMING_ID = 0;
    private static final int FIELD_INCOMING_WINDOW = 1;
    private static final int FIELD_NEXT_OUTGOING_ID = 2;
    private static final int FIELD_OUTGOING_WINDOW = 3;
    private static final int FIELD_HANDLE = 4;
    private static final int FIELD_DELIVERY_COUNT = 5;
    private static final int FIELD_LINK_CREDIT = 6;
    private static final int FIELD_AVAILABLE = 7;
    private static final int FIELD_DRAIN = 8;
    private static final int FIELD_ECHO = 9;
    private static final int FIELD_PROPERTIES = 10;

    public FlowFrame(Object... fields) {
        super(11);
        int i = 0;
        for (Object field : fields) {
            getFields()[i++] = field;
        }
    }

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public FlowFrame setNextIncomingId(Object o) {
        getFields()[FIELD_NEXT_INCOMING_ID] = o;
        return this;
    }

    public FlowFrame setIncomingWindow(Object o) {
        getFields()[FIELD_INCOMING_WINDOW] = o;
        return this;
    }

    public FlowFrame setNextOutgoingId(Object o) {
        getFields()[FIELD_NEXT_OUTGOING_ID] = o;
        return this;
    }

    public FlowFrame setOutgoingWindow(Object o) {
        getFields()[FIELD_OUTGOING_WINDOW] = o;
        return this;
    }

    public FlowFrame setHandle(Object o) {
        getFields()[FIELD_HANDLE] = o;
        return this;
    }

    public FlowFrame setDeliveryCount(Object o) {
        getFields()[FIELD_DELIVERY_COUNT] = o;
        return this;
    }

    public FlowFrame setLinkCredit(Object o) {
        getFields()[FIELD_LINK_CREDIT] = o;
        return this;
    }

    public FlowFrame setAvailable(Object o) {
        getFields()[FIELD_AVAILABLE] = o;
        return this;
    }

    public FlowFrame setDrain(Object o) {
        getFields()[FIELD_DRAIN] = o;
        return this;
    }

    public FlowFrame setEcho(Object o) {
        getFields()[FIELD_ECHO] = o;
        return this;
    }

    public FlowFrame setProperties(Object o) {
        getFields()[FIELD_PROPERTIES] = o;
        return this;
    }
}
