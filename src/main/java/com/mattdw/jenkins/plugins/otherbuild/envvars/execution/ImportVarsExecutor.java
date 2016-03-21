/**
 * ImportOtherBuildEnvVarsBaseBuilder.java
 * Created 21-Mar-2016 10:01:17
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 * The MIT License
 *
 * Copyright 2016 M.D.Ward <matthew.ward@byng.co>.
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

package com.mattdw.jenkins.plugins.otherbuild.envvars.execution;

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.OtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarImporterOrCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.OtherBuildVarImportException;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.ExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;
import java.io.IOException;
import java.util.Map;




/**
 * ImportVarsExecutor 
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 * 
 * @param <M>
 *      [Super]type for environment vars; must extend {@link Map} with
 *      {@link String} types for both key and value
 * @param <V>
 *      [Super]type for environment var copier; must extend {@link VarImporterOrCopier}
 * @param <L>
 *      [Super]type for task listener; must extend {@link TaskListener}
 * 
 */
public interface ImportVarsExecutor <M extends Map<String, String>, V extends VarImporterOrCopier, L extends TaskListener> {
    
    public ImportVarsResult perform(
        String projectName,
        String buildId,
        M currentBuildVars,
        V envVarTransferAgent,
        L listener,
        AbstractBuild currentBuild
    ) throws InterruptedException, IOException, OtherBuildVarImportException;



    public static class CopierImpl extends AbstractImpl<EnvVarsCopier> {

        public CopierImpl(
            ExternalProjectProvider projectProvider,
            ExternalBuildProvider buildProvider
        ) {
            super(projectProvider, buildProvider);
        }

        @Override
        protected void doEnvVarTransfer(
            EnvVarsCopier varCopier,
            Map<String, String> currentBuildEnvVars,
            Map<String, String> otherBuildEnvVars,
            AbstractBuild currentBuild,
            AbstractBuild otherBuild,
            TaskListener listener
        ) {
            varCopier.copyEnvVars(otherBuildEnvVars, currentBuildEnvVars);
        }

    }
    
    
    
    public static class ImporterImpl extends AbstractImpl<OtherBuildEnvVarsImporter> {

        public ImporterImpl(
            ExternalProjectProvider<AbstractProject> projectProvider,
            ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
        ) {
            super(projectProvider, buildProvider);
        }

        @Override
        protected void doEnvVarTransfer(
            OtherBuildEnvVarsImporter envImporter,
            Map<String, String> currentBuildEnvVars,
            Map<String, String> otherBuildEnvVars,
            AbstractBuild currentBuild,
            AbstractBuild otherBuild,
            TaskListener listener
        ) {
            envImporter.importVars(currentBuild, otherBuildEnvVars);
        }

    }

}



abstract class AbstractImpl <V extends VarImporterOrCopier> implements ImportVarsExecutor<EnvVars, V, TaskListener> {

    /**
     * Project provider mechanism for the target build from which
     * variables are imported
     */
    protected final transient ExternalProjectProvider<AbstractProject> projectProvider;

    /**
     * Build provider mechanism for the target build from which
     * variables are imported
     */
    protected final transient ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider;



    /**
     * 
     * @param projectProvider
     * @param buildProvider 
     */
    public AbstractImpl(
        ExternalProjectProvider<AbstractProject> projectProvider,
        ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
    ) {
        this.projectProvider = projectProvider;
        this.buildProvider = buildProvider;
    }

    /**
     * 
     * @param projectName
     * @param buildId
     * @param currentBuildVars
     * @param varCopier
     * @param listener
     * @throws InterruptedException
     * @throws IOException
     * @throws OtherBuildVarImportException 
     */
    @Override
    public final ImportVarsResult perform(
        String projectName,
        String buildId,
        EnvVars currentBuildVars,
        V envVarTransferAgent,
        TaskListener listener,
        AbstractBuild currentBuild
    ) throws InterruptedException, IOException, OtherBuildVarImportException {
        final int originalSize = currentBuildVars.size();

        final AbstractProject otherProject = this.projectProvider.provideProject(projectName);
        final AbstractBuild otherBuild = this.buildProvider.provideBuild(
            otherProject,
            currentBuildVars.expand(buildId)
        );
        
        final Map<String, String> otherBuildEnvVars = otherBuild.getEnvironment(listener);

        /*
         * Import these variables into the current build as the mechanism
         * prescribes; the exact details are decoupled from this builder
         */
        this.doEnvVarTransfer(
            envVarTransferAgent,
            currentBuildVars,
            otherBuildEnvVars,
            currentBuild,
            otherBuild,
            listener
        );

        /*
         * A return type object was designated and specified so that calling
         * implementations may use granular details about the result in
         * (for example) logging
         */
        return new ImportVarsResult(
            projectName,
            buildId,
            (currentBuildVars.size() - originalSize)
        );
    }

    protected abstract void doEnvVarTransfer(
        V envVarTransferAgent,
        Map<String, String> currentBuildEnvVars,
        Map<String, String> otherBuildEnvVars,
        AbstractBuild currentBuild,
        AbstractBuild otherBuild,
        TaskListener listener
    );

}
