/*
 * IronJacamar, a Java EE Connector Architecture implementation
 * Copyright 2013, Red Hat Inc, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.cassandra.ra;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAResource;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

/**
 * CassandraDriverManagedConnection
 *
 * @version $Revision: $
 */
public class CassandraDriverManagedConnection implements ManagedConnection {
    /** The logger */
    private static Logger log = Logger.getLogger(CassandraDriverManagedConnection.class.getName());

    /** The logwriter */
    private PrintWriter logwriter;

    /** ManagedConnectionFactory */
    private CassandraDriverManagedConnectionFactory mcf;

    /** Listeners */
    private List<ConnectionEventListener> listeners;

    /** Connection */
    private CassandraDriverConnectionImpl connection;

    private static final String CASSANDRA_RETRY_ATTEMPTS_DEFAULT = "5";
    private static final String CASSANDRA_RETRY_ATTEMPTS = "cassandra-retry-attempts";
    private static final String CASSANDRA_RETRY_INTERVAL = "cassandra-retry-interval";

    private static int attempts = Integer.parseInt(System.getProperty(CASSANDRA_RETRY_ATTEMPTS, CASSANDRA_RETRY_ATTEMPTS_DEFAULT));
    private static int interval = Integer.parseInt(System.getProperty(CASSANDRA_RETRY_INTERVAL, "500"));

    /**
     * Default constructor
     * @param mcf mcf
     */
    public CassandraDriverManagedConnection(CassandraDriverManagedConnectionFactory mcf) {
        this.mcf = mcf;
        this.logwriter = null;
        this.listeners = Collections.synchronizedList(new ArrayList<ConnectionEventListener>(1));
        this.connection = null;
    }

    /**
     * Creates a new connection handle for the underlying physical connection
     * represented by the ManagedConnection instance.
     *
     * @param subject Security context as JAAS subject
     * @param cxRequestInfo ConnectionRequestInfo instance
     * @return generic Object instance representing the connection handle.
     * @throws ResourceException generic exception if operation fails
     */
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        log.finest("getConnection()");
        connection = new CassandraDriverConnectionImpl(this, mcf);
        return connection;
    }

    /**
     * Used by the container to change the association of an
     * application-level connection handle with a ManagedConneciton instance.
     *
     * @param connection Application-level connection handle
     * @throws ResourceException generic exception if operation fails
     */
    public void associateConnection(Object connection) throws ResourceException {
        log.finest("associateConnection()");

        if (connection == null)
            throw new ResourceException("Null connection handle");

        if (!(connection instanceof CassandraDriverConnectionImpl))
            throw new ResourceException("Wrong connection handle");

        this.connection = (CassandraDriverConnectionImpl)connection;
    }

    /**
     * Application server calls this method to force any cleanup on the ManagedConnection instance.
     *
     * @throws ResourceException generic exception if operation fails
     */
    public void cleanup() throws ResourceException {
        log.finest("cleanup()");
    }

    /**
     * Destroys the physical connection to the underlying resource manager.
     *
     * @throws ResourceException generic exception if operation fails
     */
    public void destroy() throws ResourceException {
        log.finest("destroy()");
    }

    /**
     * Adds a connection event listener to the ManagedConnection instance.
     *
     * @param listener A new ConnectionEventListener to be registered
     */
    public void addConnectionEventListener(ConnectionEventListener listener) {
        log.finest("addConnectionEventListener()");
        if (listener == null)
            throw new IllegalArgumentException("Listener is null");
        listeners.add(listener);
    }

    /**
     * Removes an already registered connection event listener from the ManagedConnection instance.
     *
     * @param listener already registered connection event listener to be removed
     */
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        log.finest("removeConnectionEventListener()");
        if (listener == null)
            throw new IllegalArgumentException("Listener is null");
        listeners.remove(listener);
    }

    /**
     * Close handle
     *
     * @param handle The handle
     */
    void closeHandle(CassandraDriverConnection handle) {
        ConnectionEvent event = new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
        event.setConnectionHandle(handle);
        for (ConnectionEventListener cel : listeners)
        {
            cel.connectionClosed(event);
        }

    }

    /**
     * Gets the log writer for this ManagedConnection instance.
     *
     * @return Character output stream associated with this Managed-Connection instance
     * @throws ResourceException generic exception if operation fails
     */
    public PrintWriter getLogWriter() throws ResourceException {
        log.finest("getLogWriter()");
        return logwriter;
    }

    /**
     * Sets the log writer for this ManagedConnection instance.
     *
     * @param out Character Output stream to be associated
     * @throws ResourceException  generic exception if operation fails
     */
    public void setLogWriter(PrintWriter out) throws ResourceException {
        log.finest("setLogWriter()");
        logwriter = out;
    }

    /**
     * Returns an <code>javax.resource.spi.LocalTransaction</code> instance.
     *
     * @return LocalTransaction instance
     * @throws ResourceException generic exception if operation fails
     */
    public LocalTransaction getLocalTransaction() throws ResourceException {
        throw new NotSupportedException("getLocalTransaction() not supported");
    }

    /**
     * Returns an <code>javax.transaction.xa.XAresource</code> instance.
     *
     * @return XAResource instance
     * @throws ResourceException generic exception if operation fails
     */
    public XAResource getXAResource() throws ResourceException {
        throw new NotSupportedException("getXAResource() not supported");
    }

    /**
     * Gets the metadata information for this connection's underlying EIS resource manager instance.
     *
     * @return ManagedConnectionMetaData instance
     * @throws ResourceException generic exception if operation fails
     */
    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        log.finest("getMetaData()");
        return new CassandraDriverManagedConnectionMetaData();
    }

    public Session getSession() {
        Session session = SessionSingletonProvider.getInstance().getSession();
        if (null != session) {
            return session;
        }

        return doGetSession();
    }

    private Session doGetSession() {
        try {
            Session session = SessionSingletonProvider.getInstance().getSession();
            if (null != session) {
                return session;
            }

            session = new Cluster.Builder()
                    .addContactPoints(mcf.getNodes().split(","))
                    .withPort(new Integer(mcf.getPort()))
                    .withProtocolVersion(ProtocolVersion.V3)
                    .withoutJMXReporting()
                    .withoutMetrics()
                    .build().connect();

            return SessionSingletonProvider.getInstance().setSession(new CassandraSessionWrapper(session));
        } catch (NoHostAvailableException e) {
            if (attempts != 0) {
                log.log(Level.INFO, "Cassandra is not available (yet?). Attempts left: {0}.", attempts);
                attempts--;
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
                return doGetSession();
            } else {
                attempts = Integer.parseInt(System.getProperty(CASSANDRA_RETRY_ATTEMPTS, CASSANDRA_RETRY_ATTEMPTS_DEFAULT));
                log.log(Level.INFO, "Could not connect to Cassandra after enough attempts. Giving up.");
                return null;
            }
        }
    }

}