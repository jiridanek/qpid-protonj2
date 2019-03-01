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
package org.apache.qpid.proton4j.engine.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.qpid.proton4j.amqp.DescribedType;

public abstract class ListDescribedType implements DescribedType {

    private final Object[] fields;

    public ListDescribedType(int numberOfFields) {
        fields = new Object[numberOfFields];
    }

    @Override
    public Object getDescribed() {
        // Return a List containing only the 'used fields' (i.e up to the
        // highest field used)
        int numUsedFields = 0;
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] != null) {
                numUsedFields = i + 1;
            }
        }

        // Create a list with the fields in the correct positions.
        List<Object> list = new LinkedList<Object>();
        for (int j = 0; j < numUsedFields; j++) {
            list.add(fields[j]);
        }

        return list;
    }

    protected Object[] getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "ListDescribedType [descriptor=" + getDescriptor() + " fields=" + Arrays.toString(fields) + "]";
    }
}