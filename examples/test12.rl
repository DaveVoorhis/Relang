z = 2

fn zaz(long a) {
   return a
}

fn blah(long p) {
   q = 3
   fn zot(long r) {
      return zaz(r + p + q + z)
   }

   fn zog(long n) {
      return zot(n + 2)
   }

   return zot(p) * zog(p)
}

v = 3

write blah(v)
