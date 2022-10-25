package org.reldb.relang;

import org.junit.jupiter.api.Test;
import org.reldb.relang.helper.LanguageTests;

class FnTests extends LanguageTests {
    @Test
    void simpleProcedureWithReturn() throws Exception {
        var source =
                "p5 = 10\n" +
                "\n" +
                "test(long p1, long p2, long p3) -> {\n" +
                "\twrite p1\n" +
                "\twrite p2\n" +
                "\twrite p3\n" +
                "\tp4 = 2\n" +
                "\treturn p1 * p2 * p3 * p4 * p5\n" +
                "}\n" +
                "\n" +
                "write test(3, 4, 5)\n" +
                "\n" +
                "test(3, 4, 5)\n";

        execute(source);
    }
    
    @Test
    void procedureWithLoop() throws Exception {
        var source = 
                "p = 3\n" +
                "\n" +
                "blah(long start, long fin) -> {\n" +
                "\tfor (i=start; i<=fin; i=i+1)\n" +
                "\t\twrite i\n" +
                "\twrite p + 2\n" +
                "}\n" +
                "\n" +
                "blah(5, 10)\n";
        
        execute(source);
    }
    
    @Test
    void functionsNested1() throws Exception {
        var source = 
                "p = 3\n" +
                "\n" +
                "blah(long start, long fin) -> {\n" +
                "\n" +
                "\tzot(long x, long y) -> {\n" +
                "\t   \tzaz(long r) -> p + x * r\n" +
                "\n" +
                "\t\treturn x * y + zaz(y)\n" +
                "\t}\n" +
                "\n" +
                "\tfor (i=start; i<=fin; i=i+1) {\n" +
                "\t\twrite zot(i, p)\n" +
                "\t}\n" +
                "\n" +
                "\twrite p * 1000\n" +
                "}\n" +
                "\n" +
                "blah(5, 10)\n";
        
        execute(source);
    }
    
    @Test
    void functionsNested2() throws Exception {
        var source = 
                "z = 2\n" +
                "\n" +
                "zaz(long a) -> a\n" +
                "\n" +
                "blah(long p) -> {\n" +
                "   q = 3\n" +
                "   zot(long r) -> zaz(r + p + q + z)\n" +
                "\n" +
                "   zog(long n) -> zot(n + 2)\n" +
                "\n" +
                "   return zot(p) * zog(p)\n" +
                "}\n" +
                "\n" +
                "v = 3\n" +
                "\n" +
                "write blah(v)\n";
        
        execute(source);
    }

    @Test
    void recursion1() throws Exception {
        var source =
                "fib(long n) -> {\n" +
                        "   if (n == 0 or n == 1) {\n" +
                        "      r = n\n" +
                        "   } else {\n" +
                        "      r = fib(n - 1) + fib(n - 2)\n" +
                        "   }\n" +
                        "   return r\n" +
                        "}\n" +
                        "\n" +
                        "for (i = 0; i < 35; i = i + 1) {\n" +
                        "    write fib(i)\n" +
                        "}";

        execute(source);
    }

    @Test
    void recursion2() throws Exception {
        var source =
                "sum(long n) -> {\n" +
                "   if (n > 0)\n" +
                "      \tp = n + sum(n - 1)\n" +
                "   else\n" +
                "\t    p = 0\n" +
                "   return p\n" +
                "}\n" +
                "\n" +
                "write sum(5)\n" +
                "\n";

        execute(source);
    }
}
