/**
 * FilteringOtherProjectBuildOptionsProvider.java
 * Created 28-Feb-2016 17:07:39
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

import com.google.common.base.Predicate;
import java.util.List;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import hudson.util.RunList;



/**
 * Implementation of {@link OtherProjectBuildOptionsProvider} which filters
 * based on a single result (if one is specified otherwise presents all past
 * builds) in a given project
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class ResultFilteringOtherProjectBuildOptionsProvider implements OtherProjectBuildOptionsProvider {

    /**
     * Build result to filter by; if NULL, then no filtering is applied
     */
    protected final Result result;
    
    /**
     * Formatter to build an appropriately formatted string representation
     * of a given build
     */
    protected final BuildOptionFormatter formatter;



    /**
     * Constructor - creates a new instance of ResultFilteringOtherProjectBuildOptionsProvider
     * 
     * @param result
     *      Build result to filter by; if NULL, then no filtering is applied
     * @param formatter 
     *      Formatter to build an appropriately formatted string representation
     *      of a given build
     */
    public ResultFilteringOtherProjectBuildOptionsProvider(
        final Result result,
        final BuildOptionFormatter formatter
    ) {
        this.result = result;
        this.formatter = formatter;
    }

    /**
     * Constructor - creates a new instance of ResultFilteringOtherProjectBuildOptionsProvider
     * which assumes a default formatter implementation
     */
    public ResultFilteringOtherProjectBuildOptionsProvider(final Result result) {
        this(result, new BuildOptionFormatter.DefaultImpl());
    }

    /**
     * Produce a {@link ListBoxModel} of options, each of which represents a past
     * build, optionally from another project
     * 
     * @param project
     *      Project from which to load the builds
     * @return 
     *      {@link ListBoxModel} of selectable options, each representing a build
     */
    @Override
    public ListBoxModel getOptionsForProject(AbstractProject project) {
        ListBoxModel model = new ListBoxModel();

        List<AbstractBuild> builds = this.filterBuilds(project.getBuilds());
        
        if (builds.size() > 0) {
            for (AbstractBuild b : builds) {
                model.add(
                    this.formatter.formatBuild(b),
                    String.valueOf(b.getNumber())
                );
            }
        }
        
        return model;
    }

    /**
     * Filters a given list of builds on a pre-defined {@link Predicate} closure
     * 
     * @param runs
     *      List of runs (past builds) to filter
     * @return 
     *      Filtered list of past builds if filtering is applied; otherwise
     *      the original list of runs as passed into this method
     */
    protected List<AbstractBuild> filterBuilds(RunList runs) {
        
        // If no result filter is set, then do not filter
        if (this.result == null) {
            return runs;
        }

        return runs.filter(new Predicate<AbstractBuild>() {
            @Override
            public boolean apply(AbstractBuild b) {
                return (b.getResult() == result);
            }
        });
    }



    /**
     * Factory counterpart to generate {@link ResultFilteringOtherProjectBuildOptionsProvider}
     * instances
     */
    public static class Factory implements OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result> {

        /**
         * Produces a {@ResultFilteringOtherProjectBuildOptionsProvider} based
         * on a single given, optionally NULL 
         * 
         * @param result
         *      Result on which to filter; NULL indicates that no filtering
         *      is to take place
         * @return 
         *      {@ResultFilteringOtherProjectBuildOptionsProvider} instance
         */
        @Override
        public ResultFilteringOtherProjectBuildOptionsProvider buildProvider(Result result) {
            return new ResultFilteringOtherProjectBuildOptionsProvider(result);
        }

    }

}
