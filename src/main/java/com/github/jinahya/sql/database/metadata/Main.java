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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import static java.sql.DriverManager.getConnection;
import java.sql.SQLException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A main class for generating/storing database metadata.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public class Main {

    private static final Logger logger = getLogger(Main.class);

    /**
     * Connects to a database and marshals database metadata.
     *
     * @param args command line arguments; {@code driver_class_name},
     * {@code connection_url}, {@code database_username},
     * {@code database_password}, {@code output_filename}, and one or more
     * {@code suppression_path}.
     *
     * @throws IOException if an I/O error occurs.
     * @throws ClassNotFoundException if driver class not found
     * @throws SQLException if a database access error occurs
     * @throws ReflectiveOperationException if a reflection error occurs.
     * @throws JAXBException if an xml error occurs.
     */
    public static void main(final String[] args)
            throws IOException, ClassNotFoundException, SQLException,
                   ReflectiveOperationException, JAXBException {

        final String hr = new String(new char[80]).replace("\0", "-");
        System.out.printf("\nDRIVER INFORMATION\n%s\n", hr);
        {
            final BufferedReader r = new BufferedReader(new InputStreamReader(
                    Main.class.getResourceAsStream("/driver.properties")));
            try {
                for (String line; (line = r.readLine()) != null;) {
                    System.out.println(line);
                }
            } finally {
                r.close();
            }
        }

        final Main main = new Main();
        try {
            new CmdLineParser(main).parseArgument(args);
        } catch (final CmdLineException cle) {
            System.err.printf("\nERROR OCCURED\n%s\n", hr);
            cle.printStackTrace(System.err);
            System.out.printf("\nAVAILABLE OPTIONS\n%s\n", hr);
            cle.getParser().printUsage(System.out);
            System.out.printf(
                    "\nEXAMPLE OPTIONS\n%s\n -c %s -l %s -u %s -p %s -s %s\n\n", hr,
                    "com.some.Driver", "\"jdbc:some:lcoalhost:...\"",
                    "\"user1234\"", "\"password1234\"",
                    "schema/UDTs table/pseudoColumns");
            return;
        }

        if (main.name != null) {
            Class.forName(main.name);
        }

        final Metadata metadata;

        logger.debug("connecting...");
        final Connection connection
                = getConnection(main.url, main.user, main.password);
        try {
            logger.debug("connection: {}", connection);
            final DatabaseMetaData database = connection.getMetaData();
            logger.debug("database: {}", database);
            final MetadataContext context = new MetadataContext(database);
            if (main.suppressions != null) {
                for (final String suppression : main.suppressions) {
                    logger.debug("suppressin: {}", suppression);
                    context.suppressions(suppression);
                }
            }
            metadata = context.getMetadata();
            logger.debug("metadata retrived: {}", metadata);
        } finally {
            connection.close();
        }

        final JAXBContext context = JAXBContext.newInstance(Metadata.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        logger.debug("marshalling to {}", main.file);
        marshaller.marshal(metadata, main.file);
    }

    @Option(metaVar = "CLASS", name = "-n", usage = "driver class name")
    private String name;

    @Option(metaVar = "URL", name = "-l", required = true,
            usage = "connection url")
    private String url;

    @Option(metaVar = "USER", name = "-u", required = true,
            usage = "database user")
    private String user;

    @Option(metaVar = "PASSWORD", name = "-p", required = true,
            usage = "database password")
    private String password;

    @Option(metaVar = "OUTPUT", name = "-o", usage = "output file path")
    private File file;

    @Option(handler = StringArrayOptionHandler.class,
            metaVar = "SUPPRESSION...", name = "-s", usage = "suppressions")
    private List<String> suppressions;
}
