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
package com.mattdw.jenkins.plugins.otherbuild.envvars.execution.factory;

import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsExecutor;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.ExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.NamedBuildExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.SingletonCallExternalProjectProvider;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
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
public class ImportVarsExecutorFactoryTest {
    
    private ExternalProjectProvider<AbstractProject> projectProvider;
    private ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider;
    private ImportVarsExecutorFactory.CopierImpl copierFactory;
    private ImportVarsExecutorFactory.ImporterImpl importerFactory;
    
    @Before
    public void setUp() {
        this.projectProvider = (ExternalProjectProvider<AbstractProject>) mock(ExternalProjectProvider.class);
        this.buildProvider = (ExternalBuildProvider<AbstractProject, AbstractBuild>) mock(ExternalBuildProvider.class);

        this.copierFactory = new ImportVarsExecutorFactory.CopierImpl(this.projectProvider, this.buildProvider);
        this.importerFactory = new ImportVarsExecutorFactory.ImporterImpl(this.projectProvider, this.buildProvider);
    }

    @Test
    public void testDefaultFactoryConstructors() throws Exception {
        
        AbstractImpl[] factories = new AbstractImpl[]{
            new ImportVarsExecutorFactory.CopierImpl(),
            new ImportVarsExecutorFactory.ImporterImpl()
        };
        
        for (AbstractImpl factory : factories) {
            Class<?> copierFactoryClass = factory.getClass().getSuperclass();

            java.lang.reflect.Field projectProviderField = copierFactoryClass.getDeclaredField("projectProvider");
            projectProviderField.setAccessible(true);

            java.lang.reflect.Field buildProviderField = copierFactoryClass.getDeclaredField("buildProvider");
            buildProviderField.setAccessible(true);

            assertNotSame(projectProviderField.get(factory), this.projectProvider);
            assertNotSame(buildProviderField.get(factory), this.buildProvider);
            
            assertTrue(projectProviderField.get(factory) instanceof SingletonCallExternalProjectProvider);
            assertTrue(buildProviderField.get(factory) instanceof NamedBuildExternalBuildProvider);
        }
    }
    
    @Test
    public void testCopierCreateBuilder() throws Exception {

        ImportVarsExecutor[] executors = new ImportVarsExecutor[]{
            this.copierFactory.createExecutor(),
            this.importerFactory.createExecutor()
        };
        
        for (ImportVarsExecutor executor : executors) {
            Class<?> copierFactoryClass = executor.getClass().getSuperclass();

            java.lang.reflect.Field projectProviderField = copierFactoryClass.getDeclaredField("projectProvider");
            projectProviderField.setAccessible(true);

            java.lang.reflect.Field buildProviderField = copierFactoryClass.getDeclaredField("buildProvider");
            buildProviderField.setAccessible(true);

            assertSame(projectProviderField.get(executor), this.projectProvider);
            assertSame(buildProviderField.get(executor), this.buildProvider);
        }
    }
    
}
