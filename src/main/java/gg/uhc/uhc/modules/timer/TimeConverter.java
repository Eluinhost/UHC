/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.TimeConverter
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Graham Howden <graham_howden1 at yahoo.co.uk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package gg.uhc.uhc.modules.timer;


import gg.uhc.flagcommands.joptsimple.ValueConversionException;
import gg.uhc.flagcommands.joptsimple.ValueConverter;
import gg.uhc.uhc.util.TimeUtil;

public class TimeConverter implements ValueConverter<Long> {

    @Override
    public Long convert(String value) {
        final long seconds = TimeUtil.getSeconds(value);

        if (seconds == 0) {
            throw new ValueConversionException("Invalid time supplied, must supply using time format and must be > 0");
        }

        return seconds;
    }

    @Override
    public Class<Long> valueType() {
        return Long.class;
    }

    @Override
    public String valuePattern() {
        return "Time string e.g. 12m30s";
    }
}
