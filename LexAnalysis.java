package compilers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;







class State
{
	final static int ERROR = 0;
	final static int START = 1;
	
	final static int BUILDING_DIGIT_LETTER = 2;
	final static int FIN_BUILDING = 3; /* [a-zA-Z][a-zA-Z0-9]* */
	final static int NUMBER = 4; 
	final static int FIN_NUMBER = 5; /* [0-9]+ */ 
	
	final static int LT = 6;
	final static int FIN_LT = 7; /* < */
	
	final static int LE = 8;
	final static int FIN_LE = 9; /* <= */
	
	final static int NE = 10;
	final static int FIN_NE = 11; /* <> */
	
	
	
	final static int GT = 12;
	final static int FIN_GT = 13; /* > */
	
	final static int GE = 14;
	final static int FIN_GE = 15; /* >= */
	
	
	final static int COLON = 16;
	final static int FIN_COLON = 17; /* : */
	
	final static int ASSIGNMENT = 18;
	final static int FIN_ASSIGNMENT = 19; /* := */
	
	
	final static int SLASH = 20;
	final static int FIN_SLASH = 21; /* / */
	
	final static int LEFT_ANNO = 22;
	final static int RIGHT_ANNO_ASTERISK = 23; /* /* */
	
	
	final static int DOT = 24;
	final static int FIN_DOT = 25; /* . */
	
	final static int DOUBLE_DOT = 26; /* .. */
	final static int FIN_DOUBLE_DOT = 27; /* .. */
	
	
	final static int ASTERISK = 28;
	final static int FIN_ASTERISK = 29; /* * */
	
	final static int RIGHT_ANNO = 30; 	
	final static int FIN_RIGHT_ANNO = 31; //  */ 
	
	final static int OTHERS = 32; 
	final static int FIN_OTHERS = 33; /* ??? */
	
	final static int LEFT_QUOT = 34;
	final static int FIN_LEFT_QUOT = 35; /* ' */
	
	final static int RIHGT_QUOT = 36;
	final static int FIN_RIGHT_QUOT = 37; /* 'bla bla' */
	
	
	private static boolean T = true;
	private static boolean F = false;
	
	
	
	final static boolean[] FIN_STATES = {F, F, F, T, F, T, F, T, F, T, F,  T,  F,  T,  F,  T,  F,  T,  F,  T,  F,  T,  F,  F,  F,  T,  F,  T,  F,  T,  F,  T,  F,  T,  F,  T,  F,  T};
	//                                  {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37}

}

//二元组类
class Symbol
{
	int kindCode;
	int seqNum; //-1 stands for none
	public Symbol(int kindCode, int seqNum)
	{
		this.kindCode = kindCode;
		this.seqNum = seqNum;
	}
	
	public String toSymbolString()
	{
		String str_seqNum = this.seqNum == -1 ? "-" : String.valueOf(this.seqNum);
		return "( " + this.kindCode + " , " + str_seqNum + " )";

	}
	
}



public class LexAnalysis
{


	//其他种别码
	final static int IDENTIFIER = 36;
	final static int CONSTANT = 37;
	final static int CONST_CHARS = 38;
	
	//保留字
	static String[] keyWords = { "and", "array", "begin", "bool", "call", "case", "char", "constant", "dim",
			"do", "else", "end", "false", "for", "if", "input", "integer", "not", "of", "or", "output", "procedure",
			"program", "read", "real", "repeat", "set", "stop", "then", "to", "true", "until", "var", "while",
			"write" };

	//保留字map
	static HashMap<String, Integer> keyWord2kindCode = new HashMap<>();
	//保留字反向map
	static HashMap<Integer, String> kindCode2keyWord = new HashMap<>();

	//单界符
	static Character[] singleDelimiters = { '+', '-', '*', '/', '=', '<', '>', '(', ')', '[', ']', ':', '.', ';', ',' , '\''};
	//单界符对应的种别码
	static Integer[] kindCode_singleDelimiters = { 43, 45, 41, 48, 56, 53, 57, 39, 40, 59, 60, 50, 46, 52, 44, -'\''};
	//单界符map
	static HashMap<Character, Integer> singleDelimiter2kindCode = new HashMap<>();
	
	//单界符反向map
	static HashMap<Integer, Character> kindCode2singleDelimiter = new HashMap<>();
	
