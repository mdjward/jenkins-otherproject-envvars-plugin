/**
 * BuildOptionFormatter.java
 * Created 15-Mar-2016 12:29:34
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import hudson.model.AbstractBuild;
import hudson.model.Result;



/**
 * Formats options for past builds on another project to be presented to a user
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public interface BuildOptionFormatter <B extends AbstractBuild> {

    /**
     * Produce an appropriately formatted string for a given build
     * 
     * @param build
     *      Build to represent as a string
     * @return 
     *      String representing the given build
     */
    public String formatBuild(B build);
    
    
    
    /**
     * Default implementation of {@link BuildOptionFormatter} which formats builds
     * as strings in the form:
     * 
     * [display name] - [result/status] ([date started])
     */
    public static class DefaultImpl implements BuildOptionFormatter<AbstractBuild> {
        
        /**
         * Default date format for presenting the build date as a string
         */
        public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        /**
         * Date format used in presenting the build date as a string
         */
        protected final DateFormat dateFormat;
        
        
        
        /**
         * Constructor - creates a new instance of DefaultImpl
         * 
         * @param dateFormat
         *      Date format used in presenting the build date as a string
         */
        public DefaultImpl(final DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }
        
        /**
         * Constructor - creates a new instance of DefaultImpl assuming a default
         * date format for presenting the build date as a string
         */
        public DefaultImpl() {
            this(DEFAULT_DATE_FORMAT);
        }

        /**
         * Produce an appropriately formatted string for a given build
         * 
         * @param build
         *      Build to represent as a string
         * @return 
         *      String representing the given build
         */
        @Override
        public String formatBuild(AbstractBuild build) {
            Result result = build.getResult();
            
            return String.format(
                "%s - %s (%s)",
                build.getDisplayName(),
                (result != null ? result.toString() : "NO RESULT"),
                this.dateFormat.format(build.getTimestamp().getTime())
            );
        }
        
    }
    
}
