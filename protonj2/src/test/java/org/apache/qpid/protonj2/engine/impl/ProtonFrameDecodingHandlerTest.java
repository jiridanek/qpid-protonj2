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
package org.apache.qpid.protonj2.engine.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.apache.qpid.protonj2.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.protonj2.engine.EmptyEnvelope;
import org.apache.qpid.protonj2.engine.Engine;
import org.apache.qpid.protonj2.engine.EngineHandlerContext;
import org.apache.qpid.protonj2.engine.HeaderEnvelope;
import org.apache.qpid.protonj2.engine.IncomingAMQPEnvelope;
import org.apache.qpid.protonj2.engine.exceptions.ProtocolViolationException;
import org.apache.qpid.protonj2.engine.util.FrameReadSinkTransportHandler;
import org.apache.qpid.protonj2.engine.util.FrameRecordingTransportHandler;
import org.apache.qpid.protonj2.engine.util.FrameWriteSinkTransportHandler;
import org.apache.qpid.protonj2.types.transport.AMQPHeader;
import org.apache.qpid.protonj2.types.transport.Open;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class ProtonFrameDecodingHandlerTest {

    private FrameRecordingTransportHandler testHandler;

    @BeforeEach
    public void setUp() {
        testHandler = new FrameRecordingTransportHandler();
    }

    @Test
    public void testDecodeValidHeaderTriggersHeaderRead() {
        Engine engine = createEngine();

        engine.start();

        // Check for Header processing
        engine.pipeline().fireRead(AMQPHeader.getAMQPHeader().getBuffer());

        Object frame = testHandler.getFramesRead().get(0);
        assertTrue(frame instanceof HeaderEnvelope);
        HeaderEnvelope header = (HeaderEnvelope) frame;
        assertEquals(AMQPHeader.getAMQPHeader(), header.getBody());
    }

    @Test
    public void testReadValidHeaderInSingleByteChunks() throws Exception {
        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 'A' }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 'M' }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 'Q' }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 'P' }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 0 }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 1 }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 0 }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 0 }));

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void testReadValidHeaderInSplitChunks() throws Exception {
        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 'A', 'M', 'Q', 'P' }));
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 0, 1, 0, 0 }));

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);
    }

    @Test
    public void testDecodeValidSaslHeaderTriggersHeaderRead() {
        Engine engine = createEngine();

        engine.start();

        // Check for Header processing
        engine.pipeline().fireRead(AMQPHeader.getSASLHeader().getBuffer());

        Object frame = testHandler.getFramesRead().get(0);
        assertTrue(frame instanceof HeaderEnvelope);
        HeaderEnvelope header = (HeaderEnvelope) frame;
        assertEquals(AMQPHeader.getSASLHeader(), header.getBody());
    }

    @Test
    public void testInvalidHeaderBytesTriggersError() {
        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        try {
            handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(new byte[] { 'S' }));
            fail("Handler should throw error on invalid input");
        } catch (Throwable error) {
            // Expected
        }

        // Verify that the parser accepts no new input once in error state.
        Mockito.clearInvocations(context);
        try {
            handler.handleRead(context, AMQPHeader.getSASLHeader().getBuffer());
            fail("Handler should throw error on additional input");
        } catch (Throwable error) {
            // Expected
        }
    }

    @Test
    public void testDecodeEmptyOpenEncodedFrame() throws Exception {
        // Frame data for: Open
        //   Open{ containerId="", hostname='null', maxFrameSize=4294967295, channelMax=65535,
        //         idleTimeOut=null, outgoingLocales=null, incomingLocales=null, offeredCapabilities=null,
        //         desiredCapabilities=null, properties=null}
        final byte[] emptyOpen = new byte[] {0, 0, 0, 16, 2, 0, 0, 0, 0, 83, 16, -64, 3, 1, -95, 0};

        ArgumentCaptor<IncomingAMQPEnvelope> argument = ArgumentCaptor.forClass(IncomingAMQPEnvelope.class);

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(emptyOpen));

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verify(context).fireRead(argument.capture());
        Mockito.verifyNoMoreInteractions(context);

        assertNotNull(argument.getValue());
        assertTrue(argument.getValue().getBody() instanceof Open);

        Open decoded = (Open) argument.getValue().getBody();

        assertTrue(decoded.hasContainerId());  // Defaults to empty string from proton-j
        assertFalse(decoded.hasHostname());
        assertFalse(decoded.hasMaxFrameSize());
        assertFalse(decoded.hasChannelMax());
        assertFalse(decoded.hasIdleTimeout());
        assertFalse(decoded.hasOutgoingLocales());
        assertFalse(decoded.hasIncomingLocales());
        assertFalse(decoded.hasOfferedCapabilities());
        assertFalse(decoded.hasDesiredCapabilities());
        assertFalse(decoded.hasProperties());
    }

    @Test
    public void testDecodeSimpleOpenEncodedFrame() throws Exception {
        // Frame data for: Open
        //   Open{ containerId='container', hostname='localhost', maxFrameSize=16384, channelMax=65535,
        //         idleTimeOut=30000, outgoingLocales=null, incomingLocales=null, offeredCapabilities=null,
        //         desiredCapabilities=null, properties=null}
        final byte[] basicOpen = new byte[] {0, 0, 0, 49, 2, 0, 0, 0, 0, 83, 16, -64, 36, 5, -95, 9, 99, 111,
                                             110, 116, 97, 105, 110, 101, 114, -95, 9, 108, 111, 99, 97, 108,
                                             104, 111, 115, 116, 112, 0, 0, 64, 0, 96, -1, -1, 112, 0, 0, 117, 48};
        ArgumentCaptor<IncomingAMQPEnvelope> argument = ArgumentCaptor.forClass(IncomingAMQPEnvelope.class);

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());
        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(basicOpen));

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verify(context).fireRead(argument.capture());
        Mockito.verifyNoMoreInteractions(context);

        assertNotNull(argument.getValue());
        assertTrue(argument.getValue().getBody() instanceof Open);

        Open decoded = (Open) argument.getValue().getBody();

        assertTrue(decoded.hasContainerId());
        assertEquals("container", decoded.getContainerId());
        assertTrue(decoded.hasHostname());
        assertEquals("localhost", decoded.getHostname());
        assertTrue(decoded.hasMaxFrameSize());
        assertEquals(16384, decoded.getMaxFrameSize());
        assertTrue(decoded.hasChannelMax());
        assertTrue(decoded.hasIdleTimeout());
        assertEquals(30000, decoded.getIdleTimeout());
        assertFalse(decoded.hasOutgoingLocales());
        assertFalse(decoded.hasIncomingLocales());
        assertFalse(decoded.hasOfferedCapabilities());
        assertFalse(decoded.hasDesiredCapabilities());
        assertFalse(decoded.hasProperties());
    }

    @Test
    public void testDecodePipelinedHeaderAndOpenEncodedFrame() throws Exception {
        // Frame data for: Open
        //   Open{ containerId='container', hostname='localhost', maxFrameSize=16384, channelMax=65535,
        //         idleTimeOut=30000, outgoingLocales=null, incomingLocales=null, offeredCapabilities=null,
        //         desiredCapabilities=null, properties=null}
        final byte[] basicOpen = new byte[] {'A', 'M', 'Q', 'P', 0, 1, 0, 0, // HEADER
                                             0, 0, 0, 49, 2, 0, 0, 0, 0, 83, 16, -64, 36, 5, -95, 9, 99, 111,
                                             110, 116, 97, 105, 110, 101, 114, -95, 9, 108, 111, 99, 97, 108,
                                             104, 111, 115, 116, 112, 0, 0, 64, 0, 96, -1, -1, 112, 0, 0, 117, 48};
        ArgumentCaptor<IncomingAMQPEnvelope> argument = ArgumentCaptor.forClass(IncomingAMQPEnvelope.class);

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(basicOpen));

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verify(context).fireRead(argument.capture());
        Mockito.verifyNoMoreInteractions(context);

        assertNotNull(argument.getValue());
        assertTrue(argument.getValue().getBody() instanceof Open);

        Open decoded = (Open) argument.getValue().getBody();

        assertTrue(decoded.hasContainerId());
        assertEquals("container", decoded.getContainerId());
        assertTrue(decoded.hasHostname());
        assertEquals("localhost", decoded.getHostname());
        assertTrue(decoded.hasMaxFrameSize());
        assertEquals(16384, decoded.getMaxFrameSize());
        assertTrue(decoded.hasChannelMax());
        assertTrue(decoded.hasIdleTimeout());
        assertEquals(30000, decoded.getIdleTimeout());
        assertFalse(decoded.hasOutgoingLocales());
        assertFalse(decoded.hasIncomingLocales());
        assertFalse(decoded.hasOfferedCapabilities());
        assertFalse(decoded.hasDesiredCapabilities());
        assertFalse(decoded.hasProperties());
    }

    /*
     * Test that empty frames, as used for heartbeating, decode as expected.
     */
    @Test
    public void testDecodeEmptyFrame() throws Exception {
        // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#doc-idp124752
        // Description: '8byte sized' empty AMQP frame
        byte[] emptyFrame = new byte[] { (byte) 0x00, 0x00, 0x00, 0x08, 0x02, 0x00, 0x00, 0x00 };

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);

        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(emptyFrame));

        ArgumentCaptor<IncomingAMQPEnvelope> argument = ArgumentCaptor.forClass(IncomingAMQPEnvelope.class);
        Mockito.verify(context).fireRead(argument.capture());
        Mockito.verifyNoMoreInteractions(context);

        assertNotNull(argument.getValue());
        assertTrue(argument.getValue() instanceof EmptyEnvelope);
    }

    /*
     * Test that two empty frames, as used for heartbeating, decode as expected when arriving back to back.
     */
    @Test
    public void testDecodeMultipleEmptyFrames() throws Exception {
        // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#doc-idp124752
        // Description: 2x '8byte sized' empty AMQP frames
        byte[] emptyFrames = new byte[] { (byte) 0x00, 0x00, 0x00, 0x08, 0x02, 0x00, 0x00, 0x00,
                                          (byte) 0x00, 0x00, 0x00, 0x08, 0x02, 0x00, 0x00, 0x00 };

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);

        handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(emptyFrames));

        ArgumentCaptor<IncomingAMQPEnvelope> argument = ArgumentCaptor.forClass(IncomingAMQPEnvelope.class);
        Mockito.verify(context, Mockito.times(2)).fireRead(argument.capture());

        List<IncomingAMQPEnvelope> frames = argument.getAllValues();
        assertNotNull(frames);
        assertEquals(2, frames.size());
        assertTrue(frames.get(0) instanceof EmptyEnvelope);
        assertTrue(frames.get(1) instanceof EmptyEnvelope);

        Mockito.verifyNoMoreInteractions(context);
    }

    /*
     * Test that frames indicating they are under 8 bytes (the minimum size of the frame header) causes an error.
     */
    @Test
    public void testInputOfFrameWithInvalidSizeBelowMinimumPossible() throws Exception {
        // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#doc-idp124752
        // Description: '7byte sized' AMQP frame header
        byte[] undersizedFrameHeader = new byte[] { (byte) 0x00, 0x00, 0x00, 0x07, 0x02, 0x00, 0x00, 0x00 };

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);

        try {
            handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(undersizedFrameHeader));
            fail("Should indicate protocol has been violated.");
        } catch (ProtocolViolationException pve) {
            // Expected
            assertThat(pve.getMessage(), containsString("frame size 7 smaller than minimum"));
        }

        Mockito.verifyNoMoreInteractions(context);
    }

    /*
     * Test that frames indicating a DOFF under 8 bytes (the minimum size of the frame header) causes an error.
     */
    @Test
    public void testInputOfFrameWithInvalidDoffBelowMinimumPossible() throws Exception {
        // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#doc-idp124752
        // Description: '8byte sized' AMQP frame header with invalid doff of 1[*4 = 4bytes]
        byte[] underMinDoffFrameHeader = new byte[] { (byte) 0x00, 0x00, 0x00, 0x08, 0x01, 0x00, 0x00, 0x00 };

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);

        try {
            handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(underMinDoffFrameHeader));
            fail("Should indicate protocol has been violated.");
        } catch (ProtocolViolationException pve) {
            // Expected
            assertThat(pve.getMessage(), containsString("data offset 4 smaller than minimum"));
        }

        Mockito.verifyNoMoreInteractions(context);
    }

    /*
     * Test that frames indicating a DOFF larger than the frame size cause expected error.
     */
    @Test
    public void testInputOfFrameWithInvalidDoffAboveMaximumPossible() throws Exception {
        // http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-transport-v1.0-os.html#doc-idp124752
        // Description: '8byte sized' AMQP frame header with invalid doff of 3[*4 = 12bytes]
        byte[] overFrameSizeDoffFrameHeader = new byte[] { (byte) 0x00, 0x00, 0x00, 0x08, 0x03, 0x00, 0x00, 0x00 };

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);

        try {
            handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(overFrameSizeDoffFrameHeader));
            fail("Should indicate protocol has been violated.");
        } catch (ProtocolViolationException pve) {
            // Expected
            assertThat(pve.getMessage(), containsString("data offset 12 larger than the frame size 8"));
        }

        Mockito.verifyNoMoreInteractions(context);
    }

    /*
     * Test that frame size above limit triggers error before attempting to decode the frame
     */
    @Test
    public void testFrameSizeThatExceedsMaximumFrameSizeLimitTriggersError() throws Exception {
        byte[] overFrameSizeLimitFrameHeader = new byte[] { (byte) 0xA0, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00 };

        ProtonFrameDecodingHandler handler = createFrameDecoder();
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);

        handler.handleRead(context, AMQPHeader.getAMQPHeader().getBuffer());

        Mockito.verify(context).fireRead(Mockito.any(HeaderEnvelope.class));
        Mockito.verifyNoMoreInteractions(context);

        try {
            handler.handleRead(context, ProtonByteBufferAllocator.DEFAULT.wrap(overFrameSizeLimitFrameHeader));
            fail("Should indicate frame limit has been violated.");
        } catch (ProtocolViolationException pve) {
            // Expected 2684354560 frame size is to big
            assertThat(pve.getMessage(), containsString("2684354560"));
            assertThat(pve.getMessage(), containsString("larger than maximum frame size"));
        }

        Mockito.verifyNoMoreInteractions(context);
    }

    private ProtonFrameDecodingHandler createFrameDecoder() {
        ProtonEngineConfiguration configuration = Mockito.mock(ProtonEngineConfiguration.class);
        Mockito.when(configuration.getInboundMaxFrameSize()).thenReturn(Long.valueOf(65535));
        ProtonEngine engine = Mockito.mock(ProtonEngine.class);
        Mockito.when(engine.configuration()).thenReturn(configuration);
        Mockito.when(engine.isWritable()).thenReturn(Boolean.TRUE);
        EngineHandlerContext context = Mockito.mock(EngineHandlerContext.class);
        Mockito.when(context.engine()).thenReturn(engine);

        ProtonFrameDecodingHandler handler = new ProtonFrameDecodingHandler();
        handler.handlerAdded(context);

        return handler;
    }

    private Engine createEngine() {
        ProtonEngine engine = new ProtonEngine();

        engine.pipeline().addLast("read-sink", new FrameReadSinkTransportHandler());
        engine.pipeline().addLast("test", testHandler);
        engine.pipeline().addLast("frames", new ProtonFrameDecodingHandler());
        engine.pipeline().addLast("write-sink", new FrameWriteSinkTransportHandler());

        return engine;
    }
}
