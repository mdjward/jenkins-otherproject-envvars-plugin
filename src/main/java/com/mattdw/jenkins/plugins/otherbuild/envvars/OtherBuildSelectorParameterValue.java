/**
 * OtherBuildSelectorParameterValue.java
 * Created 28-Feb-2016 13:12:58
 *
 * @author M.D.Ward <dev@mattdw.co.uk>
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
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.TemplatingEnvVarsCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.OtherBuildVarImportException;
import hudson.EnvVars;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.model.TaskListener;
import java.io.IOException;



/**
 * Parameter value type built by {@link OtherBuildSelectorParameterDefinition}
 * which stores a past build number from another project, and may also hydrate
 * given (notionally current) build with environment variables from the
 * past build corresponding to this past build number
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class OtherBuildSelectorParameterValue extends StringParameterValue {

    /**
     * Configuration for this parameter value and the optional variable import,
     * which provides the originating project's name, build number and variable
     * importer 
     */
    private final ImportVarsConfiguration<TemplatingEnvVarsCopier<EnvVars>> configuration;

    /**
     * Variable importer mechanism; a NULL value indicates that variable import
     * will not happen as part of environment variable contribution
     */
    private final transient ImportVarsExecutor executor;

    /**
     * Build listener - necessary for the executor to derive an environment
     * from a past build
     */
    private final transient TaskListener listener;

    
    
    /**
     * Constructor - creates a new instance of OtherBuildSelectorParameterValue
     * 
     * @param parameterName
     *      Name of the parameter to which the given build number will be assigned
     * @param configuration
     *      Configuration for this parameter value and the optional variable
     *      import, which provides the originating project's name, build number
     *      and variable importer 
     * @param executor
     *      Variable importer mechanism; a NULL value indicates that variable
     *      import will not happen as part of environment variable contribution
     * @param listener 
     *      Build listener - necessary for the executor to derive an environment
     *      from a past build
     */
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
    
    /**
     * Constructor - creates a new instance of OtherBuildSelectorParameterValue
     * with a default, dummy implementation of TaskListener
     * 
     * @param parameterName
     *      Name of the parameter to which the given build number will be assigned
     * @param configuration
     *      Configuration for this parameter value and the optional variable
     *      import, which provides the originating project's name, build number
     *      and variable importer 
     * @param executor
     *      Variable importer mechanism; a NULL value indicates that variable
     *      import will not happen as part of environment variable contribution
     */
    public OtherBuildSelectorParameterValue(
        final String parameterName,
        final ImportVarsConfiguration configuration,
        final ImportVarsExecutor executor
    ) {
        this(parameterName, configuration, executor, TaskListener.NULL);
    }

    /**
     * Getter for configuration
     * 
     * @return 
     *      Configuration for this parameter value and the optional variable
     *      import, which provides the originating project's name, build number
     *      and variable importer 
     */
    public ImportVarsConfiguration<TemplatingEnvVarsCopier<EnvVars>> getConfiguration() {
        return configuration;
    }

    /**
     * Builds the environment by contributing to the current build
     * 
     * @param build
     *      Current build for which to contribute environment variables
     * @param env 
     *      Environment variable map thus far
     */
    @Override
    public void buildEnvironment(Run<?, ?> build, EnvVars env) {

        /*
         * We still want to provide the build number as per the superclass
         * {@link StringParameterValue} implementation, so invoke the corresponding
         * superclass method
         */
        super.buildEnvironment(build, env);
        
        // Retrieve the templater for a null check
        TemplatingEnvVarsCopier<EnvVars> varCopier = this.configuration.getVarTemplater();

        /*
         * If null, then we assume the condition that variable import is not 
         * configured for this parameter instance, and we therefore do not import
         */
        if (varCopier != null) {
            this.doEnvImport(varCopier, env);
        }
    }

    /**
     * Performs the variable import
     * 
     * @param varCopier
     *      Variable copier implementation
     * @param env
     *      Environment variable map thus far
     * @throws RuntimeException 
     *      Runtime wrapper for any exception that occurs during the scope
     *      of the variable import operation
     */
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
