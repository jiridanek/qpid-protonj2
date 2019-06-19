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
package org.apache.qpid.proton4j.codec.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.qpid.proton4j.amqp.Binary;
import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedInteger;
import org.apache.qpid.proton4j.buffer.ProtonBuffer;
import org.apache.qpid.proton4j.buffer.ProtonByteBufferAllocator;
import org.apache.qpid.proton4j.codec.CodecTestSupport;
import org.apache.qpid.proton4j.codec.EncodingCodes;
import org.apache.qpid.proton4j.codec.TypeDecoder;
import org.junit.Test;

/**
 * Test for the Proton List encoder / decoder
 */
public class ListTypeCodecTest extends CodecTestSupport {

    @Test
    public void testDecodeSmallSeriesOfLists() throws IOException {
        doTestDecodeListSeries(SMALL_SIZE);
    }

    @Test
    public void testDecodeLargeSeriesOfLists() throws IOException {
        doTestDecodeListSeries(LARGE_SIZE);
    }

    @Test
    public void testDecodeSmallSeriesOfSymbolLists() throws IOException {
        doTestDecodeSymbolListSeries(SMALL_SIZE);
    }

    @Test
    public void testDecodeLargeSeriesOfSymbolLists() throws IOException {
        doTestDecodeSymbolListSeries(LARGE_SIZE);
    }

    @SuppressWarnings("unchecked")
    private void doTestDecodeSymbolListSeries(int size) throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        List<Object> list = new ArrayList<>();

        for (int i = 0; i < 50; ++i) {
            list.add(Symbol.valueOf(String.valueOf(i)));
        }

        for (int i = 0; i < size; ++i) {
            encoder.writeObject(buffer, encoderState, list);
        }

