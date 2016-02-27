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

package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.project;

import hudson.model.AbstractProject;
import java.lang.reflect.InvocationTargetException;
import jenkins.model.Jenkins;

/**
 * SingletonCallExternalProjectProvider - provides a project through the singleton
 * Jenkins.getActiveInstance() method (or an extension thereof)s
 * 
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class SingletonCallExternalProjectProvider implements ExternalProjectProvider<AbstractProject> {

    /**
     * Class descriptor of the Jenkins implementation on which the static
     * singleton method to load projects will be executed
     */
    private final Class<Jenkins> jenkinsClass;

    /**
     * Constructor - creates a new instance of SingletonCallExternalProjectProvider
     * with a specific Jenkins (or extension thereof) class descriptor
     * 
     * @param jenkinsClass 
     *      Class descriptor of the Jenkins implementation on which the static
     *      singleton method to load projects will be executed
     */
    public SingletonCallExternalProjectProvider(Class<Jenkins> jenkinsClass) {
        this.jenkinsClass = (jenkinsClass != null ? jenkinsClass : Jenkins.class);
    }

    /**
     * Constructor - creates a new instance of SingletonCallExternalProjectProvider
     */
    public SingletonCallExternalProjectProvider() {
        this(null);
    }

    /**
     * Provides a target project by name
     * 
     * @param name
     *      Name (identifier) of the target project
     * @return
     *      Target project (if it can be found)
     * @throws ProjectNotFoundException 
     *      If the project cannot be found with the given name (id)
     */
    @Override
    public AbstractProject provideProject(String name) throws ProjectNotFoundException {
        try {
            // Use reflection primarily for testability
            AbstractProject project = (
                (Jenkins) this.jenkinsClass.getMethod(
                    "getActiveInstance",
                    new Class<?>[0]
                ).invoke(null, new Object[0])
            ).getItemByFullName(name, AbstractProject.class);

            // If the project was successfully loaded (non-null), return it
            if (project != null) {
                return project;
            }

            // ...otherwise throw an appropriate exception
            throw new ProjectNotFoundException(name);

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            // Any reflective/other exceptions need to be rethrown
            throw new ProjectNotFoundException(name, ex);
        }
    }
    
}
