/**
 * ResultOptionsProvider.java
 * Created 28-Feb-2016 17:17:13
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

import hudson.model.Result;
import hudson.util.ListBoxModel;



/**
 * Defines a producer of presentable, user-readable result filtering options 
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public interface ResultOptionsProvider {

    /**
     * Builds a {@link ListBoxModel} of options, each of which represents an
     * enumerable build {@link Result} (state)
     * 
     * @return 
     *      {@link ListBoxModel} of enumerable build results/states
     */
    public ListBoxModel getBuildResultOptions();
    
    
    
    /**
     * Default implementation of {@link ResultOptionsProvider} which enumerates
     * the five main Jenkins build results/states
     */
    public static class Impl implements ResultOptionsProvider {

        /**
         * Defines the possible build results/states that are to be presented
         */
        private static final Result[] RESULTS = new Result[] {
            Result.SUCCESS,
            Result.FAILURE,
            Result.UNSTABLE,
            Result.ABORTED,
            Result.NOT_BUILT
        };
        
        /**
         * Builds a {@link ListBoxModel} of options, each of which represents an
         * enumerable build {@link Result} (state)
         * 
         * @return 
         *      {@link ListBoxModel} of enumerable build results/states
         */
        @Override
        public ListBoxModel getBuildResultOptions() {
            ListBoxModel model = new ListBoxModel();

            for (Result r : RESULTS) {
                model.add(r.toString());
            }

            return model;
        }
        
    }
    
}
