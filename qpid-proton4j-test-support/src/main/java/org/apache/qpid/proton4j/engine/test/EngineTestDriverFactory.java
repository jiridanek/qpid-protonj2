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

/**
 * Factory for creating Proton Engine test driver instances.
 */
public abstract class EngineTestDriverFactory {

    /**
     * Create an EngineTestDriver linked to the given Engine and configure it for use in tests.
     *
     * @param engine
     *      The engine implementation to test.
     *
     * @return an engine test driver to use when testing the engine implementation.
     */
    public static EngineTestDriver createDriver() {
        return new EngineTestDriver();
    }
}
