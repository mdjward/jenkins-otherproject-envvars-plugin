/**
 * EnvVarsCopier.java
 * Created 21-Mar-2016 09:42:34
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

package com.mattdw.jenkins.plugins.otherbuild.envvars.importer;

import java.util.Map;



/**
 * EnvVarsCopier - definition of a variable copier for transferring variables
 * from one string/string key/value map to another
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 * 
 * @param <M>
 *      [Super]type for environment vars; must extend {@link Map} with
 *      {@link String} types for both key and value
 */
public interface EnvVarsCopier <M extends Map<String, String>> extends VarImporterOrCopier {

    /**
     * Copies variables from a source map to target map, applying the variable
     * name templating as configured
     * 
     * @param source
     *      Source map from which to copy variables
     * @param target 
     *      Target map on which to place variables with their templated name
     */
    public void copyEnvVars(M source, M target);

}
