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
package org.apache.qpid.proton4j.engine.test.matchers;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedLong;
import org.apache.qpid.proton4j.engine.test.FrameType;
import org.apache.qpid.proton4j.engine.test.FrameWithPayloadMatchingHandler;
import org.hamcrest.Matcher;

public class TransferMatcher extends FrameWithPayloadMatchingHandler {

    /**
     * Note that the ordinals of the Field enums match the order specified in
     * the AMQP spec
     */
    public enum Field {
        HANDLE, DELIVERY_ID, DELIVERY_TAG, MESSAGE_FORMAT, SETTLED, MORE, RCV_SETTLE_MODE, STATE, RESUME, ABORTED, BATCHABLE,
    }

    public TransferMatcher() {
        super(FrameType.AMQP, ANY_CHANNEL, UnsignedLong.valueOf(0x0000000000000014L), Symbol.valueOf("amqp:transfer:list"));
    }

    @Override
    public TransferMatcher onCompletion(Runnable onCompletion) {
        super.onCompletion(onCompletion);
        return this;
    }

    public TransferMatcher withHandle(Matcher<?> m) {
        getMatchers().put(Field.HANDLE, m);
        return this;
    }

    public TransferMatcher withDeliveryId(Matcher<?> m) {
        getMatchers().put(Field.DELIVERY_ID, m);
        return this;
    }

    public TransferMatcher withDeliveryTag(Matcher<?> m) {
        getMatchers().put(Field.DELIVERY_TAG, m);
        return this;
    }

    public TransferMatcher withMessageFormat(Matcher<?> m) {
        getMatchers().put(Field.MESSAGE_FORMAT, m);
        return this;
    }

    public TransferMatcher withSettled(Matcher<?> m) {
        getMatchers().put(Field.SETTLED, m);
        return this;
    }

    public TransferMatcher withMore(Matcher<?> m) {
        getMatchers().put(Field.MORE, m);
        return this;
    }

    public TransferMatcher withRcvSettleMode(Matcher<?> m) {
        getMatchers().put(Field.RCV_SETTLE_MODE, m);
        return this;
    }

    public TransferMatcher withState(Matcher<?> m) {
        getMatchers().put(Field.STATE, m);
        return this;
    }

    public TransferMatcher withResume(Matcher<?> m) {
        getMatchers().put(Field.RESUME, m);
        return this;
    }

    public TransferMatcher withAborted(Matcher<?> m) {
        getMatchers().put(Field.ABORTED, m);
        return this;
    }

    public TransferMatcher withBatchable(Matcher<?> m) {
        getMatchers().put(Field.BATCHABLE, m);
        return this;
    }

    public Object getReceivedHandle() {
        return getReceivedFields().get(Field.HANDLE);
    }

    public Object getReceivedDeliveryId() {
        return getReceivedFields().get(Field.DELIVERY_ID);
    }

    public Object getReceivedDeliveryTag() {
        return getReceivedFields().get(Field.DELIVERY_TAG);
    }

    public Object getReceivedMessageFormat() {
        return getReceivedFields().get(Field.MESSAGE_FORMAT);
    }

    public Object getReceivedSettled() {
        return getReceivedFields().get(Field.SETTLED);
    }

    public Object getReceivedMore() {
        return getReceivedFields().get(Field.MORE);
    }

    public Object getReceivedRcvSettleMode() {
        return getReceivedFields().get(Field.RCV_SETTLE_MODE);
    }

    public Object getReceivedState() {
        return getReceivedFields().get(Field.STATE);
    }

    public Object getReceivedResume() {
        return getReceivedFields().get(Field.RESUME);
    }

    public Object getReceivedAborted() {
        return getReceivedFields().get(Field.ABORTED);
    }

    public Object getReceivedBatchable() {
        return getReceivedFields().get(Field.BATCHABLE);
    }

    @Override
    protected Enum<?> getField(int fieldIndex) {
        return Field.values()[fieldIndex];
    }
}
