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

import com.google.common.base.Predicate;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import java.util.List;

/**
 * NamedBuildExternalBuildProvider
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 * @copyright (c) 2016, Byng Services Ltd
 */
public class NamedBuildExternalBuildProvider extends BuildNumberExternalBuildProvider {

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

    protected AbstractBuild findBuildByName(
        final AbstractProject project,
        final String name
    ) throws BuildNotFoundException {
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

        if (matchingBuilds.size() > 0) {
            return matchingBuilds.get(0);
        }
        
        throw new BuildNotFoundException(project.getName(), name);
    }
    
}
