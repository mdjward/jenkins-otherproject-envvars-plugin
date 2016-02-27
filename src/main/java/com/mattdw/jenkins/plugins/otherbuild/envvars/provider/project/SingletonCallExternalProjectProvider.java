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

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project;

import hudson.model.AbstractProject;
import java.lang.reflect.InvocationTargetException;
import jenkins.model.Jenkins;

/**
 * SingletonCallExternalProjectProvider
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 * @copyright (c) 2016, Byng Services Ltd
 */
public class SingletonCallExternalProjectProvider implements ExternalProjectProvider<AbstractProject> {

    private final Class<Jenkins> jenkinsClass;

    public SingletonCallExternalProjectProvider(Class<Jenkins> jenkinsClass) {
        this.jenkinsClass = (jenkinsClass != null ? jenkinsClass : Jenkins.class);
    }

    public SingletonCallExternalProjectProvider() {
        this(null);
    }

    public AbstractProject provideProject(String name) throws ProjectNotFoundException {
        try {
            AbstractProject project = (
                (Jenkins) this.jenkinsClass.getMethod(
                    "getActiveInstance",
                    new Class<?>[0]
                ).invoke(null, new Object[0])
            ).getItemByFullName(name, AbstractProject.class);

            if (project != null) {
                return project;
            }

            throw new ProjectNotFoundException(name);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new ProjectNotFoundException(name, ex);
        }
    }
    
}
