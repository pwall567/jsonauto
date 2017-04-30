/*
 * @(#) DummyObject17.java
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
import java.util.OptionalDouble;

import net.pwall.json.annotation.JSONAlways;

/**
 * Dummy object for testing use of {@link OptionalDouble} combined with {@link JSONAlways}.
 *
 * @author Peter Wall
 */
public class DummyObject17 {

    @JSONAlways
    private OptionalDouble value1 = OptionalDouble.empty();

    public double getValue1() {
        return value1.getAsDouble();
    }

    public boolean isValue1Present() {
        return value1.isPresent();
    }

    public void setValue1(double value1) {
        this.value1 = OptionalDouble.of(value1);
    }

    public void setValue1Empty() {
        this.value1 = OptionalDouble.empty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof DummyObject17))
            return false;
        DummyObject17 otherObject = (DummyObject17)other;
        return Objects.equals(value1, otherObject.value1);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value1);
    }

}
