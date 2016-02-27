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
import hudson.util.AlternativeUiTextProvider;
import hudson.util.FormValidation;
import java.io.PrintStream;
import java.io.IOException;
import javax.servlet.ServletException;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link ImportOtherBuildEnvVarsBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform} method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class ImportOtherBuildEnvVarsBuilder extends Builder implements SimpleBuildStep {

    private final String projectName;
    private final String buildId;
    private final TemplatingOtherBuildEnvVarsImporter varImporter;

    private transient ExternalProjectProvider<AbstractProject> projectProvider = new SingletonCallExternalProjectProvider();
    private transient ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider = new NamedBuildExternalBuildProvider();

    public ImportOtherBuildEnvVarsBuilder(
        final String projectName,
        final String buildId,
        final TemplatingOtherBuildEnvVarsImporter varImporter
    ) {
        this.projectName = projectName;
        this.buildId = buildId;
        this.varImporter = varImporter;
    }

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

    public String getProjectName() {
        return this.projectName;
    }

    public String getBuildId() {
        return this.buildId;
    }

    public String getVarNameTemplate() {
        return this.varImporter.getVarNameTemplate();
    }

    public void setProjectProvider(ExternalProjectProvider<AbstractProject> projectProvider) {
        this.projectProvider = projectProvider;
    }

    public void setBuildProvider(ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider) {
        this.buildProvider = buildProvider;
    }

    protected void prePerform() throws RuntimeException {
        if (this.projectProvider == null) {
            this.projectProvider = new SingletonCallExternalProjectProvider();
        }
        
        if (this.buildProvider == null) {
            this.buildProvider = new NamedBuildExternalBuildProvider();
        }
    }

    @Override
    public void perform(Run<?,?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        this.prePerform();
        
        final PrintStream logger = listener.getLogger();

        try {
            final AbstractProject otherProject = this.projectProvider.provideProject(this.projectName);
            final AbstractBuild otherBuild = this.buildProvider.provideBuild(
                otherProject,
                build.getEnvironment(listener).expand(this.buildId)
            );

            EnvVars otherBuildEnvVars = otherBuild.getEnvironment(listener);

            this.varImporter.importVars(build, otherBuildEnvVars);

            logger.println(
                Messages.ImportOtherBuildEnvVarsBuilder_Imported(
                    otherBuildEnvVars.size(),
                    otherBuild.getDisplayName(),
                    otherProject.getName()
                )
            );

        } catch (OtherBuildVarImportException ex) {
            build.setResult(Result.FAILURE);
            logger.println(
                Messages.ImportOtherBuildEnvVarsBuilder_ImportError(ex.getMessage())
            );

        } catch (InterruptedException | IOException ex) {
            build.setResult(Result.FAILURE);

            logger.println(
                Messages.ImportOtherBuildEnvVarsBuilder_FailedToObtainEnvironment()
            );
            ex.printStackTrace(logger);
        }
    }

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
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public FormValidation doCheckVarNameTemplate(@QueryParameter String value) throws IOException, ServletException {
            return (
                value.isEmpty() || EnvContributingVarsImporter.isVarNameTemplateValid(value)
                ? FormValidation.ok()
                : FormValidation.error("Variable name template must contain one instance of '%s' for string population")
            );
        }
        
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public String getDisplayName() {
            return "Import environment vars from another build";
        }

    }

}
