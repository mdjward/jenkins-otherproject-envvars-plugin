/**
 * ImportVarsConfiguration.java
 * Created 21-Mar-2016 12:17:40
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

package com.mattdw.jenkins.plugins.otherbuild.envvars.execution;

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarNameTemplateAware;



/**
 * Compound configuration object for an environment variable import (on build,
 * or as part of parameter value generation and environment construction) to
 * store project name, build identifier and a variable template aware object
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 * 
 * @param <V>
 *      Supertype of the variable template aware object (which may also provide
 *      an implementation of the actual import); must ultimately 
 *      extend {@link VarNameTemplateAware}
 */
public class ImportVarsConfiguration <V extends VarNameTemplateAware> {

    /**
     * Project name of the project to which the target build belongs
     */
    protected final String projectName;
    
    /**
     * Identifier of the target build from which environment variables
     * are imported
     */
    protected final String buildId;
    
    /**
     * Variable template aware object
     */
    protected final V varTemplater;

    
    
    /**
     * Constructor - creates a new instance of ImportVarsConfiguration
     * 
     * @param projectName
     *      Project name of the project to which the target build belongs
     * @param buildId
     *      Identifier of the target build from which environment variables
     *      are imported
     * @param varTemplater 
     *      Variable template aware object
     */
    public ImportVarsConfiguration(String projectName, String buildId, V varTemplater) {
        this.projectName = projectName;
        this.buildId = buildId;
        this.varTemplater = varTemplater;
    }
    
   /**
    * Getter for projectName
    * 
    * @return 
     *      Project name of the project to which the target build belongs
    */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Getter for buildId
     * 
     * @return 
     *      Identifier of the target build from which environment variables
     *      are imported
     */
    public String getBuildId() {
        return buildId;
    }

    /**
     * Getter for varTemplater
     * 
     * @return 
     *      Variable template aware object
     */
    public V getVarTemplater() {
        return varTemplater;
    }
    
}