	//其他合法字符 即所有数字及大小写字母
	static Character[] otherLegalCharacters = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	//合法字符 即所有数字及大小写字母及单界符的并集
	static HashSet<Character> legalCharacterSet = new HashSet<>();
	

	

	static
	{
		
		//初始化
		
		//合法字符 即所有数字及大小写字母及单界符的并集
		legalCharacterSet.addAll(Arrays.asList(singleDelimiters));
		legalCharacterSet.addAll(Arrays.asList(otherLegalCharacters));
		
		
		int len_keyWords = keyWords.length;
		for (int k = 0; k < len_keyWords; ++k)
		{
			keyWord2kindCode.put(keyWords[k], k + 1);
			kindCode2keyWord.put(k + 1, keyWords[k]);
		}

		int len_singleDelimiters = singleDelimiters.length;
		for (int k = 0; k < len_singleDelimiters; ++k)
		{
			singleDelimiter2kindCode.put(singleDelimiters[k], kindCode_singleDelimiters[k]);
			
			kindCode2singleDelimiter.put(kindCode_singleDelimiters[k], singleDelimiters[k]);
		}
		
		

	}
	
	
	//状态转移矩阵
	private static int transMatrix[][] = new int[50][256];
	
	
	static
	{
		//状态转移矩阵初始化
		
		//空格
		transMatrix[State.START][' '] = State.START;
		//-空格
		
		//字母
		for(char ch = 'a'; ch <= 'z'; ++ch)
		{
			transMatrix[State.START][ch] = State.BUILDING_DIGIT_LETTER;
			transMatrix[State.START][ch - 0x20] = State.BUILDING_DIGIT_LETTER;
		}
		//-字母
		
		//字母数字
		for(char ch : otherLegalCharacters)
		{
			transMatrix[State.BUILDING_DIGIT_LETTER][ch] = State.BUILDING_DIGIT_LETTER;
		}
		//-字母数字
		
		//非字母数字
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			transMatrix[State.BUILDING_DIGIT_LETTER][ch] = State.FIN_BUILDING;
		}
		transMatrix[State.BUILDING_DIGIT_LETTER][' '] = State.FIN_BUILDING;
		//-非字母数字
		
		//数字
		for(char ch = '0'; ch <= '9'; ++ch)
		{
			transMatrix[State.START][ch] = State.NUMBER;
			transMatrix[State.NUMBER][ch] = State.NUMBER;
		}
		//-数字
		
