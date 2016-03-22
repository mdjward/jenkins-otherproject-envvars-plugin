/**
 * OtherProjectBuildOptionsProvider.java
 * Created 28-Feb-2016 16:46:22
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

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options;

import hudson.model.AbstractProject;
import hudson.util.ListBoxModel;



/**
 * Defines a producer of presentable list options for past builds [from another
 * project]
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public interface OtherProjectBuildOptionsProvider {
    
    /**
     * Produce a {@link ListBoxModel} of options, each of which represents a past
     * build, optionally from another project
     * 
     * @param project
     *      Project from which to load the builds
     * @return 
     *      {@link ListBoxModel} of selectable options, each representing a build
     */
    public ListBoxModel getOptionsForProject(AbstractProject project);
    
    
    
    /**
     * Factory for producing {@link OtherProjectBuildOptionsProvider} of a
     * given supertype
     * 
     * @param <P>
     *      [Super]type of {@link OtherProjectBuildOptionsProvider} 
     * @param <A> 
     *      Type of argument to pass to <pre>buildProvider</pre>
     */
    public static interface Factory <P extends OtherProjectBuildOptionsProvider, A> {
        
        /**
         * Produces a {@OtherProjectBuildOptionsProvider}
         * 
         * @param arguments
         *      Arguments to be used in constructing the provider
         * @return 
         *      {@OtherProjectBuildOptionsProvider} instance based on given
         *      arguments and specific implementation
         */
        public P buildProvider(A arguments);
        
    }
    
}
