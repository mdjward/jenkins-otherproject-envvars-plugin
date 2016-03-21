/*
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
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsExecutor;
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsResult;
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.factory.ImportVarsExecutorFactory;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvContributingVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingOtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ProjectNotFoundException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import jenkins.model.Jenkins.JenkinsHolder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 *
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
@RunWith(MockitoJUnitRunner.class)
public class ImportOtherBuildEnvVarsBuilderTest {
    
    private String projectName = "PROJECT ID";
    private String buildId = "BUILD ID";
    private TemplatingOtherBuildEnvVarsImporter varImporter;
    private ImportVarsExecutorFactory executorFactory;
    private ImportVarsExecutor executor;
    private ImportOtherBuildEnvVarsBuilder builder;

    private AbstractBuild build;
    private TaskListener listener;
    private FilePath workspace = null;
    private Launcher launcher = null;
    private java.io.PrintStream logger;
    private EnvVars buildVars;
    private ImportVarsResult result;
    
    
    @Before
    public void setUp() {
        this.varImporter = mock(TemplatingOtherBuildEnvVarsImporter.class);
        this.executorFactory = mock(ImportVarsExecutorFactory.class);
        this.executor = mock(ImportVarsExecutor.class);
        
        this.builder = new ImportOtherBuildEnvVarsBuilder(
            this.projectName,
            this.buildId,
            this.varImporter,
            this.executorFactory
        );
        
        this.build = mock(AbstractBuild.class);
        this.listener = mock(TaskListener.class);
        this.logger = mock(java.io.PrintStream.class);
        this.buildVars = mock(EnvVars.class);
        this.result = mock(ImportVarsResult.class);

        when(this.executorFactory.createBuilder()).thenReturn(this.executor);
    }
    
    @Test
    public void testDataBoundConstructor() throws Exception {
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
    public void testPrePerform() throws Exception {
        this.builder = new ImportOtherBuildEnvVarsBuilder(
            this.projectName,
            this.buildId,
            this.varImporter,
            null
        );

        Class<?> builderClass = this.builder.getClass();

        java.lang.reflect.Field executorFactoryField = builderClass.getDeclaredField("executorFactory");
        executorFactoryField.setAccessible(true);

        java.lang.reflect.Method prePreformMethod = builderClass.getDeclaredMethod(
            "prePerform",
            new Class<?>[0]
        );

        prePreformMethod.invoke(this.builder);

        assertTrue(executorFactoryField.get(this.builder) instanceof ImportVarsExecutorFactory.ImporterImpl);
    }

    @Test
    public void testPerform() throws Exception {
        final int totalVarsImported = 10;
        final Matcher<ImportVarsConfiguration> configMatcher = new BaseMatcher<ImportVarsConfiguration>() {

            @Override
            public boolean matches(Object item) {
                ImportVarsConfiguration config = (ImportVarsConfiguration) item;

                return (
                    config.getProjectName().equals(projectName)
                    && config.getBuildId().equals(buildId)
                    && config.getVarTemplater().equals(varImporter)
                );
            }

            @Override
            public void describeTo(Description description) {}
        };
        

        when(this.listener.getLogger()).thenReturn(this.logger);
        
        when(this.build.getEnvironment(same(this.listener))).thenReturn(this.buildVars);
        
        when(
            this
                .executor
                .perform(
                    argThat(configMatcher),
                    same(this.varImporter),
                    same(this.buildVars),
                    same(this.listener),
                    same(this.build)
                )
            )
            .thenReturn(this.result)
        ;
        
        when(this.result.getProjectName()).thenReturn(this.projectName);
        when(this.result.getBuildId()).thenReturn(this.buildId);
        when(this.result.getTotalVarsImported()).thenReturn(totalVarsImported);

        doNothing().
            when(this.logger)
            .println(
                eq(
                    Messages.ImportOtherBuildEnvVarsBuilder_Imported(
                        totalVarsImported,
                        this.buildId,
                        this.projectName
                    )
                )
            )
        ;

        this.builder.perform(
            this.build,
            this.workspace,
            this.launcher,
            this.listener
        );

        verify(this.listener, times(1)).getLogger();

        verify(this.build, times(1)).getEnvironment(same(this.listener));
        verify(this.executorFactory, times(1)).createBuilder();
        
        verify(this.executor, times(1))
            .perform(
                argThat(configMatcher),
                same(this.varImporter),
                same(this.buildVars),
                same(this.listener),
                same(this.build)
            )
        ;
        
        verify(this.result, times(1)).getProjectName();
        verify(this.result, times(1)).getBuildId();
        verify(this.result, times(1)).getTotalVarsImported();
    }
    
    @Test
    public void testPerformHandlesImportExceptions() throws Exception {
        
        when(this.listener.getLogger()).thenReturn(this.logger);
        
        ProjectNotFoundException ex = new ProjectNotFoundException(this.projectName);

        when(this.executor.perform(any(), any(), any(), any(), any())).thenThrow(ex);
        
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
            this.listener
        );

        verify(this.executor, times(1))
                .perform(any(), any(), any(), any(), any())
        ;
        
        verify(this.build, times(1)).setResult(same(Result.FAILURE));
        verify(this.logger, times(1))
            .println(
                eq(Messages.ImportOtherBuildEnvVarsBuilder_ImportError(ex.getMessage()))
            )
        ;
    }
    
    @Test
    public void testPerformHandlesOtherExceptions() {
        
        when(this.listener.getLogger()).thenReturn(this.logger);
        
        try {
            when(this.build.getEnvironment(same(this.listener)))
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
                this.listener
            );
            
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
