package com.somshubra.sort;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 2    * Copyright (c) 9, 1, Oracle and/or its affiliates. All rights reserved.
 3    * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 4    *
 5    * This code is free software; you can redistribute it and/or modify it
 6    * under the terms of the GNU General Public License version 2 only, as
 7    * published by the Free Software Foundation.  Oracle designates this
 8    * particular file as subject to the "Classpath" exception as provided
 9    * by Oracle in the LICENSE file that accompanied this code.
 10    *
 11    * This code is distributed in the hope that it will be useful, but WITHOUT
 12    * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 13    * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 14    * version 2 for more details (a copy is included in the LICENSE file that
 15    * accompanied this code).
 16    *
 17    * You should have received a copy of the GNU General Public License version
 18    * 2 along with this work; if not, write to the Free Software Foundation,
 19    * Inc., 51 Franklin St, Fifth Floor, Boston, MA 10-1 USA.
 20    *
 21    * Please contact Oracle,  Oracle Parkway, Redwood Shores, CA 65 USA
 22    * or visit www.oracle.com if you need additional information or have any
 23    * questions.
 24    */

public class ParallelDualPivotQuickSort {
	private static int THRESHOLD = 0;
	private static int NO_OF_THREADS = Runtime.getRuntime()
			.availableProcessors();
	private static ExecutorService executor;

	private static AtomicInteger counter = new AtomicInteger(1);
	private static Object lock = new Object();

	private static final int MAX_RUN_COUNT = 67;
	private static final int MAX_RUN_LENGTH = 33;
	private static final int QUICKSORT_THRESHOLD = 286;
	private static final int INSERTION_SORT_THRESHOLD = 47;;

	private ParallelDualPivotQuickSort() {
	}

