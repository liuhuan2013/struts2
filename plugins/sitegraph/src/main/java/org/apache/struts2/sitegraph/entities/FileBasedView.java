/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.struts2.sitegraph.entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.sitegraph.model.Link;

/**
 */
public abstract class FileBasedView implements View {
    private String name;
    private String contents;

    private static final Log log = LogFactory.getLog(FileBasedView.class);

    public FileBasedView(File file) {
        this.name = file.getName();
        // get the contents as a single line
        this.contents = readFile(file).replaceAll("[\r\n ]+", " ");
    }

    public String getName() {
        return name;
    }

    public Set getTargets() {
        TreeSet targets = new TreeSet();

        // links
        matchPatterns(getLinkPattern(), targets, Link.TYPE_HREF);

        // actions
        matchPatterns(getActionPattern(), targets, Link.TYPE_ACTION);

        // forms
        matchPatterns(getFormPattern(), targets, Link.TYPE_FORM);

        return targets;
    }

    protected Pattern getLinkPattern() {
        // FIXME: work with new configuration style
        //Object ext = Settings.get(StrutsConstants.STRUTS_ACTION_EXTENSION);
        String ext = "action";
        String actionRegex = "([A-Za-z0-9\\._\\-\\!]+\\." + ext + ")";
        return Pattern.compile(actionRegex);
    }

    private void matchPatterns(Pattern pattern, Set targets, int type) {
        Matcher matcher = pattern.matcher(contents);
        while (matcher.find()) {
            String target = matcher.group(1);
            targets.add(new Target(target, type));
        }
    }

    protected abstract Pattern getActionPattern();

    protected abstract Pattern getFormPattern();

    protected String readFile(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            String s = new String();
            StringBuffer buffer = new StringBuffer();

            while ((s = in.readLine()) != null) {
                buffer.append(s + "\n");
            }

            in.close();

            return buffer.toString();
        } catch (FileNotFoundException e) {
            log.warn("File not found");
        } catch (IOException e) {
            log.error(e);
        }

        return null;
    }
}
