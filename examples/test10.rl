p = 3

fn blah(long start, long fin) {

	fn zot(long x, long y) {
	   	fn zaz(long r) {
		   return p + x * r
		}

		return x * y + zaz(y)
	}

	for (i=start; i<=fin; i=i+1) {
		write zot(i, p)
	}

	write p * 1000
}

blah(5, 10)
