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
package com.mattdw.jenkins.plugins.otherbuild.envvars.execution;

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingOtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.ExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportVarsExecutorTest {
    
    private static final int CURRENT_BUILD_VARS_TOTAL = 5;
    private static final String PROJECT_NAME = "TARGET PROJECT NAME";
    private static final String BUILD_ID = "BUILD ID";

    private ExternalProjectProvider<AbstractProject> projectProvider;
    private ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider;
    private ImportVarsExecutor.CopierImpl copierImpl;
    private ImportVarsExecutor.ImporterImpl importerImpl;
    private TemplatingOtherBuildEnvVarsImporter varImporter;
    private TemplatingEnvVarsCopier varCopier;
    private ImportVarsConfiguration config;
    private EnvVars currentBuildVars;
    private EnvVars otherBuildVars;
    private TaskListener listener;
    private AbstractProject otherProject;
    private AbstractBuild currentBuild;
    private AbstractBuild otherBuild;



    @Before
    public void setUp() throws Exception {
        this.projectProvider = (ExternalProjectProvider<AbstractProject>) mock(ExternalProjectProvider.class);
        this.buildProvider = (ExternalBuildProvider<AbstractProject, AbstractBuild>) mock(ExternalBuildProvider.class);

        this.copierImpl = new ImportVarsExecutor.CopierImpl(this.projectProvider, this.buildProvider);
        this.importerImpl = new ImportVarsExecutor.ImporterImpl(this.projectProvider, this.buildProvider);

        this.varImporter = mock(TemplatingOtherBuildEnvVarsImporter.class);
        this.varCopier = mock(TemplatingEnvVarsCopier.class);
        this.currentBuildVars = mock(EnvVars.class);
        this.otherBuildVars = mock(EnvVars.class);
        this.listener = mock(TaskListener.class);
        this.otherProject = mock(AbstractProject.class);
        this.currentBuild = mock(AbstractBuild.class);
        this.otherBuild = mock(AbstractBuild.class);

        when(this.currentBuildVars.size()).thenReturn(CURRENT_BUILD_VARS_TOTAL);
        when(this.currentBuildVars.expand(same(BUILD_ID))).thenReturn(BUILD_ID);

        when(this.projectProvider.provideProject(same(PROJECT_NAME))).thenReturn(this.otherProject);
        when(this.buildProvider.provideBuild(same(this.otherProject), same(BUILD_ID))).thenReturn(this.otherBuild);
        when(this.otherBuild.getEnvironment(same(this.listener))).thenReturn(this.otherBuildVars);
    }

    @After
    public void tearDown() throws Exception {
        verify(this.currentBuildVars, atLeast(2)).size();
        verify(this.currentBuildVars, times(1)).expand(same(BUILD_ID));

        verify(this.projectProvider, times(1)).provideProject(same(PROJECT_NAME));
        verify(this.buildProvider, times(1)).provideBuild(same(this.otherProject), same(BUILD_ID));
        verify(this.otherBuild, times(1)).getEnvironment(same(this.listener));
    }

    @Test
    public void testCopierImplPerform() throws Exception {
        this.config = new ImportVarsConfiguration(
            PROJECT_NAME,
            BUILD_ID,
            this.varCopier
        );

        doNothing().when(this.varCopier).copyEnvVars(same(this.otherBuildVars), same(this.currentBuildVars));

        ImportVarsResult result = this.copierImpl.perform(
            this.config,
            this.varCopier,
            this.currentBuildVars,
            this.listener,
            this.currentBuild
        );

        assertSame(PROJECT_NAME, result.getProjectName());
        assertSame(BUILD_ID, result.getBuildId());
        assertEquals(0, result.getTotalVarsImported());

        verify(this.varCopier, times(1)).copyEnvVars(same(this.otherBuildVars), same(this.currentBuildVars));
    }

    @Test
    public void testImporterImplPerform() throws Exception {
        this.config = new ImportVarsConfiguration(
            PROJECT_NAME,
            BUILD_ID,
            this.varImporter
        );

        doNothing().when(this.varImporter).importVars(same(this.currentBuild), same(this.otherBuildVars));

        ImportVarsResult result = this.importerImpl.perform(
            this.config,
            this.varImporter,
            this.currentBuildVars,
            this.listener,
            this.currentBuild
        );

        assertSame(PROJECT_NAME, result.getProjectName());
        assertSame(BUILD_ID, result.getBuildId());
        assertEquals(0, result.getTotalVarsImported());

        verify(this.varImporter, times(1)).importVars(same(this.currentBuild), same(this.otherBuildVars));
    }

}
