package org.reldb.relang;

import org.junit.jupiter.api.Test;
import org.reldb.relang.helper.LanguageTests;

class CoreTests extends LanguageTests {
    @Test
    void forLoopAndWriteWithBraces() throws Exception {
        var source =
                "for (i=1; i<10; i=i+1) {\n" +
                "  write i\n" +
                "}";

        execute(source);
    }

    @Test
    void forLoopAndWriteWithBracesAndSpaces() throws Exception {
        var source =
                "for (i = 1; i<20; i=i+1) {\n" +
                "   write i\n" +
                "}";

        execute(source);
    }

    @Test
    void forLoopAndWriteNoBraces() throws Exception {
        var source =
                "for (i=10; i>=0; i=i-1)\n" +
                "  write i";

        execute(source);
    }

    @Test
    void nestedLoop() throws Exception {
        var source =
                "for (i = 1; i<20; i=i+1) {\n" +
                "  write i\n" +
                "  for (j = 1; j<20; j=j+1) {\n" +
                "   write i * j\n" +
                "  }\n" +
                "}\n";

        execute(source);
    }

    @Test
    void ifStatements() throws Exception {
        var source =
                "if (false) {\n" +
                "\twrite 1\n" +
                "\twrite 2\n" +
                "\twrite 3\n" +
                "} else {\n" +
                "\twrite 4\n" +
                "\twrite 5\n" +
                "\twrite 6\n" +
                "}\n" +
                "\n" +
                "if (true) {\n" +
                "\twrite 11\n" +
                "\twrite 12\n" +
                "\twrite 13\n" +
                "} else {\n" +
                "\twrite 14\n" +
                "\twrite 15\n" +
                "\twrite 16\n" +
                "}\n";

        execute(source);
    }

    @Test
    void blocks() throws Exception {
        var source =
                "write 1\n" +
                "write 2\n" +
                "write 3\n" +
                "\n" +
                "{\n" +
                "\twrite 4\n" +
                "\twrite 5\n" +
                "\twrite 6\n" +
                "}\n";

        execute(source);
    }

    @Test
    void writesAndExpressions() throws Exception {
        var source = 
                "write 2 + 2\n" +
                "write 5 - 3\n" +
                "write 2 + 2 > 3 + 3\n" +
                "write 77 * 23\n" +
                "write 10 / 2\n" +
                "\n" +
                "a = 7\n" +
                "b = 4\n" +
                "write a\n" +
                "write b\n" +
                "write a * b";
        
        execute(source);
    }

    @Test
    void longLoop() throws Exception {
        var source =
                "write 0\n" +
                "for (i=0; i<100000000; i=i+1) {}\n" +
                "write 1";

        execute(source);
    }
}
