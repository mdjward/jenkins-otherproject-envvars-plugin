/**
 * ImportOtherBuildEnvVarsBaseBuilderFactory.java
 * Created 21-Mar-2016 11:07:47
 *
 * @author M.D.Ward <matthew.ward@byng.co>
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
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.EnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.OtherBuildEnvVarsImporter;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarImporterOrCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.ExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build.NamedBuildExternalBuildProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.ExternalProjectProvider;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project.SingletonCallExternalProjectProvider;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TaskListener;



/**
 * ImportVarsExecutorFactory 
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public interface ImportVarsExecutorFactory <V extends VarImporterOrCopier, E extends ImportVarsExecutor<EnvVars, V, TaskListener>> {

    public E createBuilder();



    public static class CopierImpl extends AbstractImpl<EnvVarsCopier, ImportVarsExecutor.CopierImpl> {

        public CopierImpl(
            ExternalProjectProvider<AbstractProject> projectProvider,
            ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
        ) {
            super(projectProvider, buildProvider);
        }

        public CopierImpl() {
            super();
        }
        
        @Override
        public ImportVarsExecutor.CopierImpl createBuilder() {
            return new ImportVarsExecutor.CopierImpl(
                this.projectProvider,
                this.buildProvider
            );
        }

    }
    
    public static class ImporterImpl extends AbstractImpl<OtherBuildEnvVarsImporter, ImportVarsExecutor.ImporterImpl> {

        public ImporterImpl(
            ExternalProjectProvider<AbstractProject> projectProvider,
            ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
        ) {
            super(projectProvider, buildProvider);
        }

        public ImporterImpl() {
            super();
        }
        
        @Override
        public ImportVarsExecutor.ImporterImpl createBuilder() {
            return new ImportVarsExecutor.ImporterImpl(
                this.projectProvider,
                this.buildProvider
            );
        }

    }

}



abstract class AbstractImpl <V extends VarImporterOrCopier, E extends ImportVarsExecutor<EnvVars, V, TaskListener>> implements ImportVarsExecutorFactory<V, E> {

    /**
     * Project provider mechanism for the target build from which
     * variables are imported
     */
    protected final transient ExternalProjectProvider<AbstractProject> projectProvider;

    /**
     * Build provider mechanism for the target build from which
     * variables are imported
     */
    protected final transient ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider;



    /**
     * 
     * @param projectProvider
     * @param buildProvider 
     */
    public AbstractImpl(
        ExternalProjectProvider<AbstractProject> projectProvider,
        ExternalBuildProvider<AbstractProject, AbstractBuild> buildProvider
    ) {
        this.projectProvider = projectProvider;
        this.buildProvider = buildProvider;
    }

    /**
     * 
     */
    public AbstractImpl() {
        this(
            new SingletonCallExternalProjectProvider(),
            new NamedBuildExternalBuildProvider()
        );
    }

}
