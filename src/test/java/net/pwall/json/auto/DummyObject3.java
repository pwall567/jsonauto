/*
 * @(#) DummyObject3.java
 *
 * jsonauto JSON Auto-serialization Library
 * Copyright (c) 2015, 2016 Peter Wall
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

import java.util.Arrays;
import java.util.Objects;

/**
 * Dummy object for testing JSON auto-serialization and deserialization.
 *
 * @author Peter Wall
 */
public class DummyObject3 {

    private String string1;
    private Integer integer1;
    private DummyObject dummy1;
    private int[] array1;

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public Integer getInteger1() {
        return integer1;
    }

    public void setInteger1(Integer integer1) {
        this.integer1 = integer1;
    }

    public DummyObject getDummy1() {
        return dummy1;
    }

    public void setDummy1(DummyObject dummy1) {
        this.dummy1 = dummy1;
    }

    public int[] getArray1() {
        return array1;
    }

    public void setArray1(int[] array1) {
        this.array1 = array1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DummyObject3))
            return false;
        if (this == obj)
            return true;
        DummyObject3 obj3 = (DummyObject3)obj;
        if (!Objects.equals(string1, obj3.string1))
            return false;
        if (!Objects.equals(integer1, obj3.integer1))
            return false;
        if (!Objects.equals(dummy1, obj3.dummy1))
            return false;
        if (!Arrays.equals(array1, obj3.array1))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = string1 == null ? 0 : string1.hashCode();
        if (integer1 != null)
            result ^= integer1.hashCode();
        if (dummy1 != null)
            result ^= dummy1.hashCode();
        if (array1 != null)
            result ^= Arrays.hashCode(array1);
        return result;
    }

}
