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
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import static java.sql.DriverManager.getConnection;
import java.sql.SQLException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


/**
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class Main {


    /**
     * Connects to a database and marshals database metadata.
     *
     * @param args command line arguments;
     * {@code <url> <username> <password> <filename> <suppressionPath>...}
     *
     * @throws ClassNotFoundException if the driver
     * class({@code com.mysql.jdbc.Driver} not found
     * @throws SQLException if a database access error occurs
     * @throws ReflectiveOperationException if a reflection error occurs.
     * @throws JAXBException if an xml error occurs.
     */
    public static void main(final String[] args)
        throws ClassNotFoundException, SQLException,
               ReflectiveOperationException, JAXBException {

        final Metadata metadata;

        final Connection connection = getConnection(args[0], args[1], args[2]);
        try {
            final DatabaseMetaData database = connection.getMetaData();
            final MetadataContext context = new MetadataContext(database);
            for (int i = 4; i < args.length; i++) {
                context.addSuppressionPaths(args[i]);
            }
            metadata = context.getMetadata();
        } finally {
            connection.close();
        }

        final JAXBContext context = JAXBContext.newInstance(Metadata.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        marshaller.marshal(metadata, new File(args[3]));
    }


}

