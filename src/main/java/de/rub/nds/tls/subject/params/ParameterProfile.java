/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2022 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.params;

import de.rub.nds.tls.subject.ConnectionRole;
import de.rub.nds.tls.subject.TlsImplementationType;
import de.rub.nds.tls.subject.properties.ImageProperties;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterProfile implements Serializable {
    private static final Logger LOGGER = LogManager.getLogger();

    private String name;

    private String description;

    private TlsImplementationType type;

    private ConnectionRole role;

    @XmlElements(value = {@XmlElement(type = String.class, name = "Version")})
    private List<String> versionList;

    @XmlElements(value = {@XmlElement(type = Parameter.class, name = "Parameter")})
    private List<Parameter> parameterList;

    public ParameterProfile() {}

    public ParameterProfile(
            String name,
            String description,
            TlsImplementationType type,
            ConnectionRole role,
            List<String> versionList,
            List<Parameter> parameterList) {
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

    public String getDescription() {
        return description;
    }

    public TlsImplementationType getType() {
        return type;
    }

    public ConnectionRole getRole() {
        return role;
    }

    public List<String> getVersionList() {
        return versionList;
    }

    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public boolean supportsInsecure() {
        return parameterList.stream().anyMatch(i -> i.getType() == ParameterType.INSECURE);
    }

    @Override
    public String toString() {
        return "ParameterProfile{"
                + "name="
                + name
                + ", description="
                + description
                + ", type="
                + type
                + ", role="
                + role
                + ", versionList="
                + versionList
                + ", parameterList="
                + parameterList
                + '}';
    }

    @SuppressWarnings(
            "squid:S3776") // sonarlint: Cognitive Complexity of methods should not be too high
    // at some point this should be refactored (especially if more params are added)
    public String[] toParameters(
            String host,
            Integer targetPort,
            ImageProperties imageProperties,
            String additionalParameters,
            boolean parallelize,
            boolean insecureConnection) {
        StringBuilder finalParams = new StringBuilder();
        if (parameterList != null) {
            for (Parameter param : parameterList) {
                if (supportsInsecure()) {
                    if ((insecureConnection && param.getType() == ParameterType.CA_CERTIFICATE)
                        || (!insecureConnection && param.getType() == ParameterType.INSECURE)) {
                        // do not add CA param if we use insecure, do not add insecure
                        // if not wanted
                        continue;
                    }
                }

                if (!parallelize && param.getType() == ParameterType.PARALLELIZE) {
                    // do not add parallelize if not wanted
                    continue;
                }
                if (param.getCmdParameter().equals("")) {
                    // do not add empty commands that cause a blank space
                    continue;
                }
                finalParams.append(param.getCmdParameter());
                finalParams.append(" ");
            }
        }
        if (additionalParameters != null) {
            finalParams.append(additionalParameters);
        }
        String afterReplace = finalParams.toString();

        if (host != null) {
            afterReplace = afterReplace.replace("[host]", host);
        }
        if (targetPort != null) {
            afterReplace = afterReplace.replace("[port]", "" + targetPort);
        }
        if (imageProperties.getDefaultCertPath() != null) {
            afterReplace = afterReplace.replace("[cert]", imageProperties.getDefaultCertPath());
        }
        if (imageProperties.getDefaultKeyPath() != null) {
            afterReplace = afterReplace.replace("[key]", imageProperties.getDefaultKeyPath());
        }
        if (imageProperties.getDefaultCertKeyCombinedPath() != null) {
            afterReplace =
                    afterReplace.replace(
                            "[combined]", imageProperties.getDefaultCertKeyCombinedPath());
        }
        afterReplace = afterReplace.trim();
        LOGGER.debug("Final parameters: {}", afterReplace);
        return afterReplace.split(" ");
    }
}
