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
 * EnvContributingVarsImporter
 * 
 * @author M.D.Ward <matthew.ward@byng.co>
 * @copyright (c) 2016, Byng Services Ltd
 */
public class EnvContributingVarsImporter implements TemplatingOtherBuildEnvVarsImporter {

    private final String varNameTemplate;



    @Override
    public String getVarNameTemplate() {
        return this.varNameTemplate;
    }

    public EnvContributingVarsImporter(String varNameTemplate) throws IllegalArgumentException {
        if (!isVarNameTemplateValid(varNameTemplate)) {
            throw new IllegalArgumentException(
                String.format("Var name template %s is invalid", varNameTemplate)
            );
        }
        
        this.varNameTemplate = varNameTemplate;
    }
    
    @Override
    public void importVars(Run<?, ?> targetBuild, Map<String, String> otherBuildEnvVars) {
        targetBuild.replaceAction(new ContributingAction(otherBuildEnvVars));
    }
    
    public static boolean isVarNameTemplateValid(String varNameTemplate) {
        Pattern p = Pattern.compile("(%s)");
        Matcher m = p.matcher(varNameTemplate);
        
        int count = 0;
        while (m.find()) {
            if (count > 1) {
                return false;
            }

            count++;
        }
        
        return count == 1;
    }
    
    
    

    protected class ContributingAction implements EnvironmentContributingAction {

        private final Map<String, String> otherBuildEnvVars;

        public ContributingAction(final Map<String, String> otherBuildEnvVars) {
            this.otherBuildEnvVars = otherBuildEnvVars;
        }

        @Override
        public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
            for (String key : this.otherBuildEnvVars.keySet()) {
                env.put(
                    String.format(varNameTemplate, key),
                    otherBuildEnvVars.get(key)
                );
            }
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return null;
        }

    }
    
}
