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
package com.mattdw.jenkins.plugins.otherbuild.envvars.execution;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
public class ImportVarsResultTest {
    
    private final String projectName = "PROJECT NAME";
    private final String buildId = "BUILD ID";
    private final int totalVarsImported = 9;
    private ImportVarsResult result;

    @Before
    public void setUp() {
        this.result = new ImportVarsResult(
            this.projectName,
            this.buildId,
            this.totalVarsImported
        );
    }

    @Test
    public void testGetProjectName() {
        assertSame(this.projectName, this.result.getProjectName());
    }

    @Test
    public void testGetBuildId() {
        assertSame(this.buildId, this.result.getBuildId());
    }

    @Test
    public void testGetTotalVarsImported() {
        assertSame(this.totalVarsImported, this.result.getTotalVarsImported());
    }
    
}
