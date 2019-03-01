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
package org.apache.qpid.proton4j.engine.test.describedtypes.sections;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.engine.test.ListDescribedType;

public class HeaderDescribedType extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:header:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000070L);

    private static final int FIELD_DURABLE = 0;
    private static final int FIELD_PRIORITY = 1;
    private static final int FIELD_TTL = 2;
    private static final int FIELD_FIRST_ACQUIRER = 3;
    private static final int FIELD_DELIVERY_COUNT = 4;

    public HeaderDescribedType(Object... fields) {
        super(5);
        int i = 0;
        for (Object field : fields) {
            getFields()[i++] = field;
        }
    }

    @Override
    public Symbol getDescriptor() {
        return DESCRIPTOR_SYMBOL;
    }

    public HeaderDescribedType setDurable(Object o) {
        getFields()[FIELD_DURABLE] = o;
        return this;
    }

    public HeaderDescribedType setPriority(Object o) {
        getFields()[FIELD_PRIORITY] = o;
        return this;
    }

    public HeaderDescribedType setTtl(Object o) {
        getFields()[FIELD_TTL] = o;
        return this;
    }

    public HeaderDescribedType setFirstAcquirer(Object o) {
        getFields()[FIELD_FIRST_ACQUIRER] = o;
        return this;
    }

    public HeaderDescribedType setDeliveryCount(Object o) {
        getFields()[FIELD_DELIVERY_COUNT] = o;
        return this;
    }
}
