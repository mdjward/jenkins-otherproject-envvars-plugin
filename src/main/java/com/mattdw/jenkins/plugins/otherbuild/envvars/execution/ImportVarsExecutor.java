/**
 * ImportOtherBuildEnvVarsBaseBuilder.java
 * Created 21-Mar-2016 10:01:17
 *
 * @author M.D.Ward <dev@mattdw.co.uk>
 * The MIT License
 *
 * Copyright 2016 M.D.Ward <dev@mattdw.co.uk>.
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
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingOtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarImporterOrCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarNameTemplateAware;
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
 * Definition of functionality to perform the actual "heavy lifting" logic
 * of importing variables from one build into another.
 * 
 * This function was abstracted out in this manner for reuse in a builder,
 * the parameter and possible future elements such as build wrappers or publishers.
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 * 
 * @param <M>
 *      [Super]type for environment vars; must extend {@link Map} with
 *      {@link String} types for both key and value
 * @param <V>
 *      [Super]type for environment var copier; must extend {@link VarImporterOrCopier}
 * @param <T>
 *      [Super]type for template aware generic used in {@link ImportVarsConfiguration};
      must extend VarNameTemplateAware
 * @param <L>
 *      [Super]type for task listener; must extend {@link TaskListener}
 * 
 */
public interface ImportVarsExecutor <M extends Map<String, String>, V extends VarImporterOrCopier, T extends VarNameTemplateAware, L extends TaskListener> {
    
    /**
     * Performs (executes) the action of importing environment variables
     * from another build into the current build
     * 
     * @param configuration
     *      Configuration for this parameter value and the optional variable
     *      import, which provides the originating project's name, build number
     *      and variable importer 
     * @param envVarTransferAgent
     *      Variable importer mechanism; a NULL value indicates that variable
     *      import will not happen as part of environment variable contribution
     * @param currentBuildVars
     *      Build variables (so far) for the current build
     * @param listener
     *      Build listener - necessary for the executor to derive an environment
     *      from a past build
     * @param currentBuild
     *      Current build
     * @return
     *      {@link ImportVarsResult} object describing the result of the 
     *      variable import
     * @throws IOException
     *      If any I/O errors occur during generation
     * @throws InterruptedException 
     *      If any interruption errors occur during generation
     * @throws OtherBuildVarImportException 
     *      If any errors occur during the import of an external build
     */
    public ImportVarsResult perform(
        ImportVarsConfiguration<T> configuration,
        V envVarTransferAgent,
        M currentBuildVars,
        L listener,
        AbstractBuild currentBuild
    ) throws InterruptedException, IOException, OtherBuildVarImportException;



    /**
    * Concrete implementation of {@link ImportVarsExecutor} which utilises
    * variable copier and name aware objects of the corresponding types
    * 
    * @author M.D.Ward <matthew.ward@byng.co>
    */
    public static class CopierImpl extends AbstractImpl<EnvVarsCopier, TemplatingEnvVarsCopier> {

        /**
         * Constructor - creates a new instance of CopierImpl
         * 
         * @param projectProvider
         *      Project provider mechanism for the target build from which
         *      variables are imported
         * @param buildProvider 
         *      Build provider mechanism for the target build from which
         *      variables are imported
         */
        public CopierImpl(
            ExternalProjectProvider projectProvider,
            ExternalBuildProvider buildProvider
        ) {
            super(projectProvider, buildProvider);
        }

        /**
         * Handles the actual transfer of environment variables from
         * other build to current build
         * 
         * @param varCopier
         *      Variable copier
         * @param currentBuildEnvVars
         *      Map of current build environment variables
         * @param otherBuildEnvVars
         *      Map of already-derived environment variables from target build
         *      - to be transferred within the scope of this method
         * @param currentBuild
         *      Current build
         * @param otherBuild
         *      Other (target) build from which to import the variables
         * @param listener 
         *      Build listener
         */
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



    /**
    * Concrete implementation of {@link ImportVarsExecutor} which utilises
    * variable importer and name aware objects of the corresponding types
    * 
    * @author M.D.Ward <matthew.ward@byng.co>
    */
    public static class ImporterImpl extends AbstractImpl<OtherBuildEnvVarsImporter, TemplatingOtherBuildEnvVarsImporter> {

        /**
         * Constructor - creates a new instance of ImporterImpl
         * 
         * @param projectProvider
         *      Project provider mechanism for the target build from which
         *      variables are imported
         * @param buildProvider 
         *      Build provider mechanism for the target build from which
         *      variables are imported
         */
        public ImporterImpl(
            ExternalProjectProvider<AbstractProject> projectProvider,
            ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
        ) {
            super(projectProvider, buildProvider);
        }

