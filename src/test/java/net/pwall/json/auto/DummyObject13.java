/*
 * @(#) DummyObject13.java
 *
 * jsonauto JSON Auto-serialization Library
 * Copyright (c) 2017 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json.auto;

import java.util.Objects;
import java.util.OptionalInt;

import net.pwall.json.annotation.JSONAlways;

/**
 * Dummy object for testing use of {@link OptionalInt} combined with {@link JSONAlways}.
 *
 * @author Peter Wall
 */
public class DummyObject13 {

    @JSONAlways
    private OptionalInt value1 = OptionalInt.empty();

    public int getValue1() {
        return value1.getAsInt();
    }

    public boolean isValue1Present() {
        return value1.isPresent();
    }

    public void setValue1(int value1) {
        this.value1 = OptionalInt.of(value1);
    }

    public void setValue1Empty() {
        this.value1 = OptionalInt.empty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof DummyObject13))
            return false;
        DummyObject13 otherObject = (DummyObject13)other;
        return Objects.equals(value1, otherObject.value1);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value1);
    }

}
