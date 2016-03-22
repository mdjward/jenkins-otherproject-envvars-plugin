/**
 * OtherBuildSelectorParameterValue.java
 * Created 28-Feb-2016 13:12:58
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

package com.mattdw.jenkins.plugins.otherbuild.envvars;

import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsConfiguration;
import com.mattdw.jenkins.plugins.otherbuild.envvars.execution.ImportVarsExecutor;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.OtherBuildVarImportException;
import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.model.TaskListener;
import java.io.IOException;



/**
 * OtherBuildSelectorParameterValue
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class OtherBuildSelectorParameterValue extends StringParameterValue {

    private final ImportVarsConfiguration<TemplatingEnvVarsCopier<EnvVars>> configuration;
    private final ImportVarsExecutor executor;
    private final TaskListener listener;

    public OtherBuildSelectorParameterValue(
        final String parameterName,
        final ImportVarsConfiguration configuration,
        final ImportVarsExecutor executor,
        final TaskListener listener
    ) {
        super(parameterName, configuration.getBuildId());

        this.configuration = configuration;
        this.executor = executor;
        this.listener = listener;
    }
    
    public OtherBuildSelectorParameterValue(
        final String parameterName,
        final ImportVarsConfiguration configuration,
        final ImportVarsExecutor executor
    ) {
        this(parameterName, configuration, executor, TaskListener.NULL);
    }

    public ImportVarsConfiguration<TemplatingEnvVarsCopier<EnvVars>> getConfiguration() {
        return configuration;
    }

    @Override
    public void buildEnvironment(Run<?, ?> build, EnvVars env) {
        super.buildEnvironment(build, env);

        TemplatingEnvVarsCopier<EnvVars> varCopier = this.configuration.getVarTemplater();
        
        if (varCopier != null) {
            this.doEnvImport(varCopier, env);
        }
    }
    
    protected void doEnvImport(
        TemplatingEnvVarsCopier<EnvVars> varCopier,
        EnvVars env
    ) throws RuntimeException {
        try {
            this.executor.perform(
                this.configuration,
                varCopier,
                env,
                this.listener,
                null
            );
        } catch (InterruptedException | IOException | OtherBuildVarImportException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
