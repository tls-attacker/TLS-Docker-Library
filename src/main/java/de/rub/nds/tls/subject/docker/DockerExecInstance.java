/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tls.subject.docker;

import java.io.IOException;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;

import de.rub.nds.tls.subject.instance.ExecInstance;

public class DockerExecInstance implements ExecInstance {
    private final DockerClient DOCKER;
    public final ExecCreateCmdResponse execCreation;
    public final FrameHandler frameHandler;

    public DockerExecInstance(ExecCreateCmdResponse execCreation) {
        // if we are not using detach in execStart we must use our own docker client (as
        // we otherwise block other execStarts)
        DOCKER = DockerClientManager.getDockerClient();
        this.execCreation = execCreation;
        this.frameHandler = new FrameHandler();
        DOCKER.execStartCmd(execCreation.getId()).exec(frameHandler);
    }

    @Override
    public void close() {
        try {
            frameHandler.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return DOCKER.inspectExecCmd(execCreation.getId()).exec().isRunning();
    }

    public long getExitCode() {
        return DOCKER.inspectExecCmd(execCreation.getId()).exec().getExitCodeLong();
    }
}