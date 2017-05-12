/*
 * The MIT License
 *
 * Copyright (c) 2016, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.pipeline.maven.eventspy.handler;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.jenkinsci.plugins.pipeline.maven.eventspy.RuntimeIOException;
import org.jenkinsci.plugins.pipeline.maven.eventspy.reporter.MavenEventReporter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class ProjectSucceededExecutionHandler extends AbstractExecutionHandler {

    public ProjectSucceededExecutionHandler(MavenEventReporter reporter) {
        super(reporter);
    }

    @Override
    protected ExecutionEvent.Type getSupportedType() {
        return ExecutionEvent.Type.ProjectSucceeded;
    }

    @Override
    protected void addDetails(ExecutionEvent executionEvent, Xpp3Dom element) {
        super.addDetails(executionEvent, element);
        MavenProject project = executionEvent.getProject();

        Artifact artifact = project.getArtifact();
        if (artifact == null) {

        } else {
            Xpp3Dom artifactElt = newElement("artifact", artifact);
            File file = artifact.getFile();
            try {
                artifactElt.addChild(newElement("file", file == null ? null : file.getCanonicalPath()));
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
            element.addChild(artifactElt);
        }

        Xpp3Dom attachedArtifactsElt = new Xpp3Dom("attachedArtifacts");
        element.addChild(attachedArtifactsElt);
        for (Artifact attachedArtifact : project.getAttachedArtifacts()) {
            Xpp3Dom artifactElt = newElement("artifact", attachedArtifact);
            File file = attachedArtifact.getFile();
            try {
                artifactElt.addChild(newElement("file", file == null ? null : file.getCanonicalPath()));
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
            attachedArtifactsElt.addChild(artifactElt);
        }

    }

    @Override
    protected List<String> getConfigurationParametersToReport(ExecutionEvent executionEvent) {
        return Collections.emptyList();
    }
}
