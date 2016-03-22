/**
 * OtherBuildSelectorParameter.java
 * Created 28-Feb-2016 12:49:11
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

package com.mattdw.jenkins.plugins.otherbuild.envvars;

import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsConfiguration;
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.factory.ImportVarsExecutorFactory;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvContributingVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.OtherProjectBuildOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.ResultFilteringOtherProjectBuildOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.ResultOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ProjectNotFoundException;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.SingletonCallExternalProjectProvider;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterDefinition.ParameterDescriptor;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Result;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import java.io.IOException;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;



/**
 * Definition of a parameter to select a previous build [in another project] 
 * from a list which is optionally filtered
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class OtherBuildSelectorParameterDefinition extends ParameterDefinition {

    /**
     * String name of the result status (corresponding to {@link Result} constants;
     * a NULL value indicates no filtering
     */
    protected final String buildResultFilter;
    
    /**
     * Project name of the project from which past builds will be displayed
     */
    protected final String projectName;
    
    /**
     * Variable importer mechanism; a NULL value indicates that variable import
     * will not happen post parameter value generation
     */
    protected final TemplatingEnvVarsCopier varImporter;

    /**
     * Factory the build executor (to which the actual logic of importing build
     * variables from another project is delegated)
     */
    protected transient ImportVarsExecutorFactory executorFactory;


    
    /**
     * Constructor - creates a new instance of OtherBuildSelectorParameterDefinition
     * 
     * @param name
     *      Given name of the parameter
     * @param description
     *      Description of the parameter
     * @param projectName
     *      Project name of the project from which past builds will be displayed
     * @param buildResultFilter
     *      String name of the result status (corresponding to {@link Result} 
     *      constants; a NULL value indicates no filtering
     * @param varImporter
     *      Variable importer mechanism; a NULL value indicates that variable
     *      import will not happen post parameter value generation
     * @param executorFactory 
     *      Factory the build executor (to which the actual logic of importing
     *      build variables from another project is delegated)
     */
    public OtherBuildSelectorParameterDefinition(
        final String name,
        final String description,
        final String projectName,
        final String buildResultFilter,
        final TemplatingEnvVarsCopier varImporter,
        final ImportVarsExecutorFactory<EnvVarsCopier, TemplatingEnvVarsCopier, ?> executorFactory
    ) {
        super(name, description);

        this.projectName = projectName;
        this.buildResultFilter = buildResultFilter;
        this.varImporter = varImporter;
        this.executorFactory = executorFactory;
    }

    /**
     * 
     * @param name
     *      Given name of the parameter
     * @param description
     *      Description of the parameter
     * @param projectName
     *      Project name of the project from which past builds will be displayed
     * @param filterByBuildResult
     *      Indicates whether or not to filter by build result
     * @param buildResultFilter
     *      String name of the result status (corresponding to {@link Result} 
     *      constants (subject to filterByBuildResult)
     * @param doVariableImport
     *      Indicates whether or not to do variable import as part of
     *      parameter generation
     * @param varNameTemplate 
     *      Variable name template for the default variable importer
     *      implementation (subject to doVariableImport)
     * @see OtherBuildSelectorParameterDefinition.validateVarNameTemplate
     */
    @DataBoundConstructor
    public OtherBuildSelectorParameterDefinition(
        final String name,
        final String description,
        final String projectName,
        final boolean filterByBuildResult,
        final String buildResultFilter,
        final boolean doVariableImport,
        final String varNameTemplate
    ) {
        this(name,
            description,
            projectName,
            (filterByBuildResult ? buildResultFilter : null),
            validateVarNameTemplate(doVariableImport, varNameTemplate),
            new ImportVarsExecutorFactory.CopierImpl()
        );
    }
    
    /**
     * Validates input of variable name template arguments for the constructor
     * 
     * @param doVariableImport
     *      Indicates whether or not to do variable import as part of
     *      parameter generation
     * @param varNameTemplate 
     *      Variable name template for the default variable importer
     *      implementation (subject to doVariableImport)
     * @return 
     *      {@link EnvContributingVarsImporter} if doVariableImport is TRUE,
     *      variable name template is valid and constructor call does not
     *      throw an {@link IllegalArgumentException}; otherwise NULL
     *      
     */
    protected static TemplatingEnvVarsCopier validateVarNameTemplate(
        final boolean doVariableImport,
        final String varNameTemplate
    ) {
        try {
            // Trigger a phony exception to reuse logic in the corresponding catch block
            if (doVariableImport == false) {
                throw new IllegalArgumentException();
            }

            return new EnvContributingVarsImporter(varNameTemplate);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Boolean indicator for filtering by build resultsr
     * 
     * @return TRUE if the result filter has been set; otherwise FALSE
     */
    public boolean isFilterByBuildResult() {
        return (this.buildResultFilter != null);
    }

    /**
     * Alternative boolean indicator for filtering by build results
     * 
     * @return TRUE if the result filter has been set; otherwise FALSE
     * 
     * @see OtherBuildSelectorParameterDefinition.isFilterByBuildResult
     */
    public boolean getFilterByBuildResult() {
        return this.isFilterByBuildResult();
    }

    /**
     * Getter for buildResultFilter
     * 
     * @return String name of the result status (corresponding to {@link Result} 
     * constants; a NULL value indicates no filtering
     */
    public String getBuildResultFilter() {
        return this.buildResultFilter;
    }

    /**
     * Boolean indicator for doing variable import
     * 
     * @return TRUE if the variable importer has been set; otherwise FALSE
     */
    public boolean isDoVariableImport() {
        return (this.varImporter != null);
    }

    /**
     * Alternative boolean indicator for doing variable import
     * 
     * @return TRUE if the variable importer has been set; otherwise FALSE
     */
    public boolean getDoVariableImport() {
        return this.isDoVariableImport();
    }

    /**
     * Pseudo-getter for varNameTemplate
     * 
     * @return 
     *      Variable name template given for the variable importer
     */
    public String getVarNameTemplate() {
        return (
            this.varImporter != null
            ? this.varImporter.getVarNameTemplate()
            : null
        );
    }

    /**
     * Getter for projectName 
     * 
     * @return
     *      Project name of the project from which past builds will be displayed
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Executes preparatory actions for <pre>createValue()</pre>, most notably
     * ensuring that service properties have been set, or are initialised to
     * their nominal defaults
     * 
     * This is primarily to resolve backwards compatibility issues as the plugin
     * moves between versions
     * 
     * @throws RuntimeException 
     *      Implementation-specific runtime exceptions that may be thrown
     *      within the scope of individual service initialisation
     */
    protected void preCreateValue() {
        if (this.executorFactory == null) {
            this.executorFactory = new ImportVarsExecutorFactory.CopierImpl();
        }
    }

    /**
     * Performs the action of generating a parameter value of type
     * {@link OtherBuildSelectorParameterValue} based on user-entered name
     * and values
     * 
     * @param req
     *      Stapler request in which user data was submitted (not used in this
     *      implementation)
     * @param jo
     *      JSONObject containing key value pairs of user-specified input
     * @return 
     *      {@link OtherBuildSelectorParameterValue} if "name" and "value"
     *      request parameters have been given; otherwise NULL
     */
    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        this.preCreateValue();

        if (!jo.containsKey("name") || !jo.containsKey("value")) {
            return null;
        }

        return new OtherBuildSelectorParameterValue(
            jo.getString("name"),
            new ImportVarsConfiguration(
                this.projectName,
                jo.getString("value"),
                this.varImporter
            ),
            this.executorFactory.createExecutor()
        );
    }

    /**
     * Creates value based on a Stapler request alone; as this is not supported
     * in this implementation, the result is unconditionally NULL
     * 
     * @param req
     *      Stapler request
     * @return 
     *      NULL, unconditionally
     */
    @Override
    public ParameterValue createValue(StaplerRequest req) {
        return null;
    }

    /**
     * Returns the descriptor of this extensible object, which is defined below
     * as a public, static inner class
     * 
     * @return
     *      {@link DescriptorImpl} of this extensible object
     * 
     * @see OtherBuildSelectorParameterDefinition.DescriptorImpl
     */
    @Override
    public ParameterDescriptor getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }



    /**
     * Descriptor for {@link OtherBuildSelectorParameterDefinition}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     * </p>
     */
    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        /**
         * Provider of possible result state options for list hydration
         */
        protected ResultOptionsProvider resultOptionsProvider;
        
        /**
         * External project provider, for the purpose of enumerating projects
         * from which optionally-filtered builds may be presented for selection
         */
        protected ExternalProjectProvider projectProvider;
        
        /**
         * Provider of possible build options given an optional result filter
         * and a selected project
         */
        protected OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result> buildOptionsProviderFactory;



        /**
         * Constructor - creates a new instance of DescriptorImpl;
         * 
         * Initialises the descriptor and the persisted global configuration
         * through invocation of <pre>load()</pre>
         * 
         * @param resultOptionsProvider
         *      Provider of possible result state options for list hydration
         * @param projectProvider
         *      External project provider, for the purpose of enumerating projects
         *      from which optionally-filtered builds may be presented for selection
         * @param buildOptionsProviderFactory 
         *      Provider of possible build options given an optional result filter
         *      and a selected project
         */
        public DescriptorImpl(
            final ResultOptionsProvider resultOptionsProvider,
            final ExternalProjectProvider projectProvider,
            final OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result> buildOptionsProviderFactory
        ) {
            this.resultOptionsProvider = resultOptionsProvider;
            this.projectProvider = projectProvider;
            this.buildOptionsProviderFactory = buildOptionsProviderFactory;
            
            load();
        }

        /**
         * Constructor - creates a new instance of DescriptorImpl with no arguments,
         * assuming default implementations of services required by the
         * other constructor.
         * 
         * This will be the constructor that is nominally used by Jenkins.
         */
        public DescriptorImpl() {
            this(
                new ResultOptionsProvider.Impl(),
                new SingletonCallExternalProjectProvider(),
                new ResultFilteringOtherProjectBuildOptionsProvider.Factory()
            );
        }

        /**
         * Generates a list box model of options for build result filters
         * 
         * @return 
         *      {@link ListBoxModel} hydrated with options for build result filters
         */
        public ListBoxModel doFillBuildResultFilterItems() {
            return this.resultOptionsProvider.getBuildResultOptions();
        }

        /**
         * Generates a list box model of options for past builds from the
         * configured source project
         * 
         * @param project
         *      Current project in the context of this request
         * @param param
         *      Name of the defined parameter for which to retrieve these options
         * @return
         *      {@link ListBoxModel} hydrated with options for build result filters,
         *      or an empty model if any issues arise in pulling up the
         *      parameter definition
         * @throws IOException
         *      If any I/O errors occur during generation
         * @throws InterruptedException 
         *      If any interruption errors occur during generation
         */
        public ListBoxModel doFillValueItems(
            @AncestorInPath AbstractProject project,
            @QueryParameter String param
        ) throws IOException, InterruptedException {
            ParametersDefinitionProperty prop = (ParametersDefinitionProperty) project.getProperty(ParametersDefinitionProperty.class);

            ParameterDefinition def;
            if (
                prop != null
                && (def = prop.getParameterDefinition(param)) instanceof OtherBuildSelectorParameterDefinition
            ) {
                return this.doFillValueItems((OtherBuildSelectorParameterDefinition) def);
            }

            /*
             * We always want to return a ListBoxModel, but a failure in the above
             * conditions necessitates that an empty model be returned
             */
            return new ListBoxModel();
        }

        /**
         * Fills the value items for a given {@link OtherBuildSelectorParameterDefinition}
         * object, as established by <pre>DescriptorImpl.doFillValueItems</pre>
         * 
         * @param definition
         *      Parameter definition from which source project and optional
         *      filter can be retrieved to produce a list box model
         * @return 
         *      {@link ListBoxModel} hydrated with options for build result filters,
         *      or an empty model if any exceptions occur
         */
        protected ListBoxModel doFillValueItems(OtherBuildSelectorParameterDefinition definition) {
            String resultFilter = definition.getBuildResultFilter();

            try {
                return this.buildOptionsProviderFactory.buildProvider(
                    resultFilter != null ? Result.fromString(resultFilter) : null
                ).getOptionsForProject(this.projectProvider.provideProject(definition.getProjectName()));
            } catch (ProjectNotFoundException ex) {
                return new ListBoxModel();
            }
        }
        
        /**
         * Performs validation on any submitted value for varImporter;
         * automatically triggered by Jenkins
         * 
         * @param value
         *      Given value of varImporter (injected as a query parameter)
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
         * Returns the display name of this parameter definition
         * 
         * @return Display name of this parameter definition
         */
        @Override
        public String getDisplayName() {
            return Messages.OtherBuildSelectorParameterDefinition_ParameterDefinitionDisplayName();
        }

    }
    
}
