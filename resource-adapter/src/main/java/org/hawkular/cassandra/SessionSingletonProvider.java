/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.cassandra;

import java.util.concurrent.atomic.AtomicReference;

import com.datastax.driver.core.Session;

/**
 * @author Juraci Paixão Kröhling
 */
public class SessionSingletonProvider {
    private static final SessionSingletonProvider INSTANCE =  new SessionSingletonProvider();
    private final AtomicReference<Session> sessionAtomicReference = new AtomicReference<>();

    private SessionSingletonProvider() {
    }

    public Session getSession() {
        return sessionAtomicReference.get();
    }

    public Session setSession(Session session) {
        if (!sessionAtomicReference.compareAndSet(null, session)) {
            if (null == session) {
                // we were asked to set the session to null!
                sessionAtomicReference.set(null);
                return null;
            }

            // we got a new session, but we had one already, so, close the one we got and return the one we had
            session.closeAsync();
        }
        return sessionAtomicReference.get();
    }

    public static SessionSingletonProvider getInstance() {
        return INSTANCE;
    }
}
