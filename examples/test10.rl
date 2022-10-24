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
