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

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class InfiniteLoopPatternDetectorTest extends BaseDetectorTest {
    @Test
    public void InfiniteLoop() throws Exception {
        //Locate test code
        String[] files = {
                getClassFilePath("testcode/sparrow/InfiniteLoop__do"),
                getClassFilePath("testcode/sparrow/InfiniteLoop__do_true"),
                getClassFilePath("testcode/sparrow/InfiniteLoop__for"),
                getClassFilePath("testcode/sparrow/InfiniteLoop__for_empty"),
                getClassFilePath("testcode/sparrow/InfiniteLoop__while"),
                getClassFilePath("testcode/sparrow/InfiniteLoop__while_true"),
        };

        //Run the analysis
        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INFINITE_LOOP_PATTERN")
                        .inClass("InfiniteLoop__do")
                        .inMethod("bad")
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INFINITE_LOOP_PATTERN")
                        .inClass("InfiniteLoop__do_true")
                        .inMethod("bad")
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INFINITE_LOOP_PATTERN")
                        .inClass("InfiniteLoop__for")
                        .inMethod("bad")
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INFINITE_LOOP_PATTERN")
                        .inClass("InfiniteLoop__for_empty")
                        .inMethod("bad")
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INFINITE_LOOP_PATTERN")
                        .inClass("InfiniteLoop__while")
                        .inMethod("bad")
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("INFINITE_LOOP_PATTERN")
                        .inClass("InfiniteLoop__while_true")
                        .inMethod("bad")
                        .build()
        );
    }
}
