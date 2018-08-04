package compiling2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

class Test {
	
	
	public static void main(String[] args)
	{
		String str="i+i*i#";
		ArrayList<String> input = new ArrayList<String>();//输入内容(产生式集合)
		HashMap<Character,TreeSet<Character>> firstMap = new HashMap<Character,TreeSet<Character>>();
		HashMap<Character,TreeSet<Character>> followMap = new HashMap<Character,TreeSet<Character>>();
		input.add("E->TA");
		input.add("A->+TA");
		input.add("A->ε");
		input.add("T->FB");
		input.add("B->*FB");
		input.add("B->ε");
		input.add("F->i");
		input.add("F->(E)");
		TreeSet<Character> first1 = new TreeSet<Character>();
		TreeSet<Character> first2 = new TreeSet<Character>();
		TreeSet<Character> first3 = new TreeSet<Character>();
		TreeSet<Character> first4 = new TreeSet<Character>();
		TreeSet<Character> first5 = new TreeSet<Character>();
		
		first1.add('(');
		first1.add('i');
		
		first2.add('+');
		first2.add('ε');
		
		first3.add('(');
		first3.add('i');
		
		first4.add('*');
		first4.add('ε');
		
		first5.add('(');
		first5.add('i');
		
		firstMap.put('E',first1);
		firstMap.put('A',first2);
		firstMap.put('T',first3);
		firstMap.put('B',first4);
		firstMap.put('F',first5);
		
		
		TreeSet<Character> follow1 = new TreeSet<Character>();
		TreeSet<Character> follow2 = new TreeSet<Character>();
		TreeSet<Character> follow3 = new TreeSet<Character>();
		TreeSet<Character> follow4 = new TreeSet<Character>();
		TreeSet<Character> follow5 = new TreeSet<Character>();
		
		follow1.add(')');
		follow1.add('#');
		
		follow2.add(')');
		follow2.add('#');
		
		follow3.add('+');
		follow3.add(')');
		follow3.add('#');

		follow4.add('+');
		follow4.add(')');
		follow4.add('#');
		
		follow5.add('*');
		follow5.add('+');
		follow5.add(')');
		follow5.add('#');
		followMap.put('E',follow1);
		followMap.put('A',follow2);
		followMap.put('T',follow3);
		followMap.put('B',follow4);
		followMap.put('F',follow5);
		
		yucefenxi test = new yucefenxi(str,input,firstMap,followMap);
		test.getNvNt();
	}
}