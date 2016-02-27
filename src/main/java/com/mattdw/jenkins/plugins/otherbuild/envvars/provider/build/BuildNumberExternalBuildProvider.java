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

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;



/**
 * BuildNumberBuildProvider - Provides builds identified solely by their build
 * number
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class BuildNumberExternalBuildProvider implements ExternalBuildProvider<AbstractProject, AbstractBuild> {

    /**
     * Provides a target build by build number and project
     * 
     * @param project
     *      Project from which to provide the target build
     * @param id
     *      Build number of the target build
     * @return
     *      Target build (if it can be found)
     * @throws BuildNotFoundException 
     *      If the build could not be found with the given build number (id)
     *      for the given project
     */
    @Override
    public AbstractBuild provideBuild(
        AbstractProject project,
        String id
    ) throws BuildNotFoundException {
        AbstractBuild build = project.getBuild(id);

        if (build != null) {
            return build;
        }
        
        throw new BuildNotFoundException(project.getName(), id);
    }

}
