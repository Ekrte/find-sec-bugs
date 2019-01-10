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
import edu.umd.cs.findbugs.ba.*;
import edu.umd.cs.findbugs.classfile.CheckedAnalysisException;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.*;

/**
 * If multiple threads of execution use a resource simultaneously, atomicity of operation could not be guaranteed.
 * <br>
 *     http://cwe.mitre.org/data/definitions/366.html
 *
 * @author Dongyong Kim (Sparrow Co., Ltd.)
 */
public class RaceConditionDetector implements Detector {

    private static final String RACE_CONDITION = "RACE_CONDITION_FILE_IO";
    private BugReporter bugReporter;

    public RaceConditionDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    private boolean checkThreadClass(JavaClass javaClass) {
        try {
            JavaClass test = javaClass.getSuperClass();
            Method[] methodList = javaClass.getMethods();

            while(!(test.getClassName().contains("Object"))) { // It means this class has no explicit SuperClass
                for (String tokens : test.getClassName().split("\\.")) {
                    if (tokens.equals("Thread")) return true;
                }
                test = test.getSuperClass(); // Final class would be "Object"
            }
            return false;
        } catch(ClassNotFoundException ex) {
            AnalysisContext.reportMissingClass(ex);
            return false;
        }
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass javaClass = classContext.getJavaClass();
        Method[] methodList = javaClass.getMethods();

        // Check current class is thread-related object.
        //try {
        if (checkThreadClass(javaClass)) {
            for (Method m : methodList) {
                if (!m.toString().contains(" synchronized ")) {
                    try {
                        // Check unsafe synchronized thread which uses File IO
                        analyzeMethod(m, classContext);
                    } catch (CFGBuilderException e) {
                    }
                }
            }
        }
    }

    private void analyzeMethod(Method m, ClassContext classContext) throws CFGBuilderException {
        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        CFG cfg = classContext.getCFG(m);
        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            Instruction inst = loc.getHandle().getInstruction();

            if(inst instanceof NEW) {
                NEW new_inst = (NEW)inst;
                if(new_inst.getLoadClassType(cpg).toString().equals("java.io.File")) {
                    JavaClass javaClass = classContext.getJavaClass();

                    bugReporter.reportBug(new BugInstance(this, RACE_CONDITION, Priorities.NORMAL_PRIORITY) //
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
