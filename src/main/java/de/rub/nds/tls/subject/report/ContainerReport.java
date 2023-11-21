/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.report;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContainerReport implements Serializable {

    static final Logger LOGGER = LogManager.getLogger();

    private static JAXBContext context;

    private static synchronized JAXBContext getJAXBContext() throws JAXBException, IOException {
        if (context == null) {
            context = JAXBContext.newInstance(ContainerReport.class, InstanceContainer.class);
        }
        return context;
    }

    public static void write(File file, ContainerReport report)
            throws FileNotFoundException, JAXBException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        write(fos, report);
    }

    public static void write(OutputStream outputStream, ContainerReport report)
            throws JAXBException, IOException {
        context = getJAXBContext();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(report, outputStream);
        outputStream.close();
    }

    public static ContainerReport read(InputStream inputStream)
            throws JAXBException, IOException, XMLStreamException {
        context = getJAXBContext();
        Unmarshaller m = context.createUnmarshaller();

        XMLInputFactory xif = XMLInputFactory.newFactory();
        xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XMLStreamReader xsr = xif.createXMLStreamReader(inputStream);

        ContainerReport report = (ContainerReport) m.unmarshal(xsr);
        inputStream.close();
        return report;
    }

    @XmlElements(value = {@XmlElement(type = InstanceContainer.class, name = "Container")})
    private final List<InstanceContainer> functionalContainerList;

    @XmlElements(value = {@XmlElement(type = InstanceContainer.class, name = "Container")})
    private final List<InstanceContainer> nonFunctionalContainerList;

    @XmlElements(value = {@XmlElement(type = InstanceContainer.class, name = "Container")})
    private final List<InstanceContainer> totalContainerList;

    public ContainerReport() {
        functionalContainerList = new LinkedList<>();
        nonFunctionalContainerList = new LinkedList<>();
        totalContainerList = new LinkedList<>();
    }

    public void addInstanceContainer(InstanceContainer container) {
        if (container.isFunctional()) {
            functionalContainerList.add(container);
        } else {
            nonFunctionalContainerList.add(container);
        }
        totalContainerList.add(container);
    }

    public List<InstanceContainer> getFunctionalImplementationList() {
        return functionalContainerList;
    }

    public List<InstanceContainer> getNonFunctionalImplementationList() {
        return nonFunctionalContainerList;
    }

    public List<InstanceContainer> getTotalImplementationList() {
        return totalContainerList;
    }
}
