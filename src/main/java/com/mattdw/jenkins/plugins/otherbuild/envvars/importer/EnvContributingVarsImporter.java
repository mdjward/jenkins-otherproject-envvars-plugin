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

package com.mattdw.jenkins.plugins.otherbuild.envvars.importer;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Run;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * EnvContributingVarsImporter - makes use of build actions to handle the import
 * of environment variables into a given build
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class EnvContributingVarsImporter implements TemplatingOtherBuildEnvVarsImporter {

    /**
     * String.format (printf) template to which the original environment
     * variable names will be provided (notionally, so as not to overwrite
     * existing variables within the scope of the build)
     */
    private final String varNameTemplate;



    /**
     * Constructor - creates a new instance of EnvContributingVarsImporter
     * 
     * @param varNameTemplate
     *      String.format (printf) template to which the original environment
     *      variable names will be provided (notionally, so as not to overwrite
     *      existing variables within the scope of the build)
     * @throws IllegalArgumentException 
     *      If the string template does not conform to the valid pattern laid
     *      out in isVarNameTemplateValid()
     */
    public EnvContributingVarsImporter(String varNameTemplate) throws IllegalArgumentException {
        if (!isVarNameTemplateValid(varNameTemplate)) {
            throw new IllegalArgumentException(
                String.format("Var name template %s is invalid", varNameTemplate)
            );
        }
        
        this.varNameTemplate = varNameTemplate;
    }
    
    /**
     * Getter for varNameTemplate
     * 
     * @return String.format (printf) template to which the original environment
     * variable names will be provided (notionally, so as not to overwrite
     * existing variables within the scope of the build)
     */
    @Override
    public String getVarNameTemplate() {
        return this.varNameTemplate;
    }

    /**
     * Imports variables into a build by adding a {@link ContributingAction}
     * action to the build which handles the actual import
     * 
     * @param targetBuild
     *      Build into which variables are to be imported
     * @param otherBuildEnvVars 
     *      Variables to be imported
     */
    @Override
    public void importVars(Run<?, ?> targetBuild, Map<String, String> otherBuildEnvVars) {
        targetBuild.replaceAction(new ContributingAction(otherBuildEnvVars));
    }
    
    /**
     * Indicates whether or not a given variable name is of a valid format
     * 
     * @param varNameTemplate
     *      String.format (printf) template to which the original environment
     *      variable names will be provided (notionally, so as not to overwrite
     *      existing variables within the scope of the build)
     * @return
     *      TRUE if the string has one (and only one) instance of %s;
     *      otherwise FALSE
     */
    public static boolean isVarNameTemplateValid(String varNameTemplate) {
        
        // Compile a regular expresion pattern to locate %s inside a string
        Pattern p = Pattern.compile("(%s)");
        Matcher m = p.matcher(varNameTemplate);
        
        // Not only must the pattern match, but we only want a single %s
        int count = 0;
        while (m.find()) {
            
            // Only one instance of %s can exist for the string to be valid;
            // this condition immediately reveals it to be invalid and
            // (any) further loops are unnecessary
            if (count > 1) {
                return false;
            }

            count++;
        }
        
        // Return TRUE only if one instance has been located
        return count == 1;
    }
    
    
    
    /**
     * ContributionAction - protected inner class implementing
     * {@link EnvironmentContributingAction} to contribute environment variables
     * to a given build
     * 
     * @author M.D.Ward <dev@mattdw.co.uk>
     */
    protected class ContributingAction implements EnvironmentContributingAction {

        /**
         * Environment variables to inject into a build when triggered
         */
        private final Map<String, String> otherBuildEnvVars;

        /**
         * Constructor - creates a new instance of ContributingAction
         * 
         * @param otherBuildEnvVars 
         *      Environment variables to inject into a build when triggered
         */
        public ContributingAction(final Map<String, String> otherBuildEnvVars) {
            this.otherBuildEnvVars = otherBuildEnvVars;
        }

        /**
         * Contributes pre-provided environment variables to a given build
         * (notionally the current build, but the specific build provision is
         * decoupled from this implementation)
         * 
         * @param build
         *      Build for which environment variables are to be contributed;
         *      not used in this implementation
         * @param env 
         *      Environment for the given build
         */
        @Override
        public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
            for (String key : this.otherBuildEnvVars.keySet()) {
                env.put(
                    String.format(varNameTemplate, key),
                    otherBuildEnvVars.get(key)
                );
            }
        }

        /**
         * Getter for icon file name (not supported in this implementation)
         * 
         * @return null
         */
        @Override
        public String getIconFileName() {
            return null;
        }

        /**
         * Getter for display name (not supported in this implementation)
         * 
         * @return null
         */
        @Override
        public String getDisplayName() {
            return null;
        }

        /**
         * Getter for URL name (not supported in this implementation)
         * 
         * @return null
         */
        @Override
        public String getUrlName() {
            return null;
        }

    }
    
}
