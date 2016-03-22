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

import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsConfiguration;
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsExecutor;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
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
public class OtherBuildSelectorParameterValueTest {
    
    private final String parameterName = "PARAMETER NAME";
    private ImportVarsConfiguration<TemplatingEnvVarsCopier<EnvVars>> configuration;
    private ImportVarsExecutor executor;
    private TaskListener listener;
    private OtherBuildSelectorParameterValue value;
    
    private Run<?, ?> build;
    private EnvVars vars;
    private TemplatingEnvVarsCopier<EnvVars> varCopier;

    @Before
    public void setUp() throws Exception {
        this.configuration = (ImportVarsConfiguration<TemplatingEnvVarsCopier<EnvVars>>) mock(ImportVarsConfiguration.class);
        this.executor = mock(ImportVarsExecutor.class);
        this.listener = mock(TaskListener.class);
        
        this.value = new OtherBuildSelectorParameterValue(
            parameterName,
            configuration,
            executor,
            listener
        );
        
        this.build = mock(Run.class);
        this.vars = mock(EnvVars.class);
        this.varCopier = (TemplatingEnvVarsCopier<EnvVars>) mock(TemplatingEnvVarsCopier.class);
        
        when(this.vars.put(any(String.class), any(String.class))).thenReturn(null);
    }
    
    @Test
    public void testOtherConstructorAssignsNullTaskListener() throws Exception {
        this.value = new OtherBuildSelectorParameterValue(
            parameterName,
            configuration,
            executor
        );
        
        java.lang.reflect.Field listenerField = OtherBuildSelectorParameterValue.class.getDeclaredField("listener");
        listenerField.setAccessible(true);
        
        Object currentListener = listenerField.get(this.value);
        
        assertNotSame(this.listener, currentListener);
        assertSame(TaskListener.NULL, currentListener);
    }

    @Test
    public void testGetConfiguration() {
        assertSame(this.configuration, this.value.getConfiguration());
    }

    @Test
    public void testBuildEnvironment() throws Exception {
        when(this.configuration.getVarTemplater()).thenReturn(this.varCopier);
        
        when(
            this.executor.perform(
                same(this.configuration),
                same(this.varCopier),
                same(this.vars),
                same(this.listener),
                isNull(AbstractBuild.class)
            )
        ).thenReturn(null);
        
        this.value.buildEnvironment(this.build, this.vars);
        
        verify(this.configuration, times(1)).getVarTemplater();
        
        verify(this.executor, times(1)).perform(
            same(this.configuration),
            same(this.varCopier),
            same(this.vars),
            same(this.listener),
            isNull(AbstractBuild.class)
        );
    }

    @Test
    public void testBuildEnvironmentRethrowsExceptions() throws Exception {
        when(this.configuration.getVarTemplater()).thenReturn(this.varCopier);
        
        Throwable underlyingException = new java.io.IOException("Exception message");
        
        when(
            this.executor.perform(
                same(this.configuration),
                same(this.varCopier),
                same(this.vars),
                same(this.listener),
                isNull(AbstractBuild.class)
            )
        ).thenThrow(underlyingException);
        
        try {
            this.value.buildEnvironment(this.build, this.vars);
        } catch (RuntimeException ex) {
            assertSame(underlyingException, ex.getCause());
        }
        
        verify(this.configuration, times(1)).getVarTemplater();
    }

    @Test
    public void testBuildEnvironmentDoesNotCopyVarsIfNoCopierGiven() throws Exception {
        when(this.configuration.getVarTemplater()).thenReturn(null);
        
        this.value.buildEnvironment(this.build, this.vars);
        
        verify(this.configuration, times(1)).getVarTemplater();
        
        verify(this.executor, times(0)).perform(
            same(this.configuration),
            same(this.varCopier),
            same(this.vars),
            same(this.listener),
            isNull(AbstractBuild.class)
        );
    }

}
