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
package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.util.RunList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class NamedBuildExternalBuildProviderTest {
    
    private final String buildId = "NEW BUILD ID";
    private AbstractProject project;
    private AbstractBuild build;
    private NamedBuildExternalBuildProvider buildProvider;
    private RunList runList;
    
    @Before
    public void setUp() {
        this.project = mock(AbstractProject.class);
        this.build = mock(AbstractBuild.class);

        this.buildProvider = new NamedBuildExternalBuildProvider();
    }

    @Test
    public void testProvideBuildFindsBuildByName() throws Exception  {
        this.runList = RunList.fromRuns(java.util.Arrays.asList(new AbstractBuild[]{this.build}));

        when(this.build.getDisplayName()).thenReturn(this.buildId);
        when(this.project.getBuilds()).thenReturn(this.runList);

        assertSame(
            this.build,
            this.buildProvider.provideBuild(this.project, this.buildId)
        );

        verify(this.build, atLeast(1)).getDisplayName();
        verify(this.project, times(1)).getBuilds();
    }
    
    @Test
    public void testProvideBuildFallsBackToNumberOnException() throws Exception {
        this.runList = new RunList();
        
        when(this.project.getBuilds()).thenReturn(this.runList);
        when(this.project.getBuild(same(this.buildId))).thenReturn(this.build);

        assertSame(this.build, this.buildProvider.provideBuild(this.project, this.buildId));

        verify(this.project, times(1)).getBuilds();
        verify(this.project, times(1)).getBuild(same(buildId));
    }
    
}
