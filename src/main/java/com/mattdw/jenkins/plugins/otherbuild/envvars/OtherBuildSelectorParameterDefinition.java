/**
 * OtherBuildSelectorParameter.java
 * Created 28-Feb-2016 12:49:11
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

package com.mattdw.jenkins.plugins.otherbuild.envvars;

import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsConfiguration;
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.factory.ImportVarsExecutorFactory;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvContributingVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingOtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.OtherProjectBuildOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.ResultFilteringOtherProjectBuildOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.ResultOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ProjectNotFoundException;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.SingletonCallExternalProjectProvider;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor.FormException;
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
 * OtherBuildSelectorParameterDefinition
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class OtherBuildSelectorParameterDefinition extends ParameterDefinition {

    protected final String buildResultFilter;
    protected final String projectName;
    protected final TemplatingEnvVarsCopier varImporter;

    protected transient ImportVarsExecutorFactory executorFactory;


    
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
    
    protected static TemplatingEnvVarsCopier validateVarNameTemplate(
        final boolean doVariableImport,
        final String varNameTemplate
    ) {
        try {
            if (doVariableImport == false) {
                throw new IllegalArgumentException();
            }

            return new EnvContributingVarsImporter(varNameTemplate);
        } catch (Throwable ex) {
            return null;
        }
    }

    public String getBuildResultFilter() {
        return this.buildResultFilter;
    }

    public boolean isFilterByBuildResult() {
        return (this.buildResultFilter != null);
    }

    public boolean getFilterByBuildResult() {
        return this.isFilterByBuildResult();
    }

    public boolean isDoVariableImport() {
        return (this.varImporter != null);
    }

    public boolean getDoVariableImport() {
        return this.isDoVariableImport();
    }

    public String getVarNameTemplate() {
        return (
            this.varImporter != null
            ? this.varImporter.getVarNameTemplate()
            : null
        );
    }

    public String getProjectName() {
        return this.projectName;
    }

    protected void preCreateValue() {
        if (this.executorFactory == null) {
            this.executorFactory = new ImportVarsExecutorFactory.CopierImpl();
        }
    }

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

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        return null;
    }

    @Override
    public ParameterDescriptor getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }



    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        protected ResultOptionsProvider resultOptionsProvider;
        protected ExternalProjectProvider projectProvider;
        protected OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result> buildOptionsProviderFactory;

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

        public DescriptorImpl() {
            this(
                new ResultOptionsProvider.Impl(),
                new SingletonCallExternalProjectProvider(),
                new ResultFilteringOtherProjectBuildOptionsProvider.Factory()
            );
        }

        public ListBoxModel doFillBuildResultFilterItems() {
            return this.resultOptionsProvider.getBuildResultOptions();
        }

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

            return new ListBoxModel();
        }

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

        @Override
        public String getDisplayName() {
            return "Other build";
        }

    }
    
}
