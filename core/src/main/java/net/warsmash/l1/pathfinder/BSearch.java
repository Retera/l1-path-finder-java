package net.warsmash.l1.pathfinder;

import java.util.List;

public class BSearch {
	public static interface SearchIntervalFunc<T, Y> {
		int call(T t, Y y);
	}

	private static <T, Y> int searchInternal(List<T> a, int l, int h, Y y, SearchIntervalFunc<T, Y> c) {
		int i = l - 1;
		while (l <= h) {
			int m = (int) ((long) (l + h) >> 1);
			T x = a.get(m);
			if (c.call(x, y) < 0) {
				i = m;
				l = m + 1;
			} else {
				h = m - 1;
			}
		}
		return i;
	}

	public static <T, Y> int search(List<T> a, Y y, SearchIntervalFunc<T, Y> c) {
		return searchInternal(a, 0, a.size() - 1, y, c);
	}
}
