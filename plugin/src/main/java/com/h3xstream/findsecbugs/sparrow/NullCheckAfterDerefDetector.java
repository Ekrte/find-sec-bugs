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
import edu.umd.cs.findbugs.OpcodeStack;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

import java.util.*;

public class NullCheckAfterDerefDetector extends OpcodeStackDetector {

    private final BugReporter bugReporter;
    Set<Integer> dereferencedVariable = new HashSet<>();
    String currentMethod = "";

    public NullCheckAfterDerefDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        if(!currentMethod.equals(getFullyQualifiedMethodName())) {
            dereferencedVariable.clear();
            currentMethod = getFullyQualifiedMethodName();
        }
        if(isInvokeInstruction(seen)) {
            markDereferencedVariable();
        }
        if(seen == IFNULL || seen == IFNONNULL) {
            if(dereferencedVariable.contains(stack.getStackItem(0).hashCode())) {
                bugReporter.reportBug(new BugInstance(this, "NULL_CHECK_AFTER_DEREF", Priorities.NORMAL_PRIORITY) //
                        .addClass(this).addMethod(this).addSourceLine(this));
            }
        }
    }

    private static boolean isInvokeInstruction(int seen) {
        return seen >= INVOKEVIRTUAL && seen <= INVOKEINTERFACE;
    }

    private void markDereferencedVariable() {
        for (int i = 0; i < stack.getStackDepth(); i++) {
            OpcodeStack.Item stackItem = stack.getStackItem(i);
            if ((stackItem.getConstant() != null || stackItem.isNull())) {
                dereferencedVariable.add(stackItem.hashCode());
            }
        }
    }
}
