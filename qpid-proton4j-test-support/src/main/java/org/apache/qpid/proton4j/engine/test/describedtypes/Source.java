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

public class Source extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:source:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000028L);

    private static final int FIELD_ADDRESS = 0;
    private static final int FIELD_DURABLE = 1;
    private static final int FIELD_EXPIRY_POLICY = 2;
    private static final int FIELD_TIMEOUT = 3;
    private static final int FIELD_DYNAMIC = 4;
    private static final int FIELD_DYNAMIC_NODE_PROPERTIES = 5;
    private static final int FIELD_DISTRIBUTION_MODE = 6;
    private static final int FIELD_FILTER = 7;
    private static final int FIELD_DEFAULT_OUTCOME = 8;
    private static final int FIELD_OUTCOMES = 9;
    private static final int FIELD_CAPABILITIES = 10;

    public Source(Object... fields) {
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

    public Source setAddress(Object o) {
        getFields()[FIELD_ADDRESS] = o;
        return this;
    }

    public Source setDurable(Object o) {
        getFields()[FIELD_DURABLE] = o;
        return this;
    }

    public Source setExpiryPolicy(Object o) {
        getFields()[FIELD_EXPIRY_POLICY] = o;
        return this;
    }

    public Source setTimeout(Object o) {
        getFields()[FIELD_TIMEOUT] = o;
        return this;
    }

    public Source setDynamic(Object o) {
        getFields()[FIELD_DYNAMIC] = o;
        return this;
    }

    public Source setDynamicNodeProperties(Object o) {
        getFields()[FIELD_DYNAMIC_NODE_PROPERTIES] = o;
        return this;
    }

    public Source setDistributionMode(Object o) {
        getFields()[FIELD_DISTRIBUTION_MODE] = o;
        return this;
    }

    public Source setFilter(Object o) {
        getFields()[FIELD_FILTER] = o;
        return this;
    }

    public Source setDefaultOutcome(Object o) {
        getFields()[FIELD_DEFAULT_OUTCOME] = o;
        return this;
    }

    public Source setOutcomes(Object o) {
        getFields()[FIELD_OUTCOMES] = o;
        return this;
    }

    public Source setCapabilities(Object o) {
        getFields()[FIELD_CAPABILITIES] = o;
        return this;
    }
}
