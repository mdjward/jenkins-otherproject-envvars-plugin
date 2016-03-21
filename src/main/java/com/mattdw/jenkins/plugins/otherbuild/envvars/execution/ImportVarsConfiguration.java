/**
 * ImportVarsConfiguration.java
 * Created 21-Mar-2016 12:17:40
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

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarImporterOrCopier;
import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarTemplateNameAware;



/**
 * ImportVarsConfiguration
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 * 
 * @param <V>
 */
public class ImportVarsConfiguration <V extends VarTemplateNameAware & VarImporterOrCopier> {

    protected String projectName;
    protected String buildId;
    protected V varTemplater;

    public ImportVarsConfiguration(String projectName, String buildId, V varTemplater) {
        this.projectName = projectName;
        this.buildId = buildId;
        this.varTemplater = varTemplater;
    }
    
    public ImportVarsConfiguration() {
        this(null, null, null);
    }

    public String getProjectName() {
        return projectName;
    }

    public String getBuildId() {
        return buildId;
    }

    public V getVarTemplater() {
        return varTemplater;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public void setVarTemplater(V varTemplater) {
        this.varTemplater = varTemplater;
    }
    
}
