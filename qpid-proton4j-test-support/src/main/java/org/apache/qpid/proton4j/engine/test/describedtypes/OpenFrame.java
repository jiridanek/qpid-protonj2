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
import org.apache.qpid.proton4j.engine.test.ListDescribedType;

public class OpenFrame extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:open:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000010L);

    private static final int FIELD_CONTAINER_ID = 0;
    private static final int FIELD_HOSTNAME = 1;
    private static final int FIELD_MAX_FRAME_SIZE = 2;
    private static final int FIELD_CHANNEL_MAX = 3;
    private static final int FIELD_IDLE_TIME_OUT = 4;
    private static final int FIELD_OUTGOING_LOCALES = 5;
    private static final int FIELD_INCOMING_LOCALES = 6;
    private static final int FIELD_OFFERED_CAPABILITIES = 7;
    private static final int FIELD_DESIRED_CAPABILITIES = 8;
    private static final int FIELD_PROPERTIES = 9;

    public OpenFrame(Object... fields) {
        super(10);
        int i = 0;
        for (Object field : fields) {
            getFields()[i++] = field;
        }
    }

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public OpenFrame setContainerId(Object o) {
        getFields()[FIELD_CONTAINER_ID] = o;
        return this;
    }

    public OpenFrame setHostname(Object o) {
        getFields()[FIELD_HOSTNAME] = o;
        return this;
    }

    public OpenFrame setMaxFrameSize(Object o) {
        getFields()[FIELD_MAX_FRAME_SIZE] = o;
        return this;
    }

    public OpenFrame setChannelMax(Object o) {
        getFields()[FIELD_CHANNEL_MAX] = o;
        return this;
    }

    public OpenFrame setIdleTimeOut(Object o) {
        getFields()[FIELD_IDLE_TIME_OUT] = o;
        return this;
    }

    public OpenFrame setOutgoingLocales(Object o) {
        getFields()[FIELD_OUTGOING_LOCALES] = o;
        return this;
    }

    public OpenFrame setIncomingLocales(Object o) {
        getFields()[FIELD_INCOMING_LOCALES] = o;
        return this;
    }

    public OpenFrame setOfferedCapabilities(Object o) {
        getFields()[FIELD_OFFERED_CAPABILITIES] = o;
        return this;
    }

    public OpenFrame setDesiredCapabilities(Object o) {
        getFields()[FIELD_DESIRED_CAPABILITIES] = o;
        return this;
    }

    public OpenFrame setProperties(Object o) {
        getFields()[FIELD_PROPERTIES] = o;
        return this;
    }
}
