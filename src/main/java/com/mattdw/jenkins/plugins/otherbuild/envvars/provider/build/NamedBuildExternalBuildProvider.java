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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.build;

import com.google.common.base.Predicate;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import java.util.List;

/**
 * NamedBuildExternalBuildProvider - Provides builds identified primarily by
 * display name, falling back to the build number upon failure
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class NamedBuildExternalBuildProvider extends BuildNumberExternalBuildProvider {

    /**
     * Provides a target build by build name/number and project
     * 
     * @param project
     *      Project from which to provide the target build
     * @param id
     *      Display name or build number of the target build
     * @return
     *      Target build (if it can be found)
     * @throws BuildNotFoundException 
     *      If the build could not be found with the given build display name
     *      or number for the given project
     */
    @Override
    public AbstractBuild provideBuild(
        final AbstractProject project,
        final String id
    ) throws BuildNotFoundException {
        try {
            return this.findBuildByName(project, id);
        } catch (BuildNotFoundException ex) {
            return super.provideBuild(project, id);
        }
    }

    /**
     * Finds a build by display name and project
     * 
     * @param project
     *      Project from which to provide the target build
     * @param name
     *      Display name of the target build
     * @return
     *      Target build (if it can be found)
     * @throws BuildNotFoundException 
     *      If the build could not be found with the given build display name
     *      for the given project
     */
    protected AbstractBuild findBuildByName(
        final AbstractProject project,
        final String name
    ) throws BuildNotFoundException {
        
        // Filter builds by a name matching predicate (rather than 
        // inefficiently iterating through)
        List<AbstractBuild> matchingBuilds = project.getBuilds().filter(
            new Predicate<AbstractBuild>() {
                @Override
                public boolean apply(AbstractBuild t) {
                    String displayName;
                    
                    return (
                        t != null
                        && (displayName = t.getDisplayName()) != null
                        && t.getDisplayName().equals(name)
                    );
                }
            }
        );

        // Return the first build in the filtered list if at least one was found
        if (matchingBuilds.size() > 0) {
            return matchingBuilds.get(0);
        }
        
        // ...and throw an exception if none were found
        throw new BuildNotFoundException(project.getName(), name);
    }
    
}
