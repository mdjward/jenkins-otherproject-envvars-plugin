/**
 * OtherProjectBuildOptionFormatter.java
 * Created 15-Mar-2016 12:29:34
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import hudson.model.AbstractBuild;
import hudson.model.Result;



/**
 * OtherProjectBuildOptionFormatter 
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public interface OtherProjectBuildOptionFormatter <B extends AbstractBuild> {

    public String formatBuild(B build);
    
    
    
    public static class DefaultImpl implements OtherProjectBuildOptionFormatter<AbstractBuild> {
        
        public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

        protected final DateFormat dateFormat;
        
        
        
        public DefaultImpl(final DateFormat dateFormat) {
            this.dateFormat = dateFormat;
        }
        
        public DefaultImpl() {
            this(DEFAULT_DATE_FORMAT);
        }

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
