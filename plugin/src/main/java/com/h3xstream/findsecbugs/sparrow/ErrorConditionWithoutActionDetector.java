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
import edu.umd.cs.findbugs.visitclass.PreorderVisitor;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

import java.util.*;

/**
 * If errorable case is not appropriately handled, it causes unexpected behaviour.</p>
 * In a below example, there is no exception handling even if exception is caught in try block.</p>
 * <br>
 *     https://cwe.mitre.org/data/definitions/390.html
 */
public class ErrorConditionWithoutActionDetector extends PreorderVisitor implements Detector {

    private BugReporter bugReporter;
    // handlerPC map is consist of <method, List<catch block represented to bytecode order>>

    Map<String, List<Integer>> handlerPC;

    public ErrorConditionWithoutActionDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
        handlerPC = new HashMap<>();
    }

    @Override
    public void visit(CodeException obj) {
        int type = obj.getCatchType();
        // Remove type is zero. Zero means finally block, not catch block.
        if (type == 0) return;

        // Accumulate catch block program count for post-analysis.
        int target_inst = obj.getHandlerPC();
        String methodName = getClassName()+"."+getMethodName();
        if (handlerPC.containsKey(methodName)) {
            handlerPC.get(methodName).add(target_inst);
        } else {
            handlerPC.put(methodName, new ArrayList<>());
            handlerPC.get(methodName).add(target_inst);
        }
    }

    @Override
    public void visitClassContext(ClassContext classContext) {
        JavaClass javaClass = classContext.getJavaClass();
        Method[] methodList = javaClass.getMethods();

        javaClass.accept(this);

        for (Method m : methodList) {
            try {
                analyzeMethod(m, classContext);
            } catch (CFGBuilderException e) {
            }
        }
    }

    private void analyzeMethod(Method m, ClassContext classContext) throws CFGBuilderException {
        //System.out.println("==="+m.getName()+"===");

        CFG cfg = classContext.getCFG(m);
        // Do not need to analyze if there is no exception table.

        List<Integer> target_inst = handlerPC.get(getClassName()+'.'+m.getName());
        if(target_inst == null) return;

        Set<Integer> sourceCodeLine = new HashSet<>();
        LineNumberTable LNT = m.getLineNumberTable();
        if(LNT == null) return;

        // To remove duplicate alarm, store catch block as source code line number, not bytecode order.
        for (Integer pc : target_inst) {
            sourceCodeLine.add(new Integer(LNT.getSourceLine(pc)));
        }

        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            InstructionHandle handle = loc.getHandle();
            if(handle == null || handle.getNext() == null) continue;

            // Position in source code level.
            int position = LNT.getSourceLine(handle.getPosition());

            Instruction first_inst = handle.getInstruction();
            Instruction second_inst = handle.getNext().getInstruction();

            boolean reportFlag = false;
            if(sourceCodeLine.contains(position)) {
                // In general case, a catch block without an action is represented to (astore, aload) pattern.
                // If the catch block is in the last part of method, it shows (astore, goto end) pattern.
                if (first_inst instanceof ASTORE && second_inst instanceof ALOAD) {
                    if (handle.getNext().getNext() != null) {
                        Instruction third_inst = handle.getNext().getNext().getInstruction();
                        // If third instruction is ATHROW, it just throwing exception without a other action in catch block.
                        // This is delegating exception handling to other catch block. So, skip this case.
                        // If third instruction is INVOKEVIRTUAL,it call virtual method in catch block.
                        if (third_inst instanceof ATHROW || third_inst instanceof INVOKEVIRTUAL) continue;
                        reportFlag = true;
                    }
                }

                if (first_inst instanceof ASTORE &&
                        (second_inst instanceof RETURN || second_inst instanceof GOTO)) {
                    reportFlag = true;
                }

                if(reportFlag) {
                    JavaClass javaClass = classContext.getJavaClass();
                    bugReporter.reportBug(new BugInstance(this, "ERROR_CONDITION_WITHOUT_ACTION", Priorities.NORMAL_PRIORITY) //
                            .addClass(javaClass)
                            .addMethod(javaClass, m)
                            .addSourceLine(classContext, m, loc));
                    // Remove catch block for avoiding duplicated alarm
                    sourceCodeLine.remove(position);
                }
            }
        }
    }

    @Override
    public void report() {

    }
}
