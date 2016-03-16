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

import java.io.IOException;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.OtherProjectBuildOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.ResultFilteringOtherProjectBuildOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options.ResultOptionsProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ProjectNotFoundException;
import hudson.model.AbstractProject;
import hudson.model.ParameterValue;
import hudson.model.ParametersDefinitionProperty;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import jenkins.model.Jenkins.JenkinsHolder;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.kohsuke.stapler.StaplerRequest;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class OtherBuildSelectorParameterDefinitionTest {
    
    private final String parameterName = "PARAMETER NAME";
    private final String parameterDescription = "PARAMETER DESCRIPTION";
    private final String buildResultFilter = Result.SUCCESS.toString();
    private final boolean filterByBuildResult = true;
    private final String projectName = "PROJECT NAME";
    private OtherBuildSelectorParameterDefinition parameter;
    private ResultOptionsProvider resultOptionsProvider;
    private ExternalProjectProvider projectProvider;
    private OtherBuildSelectorParameterDefinition.DescriptorImpl descriptor;
    private OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result> buildOptionsProviderFactory;

    @Before
    public void setUp() {
        this.parameter = new OtherBuildSelectorParameterDefinition(
            this.parameterName,
            this.parameterDescription,
            this.projectName,
            this.filterByBuildResult,
            this.buildResultFilter
        );

        this.descriptor = mock(OtherBuildSelectorParameterDefinition.DescriptorImpl.class);
        doNothing().when(this.descriptor).load();

        this.descriptor.resultOptionsProvider = (this.resultOptionsProvider = mock(ResultOptionsProvider.class));
        this.descriptor.projectProvider = (this.projectProvider = mock(ExternalProjectProvider.class));
        
        this.descriptor.buildOptionsProviderFactory = (OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result>) 
                (this.buildOptionsProviderFactory = mock(OtherProjectBuildOptionsProvider.Factory.class))
        ;
    }

    @Test
    public void testGetBuildResultFilter() {
        assertSame(this.buildResultFilter, this.parameter.getBuildResultFilter());
    }

    @Test
    public void testIsFilterByBuildResult() {
        assertEquals(this.filterByBuildResult, this.parameter.getFilterByBuildResult());
        assertEquals(this.filterByBuildResult, this.parameter.isFilterByBuildResult());
    }

    @Test
    public void testGetProjectName() {
        assertSame(this.projectName, this.parameter.getProjectName());
    }

    @Test
    public void testCreateValue_StaplerRequest_JSONObject_handlesMissingKeys() {
        JSONObject jo = new JSONObject();
        
        jo.put("name", this.parameterName);
        
        assertNull(this.parameter.createValue(mock(StaplerRequest.class), jo));
    }

    @Test
    public void testCreateValue_StaplerRequest_JSONObject_returnsObject() {
        JSONObject jo = new JSONObject();
        
        jo.put("name", this.parameterName);
        jo.put("value", this.parameterDescription);
        
        ParameterValue value = this.parameter.createValue(mock(StaplerRequest.class), jo);
        assertTrue(value instanceof OtherBuildSelectorParameterValue);
        
        OtherBuildSelectorParameterValue castValue = (OtherBuildSelectorParameterValue) value;
        assertSame(this.parameterName, castValue.getName());
        assertSame(this.parameterDescription, castValue.getValue());
    }

    @Test
    public void testCreateValue_StaplerRequest() {
        assertNull(this.parameter.createValue(mock(StaplerRequest.class)));
    }

    @Test
    public void testGetDescriptor() {
        Jenkins jenkins = mock(Jenkins.class);
        
        try {
            JenkinsHolder jenkinsHolder = mock(JenkinsHolder.class);
            
            java.lang.reflect.Field holderField = Jenkins.class.getDeclaredField("HOLDER");
            holderField.setAccessible(true);
            
            holderField.set(null, jenkinsHolder);
            
            when(jenkinsHolder.getInstance()).thenReturn(jenkins);
            
        } catch (ReflectiveOperationException ex) {
            fail("Reflection exception occured: " + ex.getMessage());
        }
        
        when(jenkins.getDescriptorOrDie(eq(OtherBuildSelectorParameterDefinition.class))).thenReturn(this.descriptor);
        
        assertSame(
            this.descriptor,
            this.parameter.getDescriptor()
        );
        
        verify(jenkins, times(1)).getDescriptorOrDie(eq(OtherBuildSelectorParameterDefinition.class));
    }
    
    @Test
    public void testDoFillBuildResultFilterItems() {
        ListBoxModel listBox = mock(ListBoxModel.class);
        
        when(this.resultOptionsProvider.getBuildResultOptions()).thenReturn(listBox);
        when(this.descriptor.doFillBuildResultFilterItems()).thenCallRealMethod();
        
        assertSame(listBox, this.descriptor.doFillBuildResultFilterItems());
        
        verify(this.resultOptionsProvider, times(1)).getBuildResultOptions();
        verify(this.descriptor, times(1)).doFillBuildResultFilterItems();
    }
    
    @Test
    public void testDoFillValueItems() throws Exception {
        final String queryParam = "PARAMETER";
        
        AbstractProject project = mock(AbstractProject.class);
        ParametersDefinitionProperty prop = mock(ParametersDefinitionProperty.class);
        OtherBuildSelectorParameterDefinition parameter = mock(OtherBuildSelectorParameterDefinition.class);
        
        when(project.getProperty(same(ParametersDefinitionProperty.class))).thenReturn(prop);
        when(prop.getParameterDefinition(same(queryParam))).thenReturn(parameter);
        when(parameter.getBuildResultFilter()).thenReturn(this.buildResultFilter);
        when(parameter.getProjectName()).thenReturn(this.projectName);
        
        ListBoxModel listBox = mock(ListBoxModel.class);
        ResultFilteringOtherProjectBuildOptionsProvider buildOptionsProvider = mock(ResultFilteringOtherProjectBuildOptionsProvider.class);
        when(this.buildOptionsProviderFactory.buildProvider(eq(Result.SUCCESS))).thenReturn(buildOptionsProvider);
        when(this.projectProvider.provideProject(same(this.projectName))).thenReturn(project);
        when(buildOptionsProvider.getOptionsForProject(same(project))).thenReturn(listBox);
        
        when(this.descriptor.doFillValueItems(same(project), same(queryParam))).thenCallRealMethod();
        when(this.descriptor.doFillValueItems(isA(OtherBuildSelectorParameterDefinition.class))).thenCallRealMethod();
        assertSame(
            listBox,
            this.descriptor.doFillValueItems(project, queryParam)
        );
        
        verify(project, times(1)).getProperty(same(ParametersDefinitionProperty.class));
        verify(prop, times(1)).getParameterDefinition(same(queryParam));
        verify(parameter, times(1)).getBuildResultFilter();
        verify(parameter, times(1)).getProjectName();

        verify(this.buildOptionsProviderFactory, times(1)).buildProvider(eq(Result.SUCCESS));
        verify(this.projectProvider, times(1)).provideProject(same(this.projectName));
        verify(buildOptionsProvider, times(1)).getOptionsForProject(same(project));
    }

    @Test
    public void testDoFillValueItemsReturnsEmptyListAsFallback() throws Exception {
        final String queryParam = "PARAMETER";
        
        AbstractProject project = mock(AbstractProject.class);
        ParametersDefinitionProperty prop = mock(ParametersDefinitionProperty.class);
        
        when(project.getProperty(same(ParametersDefinitionProperty.class))).thenReturn(prop);
        when(prop.getParameterDefinition(same(queryParam))).thenReturn(null);
        
        when(this.descriptor.doFillValueItems(same(project), same(queryParam))).thenCallRealMethod();
        when(this.descriptor.doFillValueItems(isA(OtherBuildSelectorParameterDefinition.class))).thenCallRealMethod();

        ListBoxModel returnedList = this.descriptor.doFillValueItems(project, queryParam);
        assertNotNull(returnedList);
        assertTrue(returnedList.size() == 0);
        
        verify(project, times(1)).getProperty(same(ParametersDefinitionProperty.class));
        verify(prop, times(1)).getParameterDefinition(same(queryParam));
    }
    
    @Test
    public void testDoFillValueItemsReturnsEmptyListOnException() throws Exception {
        final String queryParam = "PARAMETER";
        
        AbstractProject project = mock(AbstractProject.class);
        ParametersDefinitionProperty prop = mock(ParametersDefinitionProperty.class);
        OtherBuildSelectorParameterDefinition parameter = mock(OtherBuildSelectorParameterDefinition.class);
        
        when(project.getProperty(same(ParametersDefinitionProperty.class))).thenReturn(prop);
        when(prop.getParameterDefinition(same(queryParam))).thenReturn(parameter);
        when(parameter.getBuildResultFilter()).thenReturn(this.buildResultFilter);
        when(parameter.getProjectName()).thenReturn(this.projectName);
        
        ResultFilteringOtherProjectBuildOptionsProvider buildOptionsProvider = mock(ResultFilteringOtherProjectBuildOptionsProvider.class);
        when(this.buildOptionsProviderFactory.buildProvider(eq(Result.SUCCESS))).thenReturn(buildOptionsProvider);
        when(this.projectProvider.provideProject(same(this.projectName))).thenThrow(new ProjectNotFoundException(this.projectName));
        when(this.descriptor.doFillValueItems(same(project), same(queryParam))).thenCallRealMethod();
        when(this.descriptor.doFillValueItems(isA(OtherBuildSelectorParameterDefinition.class))).thenCallRealMethod();
        
        ListBoxModel returnedList = this.descriptor.doFillValueItems(project, queryParam);
        assertNotNull(returnedList);
        assertTrue(returnedList.size() == 0);
        
        verify(project, times(1)).getProperty(same(ParametersDefinitionProperty.class));
        verify(prop, times(1)).getParameterDefinition(same(queryParam));
        verify(parameter, times(1)).getBuildResultFilter();
        verify(parameter, times(1)).getProjectName();

        verify(this.buildOptionsProviderFactory, times(1)).buildProvider(eq(Result.SUCCESS));
        verify(this.projectProvider, times(1)).provideProject(same(this.projectName));
    }
    
    @Test
    public void testGetDescriptorDisplayName() {
        when(this.descriptor.getDisplayName()).thenCallRealMethod();
        assertEquals("Other build", this.descriptor.getDisplayName());
    }
    
}
