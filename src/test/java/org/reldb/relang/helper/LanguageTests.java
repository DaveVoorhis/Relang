package org.reldb.relang.helper;

import org.reldb.relang.Main;
import org.reldb.relang.parser.ast.ParseException;

import java.lang.reflect.InvocationTargetException;

import static org.reldb.relang.helper.ToInputStream.toInputStream;

public class LanguageTests {
    protected void execute(String source) throws
            ParseException,
            ClassNotFoundException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchMethodException {
        new Main().execute(toInputStream(source));
    }
}
