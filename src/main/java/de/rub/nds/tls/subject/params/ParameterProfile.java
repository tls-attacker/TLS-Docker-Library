package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterProfile implements Serializable {

    private String name;

    private String description;

    private TlsImplementationType type;

    private ConnectionRole role;

    @XmlElements(value = {
        @XmlElement(type = String.class, name = "Version")})
    private List<String> versionList;

    @XmlElements(value = {
        @XmlElement(type = Parameter.class, name = "Parameter")})
    private List<Parameter> parameterList;

    public ParameterProfile() {
    }

    public ParameterProfile(String name, String description, TlsImplementationType type, ConnectionRole role, List<String> versionList, List<Parameter> parameterList) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.role = role;
        this.versionList = versionList;
        this.parameterList = parameterList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TlsImplementationType getType() {
        return type;
    }

    public void setType(TlsImplementationType type) {
        this.type = type;
    }

    public ConnectionRole getRole() {
        return role;
    }

    public void setRole(ConnectionRole role) {
        this.role = role;
    }

    public List<String> getVersionList() {
        return versionList;
    }

    public void setVersionList(List<String> versionList) {
        this.versionList = versionList;
    }

    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }

    @Override
    public String toString() {
        return "ParameterProfile{" + "name=" + name + ", description=" + description + ", type=" + type + ", role=" + role + ", versionList=" + versionList + ", parameterList=" + parameterList + '}';
    }
}
