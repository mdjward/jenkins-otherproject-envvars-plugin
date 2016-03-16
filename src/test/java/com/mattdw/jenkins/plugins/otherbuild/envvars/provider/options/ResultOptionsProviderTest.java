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
package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options;

import hudson.model.Result;
import hudson.util.ListBoxModel;
import hudson.util.ListBoxModel.Option;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

/**
 *
 * @author M.D.Ward <matthew.ward@byng.co>
 */
@RunWith(MockitoJUnitRunner.class)
public class ResultOptionsProviderTest {
    
    private ResultOptionsProvider optionsProvider;
    
    @Before
    public void setUp() {
        this.optionsProvider = new ResultOptionsProvider.Impl();
    }
    
    @Test
    public void testGetBuildResultOptions() {
        final String[] possibleResultStates = new String[] {
            Result.SUCCESS.toString(),
            Result.FAILURE.toString(),
            Result.UNSTABLE.toString(),
            Result.ABORTED.toString(),
            Result.NOT_BUILT.toString()
        };
        final ListBoxModel optionsList = this.optionsProvider.getBuildResultOptions();
        
        int i = 0;
        
        for (Option o : optionsList) {
            assertEquals(possibleResultStates[i], optionsList.get(i).value);
            assertEquals(possibleResultStates[i], optionsList.get(i).name);

            i++;
        }
    }

}