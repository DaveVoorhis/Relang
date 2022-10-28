package org.reldb.relang;

import org.junit.jupiter.api.Test;
import org.reldb.relang.helper.LanguageTests;

class FnTests extends LanguageTests {
    @Test
    void simpleProcedureWithReturn() throws Exception {
        var source = """
                p5 = 10
                
                test(long p1, long p2, long p3) -> {
                   write p1
                   write p2
                   write p3
                   p4 = 2
                   return p1 * p2 * p3 * p4 * p5
                }
                
                write test(3, 4, 5)
                
                test(3, 4, 5)
                """;

        execute(source);
    }
    
    @Test
    void procedureWithLoop() throws Exception {
        var source = """ 
                p = 3
                
                blah(long start, long fin) -> {
                   for (i=start; i<=fin; i=i+1)
                      write i
                   write p + 2
                }
                
                blah(5, 10)
                """;
        
        execute(source);
    }
    
    @Test
    void functionsNested1() throws Exception {
        var source = """ 
                p = 3
                
                blah(long start, long fin) -> {
                
                   zot(long x, long y) -> {
                         zaz(long r) -> p + x * r
                
                      return x * y + zaz(y)
                   }
                
                   for (i=start; i<=fin; i=i+1) {
                      write zot(i, p)
                   }
                
                   write p * 1000
                }
                
                blah(5, 10)
                """;
        
        execute(source);
    }
    
    @Test
    void functionsNested2() throws Exception {
        var source = """ 
                z = 2
                 
                zaz(long a) -> a
                 
                blah(long p) -> {
                   q = 3
                   zot(long r) -> zaz(r + p + q + z)
                
                   zog(long n) -> zot(n + 2)
                
                   return zot(p) * zog(p)
                }
                
                v = 3
                
                write blah(v)
                """;
        
        execute(source);
    }

    @Test
    void recursion1() throws Exception {
        var source = """
                fib(long n) -> {
                   if (n == 0 or n == 1) {
                      r = n
                   } else {
                      r = fib(n - 1) + fib(n - 2)
                   }
                   return r
                }
                        
                for (i = 0; i < 35; i = i + 1) {
                    write fib(i)
                }
                """;

        execute(source);
    }

    @Test
    void recursion2() throws Exception {
        var source = """
                sum(long n) -> {
                   if (n > 0)
                         p = n + sum(n - 1)
                   else
                       p = 0
                   return p
                }
                
                write sum(5)
                """;

        execute(source);
    }
}
