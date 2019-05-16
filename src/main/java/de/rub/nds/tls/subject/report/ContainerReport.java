/*
 */
package de.rub.nds.tls.subject.report;

import de.rub.nds.tls.subject.params.Parameter;
import de.rub.nds.tls.subject.params.ParameterProfile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
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

    public static void write(File file, ContainerReport report) throws FileNotFoundException, JAXBException, IOException {
        FileOutputStream fos = new FileOutputStream(file);
        write(fos, report);
    }

    public static void write(OutputStream outputStream, ContainerReport report) throws JAXBException, IOException {
        context = getJAXBContext();
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(report, outputStream);
        outputStream.close();
    }

    public static ContainerReport read(InputStream inputStream) throws JAXBException, IOException, XMLStreamException {
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

    @XmlElements(value = {
        @XmlElement(type = InstanceContainer.class, name = "Container")})
    private final List<InstanceContainer> functionalContainerList;
    @XmlElements(value = {
        @XmlElement(type = InstanceContainer.class, name = "Container")})
    private final List<InstanceContainer> nonFunctionalContainerList;
    @XmlElements(value = {
        @XmlElement(type = InstanceContainer.class, name = "Container")})

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
