/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.params;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParameterProfileSerializer {

    static final Logger LOGGER = LogManager.getLogger(ParameterProfileSerializer.class.getName());

    private static JAXBContext context;

    private static synchronized JAXBContext getJAXBContext() throws JAXBException, IOException {
        if (context == null) {
            context = JAXBContext.newInstance(ParameterProfile.class, Parameter.class);
        }
        return context;
    }

    public static void write(File file, ParameterProfile profile)
            throws FileNotFoundException, JAXBException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ParameterProfileSerializer.write(fos, profile);
    }

    public static String write(ParameterProfile profile) throws JAXBException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ParameterProfileSerializer.write(bos, profile);
        return new String(bos.toByteArray(), "UTF-8");
    }

    public static void write(OutputStream outputStream, ParameterProfile profile)
            throws JAXBException, IOException {
        context = getJAXBContext();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(profile, outputStream);
        outputStream.close();
    }

    public static ParameterProfile read(InputStream inputStream)
            throws JAXBException, IOException, XMLStreamException {
        context = getJAXBContext();
        Unmarshaller m = context.createUnmarshaller();

        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xsr = xif.createXMLStreamReader(inputStream);

        ParameterProfile profile = (ParameterProfile) m.unmarshal(xsr);
        inputStream.close();
        return profile;
    }

    public static List<ParameterProfile> readFolder(File f) {
        if (f.isDirectory()) {
            ArrayList<ParameterProfile> list = new ArrayList<>();
            for (File file : f.listFiles()) {
                if (file.getName().startsWith(".")) {
                    // We ignore the .gitignore File
                    continue;
                }
                ParameterProfile profile;
                try {
                    profile = ParameterProfileSerializer.read(new FileInputStream(file));
                    list.add(profile);
                } catch (JAXBException | IOException | XMLStreamException ex) {
                    LOGGER.warn("Could not read " + file.getAbsolutePath() + " from Folder.");
                    LOGGER.debug(ex.getLocalizedMessage(), ex);
                }
            }
            return list;
        } else {
            throw new IllegalArgumentException("Cannot read Folder, because its not a Folder");
        }
    }

    private ParameterProfileSerializer() {}
}
