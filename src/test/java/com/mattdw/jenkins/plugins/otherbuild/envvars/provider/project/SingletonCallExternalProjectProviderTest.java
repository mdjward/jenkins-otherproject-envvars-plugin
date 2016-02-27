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
package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project;

import hudson.model.AbstractProject;
import jenkins.model.Jenkins;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class SingletonCallExternalProjectProviderTest {

    private Jenkins.JenkinsHolder jenkinsHolder;
    private Jenkins jenkins;
    private SingletonCallExternalProjectProvider projectProvider;
    private AbstractProject project;
    private final String projectName = "PROJECT NAME";

    @Before
    public void setUp() throws Exception {
        this.jenkinsHolder = mock(Jenkins.JenkinsHolder.class);
        this.jenkins = mock(Jenkins.class);
        
        when(this.jenkinsHolder.getInstance()).thenReturn(this.jenkins);
        
        java.lang.reflect.Field jenkinsHolderField = Jenkins.class.getDeclaredField("HOLDER");
        jenkinsHolderField.setAccessible(true);
        
        jenkinsHolderField.set(null, this.jenkinsHolder);
        
        this.project = mock(AbstractProject.class);
        this.projectProvider = new SingletonCallExternalProjectProvider((Class<Jenkins>) this.jenkins.getClass());
    }
    
    @Test
    public void testConstructorFallsBackToJenkinsSingleton() throws Exception {
        this.projectProvider = new SingletonCallExternalProjectProvider();
        
        java.lang.reflect.Field jenkinsClassField = this.projectProvider.getClass().getDeclaredField("jenkinsClass");
        jenkinsClassField.setAccessible(true);
        
        assertSame(
            Jenkins.class,
            (Class<?>) (jenkinsClassField.get(this.projectProvider))
        );
    }
    
    @Test
    public void testProvideProject() throws Exception {
        when(this.jenkins.getItemByFullName(eq(this.projectName), same(AbstractProject.class))).thenReturn(this.project);
        
        assertSame(
            this.project,
            this.projectProvider.provideProject(this.projectName)
        );
        
        verify(this.jenkins, times(1)).getItemByFullName(eq(this.projectName), same(AbstractProject.class));
    }
    
    @Test
    public void testProvideProjectThrowsExceptionIfNullReturned() {
        when(this.jenkins.getItemByFullName(eq(this.projectName), same(AbstractProject.class))).thenReturn(null);

        try {
            this.projectProvider.provideProject(this.projectName);
            
            fail("Exception was expected to be thrown");
        } catch (ProjectNotFoundException ex) {
            assertEquals(this.projectName, ex.getProject());
        }

        verify(this.jenkins, times(1)).getItemByFullName(eq(this.projectName), same(AbstractProject.class));
    }
    
    @Test
    public void testProvideProjectThrowsExceptionOnReflectiveException() {
        Exception cause = new IllegalArgumentException();

        when(this.jenkins.getItemByFullName(eq(this.projectName), same(AbstractProject.class))).thenThrow(cause);

        try {
            this.projectProvider.provideProject(this.projectName);
            
            fail("Exception was expected to be thrown");
        } catch (ProjectNotFoundException ex) {
            assertEquals(this.projectName, ex.getProject());
            assertSame(cause, ex.getCause());
        }

        verify(this.jenkins, times(1)).getItemByFullName(eq(this.projectName), same(AbstractProject.class));
    }

}
