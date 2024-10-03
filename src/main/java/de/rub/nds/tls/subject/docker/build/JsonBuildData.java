/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2024 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker.build;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonBuildData {
    @JsonProperty("build_groups")
    private Map<String, BuildGroup> buildGroups;

    private String latest;

    public Map<String, BuildGroup> getBuildGroups() {
        return buildGroups;
    }

    public void setBuildGroups(Map<String, BuildGroup> buildGroups) {
        this.buildGroups = buildGroups;
    }

    public String getLatest() {
        return latest;
    }

    public void setLatest(String latest) {
        this.latest = latest;
    }

    @Override
    public String toString() {
        return "JsonData{" + "buildGroups=" + buildGroups + ", latest='" + latest + '\'' + '}';
    }

    public boolean isVersionListed(String version) {
        return getDockerfileArgumentsForVersion(version) != null;
    }

    public DockerfileArguments getDockerfileArgumentsForVersion(String version) {
        for (BuildGroup buildGroup : buildGroups.values()) {
            int matchingVersionIndex = buildGroup.getCompleteVersionsListed().indexOf(version);
            if (matchingVersionIndex != -1) {
                return new DockerfileArguments(
                        buildGroup.getDockerfile(),
                        buildGroup.getVersions().get(matchingVersionIndex));
            }
        }
        return null;
    }
}

class BuildGroup {
    private String name;
    private String dockerfile;
    private List<String> versions;

    @JsonProperty("second_versions")
    private List<String> secondVersions;

    private List<String> instances;

    @JsonProperty("image_version")
    private String imageVersion;

    @JsonProperty("build_args")
    private BuildArgs buildArgs;

    private String context;
    private String target;
    private String tag;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDockerfile() {
        return dockerfile;
    }

    public void setDockerfile(String dockerfile) {
        this.dockerfile = dockerfile;
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }

    public String getImageVersion() {
        return imageVersion;
    }

    public void setImageVersion(String imageVersion) {
        this.imageVersion = imageVersion;
    }

    public BuildArgs getBuildArgs() {
        return buildArgs;
    }

    public void setBuildArgs(BuildArgs buildArgs) {
        this.buildArgs = buildArgs;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setSecondVersions(List<String> secondVersions) {
        this.secondVersions = secondVersions;
    }

    public List<String> getSecondVersions() {
        return secondVersions;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "BuildGroup{"
                + "name='"
                + name
                + '\''
                + ", dockerfile='"
                + dockerfile
                + '\''
                + ", versions="
                + versions
                + ", instances="
                + instances
                + ", imageVersion='"
                + imageVersion
                + '\''
                + ", buildArgs="
                + buildArgs
                + ", target='"
                + target
                + '\''
                + ", tag='"
                + tag
                + '\''
                + '}';
    }

    /**
     * The JSON contains versions as one prefix and a suffix list. E.g. 1.1.1x where x denotes a
     * possible suffix.
     *
     * @return
     */
    public List<String> getCompleteVersionsListed() {
        return versions.stream()
                .map(versionSuffix -> imageVersion.replace("{v}", versionSuffix))
                .collect(Collectors.toList());
    }
}

class BuildArgs {

    @JsonProperty("VERSION")
    private String version;

    @JsonProperty("JRE_VERSION")
    private String jreVersion;

    @JsonProperty("BC_VERSION")
    private String bouncyCastleVersion;

    @JsonProperty("NSS_VERSION")
    private String nssVersion;

    @JsonProperty("NSPR_VERSION")
    private String nsprVersion;

    @JsonProperty("COMMIT")
    private String commit;

    public String getNssVersion() {
        return nssVersion;
    }

    public void setNssVersion(String nssVersion) {
        this.nssVersion = nssVersion;
    }

    public String getNsprVersion() {
        return nsprVersion;
    }

    public void setNsprVersion(String nsprVersion) {
        this.nsprVersion = nsprVersion;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public String getBouncyCastleVersion() {
        return bouncyCastleVersion;
    }

    public void setBouncyCastleVersion(String bouncyCastleVersion) {
        this.bouncyCastleVersion = bouncyCastleVersion;
    }

    public String getJreVersion() {
        return jreVersion;
    }

    public void setJreVersion(String jreVersion) {
        this.jreVersion = jreVersion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "BuildArgs{" + "version='" + version + '\'' + '}';
    }
}
