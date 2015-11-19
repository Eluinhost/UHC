/*
 * Project: UHC
 * Class: gg.uhc.uhc.messages.BaseMessageTemplates
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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

public class BaseMessageTemplates implements MessageTemplates {

    protected final Config config;
    protected final MustacheFactory templating;
    protected final Map<String, Mustache> templates;

    public BaseMessageTemplates(Config config) {
        this.config = config;
        this.templating = new DefaultMustacheFactory();
        this.templates = Maps.newHashMap();
    }

    @Override
    public Config getConfig() {
        return config;
    }

    @Override
    public String getRaw(String key) {
        return config.getString(key);
    }

    @Override
    public Mustache getTemplate(String key) {
        Mustache template = templates.get(key);

        // compile the tempalte from the config value and store it in our map
        if (template == null) {
            template = templating.compile(new StringReader(config.getString(key)), key);
            templates.put(key, template);
        }

        return template;
    }

    @Override
    public String evalTemplate(String key, Object... context) {
        Mustache template = getTemplate(key);

        StringWriter writer = new StringWriter();
        template.execute(writer, context);

        return writer.getBuffer().toString();
    }
}
