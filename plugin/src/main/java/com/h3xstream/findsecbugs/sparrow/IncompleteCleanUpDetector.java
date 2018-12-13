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
import edu.umd.cs.findbugs.ba.*;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.h3xstream.findsecbugs.common.matcher.InstructionDSL.invokeInstruction;

public class IncompleteCleanUpDetector implements Detector {

    private static final String INCOMPLETE_CLEANUP = "INCOMPLETE_CLEANUP";
    private static final String[] EXCEPTION_CLASS = new String[] {
            new String("Ljavax/servlet/http/HttpServletRequest;"),
            new String("Ljavax/servelt/http/HttpServletResponse;"),
    };
    private static final InvokeMatcherBuilder CREATE_TEMP_FILE = invokeInstruction().atMethod("createTempFile").withArgs("(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;");
    private static final InvokeMatcherBuilder DELETE_ON_EXIT = invokeInstruction().atMethod("deleteOnExit").withArgs("()V");
    private BugReporter bugReporter;
    BugInstance bugInstance;
    Map<String, Boolean> deleteTempfile = new HashMap<>();

    public IncompleteCleanUpDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass javaClass = classContext.getJavaClass();
        Method[] methodList = javaClass.getMethods();

        for (Method m : methodList) {
            try {
                for (String exception : EXCEPTION_CLASS) {
                    if (m.getSignature().contains(exception)) {
                        analyzeExceptionMethod(m, classContext);
                        return;
                    }
                }
                analyzeMethod(m, classContext);
                String qualifiedName = classContext.getFullyQualifiedMethodName(m);
                if(deleteTempfile.containsKey(qualifiedName)) {
                    // If temp file is deleted, deleteTempfile.get() returns true.
                    // Detect the false value in the Map structure which means creating tempFile which is not deleted.
                    if(!deleteTempfile.get(qualifiedName)) {
                        bugReporter.reportBug(bugInstance);
                        deleteTempfile.remove(qualifiedName);
                    }
                }
            } catch (CFGBuilderException e) {
            }
        }
    }

    private void analyzeMethod(Method m, ClassContext classContext) throws CFGBuilderException {
        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        CFG cfg = classContext.getCFG(m);

        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            Instruction inst = loc.getHandle().getInstruction();

            if(inst instanceof  INVOKESTATIC) {
                if(CREATE_TEMP_FILE.matches(inst, cpg)) {
                    deleteTempfile.put(classContext.getFullyQualifiedMethodName(m), false);
                    JavaClass javaClass = classContext.getJavaClass();
                    // Store suspicious bugInstance so that reporting the bug after analyzing the method.
                    bugInstance = new BugInstance(this, INCOMPLETE_CLEANUP, Priorities.NORMAL_PRIORITY) //
                            .addClass(javaClass)
                            .addMethod(javaClass, m)
                            .addSourceLine(classContext, m, loc);
                }
            }
            if(inst instanceof INVOKEVIRTUAL || inst instanceof INVOKEINTERFACE) {
                if(DELETE_ON_EXIT.matches(inst, cpg)) {
                    deleteTempfile.put(classContext.getFullyQualifiedMethodName(m), true);
                }
            }
        }
    }

    private void analyzeExceptionMethod(Method m, ClassContext classContext) throws CFGBuilderException {
        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        CFG cfg = classContext.getCFG(m);

        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            Instruction inst = loc.getHandle().getInstruction();

            if(inst instanceof INVOKEVIRTUAL || inst instanceof INVOKEINTERFACE) {
                if(DELETE_ON_EXIT.matches(inst, cpg)) {
                    JavaClass javaClass = classContext.getJavaClass();
                    // Report bug instantly which uses deleteOnExit with Exception classes.
                    bugReporter.reportBug(new BugInstance(this, INCOMPLETE_CLEANUP, Priorities.NORMAL_PRIORITY) //
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
