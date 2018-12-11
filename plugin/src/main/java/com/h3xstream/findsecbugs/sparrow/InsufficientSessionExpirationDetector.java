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

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.Const;

public class InsufficientSessionExpirationDetector extends OpcodeStackDetector {

    private BugReporter bugReporter;

    public InsufficientSessionExpirationDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == Const.INVOKEINTERFACE && getClassConstantOperand().equals("javax/servlet/http/HttpSession")
                && getNameConstantOperand().equals("setMaxInactiveInterval")) {

            Object maxAge = stack.getStackItem(0).getConstant();
            Integer n = (maxAge instanceof Integer) ? (Integer)maxAge : 0;

            //Max age equal or greater than one year
            System.out.println("|DEBUG| n is " + n);
            if (n == -1) {
                bugReporter.reportBug(new BugInstance(this, "INSUFFICIENT_SESSION_EXPIRATION", Priorities.NORMAL_PRIORITY) //
                        .addClass(this).addMethod(this).addSourceLine(this));
            }
        }
    }
}