/**
 * ImportOtherBuildEnvVarsResult.java
 * Created 21-Mar-2016 10:25:39
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

package com.mattdw.jenkins.plugins.otherbuild.envvars.execution;



/**
 * ImportVarsResult
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class ImportVarsResult {
    
    private final String projectName;
    private final String buildId;
    private final int totalVarsImported;

    public ImportVarsResult(String projectName, String buildId, int totalVarsImported) {
        this.projectName = projectName;
        this.buildId = buildId;
        this.totalVarsImported = totalVarsImported;
    }

    public String getProjectName() {
        return this.projectName;
    }

    public String getBuildId() {
        return this.buildId;
    }

    public int getTotalVarsImported() {
        return this.totalVarsImported;
    }

}
