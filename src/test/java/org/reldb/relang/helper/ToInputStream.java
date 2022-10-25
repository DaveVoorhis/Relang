package org.reldb.relang.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ToInputStream {
    public static InputStream toInputStream(String text) {
        return new ByteArrayInputStream(text.getBytes());
    }
}
