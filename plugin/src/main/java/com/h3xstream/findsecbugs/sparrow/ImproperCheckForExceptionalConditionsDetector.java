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
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;

import org.apache.bcel.classfile.CodeException;

import java.util.HashSet;
import java.util.Set;

/**
 * Catching exceptions improperly promotes complex error handling code.
 * Detects the usage of general exception and NullPointerException.
 * <br>
 *     http://cwe.mitre.org/data/definitions/395.html
 *     http://cwe.mitre.org/data/definitions/396.html
 *
 * @author Dongyong Kim (Sparrow Co., Ltd.)
 */
public class ImproperCheckForExceptionalConditionsDetector extends PreorderVisitor implements Detector {

    private BugReporter bugReporter;
    private ClassContext classContext;
    Set<Integer> checkDuplicate = new HashSet<>();

    public ImproperCheckForExceptionalConditionsDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visit(CodeException obj) {
        SourceLineAnnotation sourceLineAnnotation = SourceLineAnnotation.fromVisitedInstruction(this.classContext, this, obj.getHandlerPC());
        // Check this exception is already detected.
        Integer sourceCodeLine = new Integer(sourceLineAnnotation.getStartLine());
        if(checkDuplicate.contains(sourceCodeLine)) return;

        int type = obj.getCatchType();
        if (type == 0) {
            return;
        }
        String name = getConstantPool().constantToString(getConstantPool().getConstant(type));

        if ("java.lang.NullPointerException".equals(name)) {
            bugReporter.reportBug(new BugInstance(this, "USE_OF_NULL_POINTER_EXCEPTION_CATCH", NORMAL_PRIORITY).addClassAndMethod(this)
                    .addSourceLine(this.classContext, this, obj.getHandlerPC()));
        }
        else if ("java.lang.Exception".equals(name)) {
            bugReporter.reportBug(new BugInstance(this, "USE_OF_GENERIC_EXCEPTION_CATCH", NORMAL_PRIORITY).addClassAndMethod(this)
                    .addSourceLine(this.classContext, this, obj.getHandlerPC()));
        }
        checkDuplicate.add(sourceCodeLine);
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
