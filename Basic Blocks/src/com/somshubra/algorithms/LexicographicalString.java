package com.somshubra.algorithms;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;
public class LexicographicalString {
	public static void main(String[] args) throws IOException{
		BufferedReader bb = new BufferedReader(new InputStreamReader(System.in));
		TreeSet<Integer> set = new TreeSet<>();
		
		int n = Integer.parseInt(bb.readLine());
		String input = bb.readLine();
		String ip[] = input.split(" ");
		int intBuffer = -1;
		int init = Integer.parseInt(ip[0]);
		set.add(init);
		
		ArrayList<TreeSet<Integer>> resultsList = new ArrayList<>();
		resultsList.add(new TreeSet<Integer>());
		resultsList.get(0).add(init);
		int resultIndex = 0;
		
		for(int i = 1; i < n; i++) {
			intBuffer = Integer.parseInt(ip[i]);
			if(intBuffer >= set.last()) {
				set.add(intBuffer);
			}
			else {
				resultIndex++;
				resultsList.add(new TreeSet<Integer>());
				
					while(set.size() > 0 && set.last() > intBuffer) set.pollLast();
				set.add(intBuffer);
			}
			resultsList.get(resultIndex).add(intBuffer);
		}
		StringBuffer result = new StringBuffer();
		int max = -1, position = -1;
		TreeSet<Integer> list;
		for(int i = 0; i < resultIndex + 1; i++) {
			list = resultsList.get(i);
			if(list.size() > max) {
				max = list.size();
				position = i;
			}
		}
		for(Integer x : resultsList.get(position)) result.append(x + " ");
		System.out.println(result.toString().trim());
	}
}
