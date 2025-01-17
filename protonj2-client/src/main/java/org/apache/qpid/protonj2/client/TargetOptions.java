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
package org.apache.qpid.protonj2.client;

/**
 * Options type that carries configuration for link Target types.
 */
public final class TargetOptions extends TerminusOptions<TargetOptions> {

    /**
     * @param other
     * 		The instance which should receive the configuration from this options instance.
     *
     * @return the given {@link TargetOptions} instance with all configuration copied from this instance.
     */
    public TargetOptions copyInto(TargetOptions other) {
        super.copyInto(other);
        return this;
    }

    @Override
    public TargetOptions clone() {
        return copyInto(new TargetOptions());
    }

    @Override
    TargetOptions self() {
        return this;
    }
}
