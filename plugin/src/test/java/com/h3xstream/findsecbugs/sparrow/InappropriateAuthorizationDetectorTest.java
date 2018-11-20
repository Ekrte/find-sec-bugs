/**
 * Find Security Bugs
 * Copyright (c) Philippe Arteau, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.h3xstream.findsecbugs.sparrow;

import com.h3xstream.findbugs.test.BaseDetectorTest;
import com.h3xstream.findbugs.test.EasyBugReporter;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class InappropriateAuthorizationDetectorTest extends BaseDetectorTest {

    @Test
    public void InappropriateAuthorization() throws Exception {
        //Locate test code
        String[] files = {
                getClassFilePath("bytecode_samples/CWE566_Authorization_Bypass_Through_SQL_Primary__Servlet_01.class"),
                getClassFilePath("testcode/sparrow/InappropriateAuthorization")
        };

        //Run the analysis
        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        //Assertions
        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INAPPROPRIATE_AUTHORIZATION")
                        .inClass("InappropriateAuthorization")
                        .inMethod("bad")
                        .atLine(15)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INAPPROPRIATE_AUTHORIZATION")
                        .inClass("CWE566_Authorization_Bypass_Through_SQL_Primary__Servlet_01").inMethod("bad").atLine(57).build()
        );
    }
}
