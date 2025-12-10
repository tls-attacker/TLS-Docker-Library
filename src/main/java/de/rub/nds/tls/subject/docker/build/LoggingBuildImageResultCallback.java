/*
 * TLS-Docker-Library - A collection of open source TLS clients and servers
 *
 * Copyright 2017-2024 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tls.subject.docker.build;

import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.model.BuildResponseItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Logger;

/**
 * Custom callback for Docker image builds that captures and logs build output. This callback
 * extends BuildImageResultCallback to provide real-time logging of the build process and collect
 * error information for better debugging.
 */
public class LoggingBuildImageResultCallback extends BuildImageResultCallback {
    private final Logger logger;
    private final List<String> buildLogs = new ArrayList<>();
    private boolean hasErrors = false;

    /**
     * Creates a new logging callback for Docker image builds.
     *
     * @param logger The logger to use for outputting build information
     */
    public LoggingBuildImageResultCallback(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onNext(BuildResponseItem item) {
        super.onNext(item);

        // Capture and log build stream output
        if (item.getStream() != null) {
            String logLine = item.getStream().trim();
            if (!logLine.isEmpty()) {
                buildLogs.add(logLine);
                logger.info("[Docker Build] {}", logLine);
            }
        }

        // Detect and log errors indicated by the build response
        if (item.isErrorIndicated()) {
            hasErrors = true;
            String errorDetail =
                    item.getErrorDetail() != null
                            ? item.getErrorDetail().getMessage()
                            : "Unknown error";
            logger.error("[Docker Build Error] {}", errorDetail);
        }
    }

    /**
     * Returns all build log lines captured during the build process.
     *
     * @return An unmodifiable list of build log lines
     */
    public List<String> getBuildLogs() {
        return Collections.unmodifiableList(buildLogs);
    }

    /**
     * Indicates whether any errors were detected during the build process.
     *
     * @return true if errors were detected, false otherwise
     */
    public boolean hasErrors() {
        return hasErrors;
    }
}
