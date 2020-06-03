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
package org.apache.qpid.proton4j.engine.impl;

import java.util.Map;

import org.apache.qpid.proton4j.amqp.Binary;
import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.messaging.Source;
import org.apache.qpid.proton4j.amqp.transactions.Coordinator;
import org.apache.qpid.proton4j.amqp.transactions.Declare;
import org.apache.qpid.proton4j.amqp.transactions.Discharge;
import org.apache.qpid.proton4j.amqp.transport.ErrorCondition;
import org.apache.qpid.proton4j.engine.Engine;
import org.apache.qpid.proton4j.engine.EventHandler;
import org.apache.qpid.proton4j.engine.Receiver;
import org.apache.qpid.proton4j.engine.Transaction;
import org.apache.qpid.proton4j.engine.TransactionManager;
import org.apache.qpid.proton4j.engine.exceptions.EngineFailedException;
import org.apache.qpid.proton4j.engine.exceptions.EngineStateException;

/**
 * {@link TransactionManager} implementation that implements the abstraction
 * around a receiver link that responds to requests to {@link Declare} and to
 * {@link Discharge} AMQP {@link Transaction} instance.
 */
public final class ProtonTransactionManager extends ProtonEndpoint<TransactionManager> implements TransactionManager {

    private final ProtonReceiver receiverLink;

    private EventHandler<Transaction<TransactionManager>> declareEventHandler;
    private EventHandler<Transaction<TransactionManager>> dischargeEventHandler;

    public ProtonTransactionManager(ProtonReceiver receiverLink) {
        super(receiverLink.getEngine());

        this.receiverLink = receiverLink;
        this.receiverLink.openHandler(this::handleReceiverLinkOpened)
                         .closeHandler(this::handleReceiverLinkClosed)
                         .localOpenHandler(this::handleReceiverLinkLocallyOpened)
                         .localCloseHandler(this::handleReceiverLinkLocallyClosed)
                         .engineShutdownHandler(this::handleEngineShutdown);
    }

    @Override
    public ProtonSession getParent() {
        return receiverLink.getSession();
    }

    @Override
    ProtonTransactionManager self() {
        return this;
    }

    @Override
    public TransactionManager addCredit(int additional) {
        receiverLink.addCredit(additional);
        return this;
    }

    @Override
    public int getCredit() {
        return receiverLink.getCredit();
    }

    @Override
    public TransactionManager declared(Transaction<TransactionManager> transaction, Binary txnId) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public TransactionManager discharged(Transaction<TransactionManager> transaction) {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public TransactionManager declareHandler(EventHandler<Transaction<TransactionManager>> declaredEventHandler) {
        this.declareEventHandler = declaredEventHandler;
        return this;
    }

    EventHandler<Transaction<TransactionManager>> declareHandler() {
        return declareEventHandler;
    }

    @Override
    public TransactionManager dischargeHandler(EventHandler<Transaction<TransactionManager>> dischargeEventHandler) {
        this.dischargeEventHandler = dischargeEventHandler;
        return this;
    }

    EventHandler<Transaction<TransactionManager>> dischargeHandler() {
        return dischargeEventHandler;
    }

    //----- Hand off methods for link specific elements.

    @Override
    public TransactionManager open() throws IllegalStateException, EngineStateException {
        receiverLink.open();
        return this;
    }

    @Override
    public TransactionManager close() throws EngineFailedException {
        receiverLink.close();
        return this;
    }

    @Override
    public boolean isLocallyOpen() {
        return receiverLink.isLocallyOpen();
    }

    @Override
    public boolean isLocallyClosed() {
        return receiverLink.isLocallyClosed();
    }

    @Override
    public TransactionManager setSource(Source source) throws IllegalStateException {
        receiverLink.setSource(source);
        return this;
    }

    @Override
    public Source getSource() {
        return receiverLink.getSource();
    }

    @Override
    public TransactionManager setCoordinator(Coordinator coordinator) throws IllegalStateException {
        receiverLink.setTarget(coordinator);
        return this;
    }

    @Override
    public Coordinator getCoordinator() {
        return receiverLink.getTarget();
    }

    @Override
    public ErrorCondition getCondition() {
        return receiverLink.getCondition();
    }

    @Override
    public TransactionManager setCondition(ErrorCondition condition) {
        receiverLink.setCondition(condition);
        return this;
    }

    @Override
    public Map<Symbol, Object> getProperties() {
        return receiverLink.getProperties();
    }

    @Override
    public TransactionManager setProperties(Map<Symbol, Object> properties) throws IllegalStateException {
        receiverLink.setProperties(properties);
        return this;
    }

    @Override
    public TransactionManager setOfferedCapabilities(Symbol... offeredCapabilities) throws IllegalStateException {
        receiverLink.setOfferedCapabilities(offeredCapabilities);
        return this;
    }

    @Override
    public Symbol[] getOfferedCapabilities() {
        return receiverLink.getOfferedCapabilities();
    }

    @Override
    public TransactionManager setDesiredCapabilities(Symbol... desiredCapabilities) throws IllegalStateException {
        receiverLink.setDesiredCapabilities(desiredCapabilities);
        return this;
    }

    @Override
    public Symbol[] getDesiredCapabilities() {
        return receiverLink.getDesiredCapabilities();
    }

    @Override
    public boolean isRemotelyOpen() {
        return receiverLink.isRemotelyOpen();
    }

    @Override
    public boolean isRemotelyClosed() {
        return receiverLink.isRemotelyClosed();
    }

    @Override
    public Symbol[] getRemoteOfferedCapabilities() {
        return receiverLink.getRemoteOfferedCapabilities();
    }

    @Override
    public Symbol[] getRemoteDesiredCapabilities() {
        return receiverLink.getRemoteDesiredCapabilities();
    }

    @Override
    public Map<Symbol, Object> getRemoteProperties() {
        return receiverLink.getRemoteProperties();
    }

    @Override
    public ErrorCondition getRemoteCondition() {
        return receiverLink.getRemoteCondition();
    }

    @Override
    public Source getRemoteSource() {
        return receiverLink.getRemoteSource();
    }

    @Override
    public Coordinator getRemoteCoordinator() {
        return receiverLink.getRemoteTarget();
    }

    //----- Link event handlers

    private void handleReceiverLinkLocallyOpened(Receiver receiver) {
        fireLocalOpen();
    }

    private void handleReceiverLinkLocallyClosed(Receiver receiver) {
        fireLocalClose();
    }

    private void handleReceiverLinkOpened(Receiver receiver) {
        fireRemoteOpen();
    }

    private void handleReceiverLinkClosed(Receiver receiver) {
        fireRemoteClose();
    }

    private void handleEngineShutdown(Engine engine) {
        fireEngineShutdown();
    }

    //----- The Manager specific Transaction implementation

    private final class ProtonManagerTransaction extends ProtonTransaction<ProtonTransactionManager> {

        private final ProtonTransactionManager manager;

        public ProtonManagerTransaction(ProtonTransactionManager manager) {
            this.manager = manager;
        }

        @Override
        public ProtonTransactionManager parent() {
            return manager;
        }
    }
}
