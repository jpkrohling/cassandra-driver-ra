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
package org.hawkular.cassandra;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import java.util.logging.Logger;

import javax.resource.ResourceException;
import javax.resource.spi.ConfigProperty;
import javax.resource.spi.ConnectionDefinition;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;

import javax.security.auth.Subject;

/**
 * CassandraDriverManagedConnectionFactory
 *
 * @version $Revision: $
 */
@ConnectionDefinition(connectionFactory = CassandraDriverConnectionFactory.class,
        connectionFactoryImpl = CassandraDriverConnectionFactoryImpl.class,
        connection = CassandraDriverConnection.class,
        connectionImpl = CassandraDriverConnectionImpl.class)
public class CassandraDriverManagedConnectionFactory implements ManagedConnectionFactory, ResourceAdapterAssociation {

    /** The serial version UID */
    private static final long serialVersionUID = 1L;

    /** The logger */
    private static Logger log = Logger.getLogger(CassandraDriverManagedConnectionFactory.class.getName());

    /** The resource adapter */
    private ResourceAdapter ra;

    /** The logwriter */
    private PrintWriter logwriter;

    /** nodes */
    @ConfigProperty(defaultValue = "127.0.0.1")
    private String nodes;

    /** port */
    @ConfigProperty(defaultValue = "9042")
    private String port;

    /**
     * Default constructor
     */
    public CassandraDriverManagedConnectionFactory() {
    }

    /**
     * Set nodes
     * @param nodes The value
     */
    public void setNodes(String nodes)
    {
        this.nodes = nodes;
    }

    /**
     * Get nodes
     * @return The value
     */
    public String getNodes()
    {
        return nodes;
    }

    /**
     * Set port
     * @param port The value
     */
    public void setPort(String port)
    {
        this.port = port;
    }

    /**
     * Get port
     * @return The value
     */
    public String getPort()
    {
        return port;
    }

    /**
     * Creates a Connection Factory instance.
     *
     * @param cxManager ConnectionManager to be associated with created EIS connection factory instance
     * @return EIS-specific Connection Factory instance or javax.resource.cci.ConnectionFactory instance
     * @throws ResourceException Generic exception
     */
    public Object createConnectionFactory(ConnectionManager cxManager) throws ResourceException {
        log.finest("createConnectionFactory()");
        return new CassandraDriverConnectionFactoryImpl(this, cxManager);
    }

    /**
     * Creates a Connection Factory instance.
     *
     * @return EIS-specific Connection Factory instance or javax.resource.cci.ConnectionFactory instance
     * @throws ResourceException Generic exception
     */
    public Object createConnectionFactory() throws ResourceException {
        throw new ResourceException("This resource adapter doesn't support non-managed environments");
    }

    /**
     * Creates a new physical connection to the underlying EIS resource manager.
     *
     * @param subject Caller's security information
     * @param cxRequestInfo Additional resource adapter specific connection request information
     * @throws ResourceException generic exception
     * @return ManagedConnection instance
     */
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo) throws
            ResourceException {
        log.finest("createManagedConnection()");
        return new CassandraDriverManagedConnection(this);
    }

    /**
     * Returns a matched connection from the candidate set of connections.
     *
     * @param connectionSet Candidate connection set
     * @param subject Caller's security information
     * @param cxRequestInfo Additional resource adapter specific connection request information
     * @throws ResourceException generic exception
     * @return ManagedConnection if resource adapter finds an acceptable match otherwise null
     */
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject, ConnectionRequestInfo
            cxRequestInfo) throws ResourceException {
        log.finest("matchManagedConnections()");
        ManagedConnection result = null;
        Iterator it = connectionSet.iterator();
        while (result == null && it.hasNext()) {
            ManagedConnection mc = (ManagedConnection)it.next();
            if (mc instanceof CassandraDriverManagedConnection) {
                result = mc;
            }
        }
        return result;
    }

    /**
     * Get the log writer for this ManagedConnectionFactory instance.
     *
     * @return PrintWriter
     * @throws ResourceException generic exception
     */
    public PrintWriter getLogWriter() throws ResourceException {
        log.finest("getLogWriter()");
        return logwriter;
    }

    /**
     * Set the log writer for this ManagedConnectionFactory instance.
     *
     * @param out PrintWriter - an out stream for error logging and tracing
     * @throws ResourceException generic exception
     */
    public void setLogWriter(PrintWriter out) throws ResourceException {
        log.finest("setLogWriter()");
        logwriter = out;
    }

    /**
     * Get the resource adapter
     *
     * @return The handle
     */
    public ResourceAdapter getResourceAdapter() {
        log.finest("getResourceAdapter()");
        return ra;
    }

    /**
     * Set the resource adapter
     *
     * @param ra The handle
     */
    public void setResourceAdapter(ResourceAdapter ra) {
        log.finest("setResourceAdapter()");
        this.ra = ra;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CassandraDriverManagedConnectionFactory that = (CassandraDriverManagedConnectionFactory) o;

        if (!nodes.equals(that.nodes)) return false;
        return port.equals(that.port);

    }

    @Override public int hashCode() {
        int result = nodes.hashCode();
        result = 31 * result + port.hashCode();
        return result;
    }
}
