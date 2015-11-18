/*
 * Copyright 2015 Jin Kwon &lt;jinahya_at_gmail.com&gt;.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.github.jinahya.sql.database.metadata;


import com.github.jinahya.sql.database.metadata.bind.Metadata;
import com.github.jinahya.sql.database.metadata.bind.MetadataContext;
import java.beans.IntrospectionException;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import static java.sql.DriverManager.getConnection;
import java.sql.SQLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


/**
 * A main class for generating/storing database metadata.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class Main {


    /**
     * Connects to a database and marshals database metadata.
     *
     * @param args command line arguments;
     * {@code <driver_name> <connection_url> <username> <password> <filename> <suppressionPath>...}
     *
     * @throws SQLException if a database access error occurs
     * @throws ReflectiveOperationException if a reflection error occurs.
     * @throws IntrospectionException
     * @throws JAXBException if an xml error occurs.
     */
    public static void main(final String[] args)
        throws ClassNotFoundException, SQLException, ReflectiveOperationException,
               IntrospectionException, JAXBException {

	final String name = args[0];
        final String url = args[1];
        final String user = args[2];
        final String pass = args[3];

        final String file = args[4];

	if (!name.isEmpty()) {
	    Class.forName(name);
	}

        final Metadata metadata;

        final Connection connection = getConnection(url, user, pass);
        try {
            final DatabaseMetaData database = connection.getMetaData();
            final MetadataContext context = new MetadataContext(database);
            for (int i = 5; i < args.length; i++) {
                context.addSuppressions(args[i]);
            }
            metadata = context.getMetadata();
        } finally {
            connection.close();
        }

        final JAXBContext context = JAXBContext.newInstance(Metadata.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        marshaller.marshal(metadata, new File(file));
    }


}

