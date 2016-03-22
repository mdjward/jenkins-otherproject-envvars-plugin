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
package com.mattdw.jenkins.plugins.otherbuild.envvars.execution;

import com.mattdw.jenkins.plugins.otherbuild.envvars.importer.VarNameTemplateAware;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


/**
 *
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class ImportVarsConfigurationTest {
    
    private final String projectName = "PROJECT NAME";
    private final String buildId = "BUILD ID";
    private VarNameTemplateAware varTemplater;
    private ImportVarsConfiguration config;
    

    
    
    @Before
    public void setUp() {
        this.varTemplater = mock(VarNameTemplateAware.class);
        
        this.config = new ImportVarsConfiguration(
            this.projectName,
            this.buildId,
            this.varTemplater
        );
    }

    @Test
    public void testGetProjectName() {
        assertSame(this.projectName, this.config.getProjectName());
    }

    @Test
    public void testGetBuildId() {
        assertSame(this.buildId, this.config.getBuildId());
    }

    @Test
    public void testGetVarTemplater() {
        assertSame(this.varTemplater, this.config.getVarTemplater());
    }
    
}
