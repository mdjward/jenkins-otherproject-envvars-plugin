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
package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import hudson.util.RunList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class ResultFilteringOtherProjectBuildOptionsProviderTest {
    
    private Result result;
    private OtherProjectBuildOptionFormatter formatter;
    private ResultFilteringOtherProjectBuildOptionsProvider provider;
    private AbstractProject project;
    private AbstractBuild build;
    
    @Before
    public void setUp() {
        this.result = Result.ABORTED;
        this.formatter = mock(OtherProjectBuildOptionFormatter.class);
        
        this.provider = new ResultFilteringOtherProjectBuildOptionsProvider(
            this.result,
            this.formatter
        );
        
        this.project = mock(AbstractProject.class);
        this.build = mock(AbstractBuild.class);
    }
    
    @Test
    public void testAlternativeConstructorUsesDefaultFormatter() throws Exception {
        this.provider = new ResultFilteringOtherProjectBuildOptionsProvider(this.result);
        
        java.lang.reflect.Field formatterField = this.provider.getClass().getDeclaredField("formatter");
        formatterField.setAccessible(true);
        
        assertTrue(
            formatterField.get(this.provider) instanceof OtherProjectBuildOptionFormatter.DefaultImpl
        );
    }

    @Test
    public void testGetOptionsForProject() {
        final RunList<AbstractBuild> buildList = RunList.fromRuns(java.util.Arrays.asList(new AbstractBuild[]{this.build}));
        final String formattedBuildName = "FORTY TWO";

        when(this.project.getBuilds()).thenReturn(buildList);
        when(this.build.getResult()).thenReturn(this.result);
        when(this.build.getNumber()).thenReturn(42);
        when(this.formatter.formatBuild(same(this.build))).thenReturn(formattedBuildName);

        ListBoxModel list = this.provider.getOptionsForProject(this.project);
        assertEquals(1, list.size());

        Option firstListEntry = list.get(0);

        assertEquals(formattedBuildName, firstListEntry.name);
        assertEquals("42", firstListEntry.value);

        verify(this.project, times(1)).getBuilds();
        verify(this.build, times(2)).getResult();
        verify(this.build, times(1)).getNumber();
        verify(this.formatter, times(1)).formatBuild(same(this.build));
    }
    
    @Test
    public void testGetOptionsForProjectReturnsEmptyListWhenNoResultToCompare() {
        this.provider = new ResultFilteringOtherProjectBuildOptionsProvider(
            null,
            this.formatter
        );
        
        final RunList<AbstractBuild> buildList = RunList.fromRuns(java.util.Arrays.asList(new AbstractBuild[]{this.build}));

        when(this.project.getBuilds()).thenReturn(buildList);

        ListBoxModel list = this.provider.getOptionsForProject(this.project);
        assertEquals(0, list.size());

        verify(this.project, times(1)).getBuilds();
    }
   
}
