/*
 * @(#) DummyObject5.java
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

import java.util.Objects;

import net.pwall.json.JSONException;
import net.pwall.json.JSONObject;
import net.pwall.json.JSONValue;

/**
 * Dummy object for testing JSON auto-serialization and deserialization.
 *
 * @author Peter Wall
 */
public class DummyObject5 {

    private int int1;

    public int getInt1() {
        return int1;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    @SuppressWarnings("unused")
    private JSONObject toJSON() {
        return JSONObject.create().putValue("dec", Integer.toString(int1)).
                putValue("hex", Integer.toHexString(int1).toUpperCase());
    }

    @SuppressWarnings("unused")
    private static DummyObject5 fromJSON(JSONValue json) {
        JSONObject jsonObject = (JSONObject)Objects.requireNonNull(json);
        int decValue = Integer.parseInt(jsonObject.getString("dec"));
        if (decValue != Integer.parseInt(jsonObject.getString("hex"), 16))
            throw new JSONException("Inconsistent values");
        DummyObject5 result = new DummyObject5();
        result.setInt1(decValue);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DummyObject5))
            return false;
        if (o == this)
            return true;
        return int1 == ((DummyObject5)o).int1;
    }

    @Override
    public int hashCode() {
        return int1;
    }

}
