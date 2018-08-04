package compiling2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

public class yucefenxi {
	/**
	 * LL(1)文法集合
	 */
	ArrayList<String> input = new ArrayList<String>();// 输入内容(产生式集合)

	/**
	 * 表达式集合 ：字符 + 表达式（0个或多个）
	 */
	HashMap<Character, ArrayList<String>> expMap = new HashMap<Character, ArrayList<String>>();

	/**
	 * first集合
	 */

	HashMap<Character, TreeSet<Character>> firstMap = new HashMap<Character, TreeSet<Character>>();
	/**
	 * follow集合
	 */

	HashMap<Character, TreeSet<Character>> followMap = new HashMap<Character, TreeSet<Character>>();
	/**
	 * select集合, character非终结符 string表达式 treeset:select集合
	 */
	TreeMap<Character, HashMap<String, TreeSet<Character>>> selectMap = new TreeMap<Character, HashMap<String, TreeSet<Character>>>();

	// index+栈+剩余串+输出匹配
	ArrayList<Bean> analyzeProduces = new ArrayList<Bean>();
	/**
	 * LL(1)文法
	 */
	Character startChar = 'E';
	/**
	 * 分析栈
	 */
	Stack<Character> analyzeStack = new Stack<Character>();
	/**
	 * 剩余输入串
	 */
	String str;
	/**
	 * 产生式右部
	 */
	String useExp;
	/**
	 * nvSet非终结符集合
	 */
	TreeSet<Character> feizhongjiefuSet = new TreeSet<Character>();
	/**
	 * ntSet终结符集合
	 */
	TreeSet<Character> zhongjiefuSet = new TreeSet<Character>();

	/**
	 * 计算终结符和非终结符
	 */
	public yucefenxi(String str, ArrayList<String> input, HashMap<Character, TreeSet<Character>> firstMap,
			HashMap<Character, TreeSet<Character>> followMap) {
		this.str = str;
		this.input = input;
		this.firstMap = firstMap;
		this.followMap = followMap;
	}

	public void getNvNt() {

		for (String item : input) {
			String[] nvNtItem = item.split("->");
			String charItemStr = nvNtItem[0];
			char charItem = charItemStr.charAt(0);
			// 非终结符nv在左边
			feizhongjiefuSet.add(charItem);
		}
		for (String item : input) {
			String[] nvNtItem = item.split("->");
			// nt在右边
			String nvItemStr = nvNtItem[1];
			// 遍历每一个字
			for (int i = 0; i < nvItemStr.length(); i++) {
				char charItem = nvItemStr.charAt(i);
				if (!feizhongjiefuSet.contains(charItem)) {
					zhongjiefuSet.add(charItem);
				}
			}
		}

		expressionMap();
	}

	/**
	 * 得到一个表达式集合 Character + 表达式右部链表集合
	 * 
	 */
	public void expressionMap() {
		for (String item : input) {
			String[] nvNtItem = item.split("->");
			String left = nvNtItem[0];
			String right = nvNtItem[1];
			char leftChar = left.charAt(0);
			if (!expMap.containsKey(leftChar)) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(right);
				expMap.put(leftChar, list);
			} else {
				ArrayList<String> list = expMap.get(leftChar);
				list.add(right);
				expMap.put(leftChar, list);
			}
		}

