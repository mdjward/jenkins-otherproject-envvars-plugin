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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class EnvContributingVarsImporterTest {
    
    private String varNameTemplate = "VAR NAME TEMPLATE %s";
    private EnvContributingVarsImporter importer;
    
    @Before
    public void setUp() {
        this.importer = new EnvContributingVarsImporter(this.varNameTemplate);
        
        
    }

    @Test
    public void testConstructorRejectsInvalidVarTemplate() {
        String invalidVarNameTemplate = "SFJKSDJFKLAJO3UMO";

        try {
            new EnvContributingVarsImporter(invalidVarNameTemplate);
            
            fail("Exception was not thrown");
        } catch (IllegalArgumentException ex) {
            assertEquals(
                String.format("Var name template %s is invalid", invalidVarNameTemplate),
                ex.getMessage()
            );
        }
        
    }

    @Test
    public void testGetVarNameTemplate() {
        assertEquals(this.varNameTemplate, this.importer.getVarNameTemplate());
    }

    @Test
    public void testImportVarsAndContributingAction() {
        Run<?, ?> targetBuild = (Run<?, ?>) mock(Run.class);

        final Class<EnvContributingVarsImporter.ContributingAction> actionClass = EnvContributingVarsImporter.ContributingAction.class;
        final MutableHolder<EnvContributingVarsImporter.ContributingAction> holder = new MutableHolder<>();

        final EnvVars otherBuildEnvVars = new EnvVars();
        otherBuildEnvVars.put("ONE", "one");
        otherBuildEnvVars.put("TWO", "2");

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                holder.setObject(invocation.getArgumentAt(0, actionClass));

                return null;
            }
        }).when(targetBuild).replaceAction(isA(actionClass));

        this.importer.importVars(targetBuild, otherBuildEnvVars);
        verify(targetBuild, times(1)).replaceAction(isA(actionClass));

        EnvVars currentBuildEnvVars = new EnvVars();
        
        EnvironmentContributingAction action = holder.getObject();
                
        action.buildEnvVars(mock(AbstractBuild.class), currentBuildEnvVars);

        assertEquals("one", currentBuildEnvVars.get(String.format(this.varNameTemplate, "ONE")));
        assertEquals("2", currentBuildEnvVars.get(String.format(this.varNameTemplate, "TWO")));
        
        assertNull(action.getDisplayName());
        assertNull(action.getIconFileName());
        assertNull(action.getUrlName());
    }



    private static class MutableHolder<T> {
        
        private T object;
        
        public MutableHolder(T object) {
            this.object = object;
        }

        public MutableHolder() {
            this(null);
        }
        
        public T getObject() {
            return this.object;
        }

        public void setObject(T object) {
            this.object = object;
        }
        
    }

}
