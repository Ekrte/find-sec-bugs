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
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Detects iteration or loop with an exit condition that cannot be reached.
 * </br>
 *     https://cwe.mitre.org/data/definitions/835.html
 *
 * @author Dongyong Kim (Sparrow Co., Ltd.)
 */
public class InfiniteLoopPatternDetector implements Detector {

    private static final String INFINITE_LOOP_PATTERN = "INFINITE_LOOP_PATTERN";
    private BugReporter bugReporter;


    public InfiniteLoopPatternDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    /**
     * Check this condition is unreachable condition or not.
     *
     * @param current_pos the position of current opcode
     * @return true if the condition is unreachable
     */
    private boolean checkInfiniteLoop(Map<Integer, Integer> current_to_target, int current_pos) {
        // if all target_pos are smaller than the current pos, there is no exit condition in loop.
        for(int target_pos : current_to_target.values()) {
            if(target_pos > current_pos) {
                return false;
            }
        }
        return true;
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
        Map<Integer, Integer> current_to_target = new HashMap<>();

        for (Iterator<Location> i = cfg.locationIterator(); i.hasNext(); ) {
            Location loc = i.next();
            Instruction inst = loc.getHandle().getInstruction();
            int current_pos = loc.getHandle().getPosition();

            if(inst instanceof IfInstruction) {
                IfInstruction ifinst = (IfInstruction) inst;
                current_to_target.put(current_pos, ifinst.getTarget().getPosition());

                if(checkInfiniteLoop(current_to_target, current_pos)) {
                    JavaClass javaClass = classContext.getJavaClass();

                    bugReporter.reportBug(new BugInstance(this, INFINITE_LOOP_PATTERN, Priorities.HIGH_PRIORITY) //
                            .addClass(javaClass)
                            .addMethod(javaClass, m)
                            .addSourceLine(classContext, m, loc));
                    break;
                }
            }

            if(inst instanceof GOTO) {
                GOTO go_to = (GOTO)inst;
                current_to_target.put(current_pos, go_to.getTarget().getPosition());

                if(checkInfiniteLoop(current_to_target, current_pos)) {
                    JavaClass javaClass = classContext.getJavaClass();

                    bugReporter.reportBug(new BugInstance(this, INFINITE_LOOP_PATTERN, Priorities.HIGH_PRIORITY) //
                            .addClass(javaClass)
                            .addMethod(javaClass, m)
                            .addSourceLine(classContext, m, loc));
                    break;
                }
            }

            if(inst instanceof IFLT) {
                IFLT iflt_inst = (IFLT)inst;
                InstructionHandle target_handle = iflt_inst.getTarget();

                boolean exit_condition = false;

                for(InstructionHandle handle = loc.getHandle(); handle != target_handle; handle = handle.getNext()) {
                    Instruction _inst = handle.getInstruction();
                    // TODO: check the operand of ISUB is the variable which determine the condition.
                    if(_inst instanceof ISUB || _inst instanceof IF_ICMPNE || _inst instanceof IF_ICMPEQ) {
                        exit_condition = true;
                    }
                }
                if(!exit_condition) {
                    JavaClass javaClass = classContext.getJavaClass();

                    bugReporter.reportBug(new BugInstance(this, INFINITE_LOOP_PATTERN, Priorities.HIGH_PRIORITY) //
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