		getSelect();
	}

	public void getSelect() {
		// 首先得到表达式expressionMap的键Character
		// 然后通过表达式中的键找到表达式中的链表ArrayList；
		// 对每一个链表求select集合selectSet用来存放Character
		Set<Character> key = expMap.keySet();
		for (Character noneKey : key) {
			// 通过循环遍历key值得到,每一个链表值
			ArrayList<String> arrayList = expMap.get(noneKey);
			// 对每一个表达式右部String和表达式select集合，构造一个HashMap存放结果
			HashMap<String, TreeSet<Character>> selectItem = new HashMap<String, TreeSet<Character>>();
			for (String select : arrayList) {

				/*
				 * 定义一个TreeSet用来存放select集合的结果
				 */
				TreeSet<Character> selectSet = new TreeSet<Character>();
				// select集分为三种情况 1.若右部产生式是非终结符，则select集是右部产生式的first集合;
				// 2.若右部产生式是终结符，则select集是右部产生式的终结符;
				// 3.若右部产生式是空串，则select集是右边部产生式的follow集;

				if (isTerminal(zhongjiefuSet, select)) {
					char a = select.charAt(0);
					selectSet.add(a);
					selectSet.remove('ε');
					selectItem.put(select, selectSet);
				}
				if (isNone(feizhongjiefuSet, select)) {
					selectSet = firstMap.get(noneKey);
					selectSet.remove('ε');
					selectItem.put(select, selectSet);
				}
				if (isEmpty(select)) {
					selectSet = followMap.get(noneKey);
					selectSet.remove('ε');
					selectItem.put(select, selectSet);
				}
				selectMap.put(noneKey, selectItem);
			}

		}

		for (Character noneKey : key) {
			ArrayList<String> arrayList = expMap.get(noneKey);
			HashMap<String, TreeSet<Character>> selectItem = new HashMap<String, TreeSet<Character>>();

			TreeSet<Character> selectSet = new TreeSet<Character>();
			selectItem = selectMap.get(noneKey);
			for (String list : arrayList) {
				System.out.printf("%c->%-10s的select集合是", noneKey, list);
				selectSet = selectItem.get(list);
				for (Character i : selectSet) {

					System.out.print("  " + i + " ");
				}
				System.out.println();
			}

		}

		LL1();
	}

	public void LL1() {
		// 若是LL1文法，则相同非终结符的select集互不相交
		Set<Character> key = expMap.keySet();
		for (Character noneKey : key) {
			ArrayList<String> arrayList = expMap.get(noneKey);
			HashMap<String, TreeSet<Character>> selectItem = new HashMap<String, TreeSet<Character>>();
			boolean flag = false;
			selectItem = selectMap.get(noneKey);
			for (String list1 : arrayList) {
				TreeSet<Character> selectSet1 = new TreeSet<Character>();
				selectSet1 = selectItem.get(list1);
				for (String list2 : arrayList) {
					if (list1 != list2) {
						TreeSet<Character> selectSet2 = new TreeSet<Character>();
						selectSet2 = selectItem.get(list2);
						// 如果相同，返回false

						for (Character s : selectSet1) {
							if (selectSet2.contains(s)) {
								System.out.println("经判断，该文法不是LL1文法");
							}
						}

					}

				}
			}
		}
		System.out.println("经判断，该文法是LL1文法");

		analyze();
	}

	/**
	 * 
	 * @param select
	 * @return 判断表达式右部是不是空串开头
	 */
	public boolean isEmpty(String select) {
		char char_first = select.charAt(0);
		if (char_first == 'ε') {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param ntSet
	 *            终结符集合
	 * @param select
	 *            表达式右部->判断首字母是不是终结符集合
	 * @return
	 */
	public boolean isTerminal(TreeSet<Character> ntSet, String select) {
		char char_first = select.charAt(0);
		if (ntSet.contains(char_first)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param nvSet
	 *            非终结符集合
	 * @param select
	 *            表达式左部->判断首字母是不是非终结符集合
	 * @return
	 */
	public boolean isNone(TreeSet<Character> nvSet, String select) {
		char char_first = select.charAt(0);
		if (nvSet.contains(char_first)) {
			return true;
		}
		return false;
	}

	public void analyze() {
		try {
			int index = 0;
			// 开始符进栈
			analyzeStack.push('#');
			analyzeStack.push(startChar);
			System.out.println("开始符：" + startChar);

			// 若不匹配，则找到产生式，出栈，将产生式子有部加入。
			while (!analyzeStack.empty()) {
				index++;
				if (analyzeStack.peek() != str.charAt(0)) {
					// 如果第一个不相等，则在分析表中找产生式
					String nowUseExpStr = shuchuchuan(selectMap, analyzeStack.peek(), str.charAt(0));
					System.out.printf("%-20d", index);
					System.out.printf("%-30s", analyzeStack.toString());
					System.out.printf("%-15s", str);
					System.out.printf("%s->%s\n", analyzeStack.peek(), nowUseExpStr);
					// System.out.println(index+"\t\t\t"+analyzeStack.toString()+"\t\t\t"+str+"\t\t\t"+analyzeStack.peek()+"->"+nowUseExpStr);
					Bean produce = new Bean();
					produce.setIndex(index);
					produce.setAnalyzeStackStr(analyzeStack.toString());
					produce.setStr(nowUseExpStr);
					if (null == nowUseExpStr) {
						produce.setUseExpStr("匹配失败");
					} else {
						produce.setAnalyzeStackStr(analyzeStack.peek() + "->" + nowUseExpStr);
					}
					analyzeProduces.add(produce);
					// 将之前分析栈中的栈顶出栈
					analyzeStack.pop();
					// 如果输出的栈的表达式不为空或者不为空串
					if (null != nowUseExpStr && nowUseExpStr.charAt(0) != 'ε') {
						for (int j = nowUseExpStr.length() - 1; j >= 0; j--) {
							char currentChar = nowUseExpStr.charAt(j);
							analyzeStack.push(currentChar);
						}
					}
					continue;
				}

				// 如果可以匹配，分析栈出栈，串去掉一位
				if (analyzeStack.peek() == str.charAt(0)) {

					System.out.printf("%-20d", index);
					System.out.printf("%-30s", analyzeStack.toString());
					System.out.printf("%-15s", str);
					System.out.printf("'%c'匹配\n", str.charAt(0));
					// System.out.println(index+"\t\t\t"+analyzeStack.toString()+"\t\t\t"+str+"\t\t\t"+"'"+str.charAt(0)+"'匹配");
					Bean produce = new Bean();
					produce.setIndex(index);
					produce.setAnalyzeStackStr(analyzeStack.toString());
					produce.setStr(str);
					produce.setUseExpStr("'" + str.charAt(0) + "'匹配");
					analyzeProduces.add(produce);
					analyzeStack.pop();
					str = str.substring(1);
					continue;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("匹配失败");
		}

	}

	// 目的是根据nv和nt返回正确的一个输出
	// 通过非终结符找到hashmap;
	// 得到主键字符窜集合
	// 在遍历主键的过程中，如果找到终结符。则返回主键集合
	public String shuchuchuan(TreeMap<Character, HashMap<String, TreeSet<Character>>> 
	selectMap, Character nonechar,
			Character terminalchar) {
		HashMap<String, TreeSet<Character>> hashMap = selectMap.get(nonechar);
		Set<String> keySet = hashMap.keySet();
		for (String use : keySet) {
			// 获得select集合中的非终结符
			TreeSet<Character> treeset = hashMap.get(use);
			if (treeset.contains(terminalchar)) {
				return use;
			}
		}
		return null;
	}

}

// (1)E->TE' SELECT(1)={#,id}
// Character:E ; String TE'; TreeSet: {#,id}

/* S->aA S->d A->bAS A->ε */
// First{A} = {a,d,#}
// Follow(S) = {a,d,#}
