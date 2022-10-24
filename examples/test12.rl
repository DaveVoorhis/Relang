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
