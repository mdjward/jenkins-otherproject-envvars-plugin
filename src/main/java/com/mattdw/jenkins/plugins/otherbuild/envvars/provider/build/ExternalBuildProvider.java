/*
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
 * OUT OF OR IN CONNECTION WITH THE SOWFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;



/**
 * OtherBuildProvider - interface definition for providing an external build
 * based on a project and some identifier
 * 
 * @param <P> Type of project (must extend {@link AbstractProject}
 * @param <B> Type of build object to return (must extend {@link AbstractBuild}
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public interface ExternalBuildProvider <P extends AbstractProject, B extends AbstractBuild> {

    /**
     * Provides a target build by a string identifier
     * 
     * @param project
     *      Project from which to provide the target build
     * @param id
     *      Identifier of the target build
     * @return
     *      Target build (if it can be found)
     * @throws BuildNotFoundException 
     *      If the build could not be found with the given identifier for the
     *      given project
     */
    public B provideBuild(P project, String id) throws BuildNotFoundException;
    
}
