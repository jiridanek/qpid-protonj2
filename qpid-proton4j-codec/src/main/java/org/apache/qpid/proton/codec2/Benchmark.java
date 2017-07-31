/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.qpid.proton.codec2;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.apache.qpid.proton4j.amqp.Symbol;
import org.apache.qpid.proton4j.amqp.UnsignedByte;
import org.apache.qpid.proton4j.amqp.UnsignedInteger;
import org.apache.qpid.proton4j.amqp.UnsignedShort;
import org.apache.qpid.proton4j.amqp.messaging.Header;
import org.apache.qpid.proton4j.amqp.messaging.MessageAnnotations;
import org.apache.qpid.proton4j.amqp.messaging.Properties;
import org.apache.qpid.proton4j.codec.CodecFactory;
import org.apache.qpid.proton4j.codec.DecoderState;
import org.apache.qpid.proton4j.codec.EncoderState;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Benchmark
 */
public class Benchmark {

    public static final void main(String[] args) throws IOException, InterruptedException {
        int loop = 10 * 1024 * 1024;
        if (args.length > 0) {
            loop = Integer.parseInt(args[0]);
        }

        String test = "all";
        if (args.length > 1) {
            test = args[1];
        }

        System.out.println("Current PID: " + ManagementFactory.getRuntimeMXBean().getName());

        boolean runNew = false; //test.equals("all") || test.equals("new");
        boolean runExisting = test.equals("all") || test.equals("existing");

        long start, end;

        if (runNew) {
            byte[] bytes = new byte[1024];

            start = System.currentTimeMillis();
            int size = newEncode(bytes, loop);
            end = System.currentTimeMillis();
            time("new encode", start, end);

            start = System.currentTimeMillis();
            newDecode(bytes, size, loop);
            end = System.currentTimeMillis();
            time("new decode", start, end);
        }

        if (runExisting) {
            ByteBuf byteBuf = Unpooled.buffer(1024);

            start = System.currentTimeMillis();
            existingEncode(byteBuf, loop);
            end = System.currentTimeMillis();
            time("existing encode", start, end);

            start = System.currentTimeMillis();
            existingDecode(byteBuf, loop);
            end = System.currentTimeMillis();
            time("existing decode", start, end);
        }
    }

    private static final void time(String message, long start, long end) {
        System.out.println(message + ": " + (end - start) + " millis");
    }

    private static final int newEncode(byte[] bytes, int loop) {
        ByteArrayEncoder enc = new ByteArrayEncoder();

        UUID uuid = UUID.randomUUID();
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();

        for (int i = 0; i < loop; i++) {
            enc.init(bytes, 0, bytes.length);
            enc.putList();
            for (int j = 0; j < 10; j++) {
                enc.putInt(i + j);
            }
            enc.end();
            enc.putUUID(hi, lo);
        }

        return enc.getPosition();
    }

    private static final void newDecode(byte[] bytes, int size, int loop) {
        DataHandler dh = new AbstractDataHandler() {

            @Override
            public void onInt(org.apache.qpid.proton.codec2.Decoder d) {
                d.getIntBits();
            }

            @Override
            public void onUUID(org.apache.qpid.proton.codec2.Decoder d) {
                new UUID(d.getHiBits(), d.getLoBits());
            }
        };

        ByteArrayDecoder dec = new ByteArrayDecoder();
        for (int i = 0; i < loop; i++) {
            dec.init(bytes, 0, size);
            dec.decode(dh);
        }
    }

    private static final void existingEncode(ByteBuf buffer, int loop) {
        org.apache.qpid.proton4j.codec.Encoder encoder = CodecFactory.getDefaultEncoder();
        EncoderState state = encoder.newEncoderState();

        UUID uuid = UUID.randomUUID();

        Header header = new Header();
        header.setDurable(true);
        header.setFirstAcquirer(true);

        Properties properties = new Properties();
        properties.setTo("queue:1");

        MessageAnnotations annotations = new MessageAnnotations(new HashMap<>());
        annotations.getValue().put(Symbol.valueOf("test1"), UnsignedByte.valueOf((byte) 128));
        annotations.getValue().put(Symbol.valueOf("test2"), UnsignedShort.valueOf((short) 128));
        annotations.getValue().put(Symbol.valueOf("test3"), UnsignedInteger.valueOf((byte) 128));

        ArrayList<Object> list = new ArrayList<>(10);
        for (int j = 0; j < 10; j++) {
            list.add(0);
        }

        for (int i = 0; i < loop; i++) {
            buffer.clear();

//            encoder.writeList(buffer, state, list);
//            encoder.writeUUID(buffer, state, uuid);
//            encoder.writeObject(buffer, state, header);
            encoder.writeObject(buffer, state, properties);
//            encoder.writeObject(buffer, state, annotations);
        }
    }

    private static final void existingDecode(ByteBuf buffer, int loop) throws IOException {
        org.apache.qpid.proton4j.codec.Decoder decoder = CodecFactory.getDefaultDecoder();
        DecoderState state = decoder.newDecoderState();

        for (int i = 0; i < loop; i++) {
            buffer.readerIndex(0);
//            decoder.readObject(buffer, state); // List
//            decoder.readUUID(buffer, state); // UUID
//            decoder.readObject(buffer, state); // Header
            decoder.readObject(buffer, state); // Properties
//            decoder.readObject(buffer, state); // MessageAnnotations
        }
    }
}
