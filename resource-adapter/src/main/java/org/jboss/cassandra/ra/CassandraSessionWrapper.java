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
package org.jboss.cassandra.ra;

import com.datastax.driver.core.CloseFuture;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.RegularStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SimpleStatement;
import com.datastax.driver.core.Statement;
import com.google.common.util.concurrent.ListenableFuture;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Juraci Paixão Kröhling
 */
public class CassandraSessionWrapper implements Session {

    private Session session;

    public CassandraSessionWrapper(Session session) {
        this.session = session;
    }

    @Override public String getLoggedKeyspace() {
        return session.getLoggedKeyspace();
    }

    @Override public Session init() {
        return session.init();
    }

    @Override public ResultSet execute(String query) {
        return session.execute(query);
    }

    @Override public ResultSet execute(String query, Object... values) {
        return session.execute(query, values);
    }

    @Override public ResultSet execute(Statement statement) {
        return session.execute(statement);
    }

    @Override public ResultSetFuture executeAsync(String query) {
        return session.executeAsync(query);
    }

    @Override public ResultSetFuture executeAsync(String query, Object... values) {
        return session.executeAsync(query, values);
    }

    @Override public SimpleStatement newSimpleStatement(String query) {
        return session.newSimpleStatement(query);
    }

    @Override public SimpleStatement newSimpleStatement(String query, Object... values) {
        return session.newSimpleStatement(query, values);
    }

    @Override public ResultSetFuture executeAsync(Statement statement) {
        return session.executeAsync(statement);
    }

    @Override public PreparedStatement prepare(String query) {
        return session.prepare(query);
    }

    @Override public PreparedStatement prepare(RegularStatement statement) {
        return session.prepare(statement);
    }

    @Override public ListenableFuture<PreparedStatement> prepareAsync(String query) {
        return session.prepareAsync(query);
    }

    @Override public ListenableFuture<PreparedStatement> prepareAsync(RegularStatement statement) {
        return session.prepareAsync(statement);
    }

    @Override public CloseFuture closeAsync() {
        return session.closeAsync();
    }

    @Override public void close() {
        session.close();
    }

    @Override public boolean isClosed() {
        return session.isClosed();
    }

    @Override public Cluster getCluster() {
        throw new NotImplementedException();
    }

    @Override public State getState() {
        throw new NotImplementedException();
    }
}
