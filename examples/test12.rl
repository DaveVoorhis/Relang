z = 2

zaz(long a) -> {
   return a
}

blah(long p) -> {
   q = 3
   zot(long r) -> return zaz(r + p + q + z)

   zog(long n) -> return zot(n + 2)

   return zot(p) * zog(p)
}

v = 3

write blah(v)
