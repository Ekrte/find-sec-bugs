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

import com.h3xstream.findsecbugs.common.matcher.InvokeMatcherBuilder;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.Const;

import static com.h3xstream.findsecbugs.common.matcher.InstructionDSL.invokeInstruction;

/**
 * Do not use deprecated API such as System Exit.
 * Detects the usage of Runtime.exit() or System.exit()
 * <br>
 *     http://cwe.mitre.org/data/definitions/382.html
 *
 * @author Dongyong Kim (Sparrow Co., Ltd.)
 */
public class UseOfDeprecatedApiDetector extends OpcodeStackDetector {

    private static final InvokeMatcherBuilder RUNTIME_EXIT = invokeInstruction().atClass("java/lang/Runtime").atMethod("exit");
    private static final InvokeMatcherBuilder SYSTEM_EXIT = invokeInstruction().atClass("java/lang/System").atMethod("exit");
    private BugReporter bugReporter;

    public UseOfDeprecatedApiDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == Const.INVOKESTATIC || seen == Const.INVOKEVIRTUAL) {
            if(RUNTIME_EXIT.matches(this)) {
                bugReporter.reportBug(new BugInstance(this, "DEPRECATED_API_RUNTIME_EXIT", Priorities.NORMAL_PRIORITY) //
                        .addClass(this).addMethod(this).addSourceLine(this));
            } else if(SYSTEM_EXIT.matches(this)) {
                bugReporter.reportBug(new BugInstance(this, "DEPRECATED_API_SYSTEM_EXIT", Priorities.NORMAL_PRIORITY) //
                        .addClass(this).addMethod(this).addSourceLine(this));
            }
        }
    }
}