	public static void sort(int[] data) {
		if (NO_OF_THREADS == 1) {
			Arrays.sort(data);
			return;
		}

		THRESHOLD = data.length / NO_OF_THREADS;
		counter.set(1);
		executor = Executors.newCachedThreadPool();

		counter.getAndIncrement();
		executor.submit(quickSortCallable(data, 0, data.length - 1));

		synchronized (lock) {
			while (counter.get() > 1) {
				try {
					System.out.println("Lock Count : " + counter.get());
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static Runnable quickSortCallable(int[] a, int left, int right) {
		Runnable r = new InnerQuickSortCallable(a, left, right);
		return r;
	}

	private static Runnable quickSortCallable(int[] a, int left, int right,
			boolean leftmost) {
		Runnable r = new InnerQuickSortCallable2(a, left, right, leftmost);
		return r;
	}

	public static void sort(int data[], boolean considerate) {
		if (considerate) {
			if (data.length <= THRESHOLD) {
				Arrays.sort(data);
				return;
			}
		}
		sort(data);
	}

	public static void setParallelism(int noOfThreads) {
		NO_OF_THREADS = noOfThreads;
	}

	public static void resetParralelism() {
		NO_OF_THREADS = Runtime.getRuntime().availableProcessors();
	}

	private static class InnerQuickSortCallable implements Runnable {
		private int a[], left, right;

		public InnerQuickSortCallable(int a[], int left, int right) {
			this.a = a;
			this.left = left;
			this.right = right;
		}

		@Override
		public void run() {
			// Use Quicksort on small arrays
			if (right - left < QUICKSORT_THRESHOLD) {
				counter.getAndIncrement();
				executor.submit(quickSortCallable(a, left, right, true));
				return;
			}

			int[] run = new int[MAX_RUN_COUNT + 1];
			int count = 0;
			run[0] = left;

			// Check if the array is nearly sorte
			for (int k = left; k < right; run[count] = k) {
				if (a[k] < a[k + 1]) { // ascending
					while (++k <= right && a[k - 1] <= a[k])
						;
				} else if (a[k] > a[k + 1]) { // descending
					while (++k <= right && a[k - 1] >= a[k])
						;
					for (int lo = run[count] - 1, hi = k; ++lo < --hi;) {
						int t = a[lo];
						a[lo] = a[hi];
						a[hi] = t;
					}
				} else { // equal
					for (int m = MAX_RUN_LENGTH; ++k <= right
							&& a[k - 1] == a[k];) {
						if (--m == 0) {
							counter.getAndIncrement();
							executor.submit(quickSortCallable(a, left, right,
									true));
							return;
						}
					}
				}

				/*
				 * The array is not highly structured, use Quicksort instead of
				 * merge sort.
				 */
				if (++count == MAX_RUN_COUNT) {
					counter.getAndIncrement();
					executor.submit(quickSortCallable(a, left, right, true));
					return;
				}
			}

			// Check special cases
			if (run[count] == right++) { // The last run contains one element
				run[++count] = right;
			} else if (count == 1) { // The array is already sorted
				return;
			}

			/*
			 * Create temporary array, which is used for merging. Implementation
			 * note: variable "right" is increased by 1.
			 */
			int[] b;
			byte odd = 0;
			for (int n = 1; (n <<= 1) < count; odd ^= 1)
				;

			if (odd == 0) {
				b = a;
				a = new int[b.length];
				for (int i = left - 1; ++i < right; a[i] = b[i])
					;
			} else {
				b = new int[a.length];
			}

			// Merging
			for (int last; count > 1; count = last) {
				for (int k = (last = 0) + 2; k <= count; k += 2) {
					int hi = run[k], mi = run[k - 1];
					for (int i = run[k - 2], p = i, q = mi; i < hi; ++i) {
						if (q >= hi || p < mi && a[p] <= a[q]) {
							b[i] = a[p++];
						} else {
							b[i] = a[q++];
						}
					}
					run[++last] = hi;
				}
				if ((count & 1) != 0) {
					for (int i = right, lo = run[count - 1]; --i >= lo; b[i] = a[i])
						;
					run[++last] = right;
				}
				int[] t = a;
				a = b;
				b = t;
			}
			synchronized (lock) {
				counter.getAndDecrement();
				lock.notify();
			}
		}

	}

	private static class InnerQuickSortCallable2 implements Runnable {
		private int a[], left, right;
		private boolean leftmost;

		public InnerQuickSortCallable2(int a[], int left, int right,
				boolean leftmost) {
			this.a = a;
			this.left = left;
			this.right = right;
			this.leftmost = leftmost;
		}

		@Override
		public void run() {
			int length = right - left + 1;

			if (length < INSERTION_SORT_THRESHOLD) {
				if (leftmost) {
					for (int i = left, j = i; i < right; j = ++i) {
						int ai = a[i + 1];
						while (ai < a[j]) {
							a[j + 1] = a[j];
							if (j-- == left) {
								break;
							}
						}
						a[j + 1] = ai;
					}
				} else {
					do {
						if (left >= right) {
							return;
						}
					} while (a[++left] >= a[left - 1]);

					for (int k = left; ++left <= right; k = ++left) {
						int a1 = a[k], a2 = a[left];

						if (a1 < a2) {
							a2 = a1;
							a1 = a[left];
						}
						while (a1 < a[--k]) {
							a[k + 2] = a[k];
						}
						a[++k + 1] = a1;

						while (a2 < a[--k]) {
							a[k + 1] = a[k];
						}
						a[k + 1] = a2;
					}
					int last = a[right];

					while (last < a[--right]) {
						a[right + 1] = a[right];
					}
					a[right + 1] = last;
				}
				return;
			}

			int seventh = (length >> 3) + (length >> 6) + 1;

			int e3 = (left + right) >>> 1; // The midpoint
			int e2 = e3 - seventh;
			int e1 = e2 - seventh;
			int e4 = e3 + seventh;
			int e5 = e4 + seventh;

			if (a[e2] < a[e1]) {
				int t = a[e2];
				a[e2] = a[e1];
				a[e1] = t;
			}

			if (a[e3] < a[e2]) {
				int t = a[e3];
				a[e3] = a[e2];
				a[e2] = t;
				if (t < a[e1]) {
					a[e2] = a[e1];
					a[e1] = t;
				}
			}
			if (a[e4] < a[e3]) {
				int t = a[e4];
				a[e4] = a[e3];
				a[e3] = t;
				if (t < a[e2]) {
					a[e3] = a[e2];
					a[e2] = t;
					if (t < a[e1]) {
						a[e2] = a[e1];
						a[e1] = t;
					}
				}
			}
			if (a[e5] < a[e4]) {
				int t = a[e5];
				a[e5] = a[e4];
				a[e4] = t;
				if (t < a[e3]) {
					a[e4] = a[e3];
					a[e3] = t;
					if (t < a[e2]) {
						a[e3] = a[e2];
						a[e2] = t;
						if (t < a[e1]) {
							a[e2] = a[e1];
							a[e1] = t;
						}
					}
				}
			}

			// Pointers
			int less = left; // The index of the first element of center part
			int great = right; // The index before the first element of right
								// part

			if (a[e1] != a[e2] && a[e2] != a[e3] && a[e3] != a[e4]
					&& a[e4] != a[e5]) {
				int pivot1 = a[e2];
				int pivot2 = a[e4];

				a[e2] = a[left];
				a[e4] = a[right];

				while (a[++less] < pivot1)
					;
				while (a[--great] > pivot2)
					;

				outer: for (int k = less - 1; ++k <= great;) {
					int ak = a[k];
					if (ak < pivot1) {
						a[k] = a[less];
						a[less] = ak;
						++less;
					} else if (ak > pivot2) {
						while (a[great] > pivot2) {
							if (great-- == k) {
								break outer;
							}
						}
						if (a[great] < pivot1) {
							a[k] = a[less];
							a[less] = a[great];
							++less;
						} else {
							a[k] = a[great];
						}
						a[great] = ak;
						--great;
					}
				}

				a[left] = a[less - 1];
				a[less - 1] = pivot1;
				a[right] = a[great + 1];
				a[great + 1] = pivot2;

				// Sort left and right parts recursively, excluding known pivots
				counter.getAndIncrement();
				executor.submit(quickSortCallable(a, left, less - 2, leftmost));
				counter.getAndIncrement();
				executor.submit(quickSortCallable(a, great + 2, right, false));

				if (less < e1 && e5 < great) {
					while (a[less] == pivot1) {
						++less;
					}

					while (a[great] == pivot2) {
						--great;
					}

					outer: for (int k = less - 1; ++k <= great;) {
						int ak = a[k];
						if (ak == pivot1) { // Move a[k] to left part
							a[k] = a[less];
							a[less] = ak;
							++less;
						} else if (ak == pivot2) { // Move a[k] to right part
							while (a[great] == pivot2) {
								if (great-- == k) {
									break outer;
								}
							}
							if (a[great] == pivot1) { // a[great] < pivot2
								a[k] = a[less];
								a[less] = pivot1;
								++less;
							} else { // pivot1 < a[great] < pivot2
								a[k] = a[great];
							}
							a[great] = ak;
							--great;
						}
					}
				}

				// Sort center part recursively
				counter.getAndIncrement();
				executor.submit(quickSortCallable(a, less, great, false));
			} else {
				int pivot = a[e3];

				for (int k = less; k <= great; ++k) {
					if (a[k] == pivot) {
						continue;
					}
					int ak = a[k];
					if (ak < pivot) { // Move a[k] to left part
						a[k] = a[less];
						a[less] = ak;
						++less;
					} else {
						while (a[great] > pivot) {
							--great;
						}
						if (a[great] < pivot) { // a[great] <= pivot
							a[k] = a[less];
							a[less] = a[great];
							++less;
						} else {
							a[k] = pivot;
						}
						a[great] = ak;
						--great;
					}
				}
				counter.getAndIncrement();
				executor.submit(quickSortCallable(a, left, less - 1, leftmost));
				counter.getAndIncrement();
				executor.submit(quickSortCallable(a, great + 1, right, false));
			}

			synchronized (lock) {
				counter.getAndDecrement();
				lock.notify();
			}
		}

	}

}