        for (int i = 0; i < size; ++i) {
            final Object result = decoder.readObject(buffer, decoderState);

            assertNotNull(result);
            assertTrue(result instanceof List);

            List<Object> resultList = (List<Object>) result;

            assertEquals(list.size(), resultList.size());
        }
    }

    @SuppressWarnings("unchecked")
    private void doTestDecodeListSeries(int size) throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        List<Object> list = new ArrayList<>();

        Date timeNow = new Date(System.currentTimeMillis());

        list.add("ID:Message-1:1:1:0");
        list.add(new Binary(new byte[1]));
        list.add("queue:work");
        list.add(Symbol.valueOf("text/UTF-8"));
        list.add(Symbol.valueOf("text"));
        list.add(timeNow);
        list.add(UnsignedInteger.valueOf(1));
        list.add(UUID.randomUUID());

        for (int i = 0; i < size; ++i) {
            encoder.writeObject(buffer, encoderState, list);
        }

        for (int i = 0; i < size; ++i) {
            final Object result = decoder.readObject(buffer, decoderState);

            assertNotNull(result);
            assertTrue(result instanceof List);

            List<Object> resultList = (List<Object>) result;

            assertEquals(list.size(), resultList.size());
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testArrayOfListsOfUUIDs() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        ArrayList<UUID>[] source = new ArrayList[2];
        for (int i = 0; i < source.length; ++i) {
            source[i] = new ArrayList<>(3);
            source[i].add(UUID.randomUUID());
            source[i].add(UUID.randomUUID());
            source[i].add(UUID.randomUUID());
        }

        encoder.writeArray(buffer, encoderState, source);

        Object result = decoder.readObject(buffer, decoderState);
        assertNotNull(result);
        assertTrue(result.getClass().isArray());

        List[] list = (List[]) result;
        assertEquals(source.length, list.length);

        for (int i = 0; i < list.length; ++i) {
            assertEquals(source[i], list[i]);
        }
    }

    @Test
    public void testCountExceedsRemainingDetectedList32() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        buffer.writeByte(EncodingCodes.LIST32);
        buffer.writeInt(8);
        buffer.writeInt(Integer.MAX_VALUE);

        try {
            decoder.readObject(buffer, decoderState);
            fail("should throw an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
    }

    @Test
    public void testCountExceedsRemainingDetectedList8() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        buffer.writeByte(EncodingCodes.LIST8);
        buffer.writeByte(4);
        buffer.writeByte(Byte.MAX_VALUE);

        try {
            decoder.readObject(buffer, decoderState);
            fail("should throw an IllegalArgumentException");
        } catch (IllegalArgumentException iae) {}
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDecodeEmptyList() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        buffer.writeByte(EncodingCodes.LIST0);

        final Object result = decoder.readObject(buffer, decoderState);

        assertNotNull(result);
        assertTrue(result instanceof List);

        List<UUID> value = (List<UUID>) result;
        assertEquals(0, value.size());
    }

    @Test
    public void testEncodeEmptyListIsList0() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        encoder.writeList(buffer, encoderState, new ArrayList<>());

        assertEquals(1, buffer.getReadableBytes());
        assertEquals(EncodingCodes.LIST0, buffer.readByte());
    }

    @Test
    public void testDecodeFailsEarlyOnInvliadLengthList8() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate(16, 16);

        buffer.writeByte(EncodingCodes.LIST8);
        buffer.writeByte(255);

        TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
        assertEquals(List.class, typeDecoder.getTypeClass());

        try {
            typeDecoder.readValue(buffer, decoderState);
            fail("Should not be able to read list with length greater than readable bytes");
        } catch (IllegalArgumentException ex) {}

        assertEquals(2, buffer.getReadIndex());
    }

    @Test
    public void testDecodeFailsEarlyOnInvliadLengthList32() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate(16, 16);

        buffer.writeByte(EncodingCodes.LIST32);
        buffer.writeInt(Integer.MAX_VALUE);

        TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
        assertEquals(List.class, typeDecoder.getTypeClass());

        try {
            typeDecoder.readValue(buffer, decoderState);
            fail("Should not be able to read list with length greater than readable bytes");
        } catch (IllegalArgumentException ex) {}

        assertEquals(5, buffer.getReadIndex());
    }

    @Test
    public void testDecodeFailsEarlyOnInvliadElementCountForList8() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate(16, 16);

        buffer.writeByte(EncodingCodes.LIST8);
        buffer.writeByte(1);
        buffer.writeByte(255);

        TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
        assertEquals(List.class, typeDecoder.getTypeClass());

        try {
            typeDecoder.readValue(buffer, decoderState);
            fail("Should not be able to read list with length greater than readable bytes");
        } catch (IllegalArgumentException ex) {}

        assertEquals(3, buffer.getReadIndex());
    }

    @Test
    public void testDecodeFailsEarlyOnInvliadElementLengthList32() throws Exception {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate(16, 16);

        buffer.writeByte(EncodingCodes.LIST32);
        buffer.writeInt(2);
        buffer.writeInt(Integer.MAX_VALUE);

        TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
        assertEquals(List.class, typeDecoder.getTypeClass());

        try {
            typeDecoder.readValue(buffer, decoderState);
            fail("Should not be able to read list with length greater than readable bytes");
        } catch (IllegalArgumentException ex) {}

        assertEquals(9, buffer.getReadIndex());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSkipValue() throws IOException {
        ProtonBuffer buffer = ProtonByteBufferAllocator.DEFAULT.allocate();

        List<UUID> skip = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            skip.add(UUID.randomUUID());
        }

        for (int i = 0; i < 10; ++i) {
            encoder.writeList(buffer, encoderState, skip);
        }

        List<UUID> expected = new ArrayList<>();
        expected.add(UUID.randomUUID());

        encoder.writeObject(buffer, encoderState, expected);

        for (int i = 0; i < 10; ++i) {
            TypeDecoder<?> typeDecoder = decoder.readNextTypeDecoder(buffer, decoderState);
            assertEquals(List.class, typeDecoder.getTypeClass());
            typeDecoder.skipValue(buffer, decoderState);
        }

        final Object result = decoder.readObject(buffer, decoderState);

        assertNotNull(result);
        assertTrue(result instanceof List);

        List<UUID> value = (List<UUID>) result;
        assertEquals(expected, value);
    }
}
