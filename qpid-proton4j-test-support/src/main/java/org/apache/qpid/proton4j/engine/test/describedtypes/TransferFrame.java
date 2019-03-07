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

public class TransferFrame extends ListDescribedType {

    public static final Symbol DESCRIPTOR_SYMBOL = Symbol.valueOf("amqp:transfer:list");
    public static final UnsignedLong DESCRIPTOR_CODE = UnsignedLong.valueOf(0x0000000000000014L);

    private static final int FIELD_HANDLE = 0;
    private static final int FIELD_DELIVERY_ID = 1;
    private static final int FIELD_DELIVERY_TAG = 2;
    private static final int FIELD_MESSAGE_FORMAT = 3;
    private static final int FIELD_SETTLED = 4;
    private static final int FIELD_MORE = 5;
    private static final int FIELD_RCV_SETTLE_MODE = 6;
    private static final int FIELD_STATE = 7;
    private static final int FIELD_RESUME = 8;
    private static final int FIELD_ABORTED = 9;
    private static final int FIELD_BATCHABLE = 10;

    public TransferFrame(Object... fields) {
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

    public TransferFrame setHandle(Object o) {
        getFields()[FIELD_HANDLE] = o;
        return this;
    }

    public TransferFrame setDeliveryId(Object o) {
        getFields()[FIELD_DELIVERY_ID] = o;
        return this;
    }

    public TransferFrame setDeliveryTag(Object o) {
        getFields()[FIELD_DELIVERY_TAG] = o;
        return this;
    }

    public TransferFrame setMessageFormat(Object o) {
        getFields()[FIELD_MESSAGE_FORMAT] = o;
        return this;
    }

    public TransferFrame setSettled(Object o) {
        getFields()[FIELD_SETTLED] = o;
        return this;
    }

    public TransferFrame setMore(Object o) {
        getFields()[FIELD_MORE] = o;
        return this;
    }

    public TransferFrame setRcvSettleMode(Object o) {
        getFields()[FIELD_RCV_SETTLE_MODE] = o;
        return this;
    }

    public TransferFrame setState(Object o) {
        getFields()[FIELD_STATE] = o;
        return this;
    }

    public TransferFrame setResume(Object o) {
        getFields()[FIELD_RESUME] = o;
        return this;
    }

    public TransferFrame setAborted(Object o) {
        getFields()[FIELD_ABORTED] = o;
        return this;
    }

    public TransferFrame setBatchable(Object o) {
        getFields()[FIELD_BATCHABLE] = o;
        return this;
    }
}
