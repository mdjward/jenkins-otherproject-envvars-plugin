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
package com.mattdw.jenkins.plugins.otherbuild.envvars.provider.options;

import hudson.model.AbstractBuild;
import hudson.model.Result;
import org.junit.Before;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author M.D.Ward <dev@mattdw.co.uk>
 */
public class OtherProjectBuildOptionFormatterTest {
    
    private DateFormat dateFormat;
    private BuildOptionFormatter formatter;
    
    @Before
    public void setUp() {
        this.formatter = new BuildOptionFormatter.DefaultImpl(
            (this.dateFormat = mock(DateFormat.class))
        );
    }

    @Test
    public void testDefaultConstructorUsesSimpleDateFormat() {
        BuildOptionFormatter.DefaultImpl formatterImpl = new BuildOptionFormatter.DefaultImpl();

        assertTrue(formatterImpl.dateFormat instanceof SimpleDateFormat);
        assertNotNull(formatterImpl.dateFormat);
        assertEquals("yyyy-MM-dd HH:mm:ss z", ((SimpleDateFormat) (formatterImpl.dateFormat)).toPattern());
    }
    
    @Test
    public void testFormatBuild() {
        final Result result = Result.FAILURE;
        final AbstractBuild build = mock(AbstractBuild.class);
        final Calendar buildTime = mock(Calendar.class);
        final Date buildReferenceDate = new Date();
        final long timestamp = buildReferenceDate.getTime();
        
        StringBuffer buffer = new StringBuffer("DATE FORMAT");
        when(this.dateFormat.format(isA(Date.class), isA(StringBuffer.class), isA(FieldPosition.class))).thenReturn(buffer);
        
        final String buildDisplayName = "BUILD DISPLAY NAME";
        final String resultName = result.toString();
        final String formattedDate = this.dateFormat.format(buildReferenceDate);
        
        when(build.getResult()).thenReturn(result);
        when(buildTime.getTimeInMillis()).thenReturn(timestamp);
        when(build.getDisplayName()).thenReturn(buildDisplayName);
        when(build.getTimestamp()).thenReturn(buildTime);
        
        assertEquals(
            String.format("%s - %s (%s)", buildDisplayName, resultName, formattedDate),
            this.formatter.formatBuild(build)
        );
        
        verify(this.dateFormat, atLeast(1)).format(isA(Date.class), isA(StringBuffer.class), isA(FieldPosition.class));
        
        verify(build, times(1)).getResult();
        verify(buildTime, atLeast(1)).getTimeInMillis();
        verify(build, times(1)).getDisplayName();
        verify(build, times(1)).getTimestamp();
    }
    
}
