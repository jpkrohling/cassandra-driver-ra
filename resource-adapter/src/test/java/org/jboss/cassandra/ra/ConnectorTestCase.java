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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.cassandra.service.CassandraDaemon;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.datastax.driver.core.Session;

/**
 * @author Juraci Paixão Kröhling
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConnectorTestCase {
    @Deployment
    public static ResourceAdapterArchive createDeployment() {
        return getBaseArchive().addAsManifestResource("META-INF/ironjacamar.xml", "ironjacamar.xml");
    }

    /** Resource */
    @Resource(mappedName = "java:/CassandraDriverConnectionFactory")
    private CassandraDriverConnectionFactory connectionFactory;

    public static ResourceAdapterArchive getBaseArchive() {
        ResourceAdapterArchive raa =
                ShrinkWrap.create(ResourceAdapterArchive.class, "ConnectorTestCase.rar");
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class, UUID.randomUUID().toString() + ".jar");
        ja.addPackages(true, Package.getPackage("org.jboss.cassandra.ra"));
        raa.addAsLibrary(ja);
        return raa;
    }

    @Test
    public void order01_FailToConnectOnNonExistingServer() throws Throwable {
        System.setProperty("cassandra-retry-attempts", "0");
        CassandraDriverConnection connection1 = connectionFactory.getConnection();
        Session session = connection1.getSession();
        assertNull(session);
        connection1.close();
    }

    @Test
    public void order02_ConnnectToRunningServer() throws Throwable {
        File cassandraConfig = new File("cassandra.yml");

        if (!cassandraConfig.exists()) {
            cassandraConfig = new File("src/test/resources/cassandra.yml");
            if (!cassandraConfig.exists()) {
                throw new RuntimeException("Could not find Cassandra configuration file in any of the known locations");
            }
        }

        System.setProperty("cassandra.config", "file://" + cassandraConfig.getAbsolutePath());
        System.setProperty("cassandra-retry-attempts", "0");

        CassandraDaemon cassandraDaemon = new CassandraDaemon(true);
        cassandraDaemon.activate();

        CassandraDriverConnection connection1 = connectionFactory.getConnection();
        Session session = connection1.getSession();
        assertNotNull(session);
        connection1.close();

        cassandraDaemon.deactivate();
    }
}
