/**
 * ImportOtherBuildEnvVarsResult.java
 * Created 21-Mar-2016 10:25:39
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



/**
 * Compound, immutable object designed to store the result of a variable
 * import operation from another build
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class ImportVarsResult {
    
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
     * Total number of environment variables imported (not including overwrites)
     */
    private final int totalVarsImported;



    /**
     * Constructor - creates a new instance of ImportVarsResult
     * 
     * @param projectName
     *      Project name of the project to which the target build belongs
     * @param buildId
     *      Identifier of the target build from which environment variables
     *      are imported
     * @param totalVarsImported 
     *      Total number of environment variables imported (not including
     *      overwrites)
     */
    public ImportVarsResult(String projectName, String buildId, int totalVarsImported) {
        this.projectName = projectName;
        this.buildId = buildId;
        this.totalVarsImported = totalVarsImported;
    }

    /**
     * Getter for projectName
     * 
     * @return 
     *      Project name of the project to which the target build belongs
     */
    public String getProjectName() {
        return this.projectName;
    }

    /**
     * Getter for buildId
     * 
     * @return 
     *      Identifier of the target build from which environment variables
     *      are imported
     */
    public String getBuildId() {
        return this.buildId;
    }

    /**
     * Getter for totalVarsImported
     * 
     * @return 
     *      Total number of environment variables imported (not including
     *      overwrites)
     */
    public int getTotalVarsImported() {
        return this.totalVarsImported;
    }

}
