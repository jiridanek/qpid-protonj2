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
import org.apache.qpid.proton4j.engine.test.FrameWithNoPayloadMatchingHandler;
import org.hamcrest.Matcher;

public class AttachMatcher extends FrameWithNoPayloadMatchingHandler {

    /**
     * Note that the ordinals of the Field enums match the order specified in
     * the AMQP spec
     */
    public enum Field {
        NAME, HANDLE, ROLE, SND_SETTLE_MODE, RCV_SETTLE_MODE, SOURCE, TARGET, UNSETTLED, INCOMPLETE_UNSETTLED, INITIAL_DELIVERY_COUNT, MAX_MESSAGE_SIZE, OFFERED_CAPABILITIES, DESIRED_CAPABILITIES, PROPERTIES,
    }

    public AttachMatcher() {
        super(FrameType.AMQP, ANY_CHANNEL, UnsignedLong.valueOf(0x0000000000000012L), Symbol.valueOf("amqp:attach:list"));
    }

    @Override
    public AttachMatcher onCompletion(Runnable onCompletion) {
        super.onCompletion(onCompletion);
        return this;
    }

    public AttachMatcher withName(Matcher<?> m) {
        getMatchers().put(Field.NAME, m);
        return this;
    }

    public AttachMatcher withHandle(Matcher<?> m) {
        getMatchers().put(Field.HANDLE, m);
        return this;
    }

    public AttachMatcher withRole(Matcher<?> m) {
        getMatchers().put(Field.ROLE, m);
        return this;
    }

    public AttachMatcher withSndSettleMode(Matcher<?> m) {
        getMatchers().put(Field.SND_SETTLE_MODE, m);
        return this;
    }

    public AttachMatcher withRcvSettleMode(Matcher<?> m) {
        getMatchers().put(Field.RCV_SETTLE_MODE, m);
        return this;
    }

    public AttachMatcher withSource(Matcher<?> m) {
        getMatchers().put(Field.SOURCE, m);
        return this;
    }

    public AttachMatcher withTarget(Matcher<?> m) {
        getMatchers().put(Field.TARGET, m);
        return this;
    }

    public AttachMatcher withUnsettled(Matcher<?> m) {
        getMatchers().put(Field.UNSETTLED, m);
        return this;
    }

    public AttachMatcher withIncompleteUnsettled(Matcher<?> m) {
        getMatchers().put(Field.INCOMPLETE_UNSETTLED, m);
        return this;
    }

    public AttachMatcher withInitialDeliveryCount(Matcher<?> m) {
        getMatchers().put(Field.INITIAL_DELIVERY_COUNT, m);
        return this;
    }

    public AttachMatcher withMaxMessageSize(Matcher<?> m) {
        getMatchers().put(Field.MAX_MESSAGE_SIZE, m);
        return this;
    }

    public AttachMatcher withOfferedCapabilities(Matcher<?> m) {
        getMatchers().put(Field.OFFERED_CAPABILITIES, m);
        return this;
    }

    public AttachMatcher withDesiredCapabilities(Matcher<?> m) {
        getMatchers().put(Field.DESIRED_CAPABILITIES, m);
        return this;
    }

    public AttachMatcher withProperties(Matcher<?> m) {
        getMatchers().put(Field.PROPERTIES, m);
        return this;
    }

    public Object getReceivedName() {
        return getReceivedFields().get(Field.NAME);
    }

    public Object getReceivedHandle() {
        return getReceivedFields().get(Field.HANDLE);
    }

    public Object getReceivedRole() {
        return getReceivedFields().get(Field.ROLE);
    }

    public Object getReceivedSndSettleMode() {
        return getReceivedFields().get(Field.SND_SETTLE_MODE);
    }

    public Object getReceivedRcvSettleMode() {
        return getReceivedFields().get(Field.RCV_SETTLE_MODE);
    }

    public Object getReceivedSource() {
        return getReceivedFields().get(Field.SOURCE);
    }

    public Object getReceivedTarget() {
        return getReceivedFields().get(Field.TARGET);
    }

    public Object getReceivedUnsettled() {
        return getReceivedFields().get(Field.UNSETTLED);
    }

    public Object getReceivedIncompleteUnsettled() {
        return getReceivedFields().get(Field.INCOMPLETE_UNSETTLED);
    }

    public Object getReceivedInitialDeliveryCount() {
        return getReceivedFields().get(Field.INITIAL_DELIVERY_COUNT);
    }

    public Object getReceivedMaxMessageSize() {
        return getReceivedFields().get(Field.MAX_MESSAGE_SIZE);
    }

    public Object getReceivedOfferedCapabilities() {
        return getReceivedFields().get(Field.OFFERED_CAPABILITIES);
    }

    public Object getReceivedDesiredCapabilities() {
        return getReceivedFields().get(Field.DESIRED_CAPABILITIES);
    }

    public Object getReceivedProperties() {
        return getReceivedFields().get(Field.PROPERTIES);
    }

    @Override
    protected Enum<?> getField(int fieldIndex) {
        return Field.values()[fieldIndex];
    }
}
