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
 * Detects the usage of sensitive information without encryption.
 * Users could add proprietary method at sensitive-clear-text.txt
 * <br>
 *     http://cwe.mitre.org/data/definitions/256.html
 *     http://cwe.mitre.org/data/definitions/319.html
 *     http://cwe.mitre.org/data/definitions/614.html
 *
 * @author Dongyong Kim (Sparrow Co., Ltd.)
 */
public class SensitiveClearTextDetector extends BasicInjectionDetector {
    public SensitiveClearTextDetector(BugReporter bugReporter) {
        super(bugReporter);
        loadConfiguredSinks("sensitive-clear-text.txt", "SENSITIVE_CLEARTEXT");
    }

    @Override
    protected int getPriority(Taint taint) {
        if (taint.isSafe()) {
            return Priorities.IGNORE_PRIORITY;
        }
        return Priorities.HIGH_PRIORITY;
    }

}