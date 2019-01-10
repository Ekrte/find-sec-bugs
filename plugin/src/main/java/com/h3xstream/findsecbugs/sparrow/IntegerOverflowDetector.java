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

import com.h3xstream.findsecbugs.injection.BasicInjectionDetector;
import com.h3xstream.findsecbugs.taintanalysis.Taint;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.Priorities;

/**
 * Calculating integer value with a external value such as environment variable could cause integer overflow.
 * This detector is experimental.
 * <br>
 *     http://cwe.mitre.org/data/definitions/190.html
 *
 * @author Dongyong Kim (Sparrow Co., Ltd.)
 */
public class IntegerOverflowDetector extends BasicInjectionDetector {

    public IntegerOverflowDetector(BugReporter bugReporter) {
        super(bugReporter);
        setEnableIntegerOverflow();
    }

    @Override
    protected int getPriority(Taint taint) {
        if (taint.hasTag(Taint.Tag.INTEGER_OVERFLOW_SAFE) || taint.isSafe()) {
            return Priorities.IGNORE_PRIORITY;
        }
        return Priorities.HIGH_PRIORITY;
    }
}