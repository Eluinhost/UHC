/*
 * Project: UHC
 * Class: gg.uhc.uhc.messages.SubsectionMessageTemplates
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

package gg.uhc.uhc.messages;

import com.github.mustachejava.Mustache;
import com.typesafe.config.Config;

import java.util.List;

public class SubsectionMessageTemplates implements MessageTemplates {

    protected final MessageTemplates parent;
    protected final String subPath;

    public SubsectionMessageTemplates(MessageTemplates parent, String subPath) {
        this.parent = parent;
        this.subPath = subPath + ".";
    }

    public MessageTemplates getParent() {
        return parent;
    }

    @Override
    public Config getConfig() {
        return parent.getConfig();
    }

    @Override
    public String getRaw(String key) {
        return parent.getRaw(subPath + key);
    }

    @Override
    public List<String> getRawStrings(String path) {
        return parent.getRawStrings(subPath + path);
    }

    @Override
    public Mustache getTemplate(String key) {
        return parent.getTemplate(subPath + key);
    }

    @Override
    public List<Mustache> getTemplates(String path) {
        return parent.getTemplates(subPath + path);
    }

    @Override
    public String evalTemplate(String key, Object... context) {
        return parent.evalTemplate(subPath + key, context);
    }

    @Override
    public List<String> evalTemplates(String path, Object... context) {
        return parent.evalTemplates(subPath + path, context);
    }

    public MessageTemplates getRoot() {
        return parent.getRoot();
    }
}
