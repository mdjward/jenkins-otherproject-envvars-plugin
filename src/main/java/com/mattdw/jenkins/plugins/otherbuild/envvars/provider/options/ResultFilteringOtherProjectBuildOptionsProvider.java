/**
 * FilteringOtherProjectBuildOptionsProvider.java
 * Created 28-Feb-2016 17:07:39
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

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options;

import com.google.common.base.Predicate;
import java.util.List;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.util.ListBoxModel;
import hudson.util.RunList;



/**
 * ResultFilteringOtherProjectBuildOptionsProvider
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class ResultFilteringOtherProjectBuildOptionsProvider implements OtherProjectBuildOptionsProvider {

    protected static final String DEFAULT_DATE_FORMAT_STRING = "";

    protected final Result result;
    protected final OtherProjectBuildOptionFormatter formatter;



    public ResultFilteringOtherProjectBuildOptionsProvider(
        final Result result,
        final OtherProjectBuildOptionFormatter formatter
    ) {
        this.result = result;
        this.formatter = formatter;
    }

    public ResultFilteringOtherProjectBuildOptionsProvider(final Result result) {
        this(result, new OtherProjectBuildOptionFormatter.DefaultImpl());
    }

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

    protected List<AbstractBuild> filterBuilds(RunList runs) {
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



    public static class Factory implements OtherProjectBuildOptionsProvider.Factory<ResultFilteringOtherProjectBuildOptionsProvider, Result> {

        @Override
        public ResultFilteringOtherProjectBuildOptionsProvider buildProvider(Result result) {
            return new ResultFilteringOtherProjectBuildOptionsProvider(result);
        }

    }

}
