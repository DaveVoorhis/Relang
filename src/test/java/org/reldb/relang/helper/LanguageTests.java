package org.reldb.relang.helper;

import org.reldb.relang.Main;
import org.reldb.relang.parser.ast.ParseException;

import java.lang.reflect.InvocationTargetException;

import static org.reldb.relang.helper.ToInputStream.toInputStream;

public class LanguageTests {
    protected Main.DebugModes debugMode = Main.DebugModes.NORMAL;

    protected void execute(String source) throws
            ParseException,
            ClassNotFoundException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchMethodException {
        var main = new Main();
        main.setDebugMode(debugMode);
        main.execute(toInputStream(source));
    }

    protected Object evaluate(String source) throws
            ParseException,
            ClassNotFoundException,
            InvocationTargetException,
            IllegalAccessException,
            NoSuchMethodException {
        var main = new Main();
        main.setDebugMode(debugMode);
        return main.evaluate(toInputStream(source));
    }
}
