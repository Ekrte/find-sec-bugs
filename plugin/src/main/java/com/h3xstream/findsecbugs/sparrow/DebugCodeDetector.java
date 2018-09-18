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
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Location;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;
import java.util.Iterator;

/**
 * The code for debugging should be removed before release.
 * Debug code may includes sensitive information such as configuration, control portion of system.
 * <br>
 *     http://cwe.mitre.org/data/definitions/489.html
 */
public class DebugCodeDetector implements Detector {

    private static final String DEBUG_CODE = "LEFTOVER_DEBUG_CODE";

    private BugReporter bugReporter;

    public DebugCodeDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass javaClass = classContext.getJavaClass();
        Method[] methodList = javaClass.getMethods();

        for (Method m : methodList) {
            try {
                analyzeMethod(m, classContext);
            } catch (CFGBuilderException e) {
            }
        }
    }

    private void analyzeMethod(Method m, ClassContext classContext) throws CFGBuilderException {
        //System.out.println("==="+m.getName()+"===");

        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        CFG cfg = classContext.getCFG(m);

        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            String VariableName = null;
            Instruction inst = loc.getHandle().getInstruction();

            if (inst instanceof GETSTATIC) {
                GETSTATIC getstatic = (GETSTATIC) inst;
                VariableName = getstatic.getFieldName(cpg);
            } else if (inst instanceof GETFIELD) {
                GETFIELD getfield = (GETFIELD) inst;
                VariableName = getfield.getName(cpg);
            }

            if ("DEBUG".equalsIgnoreCase(VariableName)) {
                Instruction next_inst = loc.getHandle().getNext().getInstruction();
                if(next_inst instanceof IFEQ) {
                    JavaClass javaClass = classContext.getJavaClass();

                    bugReporter.reportBug(new BugInstance(this, DEBUG_CODE, Priorities.NORMAL_PRIORITY) //
                            .addClass(javaClass)
                            .addMethod(javaClass, m)
                            .addSourceLine(classContext, m, loc));
                }
            }
        }
    }

    @Override
    public void report() {

    }
}
