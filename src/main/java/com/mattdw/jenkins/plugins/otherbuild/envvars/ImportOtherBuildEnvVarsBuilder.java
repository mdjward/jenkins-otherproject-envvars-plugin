/*
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

package com.mattdw.jenkins.plugins.otherbuild.envvars;

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvContributingVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingOtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.SingletonCallExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.OtherBuildVarImportException;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.ExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.NamedBuildExternalBuildProvider;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.FormValidation;
import java.io.PrintStream;
import java.io.IOException;
import javax.servlet.ServletException;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;



/**
 * Builder to import environment variables from another build, optionally 
 * in another project
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class ImportOtherBuildEnvVarsBuilder extends Builder implements SimpleBuildStep {

    /**
     * Project name of the project to which the target build belongs
     */
    private final String projectName;
    
    /**
     * Identifier of the target build from which environment variables
     * are imported
     */
    private final String buildId;
    
    /**
     * Variable importer mechanism
     */
    private final TemplatingOtherBuildEnvVarsImporter varImporter;

    /**
     * Project provider mechanism for the target build from which
     * variables are imported
     */
    private transient ExternalProjectProvider<AbstractProject> projectProvider = new SingletonCallExternalProjectProvider();
    
    /**
     * Build provider mechanism for the target build from which
     * variables are imported
     */
    private transient ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider = new NamedBuildExternalBuildProvider();



    /**
     * Constructor - creates a new instance of ImportOtherBuildEnvVarsBuilder
     * 
     * @param projectName
     *      Project name of the project to which the target build belongs
     * @param buildId
     *      Identifier of the target build from which environment variables
     *      are imported
     * @param varImporter 
     *      Variable importer mechanism
     */
    public ImportOtherBuildEnvVarsBuilder(
        final String projectName,
        final String buildId,
        final TemplatingOtherBuildEnvVarsImporter varImporter
    ) {
        this.projectName = projectName;
        this.buildId = buildId;
        this.varImporter = varImporter;
    }

    /**
     * Constructor - creates a new instance of ImportOtherBuildEnvVarsBuilder
     * using data bound arguments provided by Jenkins when a builder of this
     * type is initialised or [re]configured
     * 
     * @param projectName
     *      Project name of the project to which the target build belongs
     * @param buildId
     *      Identifier of the target build from which environment variables
     *      are imported
     * @param varNameTemplate 
     *      String.format (printf) template to which the original environment
     *      variable names will be provided (notionally, so as not to overwrite
     *      existing variables within the scope of the build)
     */
    @DataBoundConstructor
    public ImportOtherBuildEnvVarsBuilder(
        final String projectName,
        final String buildId,
        final String varNameTemplate
    ) {
        this(
            projectName,
            buildId,
            new EnvContributingVarsImporter(varNameTemplate)
        );
    }

    /**
     * Getter for projectName
     * 
     * @return Project name of the project to which the target build belongs
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Getter for buildId
     * 
     * @return Identifier of the target build from which environment variables
     * are imported
     */
    public String getBuildId() {
        return this.buildId;
    }

    /**
     * Pseudo-getter for varNameTemplate
     * 
     * @return String.format (printf) template to which the original environment
     * variable names will be provided (notionally, so as not to overwrite
     * existing variables within the scope of the build)
     */
    public String getVarNameTemplate() {
        return this.varImporter.getVarNameTemplate();
    }

    /**
     * Setter for projectProvider
     * 
     * @param projectProvider 
     *      Project provider mechanism for the target build from which
     *      variables are imported
     */
    public void setProjectProvider(ExternalProjectProvider<AbstractProject> projectProvider) {
        this.projectProvider = projectProvider;
    }

    /**
     * Setter for buildProvider
     * 
     * @param buildProvider 
     *      Build provider mechanism for the target build from which
     *      variables are imported
     */
    public void setBuildProvider(ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider) {
        this.buildProvider = buildProvider;
    }

    /**
     * Executes preparatory actions for <pre>perform()</pre>, most notably
     * ensuring that service properties have been set, or are initialised to
     * their nominal defaults
     * 
     * @throws RuntimeException 
     *      Implementation-specific runtime exceptions that may be thrown
     *      within the scope of 
     */
    protected void prePerform() throws RuntimeException {
        if (this.projectProvider == null) {
            this.projectProvider = new SingletonCallExternalProjectProvider();
        }
        
        if (this.buildProvider == null) {
            this.buildProvider = new NamedBuildExternalBuildProvider();
        }
    }

    /**
     * Performs the build action of importing environment variables from
     * another build [in another project]
     * 
     * @param build
     *      Current build
     * @param workspace
     *      Workspace of the current build; not used in this implementation
     * @param launcher
     *      Launcher for the current build; not used in this implementation
     * @param listener
     *      TaskListener responsible for managing events for the current build;
     *      essential for accessing the current build's environment
     */
    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        
        // Essential that services have been initialised
        this.prePerform();
        
        // Logger is initialised out in method scope for use in all clauses
        final PrintStream logger = listener.getLogger();

        try {
            // Access other project and other build using the current task listener
            final AbstractProject otherProject = this.projectProvider.provideProject(this.projectName);
            final AbstractBuild otherBuild = this.buildProvider.provideBuild(
                otherProject,
                build.getEnvironment(listener).expand(this.buildId)
            );

            EnvVars otherBuildEnvVars = otherBuild.getEnvironment(listener);

            /*
             * Import these variables into the current build as the mechanism
             * prescribes; the exact details are decoupled from this builder
             */
            this.varImporter.importVars(build, otherBuildEnvVars);

            logger.println(
                Messages.ImportOtherBuildEnvVarsBuilder_Imported(
                    otherBuildEnvVars.size(),
                    otherBuild.getDisplayName(),
                    otherProject.getName()
                )
            );

        } catch (OtherBuildVarImportException ex) {
            
            // Any failure to import another project or build should fail the build
            build.setResult(Result.FAILURE);
            logger.println(
                Messages.ImportOtherBuildEnvVarsBuilder_ImportError(ex.getMessage())
            );

        } catch (InterruptedException | IOException ex) {
            
            // Any other exception that occurs should also fail the build
            build.setResult(Result.FAILURE);

            // ...with verbose output (including but not limited to the stack trace
            logger.println(
                Messages.ImportOtherBuildEnvVarsBuilder_FailedToObtainEnvironment()
            );
            ex.printStackTrace(logger);
        }
    }

    /**
     * Returns the descriptor of this extensible object, which is defined below
     * as a public, static inner class
     * 
     * @return 
     * @see ImportOtherBuildEnvVarsBuilder.DescriptorImpl
     */
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) (super.getDescriptor());
    }
    
    
    
    /**
     * Descriptor for {@link ImportOtherBuildEnvVarsBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * Constructor - creates a new instance of DescriptorImpl;
         * 
         * Initialises the descriptor and the persisted global configuration
         * through invocation of load
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs validation on any submitted value for varNameTemplate;
         * automatically triggered by Jenkins
         * 
         * @param value
         *      Given value of varNameTemplate (injected as a query parameter)
         * @return
         *      {@link FormValidation}.ok() if value is valid;
         *      otherwise FormValidation.error()
         * @throws IOException
         *      If any input/output errors occur indirectly as a result of
         *      executing this method
         * @throws ServletException
         *      If any Servlet errors occur indirectly as a result of
         *      executing this method
         */
        public FormValidation doCheckVarNameTemplate(@QueryParameter String value) throws IOException, ServletException {
            return (
                value.isEmpty() || EnvContributingVarsImporter.isVarNameTemplateValid(value)
                ? FormValidation.ok()
                : FormValidation.error("Variable name template must contain one instance of '%s' for string population")
            );
        }
        
        /**
         * Indicates whether this extension is compatible with certain projects.
         * This method always returns TRUE to indicate universal compatibility.
         * 
         * @param aClass
         *      AbstractProject implementation class descriptor to be
         *      checked for compatibility
         * @return 
         *      TRUE, unconditionally
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * Returns the display name of this builder
         * 
         * @return Display name of this builder
         */
        @Override
        public String getDisplayName() {
            return Messages.ImportOtherBuildEnvVarsBuilder_BuilderDisplayName();
        }

    }

}
