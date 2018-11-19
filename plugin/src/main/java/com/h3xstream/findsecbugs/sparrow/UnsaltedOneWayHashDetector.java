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
import edu.umd.cs.findbugs.Detector;
import edu.umd.cs.findbugs.Priorities;
import edu.umd.cs.findbugs.ba.CFG;
import edu.umd.cs.findbugs.ba.CFGBuilderException;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.Location;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.h3xstream.findsecbugs.common.matcher.InstructionDSL.invokeInstruction;

public class UnsaltedOneWayHashDetector implements Detector {
    private static final String DEBUG_CODE = "UNSALTED_ONE_WAY_HASH";
    private static final InvokeMatcherBuilder SALT_UPDATE = invokeInstruction().atClass("java/security/MessageDigest").atMethod("update");
    private static final InvokeMatcherBuilder HASH_DIGEST = invokeInstruction().atClass("java/security/MessageDigest").atMethod("digest");

    private BugReporter bugReporter;
    // This dictionary consist of <Variable Name, flag to check a usage of salt>
    Map<String, Boolean> useSalt = new HashMap<>();

    public UnsaltedOneWayHashDetector(BugReporter bugReporter) {
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
            Instruction inst = loc.getHandle().getInstruction();
            if(SALT_UPDATE.matches(inst, cpg)) {
                useSalt.put(classContext.getFullyQualifiedMethodName(m), true);
            } else if(HASH_DIGEST.matches(inst,cpg)) {
                if (!useSalt.containsKey(classContext.getFullyQualifiedMethodName(m))) {
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
