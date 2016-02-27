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

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvContributingVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingOtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.ExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.NamedBuildExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ProjectNotFoundException;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.SingletonCallExternalProjectProvider;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import jenkins.model.Jenkins.JenkinsHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportOtherBuildEnvVarsBuilderTest {
    
    private String projectName = "PROJECT ID";
    private String buildId = "BUILD ID";
    private TemplatingOtherBuildEnvVarsImporter varImporter;
    private ExternalProjectProvider<AbstractProject> projectProvider;
    private ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider;
    private ImportOtherBuildEnvVarsBuilder builder;

    private AbstractBuild build;
    private TaskListener taskListener;
    private FilePath workspace = null;
    private Launcher launcher = null;
    private java.io.PrintStream logger;
        
    @Before
    public void setUp() {
        this.varImporter = mock(TemplatingOtherBuildEnvVarsImporter.class);
        this.projectProvider = (ExternalProjectProvider<AbstractProject>) mock(ExternalProjectProvider.class);
        this.buildProvider = (ExternalBuildProvider<AbstractProject, AbstractBuild>) mock(ExternalBuildProvider.class);
        
        this.builder = new ImportOtherBuildEnvVarsBuilder(
            this.projectName,
            this.buildId,
            this.varImporter
        );
        
        this.builder.setProjectProvider(this.projectProvider);
        this.builder.setBuildProvider(this.buildProvider);
        
        this.build = mock(AbstractBuild.class);
        this.taskListener = mock(TaskListener.class);
        this.logger = mock(java.io.PrintStream.class);
    }
    
    @Test
    public void testDataBoundConstructor() {
        try {
            String newProjectName = "NEW PROJECT NAME";
            String newBuildId = "NEW BUILD ID";
            String varTemplate = "VARIABLE TEMPLATE %s";

            java.lang.reflect.Field varImporterField = this.builder.getClass().getDeclaredField("varImporter");
            varImporterField.setAccessible(true);

            ImportOtherBuildEnvVarsBuilder newBuilder = new ImportOtherBuildEnvVarsBuilder(
                newProjectName,
                newBuildId,
                varTemplate
            );
            
            assertEquals(newProjectName, newBuilder.getProjectName());
            assertEquals(newBuildId, newBuilder.getBuildId());
            
            Object varImporterFieldValue = varImporterField.get(newBuilder);
            
            assertTrue(varImporterFieldValue instanceof EnvContributingVarsImporter);
            assertEquals(
                varTemplate,
                ((EnvContributingVarsImporter) varImporterFieldValue).getVarNameTemplate()
            );

        } catch (ReflectiveOperationException ex) {
            fail("Reflection exception occurred: " + ex.getMessage());
        }
    }
    
    @Test
    public void testGetProjectName() {
        assertEquals(this.projectName, this.builder.getProjectName());
    }

    @Test
    public void testGetBuildId() {
        assertEquals(this.buildId, this.builder.getBuildId());
    }

    @Test
    public void testGetVarNameTemplate() {
        String varNameTemplate = "TEMPLATE";
        
        when(this.varImporter.getVarNameTemplate()).thenReturn(varNameTemplate);
        
        assertEquals(varNameTemplate, this.builder.getVarNameTemplate());
        
        verify(this.varImporter, times(1)).getVarNameTemplate();
    }

    @Test
    public void testPrePerform() {
        try {
            Class<?> builderClass = this.builder.getClass();
            
            java.lang.reflect.Field projectProviderField = builderClass.getDeclaredField("projectProvider");
            java.lang.reflect.Field buildProviderField = builderClass.getDeclaredField("buildProvider");
            
            projectProviderField.setAccessible(true);
            buildProviderField.setAccessible(true);
            
            this.builder.setProjectProvider(null);
            this.builder.setBuildProvider(null);
            
            java.lang.reflect.Method prePreformMethod = builderClass.getDeclaredMethod(
                "prePerform",
                new Class<?>[0]
            );
            
            prePreformMethod.invoke(this.builder);
            
            assertTrue(projectProviderField.get(this.builder) instanceof SingletonCallExternalProjectProvider);
            assertTrue(buildProviderField.get(this.builder) instanceof NamedBuildExternalBuildProvider);

        } catch (ReflectiveOperationException ex) {
            fail("Reflection exception occurred: " + ex.getMessage());
        }
    }

    @Test
    public void testPerform() {
        String expandedBuildId = "EXPANDED BUILD ID";
        
        EnvVars buildEnvironment = mock(EnvVars.class);
        EnvVars otherBuildEnvironment = mock(EnvVars.class);
        
        AbstractProject otherProject = mock(AbstractProject.class);
        AbstractBuild otherBuild = mock(AbstractBuild.class);

        

        try {
            when(this.taskListener.getLogger()).thenReturn(this.logger);

            when(this.build.getEnvironment(same(this.taskListener))).thenReturn(buildEnvironment);
            when(buildEnvironment.expand(eq(this.buildId))).thenReturn(expandedBuildId);
            
            when(this.projectProvider.provideProject(eq(this.projectName))).thenReturn(otherProject);
            when(this.buildProvider.provideBuild(same(otherProject), eq(expandedBuildId))).thenReturn(otherBuild);

            when(otherBuild.getEnvironment(same(this.taskListener))).thenReturn(otherBuildEnvironment);
            
            doNothing().when(this.varImporter).importVars(same(this.build), same(otherBuildEnvironment));
            
            int totalOtherBuildEnvVars = 5;
            String otherBuildDisplayName = "OTHER BUILD";
            String otherProjectName = "OTHER PROJECT";
            
            when(otherBuildEnvironment.size()).thenReturn(5);
            when(otherBuild.getDisplayName()).thenReturn(otherBuildDisplayName);
            when(otherProject.getName()).thenReturn(otherProjectName);
            
            doNothing().
                when(this.logger)
                .println(
                    eq(
                        Messages.ImportOtherBuildEnvVarsBuilder_Imported(
                            totalOtherBuildEnvVars,
                            otherBuildDisplayName,
                            otherProjectName
                        )
                    )
                )
            ;

            this.builder.perform(
                this.build,
                this.workspace,
                this.launcher,
                this.taskListener
            );
            
            verify(this.taskListener, times(1)).getLogger();
            
            verify(this.build, times(1)).getEnvironment(same(this.taskListener));
            verify(buildEnvironment, times(1)).expand(eq(this.buildId));
            
            verify(this.projectProvider, times(1)).provideProject(eq(this.projectName));
            verify(this.buildProvider, times(1)).provideBuild(same(otherProject), eq(expandedBuildId));
            
            verify(otherBuild, times(1)).getEnvironment(same(this.taskListener));
            
            verify(this.varImporter, times(1)).importVars(same(this.build), same(otherBuildEnvironment));
            
            verify(otherBuildEnvironment, times(1)).size();
            verify(otherBuild, times(1)).getDisplayName();
            verify(otherProject, times(1)).getName();
            
        } catch (Throwable t) {
            fail("Exception was thrown: " + t.getMessage());
        }
    }
    
    @Test
    public void testPerformHandlesImportExceptions() {
        
        when(this.taskListener.getLogger()).thenReturn(this.logger);
        
        try {
            ProjectNotFoundException ex = new ProjectNotFoundException(this.projectName);
            
            when(this.projectProvider.provideProject(eq(this.projectName))).thenThrow(ex);
            
            doNothing().when(this.build).setResult(same(Result.FAILURE));
            doNothing()
                .when(this.logger)
                .println(
                    eq(Messages.ImportOtherBuildEnvVarsBuilder_ImportError(ex.getMessage()))
                )
            ;
            
            this.builder.perform(
                this.build,
                this.workspace,
                this.launcher,
                this.taskListener
            );
            
            verify(this.projectProvider, times(1)).provideProject(eq(this.projectName));
            verify(this.build, times(1)).setResult(same(Result.FAILURE));
            verify(this.logger, times(1))
                .println(
                    eq(Messages.ImportOtherBuildEnvVarsBuilder_ImportError(ex.getMessage()))
                )
            ;
            
        } catch (AssertionError er) {
            throw er;
        } catch (Throwable t) {
            fail("Unexpected exception " + t.getClass().getSimpleName() + "; " + t.getMessage());
        }
    }
    
    @Test
    public void testPerformHandlesOtherExceptions() {
        
        when(this.taskListener.getLogger()).thenReturn(this.logger);
        
        try {
            when(this.projectProvider.provideProject(eq(this.projectName)))
                .thenReturn(null)
            ;

            when(this.build.getEnvironment(same(this.taskListener)))
                .thenThrow(new java.io.IOException("test exception"))
            ;
    
            doNothing().when(this.build).setResult(same(Result.FAILURE));
            doNothing()
                .when(this.logger)
                .println(
                    eq(Messages.ImportOtherBuildEnvVarsBuilder_FailedToObtainEnvironment())
                )
            ;

            doNothing()
                .when(this.logger)
                .println(any(String.class));
            ;

            this.builder.perform(
                this.build,
                this.workspace,
                this.launcher,
                this.taskListener
            );
            
            verify(this.projectProvider, times(1)).provideProject(eq(this.projectName));
            verify(this.build, times(1)).setResult(same(Result.FAILURE));
            verify(this.logger, times(1))
                .println(eq(Messages.ImportOtherBuildEnvVarsBuilder_FailedToObtainEnvironment()))
            ;
            
        } catch (AssertionError er) {
            throw er;
        } catch (Throwable t) {
            fail("Unexpected exception " + t.getClass().getSimpleName() + "; " + t.getMessage());
        }
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
        
        ImportOtherBuildEnvVarsBuilder.DescriptorImpl descriptor = mock(ImportOtherBuildEnvVarsBuilder.DescriptorImpl.class);
        when(jenkins.getDescriptorOrDie(eq(ImportOtherBuildEnvVarsBuilder.class))).thenReturn(descriptor);
        
        assertSame(
            descriptor,
            this.builder.getDescriptor()
        );
        
        verify(jenkins, times(1)).getDescriptorOrDie(eq(ImportOtherBuildEnvVarsBuilder.class));
    }

}
