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

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider;

/**
 * OtherBuildVarImportException - abstract supertype for exceptions related
 * to provision of resources necessary to locate another build from which
 * environment variables are to be imported
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public abstract class OtherBuildVarImportException extends Exception {

    /**
     * Constructor - creates a new instance of OtherBuildVarImportException
     * with an underlying exception [cause]
     * 
     * @param message
     *      Exception message
     * @param cause 
     *      Underlying exception which caused this exception to be triggered
     */
    public OtherBuildVarImportException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor - creates a new instance of OtherBuildVarImportException
     * without an underlying exception [cause]
     * 
     * @param message
     *      Exception message
     */
    public OtherBuildVarImportException(String message) {
        this(message, null);
    }
    
}