		//非数字
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			transMatrix[State.NUMBER][ch] = State.FIN_NUMBER;
		}
		transMatrix[State.NUMBER][' '] = State.FIN_NUMBER;
		for(char ch = 'a'; ch <= 'z'; ++ch)
		{
			transMatrix[State.NUMBER][ch] = State.FIN_NUMBER;
			transMatrix[State.NUMBER][ch - 0x20] = State.FIN_NUMBER;
		}
		//-非数字
		
		//小于号 <
		transMatrix[State.START]['<'] = State.LT;		
		//-小于号 <
		
		//fin 小于号  <
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.LT][ch] = State.FIN_LT;
		}
		transMatrix[State.LT][' '] = State.FIN_LT;
		//-fin 小于号  <
		
		//小于等于号 <=
		transMatrix[State.LT]['='] = State.LE;
		//-小于等于号 <=
		
		//fin 小于等于号 <=
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.LE][ch] = State.FIN_LE;
		}
		transMatrix[State.LE][' '] = State.FIN_LE;
		//-fin 小于等于号 <=
		
		//不等于号 <>
		transMatrix[State.LT]['>'] = State.NE;
		//-不等于号 <>
		
		
		//fin 不等于号 <>
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.NE][ch] = State.FIN_NE;
		}
		transMatrix[State.NE][' '] = State.FIN_NE;
		//fin 不等于号 <>
		

		
		//大于号 >
		transMatrix[State.START]['>'] = State.GT;
		//-大于号 >
		
		//fin 大于号 >
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.GT][ch] = State.FIN_GT;
		}
		transMatrix[State.GT][' '] = State.FIN_GT;
		//-fin 大于号 >
		
		//大于等于号 >=
		transMatrix[State.GT]['='] = State.GE;
		//-大于等于号 >=
		
		//fin 大于等于号 >=
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.GE][ch] = State.FIN_GE;
		}
		transMatrix[State.GE][' '] = State.FIN_GE;
		//-fin 大于等于号 >=
		

		
		//冒号 :
		transMatrix[State.START][':'] = State.COLON;
		//-冒号 :
		
		//fin 冒号 :
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.COLON][ch] = State.FIN_COLON;
		}
		transMatrix[State.COLON][' '] = State.FIN_COLON;
		//-fin 冒号 :
		
		//赋值号 :=
		transMatrix[State.COLON]['='] = State.ASSIGNMENT;
		//-赋值号 :=
		
		//fin 赋值号 :=
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.ASSIGNMENT][ch] = State.FIN_ASSIGNMENT;
		}
		transMatrix[State.ASSIGNMENT][' '] = State.FIN_ASSIGNMENT;
		//-fin 赋值号 :=
		

		
		//斜杠 /
		transMatrix[State.START]['/'] = State.SLASH;
		//-斜杠 /
		
		//fin 斜杠 /
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.SLASH][ch] = State.FIN_SLASH;
		}
		transMatrix[State.SLASH][' '] = State.FIN_SLASH;
		//-fin 斜杠 /
		
		//左注释号 /*
		transMatrix[State.SLASH]['*'] = State.LEFT_ANNO;
		//-左注释号 /*
		
		
		//注释状态
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.LEFT_ANNO][ch] = State.LEFT_ANNO;
		}
		transMatrix[State.LEFT_ANNO][' '] = State.LEFT_ANNO;
		
		//-注释状态
		
		
		
		//注释星号转移
		transMatrix[State.LEFT_ANNO]['*'] = State.RIGHT_ANNO_ASTERISK;
		
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.RIGHT_ANNO_ASTERISK][ch] = State.LEFT_ANNO;
		}
		transMatrix[State.RIGHT_ANNO_ASTERISK][' '] = State.LEFT_ANNO;
		transMatrix[State.RIGHT_ANNO_ASTERISK]['*'] = State.RIGHT_ANNO_ASTERISK;
		transMatrix[State.RIGHT_ANNO_ASTERISK]['/'] = State.RIGHT_ANNO;
		//-注释星号转移

		
		//点 .
		transMatrix[State.START]['.'] = State.DOT;
		//-点 .
		
		//fin 点 .
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.DOT][ch] = State.FIN_DOT;
		}
		transMatrix[State.DOT][' '] = State.FIN_DOT;
		//-fin 点 .
		
		//双点 ..
		transMatrix[State.DOT]['.'] = State.DOUBLE_DOT;
		//-双点 ..
		
		//fin 双点 ..
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.DOUBLE_DOT][ch] = State.FIN_DOUBLE_DOT;
		}
		transMatrix[State.DOUBLE_DOT][' '] = State.FIN_DOUBLE_DOT;
		//-fin 双点 ..
		
		
		//星号 *
		transMatrix[State.START]['*'] = State.ASTERISK;
		//-星号 *
		
		//fin 星号 *
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.ASTERISK][ch] = State.FIN_ASTERISK;
		}
		transMatrix[State.ASTERISK][' '] = State.FIN_ASTERISK;
		//-fin 星号 *
		
		//右注释号 */
		transMatrix[State.ASTERISK]['/'] = State.RIGHT_ANNO;
		//-右注释号 */
		
		//fin 右注释号 */
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.RIGHT_ANNO][ch] = State.FIN_RIGHT_ANNO;
		}
		transMatrix[State.RIGHT_ANNO][' '] = State.FIN_RIGHT_ANNO;
		//-fin右注释号 */
		

		

		

		
		//左单引号 '
		transMatrix[State.START]['\''] = State.LEFT_QUOT;
		//左单引号 '
		
		//字符常量 others
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.LEFT_QUOT][ch] = State.LEFT_QUOT;
			
		}
		transMatrix[State.LEFT_QUOT][' '] = State.LEFT_QUOT;
		//-字符常量 others
		
		//右单引号 '
		transMatrix[State.LEFT_QUOT]['\''] = State.RIHGT_QUOT;
		//右单引号 '
		
		//fin 字符常量
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			transMatrix[State.RIHGT_QUOT][ch] = State.FIN_RIGHT_QUOT;
		}
		transMatrix[State.RIHGT_QUOT][' '] = State.FIN_RIGHT_QUOT;
		//-fin 字符常量
		
		//其他符号
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			if(transMatrix[State.START][ch] == State.ERROR)
			{
				transMatrix[State.START][ch] = State.OTHERS;
			}
		}
		//-其他符号
		
		
		//fin 其他符号
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.OTHERS][ch] = State.FIN_OTHERS;
		}
		transMatrix[State.OTHERS][' '] = State.FIN_OTHERS;
		
		/*
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			transMatrix[State.OTHERS][ch] = State.FIN_OTHERS;
		}
		transMatrix[State.OTHERS][' '] = State.FIN_OTHERS;
		*/
		
		//-fin 其他符号
		
		
		
		
		
		
		
	}
	
	
	// 取双界符的种别码
	public static int getDoubleKindCode(String str)
	{
		int code = -1;
		switch (str)
		{
			case ":=":
				code = 51;
				break;
			case ">=":
				code = 58;
				break;
			case "<=":
				code = 54;
				break;
			case "..":
				code = 47;
				break;
			case "<>":
				code = 55;
				break;
			default:
				break;
		}
		return code;
	}
	
	public static String kindCode2Double(int kindCode)
	{
		String doubleRet = null;
		switch (kindCode)
		{
			case 51:
				doubleRet = ":=";
				break;
			case 58:
				doubleRet = ">=";
				break;
			case 54:
				doubleRet = "<=";
				break;
			case 47:
				doubleRet = "..";
				break;
			case 55:
				doubleRet = "<>";
				break;
			default:
				break;
		}
		return doubleRet;
	}
	
	static void error(int lineCnt, int ind, String errorString)
	{
		//出错处理函数
		System.err.println("terminated");
		System.err.println(errorString);
		System.err.println("in line " + lineCnt + " column " + (ind + 1));
		
		
	}
	
	static void printByLine(List<ArrayList<Symbol>> symbols)
	{
		//打印函数 按照每行打印
		for(List<Symbol> thisLineSymbol : symbols)
		{
			for(Symbol symbol : thisLineSymbol)
			{
				System.out.print(symbol.toSymbolString());
				System.out.print("\t");
			}
			System.out.println();
		}
	}
	static void printByInterval(List<ArrayList<Symbol>> symbols, int numInLine)
	{
		//打印函数 按每行多少个二元组打印
		int cnt = 0;
		for(List<Symbol> thisLineSymbol : symbols)
		{
			for(Symbol symbol : thisLineSymbol)
			{
				System.out.print(symbol.toSymbolString());
				System.out.print("\t");
				
				++cnt;
				if(cnt % numInLine == 0)
				{
					System.out.println();
				}
			}
		}
	}
	
	
	// 成员变量
	
	//二元组表 以每行记录
	ArrayList<ArrayList<Symbol>> symbolList;
	
	//常量表 如数字 标识符名 字符串
	HashMap<String, Integer> constantsAndSymbol2SeqNum;
	
	
	
	
	void tokenAnalysis(BufferedReader bufferedReader) throws IOException
	{
		//常量表 如数字 标识符名 字符串
		constantsAndSymbol2SeqNum = new HashMap<>();
		
		//常量 序号
		int seqNumCnt = 0;
		
		//二元组表 以每行记录
		symbolList = new ArrayList<>();
		String line = null;
		
		//记录行数
		int lineCnt = 0;
		
		outer: while((line = bufferedReader.readLine()) != null)
		{
			//行数+1
			++lineCnt;
			
			//方便处理 加后缀空格
			line += " ";
			
			//处理空白符"\t"
			line = line.replaceAll("\t", " ");
			
			ArrayList<Symbol> thisLineSymbol = new ArrayList<>();
			symbolList.add(thisLineSymbol);
			
			//字符缓冲
			StringBuilder stringBuilder = new StringBuilder();
			
			//当前读到的字符位置 和 行长度
			int ind = 0; int len = line.length();
			
			//将读字符
			char ch;
			
			//初始化状态
			int state = State.START;
			
			while(ind < len)
			{

				ch = line.charAt(ind);

				

				if(ch >= transMatrix[0].length)
				{
					ch = 0;
				}
				//状态转移
				state = transMatrix[state][ch];					


				
				
				
				//debug输出用
				//System.out.println("state:" + state);
				
				
				if(state == State.ERROR)
				{
					//不能转移 or 非法字符
					error(lineCnt, ind, "illegal character: " + "\'" + ch +"\', " + (int)(ch) + " in value");
					break outer;
				}
				else if(State.FIN_STATES[state])
				{
					//终结状态
					String str = stringBuilder.toString();
					
					//debug 输出 识别的符号
					//System.out.println(str);
					
					
					if(state == State.FIN_BUILDING)
					{
						//标识符或者保留字
						
						Integer kindCode = keyWord2kindCode.get(str);
						if(kindCode != null)
						{
							//保留字
							Symbol symbol = new Symbol(kindCode, -1);
							thisLineSymbol.add(symbol);
						}
						else
						{
							//标识符
							Integer val = constantsAndSymbol2SeqNum.get(str);
							if(val == null)
							{
								//新的标识符
								
								Symbol symbol = new Symbol(IDENTIFIER, ++seqNumCnt);
								thisLineSymbol.add(symbol);
								constantsAndSymbol2SeqNum.put(str, seqNumCnt);
								
							}
							else
							{
								//旧标识符
								
								Symbol symbol = new Symbol(IDENTIFIER, val);
								thisLineSymbol.add(symbol);
							}
						}
					}
					else if(state == State.FIN_NUMBER)
					{
						//整数
						Integer val = constantsAndSymbol2SeqNum.get(str);
						if(val == null)
						{
							
							Symbol symbol = new Symbol(CONSTANT, ++seqNumCnt);
							thisLineSymbol.add(symbol);
							constantsAndSymbol2SeqNum.put(str, seqNumCnt);
						}
						else
						{
							Symbol symbol = new Symbol(CONSTANT, val);
							thisLineSymbol.add(symbol);
						}
						
					}
					else if(state == State.FIN_RIGHT_QUOT)
					{
						//字符常量
						Integer val = constantsAndSymbol2SeqNum.get(str);
						if(val == null)
						{
							
							Symbol symbol = new Symbol(CONST_CHARS, ++seqNumCnt);
							thisLineSymbol.add(symbol);
							constantsAndSymbol2SeqNum.put(str, seqNumCnt);
						}
						else
						{
							Symbol symbol = new Symbol(CONST_CHARS, val);
							thisLineSymbol.add(symbol);
						}
						
					}
					//其他终结状态
					else
					{
						if(str.length() == 1)
						{
							//单分隔符
							Integer kindCode = singleDelimiter2kindCode.get(str.charAt(0));
							Symbol symbol = new Symbol(kindCode, -1);
							thisLineSymbol.add(symbol);
						}
						else
						{
							//双分割符
							Integer kindCode = getDoubleKindCode(str);
							if(state == State.FIN_RIGHT_ANNO)
							{
								//结束注释状态
								stringBuilder.setLength(0);
								state = State.START;
								continue;
							}
							
							Symbol symbol = new Symbol(kindCode, -1);
							thisLineSymbol.add(symbol);
						}
					}
					//清空缓冲
					stringBuilder.setLength(0);
					//回到最初状态
					state = State.START;
				}
				else
				{
					//中间状态
					
					if(state != State.START)
					{
						//除空格 加入字符缓冲
						stringBuilder.append(ch);
					}
					//下一字符
					++ind;
				}
				
			}
			if(state == State.LEFT_QUOT)
			{
				//缺少右单引号
				error(lineCnt, ind, "expect right single quote");
				break outer;
			}
			else if(state == State.LEFT_ANNO)
			{
				//缺少右注释号
				error(lineCnt, ind, "expect right annotation");
				break outer;
			}
			
		}
		
		//清空错误输出 java err和out混用输出顺序有问题
		System.err.flush();
		
		//打印
		//printByLine(symbolList);
		printByInterval(symbolList, 5);
		System.out.println(constantsAndSymbol2SeqNum.toString());
	}

	/*
	public static void main(String[] args) throws IOException
	{

		//输出作者姓名 班级 学号
		System.out.println("author: Huang Zhenbang");
		System.out.println("class: 16 computer science 2");
		System.out.println("id: 201630598848");
		
		Scanner scanner = new Scanner(System.in);

		System.out.println("input the file path");
		
		
		//输入文件路径
		String filePath = scanner.nextLine();
		
		scanner.close();
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
		
		LexAnalysis lexAnalysis = new LexAnalysis();
		
		//分析
		lexAnalysis.tokenAnalysis(bufferedReader);
		
		
		bufferedReader.close();
	}
	*/

}