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

import java.util.logging.Logger;

import com.datastax.driver.core.Session;

/**
 * CassandraDriverConnectionImpl
 *
 * @version $Revision: $
 */
public class CassandraDriverConnectionImpl implements CassandraDriverConnection {
    /** The logger */
    private static Logger log = Logger.getLogger(CassandraDriverConnectionImpl.class.getName());

    /** ManagedConnection */
    private CassandraDriverManagedConnection mc;

    /** ManagedConnectionFactory */
    private CassandraDriverManagedConnectionFactory mcf;

    /**
     * Default constructor
     * @param mc CassandraDriverManagedConnection
     * @param mcf CassandraDriverManagedConnectionFactory
     */
    public CassandraDriverConnectionImpl(CassandraDriverManagedConnection mc, CassandraDriverManagedConnectionFactory
            mcf) {
        this.mc = mc;
        this.mcf = mcf;
    }

    /**
     * Call me
     */
    public Session getSession() {
        return mc.getSession();
    }

    /**
     * Close
     */
    public void close() {
        mc.closeHandle(this);
    }

}
