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
import hudson.util.ListBoxModel;
import java.io.IOException;
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

    protected String buildResultFilter;
    protected boolean filterByBuildResult;
    protected String projectName;
    
    @DataBoundConstructor
    public OtherBuildSelectorParameterDefinition(
        String name,
        String description,
        String projectName,
        boolean filterByBuildResult,
        String buildResultFilter
    ) {
        super(name, description);

        this.projectName = projectName;
        this.filterByBuildResult = filterByBuildResult;
        this.buildResultFilter = (filterByBuildResult ? buildResultFilter : null);
    }

    public String getBuildResultFilter() {
        return this.buildResultFilter;
    }

    public boolean isFilterByBuildResult() {
        return this.filterByBuildResult;
    }

    public boolean getFilterByBuildResult() {
        return this.isFilterByBuildResult();
    }

    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        if (!jo.containsKey("name") || !jo.containsKey("value")) {
            return null;
        }

        return new OtherBuildSelectorParameterValue((String) jo.get("name"), (String) jo.get("value"));
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

        @Override
        public String getDisplayName() {
            return "Other build";
        }

    }
    
}
