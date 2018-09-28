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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The code for debugging should be removed before release.
 * Debug code may includes sensitive information such as configuration, control portion of system.
 * <br>
 *     http://cwe.mitre.org/data/definitions/489.html
 */
public class InfoExposureByCommentsDetector implements Detector {

    private static final String DEBUG_CODE = "INFO_EXPOSURE_BY_COMMENTS_SERVLET";
    private BugReporter bugReporter;

    public InfoExposureByCommentsDetector(BugReporter bugReporter) {
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

        ConstantPoolGen cpg = classContext.getConstantPoolGen();
        CFG cfg = classContext.getCFG(m);

        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            String servletResponse = null;

            // HttpServletResponse.getWriter().println() pattern : (INVOKEINTERFACE, LDC, INVOKEVIRTUAL)
            Instruction first_inst = loc.getHandle().getInstruction();
            if (first_inst instanceof INVOKEINTERFACE) {
                INVOKEINTERFACE ii = (INVOKEINTERFACE) first_inst;
                if(!(ii.getClassName(cpg).equals("javax.servlet.http.HttpServletResponse")
                && ii.getMethodName(cpg).equals("getWriter"))) continue;
            } else continue;

            if(loc.getHandle().getNext() == null) continue;
            Instruction second_inst = loc.getHandle().getNext().getInstruction();
            if (!(second_inst instanceof LDC) || loc.getHandle().getNext().getNext() == null) continue;

            Instruction third_inst = loc.getHandle().getNext().getNext().getInstruction();
            if (third_inst instanceof INVOKEVIRTUAL) {
                // Check this instruction means servlet response.
                INVOKEVIRTUAL iv = (INVOKEVIRTUAL) third_inst;
                if(iv.getClassName(cpg).equals("java.io.PrintWriter") &&
                        iv.getMethodName(cpg).equals("println")) {
                    LDC ldc = (LDC) second_inst;
                    servletResponse = ldc.getValue(cpg).toString();
                    // Pattern Matching Part
                    String angleBracket[] = servletResponse.split("<");
                    for(String xmlValue : angleBracket) {
                        // Check this xmlValue is comments
                        if(xmlValue.contains("!--")) {
                            Pattern p = Pattern.compile(".*(?i)password.*=.*?");
                            Matcher matcher = p.matcher(xmlValue);
                            if(matcher.lookingAt()) {
                                JavaClass javaClass = classContext.getJavaClass();

                                bugReporter.reportBug(new BugInstance(this, DEBUG_CODE, Priorities.NORMAL_PRIORITY) //
                                        .addClass(javaClass)
                                        .addMethod(javaClass, m)
                                        .addSourceLine(classContext, m, loc));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void report() {

    }
}
