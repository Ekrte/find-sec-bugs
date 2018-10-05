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
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;

import org.apache.bcel.classfile.CodeException;

/**
 * The code for debugging should be removed before release.
 * Debug code may includes sensitive information such as configuration, control portion of system.
 * <br>
 *     http://cwe.mitre.org/data/definitions/489.html
 */
public class ImproperCheckForExceptionalConditionsDetector extends PreorderVisitor implements Detector {

    private BugReporter bugReporter;
    private ClassContext classContext;

    public ImproperCheckForExceptionalConditionsDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visit(CodeException obj) {
        int type = obj.getCatchType();
        if (type == 0) {
            return;
        }
        String name = getConstantPool().constantToString(getConstantPool().getConstant(type));

        if ("java.lang.NullPointerException".equals(name)) {
            bugReporter.reportBug(new BugInstance(this, "USE_OF_NULL_POINTER_EXCEPTION_CATCH", NORMAL_PRIORITY).addClassAndMethod(this)
                    .addSourceLine(this.classContext, this, obj.getHandlerPC()));
        }

        if ("java.lang.Exception".equals(name)) {
            bugReporter.reportBug(new BugInstance(this, "USE_OF_GENERIC_EXCEPTION_CATCH", NORMAL_PRIORITY).addClassAndMethod(this)
                    .addSourceLine(this.classContext, this, obj.getHandlerPC()));
        }
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        this.classContext = classContext;
        classContext.getJavaClass().accept(this);
    }

    @Override
    public void report() {

    }
}