        /**
         * Handles the actual transfer of environment variables from
         * other build to current build
         * 
         * @param envImporter
         *      Variable importer
         * @param currentBuildEnvVars
         *      Map of current build environment variables
         * @param otherBuildEnvVars
         *      Map of already-derived environment variables from target build
         *      - to be transferred within the scope of this method
         * @param currentBuild
         *      Current build
         * @param otherBuild
         *      Other (target) build from which to import the variables
         * @param listener 
         *      Build listener
         */
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



/**
 * Abstract implementation of {@link ImportVarsExecutor} which stipulates
 * immutable service fields, a near-complete definition of <pre>perform()</pre>
 * and a skeleton method for extending classes to simply define how variables
 * are transferred.
 * 
 * This abstract class is defined as a package-protected class defined within the
 * same file as the interface it implements
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 * 
 * @param <V>
 *      [Super]type of the variable importer/copier; must extend
 *      {@link VarImporterOrCopier}
 * @param <T>
 *      [Super]type of the variable template aware object; must extend
 *      {@link VarNameTemplateAware}
 * @param <E>
 *      [Super]type of the executor to be generated by an implementation of the
 *      factory; must extend {@link ImportVarsExecutor} and is further constrained
 *      by generics V and T, as well as EnvVars and TaskListener
 *      
 */
abstract class AbstractImpl <V extends VarImporterOrCopier, T extends VarNameTemplateAware> implements ImportVarsExecutor<EnvVars, V, T, TaskListener> {

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
     * Constructor - creates a new instance of AbstractImpl
     * 
     * @param projectProvider
     *      Project provider mechanism for the target build from which
     *      variables are imported
     * @param buildProvider 
     *      Build provider mechanism for the target build from which
     *      variables are imported
     */
    public AbstractImpl(
        ExternalProjectProvider<AbstractProject> projectProvider,
        ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
    ) {
        this.projectProvider = projectProvider;
        this.buildProvider = buildProvider;
    }

    /**
     * Performs (executes) the action of importing environment variables
     * from another build into the current build
     * 
     * @param configuration
     *      Configuration for this parameter value and the optional variable
     *      import, which provides the originating project's name, build number
     *      and variable importer 
     * @param envVarTransferAgent
     *      Variable importer mechanism; a NULL value indicates that variable
     *      import will not happen as part of environment variable contribution
     * @param currentBuildVars
     *      Build variables (so far) for the current build
     * @param listener
     *      Build listener - necessary for the executor to derive an environment
     *      from a past build
     * @param currentBuild
     *      Current build
     * @return
     *      {@link ImportVarsResult} object describing the result of the 
     *      variable import
     * @throws IOException
     *      If any I/O errors occur during generation
     * @throws InterruptedException 
     *      If any interruption errors occur during generation
     * @throws OtherBuildVarImportException 
     *      If any errors occur during the import of an external build
     */
    @Override
    public final ImportVarsResult perform(
        ImportVarsConfiguration<T> configuration,
        V envVarTransferAgent,
        EnvVars currentBuildVars,
        TaskListener listener,
        AbstractBuild currentBuild
    ) throws InterruptedException, IOException, OtherBuildVarImportException {
        final int originalSize = currentBuildVars.size();
        final String projectName = configuration.getProjectName();
        final String buildId = configuration.getBuildId();

        final AbstractProject otherProject = this.projectProvider.provideProject(
            projectName
        );

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

    /**
     * Handles the actual transfer of environment variables from
     * other build to current build; defined as a skeleton method for
     * concrete implementations to define the actual behaviour per their
     * own unique implementation
     * 
     * @param varCopier
     *      Variable copier
     * @param currentBuildEnvVars
     *      Map of current build environment variables
     * @param otherBuildEnvVars
     *      Map of already-derived environment variables from target build
     *      - to be transferred within the scope of this method
     * @param currentBuild
     *      Current build
     * @param otherBuild
     *      Other (target) build from which to import the variables
     * @param listener 
     *      Build listener
     */
    protected abstract void doEnvVarTransfer(
        V envVarTransferAgent,
        Map<String, String> currentBuildEnvVars,
        Map<String, String> otherBuildEnvVars,
        AbstractBuild currentBuild,
        AbstractBuild otherBuild,
        TaskListener listener
    );

}
