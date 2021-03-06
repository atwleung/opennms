/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.opennms.core.logging.Logging;
import org.opennms.core.resource.Vault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates all initialization and configuration needed by the OpenNMS
 * servlets and JSPs.
 *
 * @author <A HREF="mailto:larry@opennms.org">Lawrence Karnowski </A>
 * @author <A HREF="http://www.opennms.org/">OpenNMS </A>
 */
public abstract class ServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ServletInitializer.class);

    /**
     * Initialize servlet and JSP configuration on the first invocation of this
     * method. All other invocations are ignored. This method is synchronized to
     * ensure only the first invocation performs the initialization.
     *
     * <p>
     * Call this method in the <code>init</code> method of your servlet or
     * JSP. It will read the servlet initialization parameters from the
     * <code>ServletConfig</code> and <code>ServletContext</code> and
     * OpenNMS configuration files.
     * </p>
     *
     * @param context
     *            the <code>ServletContext</code> instance in which your
     *            servlet is running
     * @throws javax.servlet.ServletException if any.
     */
    public static synchronized void init(ServletContext context) throws ServletException {
        if (context == null) {
            throw new IllegalArgumentException("Cannot take null parameters.");
        }

        /*
         * All ThreadCategory instances in the WebUI should use this as their
         * category prefix
         */
        
        Logging.putPrefix("web");

        Properties properties = new Properties(System.getProperties());

        /*
         * First, check if opennms.home is set, if so, we already have properties
         * because we're in Jetty.
         */
        if (properties.getProperty("opennms.home") == null) {
            throw new ServletException("The opennms.home context parameter must be set.");
        }

        String homeDir = properties.getProperty("opennms.home");

        /*
         * Now that we've got opennms.home, load $OPENNMS_HOME/etc/opennms.properties
         * in case it isn't--but if anything is already set, we don't override it.
         */
        Properties opennmsProperties = new Properties();

        try {
        	loadPropertiesFromFile(opennmsProperties, homeDir + File.separator + "etc" + File.separator + "opennms.properties");
        } catch (IOException e) {
        	throw new ServletException("Could not load opennms.properties", e);
        }

        try {
        	loadPropertiesFromContextResource(context, opennmsProperties, "/WEB-INF/version.properties");
        } catch (IOException e) {
        	throw new ServletException("Could not load version.properties", e);
        }

        for (Object key : opennmsProperties.keySet()) {
        	if (!properties.containsKey(key)) {
        		properties.put(key, opennmsProperties.get(key));
        	}
        }

        for (String name : Collections.list(context.getInitParameterNames())) {
        	properties.put(name, context.getInitParameter(name));
        }

        Vault.setProperties(properties);
    }

    private static void loadPropertiesFromFile(Properties opennmsProperties, String propertiesFile) throws FileNotFoundException, ServletException, IOException {
        InputStream configurationStream = new FileInputStream(propertiesFile);
        opennmsProperties.load(configurationStream);
        configurationStream.close();
    }

    private static void loadPropertiesFromContextResource(ServletContext context, Properties properties, String propertiesResource) throws ServletException, IOException {
        InputStream configurationStream = context.getResourceAsStream(propertiesResource);
        if (configurationStream == null) {
            throw new ServletException("Could not load properties from resource '" + propertiesResource + "'");
        } else {
            properties.load(configurationStream);
            configurationStream.close();
        }
    }
}
