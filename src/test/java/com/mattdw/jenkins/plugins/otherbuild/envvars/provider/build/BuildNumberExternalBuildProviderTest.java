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
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class BuildNumberExternalBuildProviderTest {
    
    
    private final String buildId = "NEW BUILD ID";
    private AbstractProject project;
    private AbstractBuild build;
    private BuildNumberExternalBuildProvider buildProvider;
    
    @Before
    public void setUp() {
        this.project = mock(AbstractProject.class);
        this.build = mock(AbstractBuild.class);

        this.buildProvider = new BuildNumberExternalBuildProvider();
    }

    @Test
    public void testProvideBuild() throws Exception {
        when(this.project.getBuild(same(this.buildId))).thenReturn(this.build);
        
        assertSame(this.build, this.buildProvider.provideBuild(this.project, buildId));
        
        verify(this.project, times(1)).getBuild(same(buildId));
    }

    @Test
    public void testProvideBuildThrowsException() throws Exception {
        final String projectName = "PROJECT NAME";
        when(this.project.getName()).thenReturn(projectName);
        when(this.project.getBuild(same(this.buildId))).thenReturn(null);
        
        try {
            this.buildProvider.provideBuild(this.project, this.buildId);
            
            fail("Exception was not thrown where one was expected");
        } catch (BuildNotFoundException ex) {
            assertEquals(ex.getProject(), projectName);
            assertEquals(ex.getId(), this.buildId);
                    
            verify(this.project, times(1)).getName();
            
        } catch (AssertionError er) {
            throw er;
        } catch (Throwable t) {
            fail("Unexpected other exception was thrown: " + t.getMessage());
        }
        
        verify(this.project, times(1)).getBuild(same(buildId));
    }
    
}
