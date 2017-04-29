/*
 * @(#) DummyObject11.java
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

import java.util.Optional;

import net.pwall.json.annotation.JSONAlways;

/**
 * Dummy object for testing use of {@link Optional} combined with {@link JSONAlways}.
 *
 * @author Peter Wall
 */
public class DummyObject11 {

    @JSONAlways
    private Optional<String> value1 = Optional.empty();

    public String getValue1() {
        return value1.get();
    }

    public boolean isValue1Present() {
        return value1.isPresent();
    }

    public void setValue1(String value1) {
        this.value1 = Optional.of(value1);
    }

    public void setValue1Empty() {
        this.value1 = Optional.empty();
    }

}
