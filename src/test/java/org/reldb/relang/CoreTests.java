package org.reldb.relang;

import org.junit.jupiter.api.Test;
import org.reldb.relang.helper.LanguageTests;

class CoreTests extends LanguageTests {
    @Test
    void forLoopAndWriteWithBraces() throws Exception {
        var source = """
                for (i=1; i<10; i=i+1) {
                  write i
                }
                """;

        execute(source);
    }

    @Test
    void forLoopAndWriteWithBracesAndSpaces() throws Exception {
        var source = """
                for (i = 1; i<20; i=i+1) {
                   write i
                }
                """;

        execute(source);
    }

    @Test
    void forLoopAndWriteNoBraces() throws Exception {
        var source = """
                for (i=10; i>=0; i=i-1)
                  write i
                """;

        execute(source);
    }

    @Test
    void nestedLoop() throws Exception {
        var source = """
                for (i = 1; i<20; i=i+1) {
                  write i
                  for (j = 1; j<20; j=j+1) {
                   write i * j
                  }
                }
                """;
        
        execute(source);
    }

    @Test
    void ifStatements() throws Exception {
        var source = """
                if (false) {
                   write 1
                   write 2
                   write 3
                } else {
                   write 4
                   write 5
                   write 6
                }
                
                if (true) {
                   write 11
                   write 12
                   write 13
                } else {
                   write 14
                   write 15
                   write 16
                }
                """;
        
        execute(source);
    }

    @Test
    void blocks() throws Exception {
        var source = """
                write 1
                write 2
                write 3
                
                {
                   write 4
                   write 5
                   write 6
                }
                """;
        
        execute(source);
    }

    @Test
    void writesAndExpressions() throws Exception {
        var source = """
                write 2 + 2
                write 5 - 3
                write 2 + 2 > 3 + 3
                write 77 * 23
                write 10 / 2
                
                a = 7
                b = 4
                write a
                write b
                write a * b
                """;
        
        execute(source);
    }

    @Test
    void longLoop() throws Exception {
        var source = """
                write 0
                for (i=0; i<100000000; i=i+1) {}
                   write 1
                """;

        execute(source);
    }
}
