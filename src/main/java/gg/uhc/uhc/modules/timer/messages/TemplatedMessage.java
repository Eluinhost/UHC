/*
 * Project: UHC
 * Class: gg.uhc.uhc.modules.timer.messages.TemplatedMessage
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

package gg.uhc.uhc.modules.timer.messages;

import com.github.mustachejava.Mustache;
import com.google.common.collect.ImmutableMap;
import gg.uhc.uhc.util.TimeUtil;

import java.io.StringWriter;

public class TemplatedMessage implements TimerMessage {

    protected final Mustache template;
    protected final String message;

    public TemplatedMessage(Mustache template, String message) {
        this.template = template;
        this.message = message;
    }

    @Override
    public String getMessage(long secondsRemaining) {
        StringWriter writer = new StringWriter();
        template.execute(writer, ImmutableMap.of("message", message, "timer", TimeUtil.secondsToString(secondsRemaining)));
        return writer.getBuffer().toString();
    }
}
