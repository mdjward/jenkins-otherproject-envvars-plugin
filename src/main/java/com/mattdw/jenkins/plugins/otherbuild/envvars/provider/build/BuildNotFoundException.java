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

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build;

import com.mattdw.jenkins.plugins.otherbuild.envvars.provider.OtherBuildVarImportException;



/**
 * BuildNotFoundException - describes a failure to locate an identified build
 * for an identified project
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class BuildNotFoundException extends OtherBuildVarImportException {
    
    /**
     * Identifier of the target project
     */
    private final String project;
    
    /**
     * Identifier of the target build
     */
    private final String id;
    
    
    
    /**
     * Constructor - creates a new instance of BuildNotFoundException
     * 
     * @param project
     *      Identifier of the target project
     * @param id
     *      Identifier of the target build
     */
    public BuildNotFoundException(String project, String id) {
        super("Could not find a build with id " + id + " in project " + project);
        
        this.project = project;
        this.id = id;
    }

    /**
     * Getter for project
     * 
     * @return Identifier of the target project
     */
    public String getProject() {
        return project;
    }

    /**
     * Getter for id
     * 
     * @return Identifier of the target build
     */
    public String getId() {
        return id;
    }

}
