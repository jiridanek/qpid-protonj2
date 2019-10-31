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
package org.messaginghub.amqperative.impl;

import static org.messaginghub.amqperative.impl.ClientConstants.DEFAULT_SUPPORTED_OUTCOMES;
import static org.messaginghub.amqperative.impl.ClientConstants.MODIFIED_FAILED;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.apache.qpid.proton4j.amqp.messaging.Source;
import org.apache.qpid.proton4j.amqp.messaging.Target;
import org.apache.qpid.proton4j.amqp.transport.DeliveryState;
import org.apache.qpid.proton4j.engine.IncomingDelivery;
import org.apache.qpid.proton4j.engine.LinkState;
import org.messaginghub.amqperative.Client;
import org.messaginghub.amqperative.Delivery;
import org.messaginghub.amqperative.Receiver;
import org.messaginghub.amqperative.ReceiverOptions;
import org.messaginghub.amqperative.Session;
import org.messaginghub.amqperative.futures.ClientFuture;
import org.messaginghub.amqperative.impl.exceptions.ClientOperationTimedOutException;
import org.messaginghub.amqperative.impl.exceptions.ClientResourceAllocationException;
import org.messaginghub.amqperative.impl.exceptions.ClientResourceClosedException;
import org.messaginghub.amqperative.util.FifoMessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientReceiver implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(ClientReceiver.class);

    private static final AtomicIntegerFieldUpdater<ClientReceiver> CLOSED_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(ClientReceiver.class, "closed");

    private final ClientFuture<Receiver> openFuture;
    private final ClientFuture<Receiver> closeFuture;

    private final ReceiverOptions options;
    private final ClientSession session;
    private final org.apache.qpid.proton4j.engine.Receiver protonReceiver;
    private final ScheduledExecutorService executor;
    private final AtomicReference<ClientException> failureCause = new AtomicReference<>();
    private final String receiverId;
    private final FifoMessageQueue messageQueue;
    private volatile int closed;
    private Consumer<ClientReceiver> receiverRemotelyClosedHandler;

    public ClientReceiver(ReceiverOptions options, ClientSession session, String address) {
        this.options = new ReceiverOptions(options);
        this.session = session;
        this.receiverId = session.nextReceiverId();
        this.executor = session.getScheduler();
        this.openFuture = session.getFutureFactory().createFuture();
        this.closeFuture = session.getFutureFactory().createFuture();

        if (options.getLinkName() != null) {
            this.protonReceiver = session.getProtonSession().receiver(options.getLinkName());
        } else {
            this.protonReceiver = session.getProtonSession().receiver("receiver-" + getId());
        }

        configureReceiver(address);

        if (options.getCreditWindow() > 0) {
            protonReceiver.setCredit(options.getCreditWindow());
        }

        messageQueue = new FifoMessageQueue(options.getCreditWindow());
        messageQueue.start();
    }

    @Override
    public Client getClient() {
        return session.getClient();
    }

    @Override
    public Session getSession() {
        return session;
    }

    ClientReceiver remotelyClosedHandler(Consumer<ClientReceiver> handler) {
        this.receiverRemotelyClosedHandler = handler;
        return this;
    }

    @Override
    public Future<Receiver> openFuture() {
        return openFuture;
    }

    @Override
    public Delivery receive() throws IllegalStateException {
        checkClosed();
        //TODO: verify timeout conventions align
        try {
            return messageQueue.dequeue(-1);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);//TODO: better exception
        }
    }

    @Override
    public Delivery receive(long timeout) throws IllegalStateException {
        checkClosed();
        //TODO: verify timeout conventions align
        try {
            return messageQueue.dequeue(timeout);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);//TODO: better exception
        }
    }

    @Override
    public Delivery tryReceive() throws IllegalStateException {
        checkClosed();
        try {
            return messageQueue.dequeue(0);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);//TODO: better exception
        }
    }

    @Override
    public Future<Receiver> close() {
        if (CLOSED_UPDATER.compareAndSet(this, 0, 1) && !openFuture.isFailed()) {
            executor.execute(() -> {
                try {
                    protonReceiver.close();
                } catch (Throwable error) {
                    closeFuture.complete(this);
                }

                if (!closeFuture.isDone()) {
                    final long timeout = options.getCloseTimeout() >= 0 ?
                            options.getCloseTimeout() : options.getRequestTimeout();

                    session.scheduleRequestTimeout(closeFuture, timeout,
                        () -> new ClientOperationTimedOutException("Timed out waiting for Receiver to close"));
                }
            });
        }
        return closeFuture;
    }

    @Override
    public Future<Receiver> detach() {
        if (CLOSED_UPDATER.compareAndSet(this, 0, 1) && !openFuture.isFailed()) {
            executor.execute(() -> {
                try {
                    protonReceiver.detach();
                } catch (Throwable error) {
                    closeFuture.complete(this);
                }

                if (!closeFuture.isDone()) {
                    final long timeout = options.getCloseTimeout() >= 0 ?
                            options.getCloseTimeout() : options.getRequestTimeout();

                    session.scheduleRequestTimeout(closeFuture, timeout,
                        () -> new ClientOperationTimedOutException("Timed out waiting for Receiver to detach"));
                }
            });
        }
        return closeFuture;
    }

    @Override
    public long getQueueSize() {
        return messageQueue.size();
    }

    @Override
    public Receiver onMessage(Consumer<Delivery> handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Receiver onMessage(Consumer<Delivery> handler, ExecutorService executor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Receiver addCredit(int credits) throws IllegalStateException {
        checkClosed();

        executor.execute(() -> {
            // TODO - Are we draining?  If so you've done something wrong...bang!
            // TODO - This is just a set without addition for now
            protonReceiver.setCredit(credits);
        });

        return this;
    }

    @Override
    public Future<Receiver> drain() {
        checkClosed();
        ClientFuture<Receiver> drained = session.getFutureFactory().createFuture();

        executor.execute(() -> {
            // TODO: If already draining throw IllegalStateException type error as we don't allow stacked drains.
             if (protonReceiver.isDrain()) {
                 drained.failed(new ClientException("Already draining"));
             }

             if (protonReceiver.getCredit() == 0) {
                 drained.complete(this);
                 return;
             }

            // TODO: Maybe proton should be returning something here to indicate drain started.
            protonReceiver.drain();
            protonReceiver.drainStateUpdatedHandler(x -> {
                protonReceiver.drainStateUpdatedHandler(null);
                drained.complete(this);
            });
        });

        return drained;
    }

    //----- Internal API

    void disposition(IncomingDelivery delivery, DeliveryState state, boolean settled) {
        checkClosed();
        executor.execute(() -> {
            delivery.disposition(state, settled);
        });
    }

    ClientReceiver open() {
        protonReceiver.openHandler(receiver -> handleRemoteOpen(receiver))
                      .closeHandler(receiver -> handleRemoteCloseOrDetach(receiver))
                      .detachHandler(receiver -> handleRemoteCloseOrDetach(receiver))
                      .deliveryUpdatedHandler(delivery -> handleDeliveryRemotelyUpdated(delivery))
                      .deliveryReceivedHandler(delivery -> handleDeliveryReceived(delivery))
                      .drainStateUpdatedHandler(receiver -> handleReceiverReportsDrained(receiver))
                      .open();

        return this;
    }

    void setFailureCause(ClientException failureCause) {
        this.failureCause.set(failureCause);
    }

    ClientException getFailureCause() {
        if (failureCause.get() == null) {
            return session.getFailureCause();
        }

        return failureCause.get();
    }

    String getId() {
        return receiverId;
    }

    boolean isClosed() {
        return closed > 0;
    }

    org.apache.qpid.proton4j.engine.Receiver getProtonReceiver() {
        return protonReceiver;
    }

    //----- Handlers for proton receiver events

    private void handleRemoteOpen(org.apache.qpid.proton4j.engine.Receiver receiver) {
        // Check for deferred close pending and hold completion if so
        if (receiver.getRemoteSource() != null) {
            openFuture.complete(this);
            LOG.trace("Receiver opened successfully");
        } else {
            LOG.debug("Receiver opened but remote signalled close is pending: ", receiver);
        }
    }

    private void handleRemoteCloseOrDetach(org.apache.qpid.proton4j.engine.Receiver receiver) {
        if (CLOSED_UPDATER.compareAndSet(this, 0, 1)) {
            // Close should be idempotent so we can just respond here with a close in case
            // of remotely closed sender.  We should set error state from remote though
            // so client can see it.
            try {
                if (protonReceiver.getRemoteState() == LinkState.CLOSED) {
                    LOG.info("Sender link remotely closed: ", receiver);
                    protonReceiver.close();
                } else {
                    LOG.info("Sender link remotely detached: ", receiver);
                    protonReceiver.detach();
                }
            } catch (Throwable ignored) {
                LOG.trace("Error while processing remote close event: ", ignored);
            }

            if (protonReceiver.getRemoteCondition() != null) {
                failureCause.set(ClientErrorSupport.convertToNonFatalException(protonReceiver.getRemoteCondition()));
            } else if (protonReceiver.getRemoteTarget() == null) {
                failureCause.set(new ClientResourceAllocationException("Link creation was refused"));
            } else {
                failureCause.set(new ClientResourceClosedException("The sender has been remotely closed"));
            }

            openFuture.failed(failureCause.get());
            closeFuture.complete(this);

            if (receiverRemotelyClosedHandler != null) {
                receiverRemotelyClosedHandler.accept(this);
            }
        } else {
            closeFuture.complete(this);
        }
    }

    private void handleDeliveryReceived(IncomingDelivery delivery) {
        LOG.debug("Delivery was updated: ", delivery);
        messageQueue.enqueue(new ClientDelivery(this, delivery));
        // TODO: we never replenish the credit window if configured right now.
        //       we should do something Qpid-JMS like and replenish on dispatch from
        //       prefetch when we hit a certain threshold like 50% (configurable?)
    }

    private void handleDeliveryRemotelyUpdated(IncomingDelivery delivery) {
        LOG.debug("Delivery was updated: ", delivery);
        // TODO - event or other reaction
    }

    private void handleReceiverReportsDrained(org.apache.qpid.proton4j.engine.Receiver receiver) {
        LOG.debug("Receiver reports drained: ", receiver);
        // TODO - event or other reaction, complete saved 'drained' future
    }

    //----- Private implementation details

    private void checkClosed() throws IllegalStateException {
        if (isClosed()) {
            IllegalStateException error = null;

            if (getFailureCause() == null) {
                error = new IllegalStateException("The Receiver is closed");
            } else {
                error = new IllegalStateException("The Receiver was closed due to an unrecoverable error.");
                error.initCause(getFailureCause());
            }

            throw error;
        }
    }

    private void configureReceiver(String address) {
        protonReceiver.setOfferedCapabilities(ClientConversionSupport.toSymbolArray(options.getOfferedCapabilities()));
        protonReceiver.setDesiredCapabilities(ClientConversionSupport.toSymbolArray(options.getDesiredCapabilities()));
        protonReceiver.setProperties(ClientConversionSupport.toSymbolKeyedMap(options.getProperties()));

        //TODO: flesh out source configuration
        Source source = new Source();
        source.setAddress(address);
        // TODO - User somehow sets their own desired outcomes for this receiver source.
        source.setOutcomes(DEFAULT_SUPPORTED_OUTCOMES);
        source.setDefaultOutcome(MODIFIED_FAILED);

        protonReceiver.setSource(source);
        protonReceiver.setTarget(new Target());
    }
}
