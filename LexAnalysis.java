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

//��Ԫ����
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


	//�����ֱ���
	final static int IDENTIFIER = 36;
	final static int CONSTANT = 37;
	final static int CONST_CHARS = 38;
	
	//������
	static String[] keyWords = { "and", "array", "begin", "bool", "call", "case", "char", "constant", "dim",
			"do", "else", "end", "false", "for", "if", "input", "integer", "not", "of", "or", "output", "procedure",
			"program", "read", "real", "repeat", "set", "stop", "then", "to", "true", "until", "var", "while",
			"write" };

	//������map
	static HashMap<String, Integer> keyWord2kindCode = new HashMap<>();
	//�����ַ���map
	static HashMap<Integer, String> kindCode2keyWord = new HashMap<>();

	//�����
	static Character[] singleDelimiters = { '+', '-', '*', '/', '=', '<', '>', '(', ')', '[', ']', ':', '.', ';', ',' , '\''};
	//�������Ӧ���ֱ���
	static Integer[] kindCode_singleDelimiters = { 43, 45, 41, 48, 56, 53, 57, 39, 40, 59, 60, 50, 46, 52, 44, -'\''};
	//�����map
	static HashMap<Character, Integer> singleDelimiter2kindCode = new HashMap<>();
	
	//���������map
	static HashMap<Integer, Character> kindCode2singleDelimiter = new HashMap<>();
	
	//�����Ϸ��ַ� ���������ּ���Сд��ĸ
	static Character[] otherLegalCharacters = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
	//�Ϸ��ַ� ���������ּ���Сд��ĸ��������Ĳ���
	static HashSet<Character> legalCharacterSet = new HashSet<>();
	

	

	static
	{
		
		//��ʼ��
		
		//�Ϸ��ַ� ���������ּ���Сд��ĸ��������Ĳ���
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
	
	
	//״̬ת�ƾ���
	private static int transMatrix[][] = new int[50][256];
	
	
	static
	{
		//״̬ת�ƾ����ʼ��
		
		//�ո�
		transMatrix[State.START][' '] = State.START;
		//-�ո�
		
		//��ĸ
		for(char ch = 'a'; ch <= 'z'; ++ch)
		{
			transMatrix[State.START][ch] = State.BUILDING_DIGIT_LETTER;
			transMatrix[State.START][ch - 0x20] = State.BUILDING_DIGIT_LETTER;
		}
		//-��ĸ
		
		//��ĸ����
		for(char ch : otherLegalCharacters)
		{
			transMatrix[State.BUILDING_DIGIT_LETTER][ch] = State.BUILDING_DIGIT_LETTER;
		}
		//-��ĸ����
		
		//����ĸ����
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			transMatrix[State.BUILDING_DIGIT_LETTER][ch] = State.FIN_BUILDING;
		}
		transMatrix[State.BUILDING_DIGIT_LETTER][' '] = State.FIN_BUILDING;
		//-����ĸ����
		
		//����
		for(char ch = '0'; ch <= '9'; ++ch)
		{
			transMatrix[State.START][ch] = State.NUMBER;
			transMatrix[State.NUMBER][ch] = State.NUMBER;
		}
		//-����
		
		//������
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
		//-������
		
		//С�ں� <
		transMatrix[State.START]['<'] = State.LT;		
		//-С�ں� <
		
		//fin С�ں�  <
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.LT][ch] = State.FIN_LT;
		}
		transMatrix[State.LT][' '] = State.FIN_LT;
		//-fin С�ں�  <
		
		//С�ڵ��ں� <=
		transMatrix[State.LT]['='] = State.LE;
		//-С�ڵ��ں� <=
		
		//fin С�ڵ��ں� <=
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.LE][ch] = State.FIN_LE;
		}
		transMatrix[State.LE][' '] = State.FIN_LE;
		//-fin С�ڵ��ں� <=
		
		//�����ں� <>
		transMatrix[State.LT]['>'] = State.NE;
		//-�����ں� <>
		
		
		//fin �����ں� <>
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.NE][ch] = State.FIN_NE;
		}
		transMatrix[State.NE][' '] = State.FIN_NE;
		//fin �����ں� <>
		

		
		//���ں� >
		transMatrix[State.START]['>'] = State.GT;
		//-���ں� >
		
		//fin ���ں� >
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.GT][ch] = State.FIN_GT;
		}
		transMatrix[State.GT][' '] = State.FIN_GT;
		//-fin ���ں� >
		
		//���ڵ��ں� >=
		transMatrix[State.GT]['='] = State.GE;
		//-���ڵ��ں� >=
		
		//fin ���ڵ��ں� >=
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.GE][ch] = State.FIN_GE;
		}
		transMatrix[State.GE][' '] = State.FIN_GE;
		//-fin ���ڵ��ں� >=
		

		
		//ð�� :
		transMatrix[State.START][':'] = State.COLON;
		//-ð�� :
		
		//fin ð�� :
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.COLON][ch] = State.FIN_COLON;
		}
		transMatrix[State.COLON][' '] = State.FIN_COLON;
		//-fin ð�� :
		
		//��ֵ�� :=
		transMatrix[State.COLON]['='] = State.ASSIGNMENT;
		//-��ֵ�� :=
		
		//fin ��ֵ�� :=
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.ASSIGNMENT][ch] = State.FIN_ASSIGNMENT;
		}
		transMatrix[State.ASSIGNMENT][' '] = State.FIN_ASSIGNMENT;
		//-fin ��ֵ�� :=
		

		
		//б�� /
		transMatrix[State.START]['/'] = State.SLASH;
		//-б�� /
		
		//fin б�� /
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.SLASH][ch] = State.FIN_SLASH;
		}
		transMatrix[State.SLASH][' '] = State.FIN_SLASH;
		//-fin б�� /
		
		//��ע�ͺ� /*
		transMatrix[State.SLASH]['*'] = State.LEFT_ANNO;
		//-��ע�ͺ� /*
		
		
		//ע��״̬
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.LEFT_ANNO][ch] = State.LEFT_ANNO;
		}
		transMatrix[State.LEFT_ANNO][' '] = State.LEFT_ANNO;
		
		//-ע��״̬
		
		
		
		//ע���Ǻ�ת��
		transMatrix[State.LEFT_ANNO]['*'] = State.RIGHT_ANNO_ASTERISK;
		
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.RIGHT_ANNO_ASTERISK][ch] = State.LEFT_ANNO;
		}
		transMatrix[State.RIGHT_ANNO_ASTERISK][' '] = State.LEFT_ANNO;
		transMatrix[State.RIGHT_ANNO_ASTERISK]['*'] = State.RIGHT_ANNO_ASTERISK;
		transMatrix[State.RIGHT_ANNO_ASTERISK]['/'] = State.RIGHT_ANNO;
		//-ע���Ǻ�ת��

		
		//�� .
		transMatrix[State.START]['.'] = State.DOT;
		//-�� .
		
		//fin �� .
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.DOT][ch] = State.FIN_DOT;
		}
		transMatrix[State.DOT][' '] = State.FIN_DOT;
		//-fin �� .
		
		//˫�� ..
		transMatrix[State.DOT]['.'] = State.DOUBLE_DOT;
		//-˫�� ..
		
		//fin ˫�� ..
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.DOUBLE_DOT][ch] = State.FIN_DOUBLE_DOT;
		}
		transMatrix[State.DOUBLE_DOT][' '] = State.FIN_DOUBLE_DOT;
		//-fin ˫�� ..
		
		
		//�Ǻ� *
		transMatrix[State.START]['*'] = State.ASTERISK;
		//-�Ǻ� *
		
		//fin �Ǻ� *
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.ASTERISK][ch] = State.FIN_ASTERISK;
		}
		transMatrix[State.ASTERISK][' '] = State.FIN_ASTERISK;
		//-fin �Ǻ� *
		
		//��ע�ͺ� */
		transMatrix[State.ASTERISK]['/'] = State.RIGHT_ANNO;
		//-��ע�ͺ� */
		
		//fin ��ע�ͺ� */
		for(char ch : legalCharacterSet)
		{	
			transMatrix[State.RIGHT_ANNO][ch] = State.FIN_RIGHT_ANNO;
		}
		transMatrix[State.RIGHT_ANNO][' '] = State.FIN_RIGHT_ANNO;
		//-fin��ע�ͺ� */
		

		

		

		
		//������ '
		transMatrix[State.START]['\''] = State.LEFT_QUOT;
		//������ '
		
		//�ַ����� others
		for(char ch : legalCharacterSet)
		{
			transMatrix[State.LEFT_QUOT][ch] = State.LEFT_QUOT;
			
		}
		transMatrix[State.LEFT_QUOT][' '] = State.LEFT_QUOT;
		//-�ַ����� others
		
		//�ҵ����� '
		transMatrix[State.LEFT_QUOT]['\''] = State.RIHGT_QUOT;
		//�ҵ����� '
		
		//fin �ַ�����
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			transMatrix[State.RIHGT_QUOT][ch] = State.FIN_RIGHT_QUOT;
		}
		transMatrix[State.RIHGT_QUOT][' '] = State.FIN_RIGHT_QUOT;
		//-fin �ַ�����
		
		//��������
		for(int ind = 0; ind < singleDelimiters.length; ++ind)
		{
			char ch = singleDelimiters[ind];
			if(transMatrix[State.START][ch] == State.ERROR)
			{
				transMatrix[State.START][ch] = State.OTHERS;
			}
		}
		//-��������
		
		
		//fin ��������
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
		
		//-fin ��������
		
		
		
		
		
		
		
	}
	
	
	// ȡ˫������ֱ���
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
		//��������
		System.err.println("terminated");
		System.err.println(errorString);
		System.err.println("in line " + lineCnt + " column " + (ind + 1));
		
		
	}
	
	static void printByLine(List<ArrayList<Symbol>> symbols)
	{
		//��ӡ���� ����ÿ�д�ӡ
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
		//��ӡ���� ��ÿ�ж��ٸ���Ԫ���ӡ
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
	
	
	// ��Ա����
	
	//��Ԫ��� ��ÿ�м�¼
	ArrayList<ArrayList<Symbol>> symbolList;
	
	//������ ������ ��ʶ���� �ַ���
	HashMap<String, Integer> constantsAndSymbol2SeqNum;
	
	
	
	
	void tokenAnalysis(BufferedReader bufferedReader) throws IOException
	{
		//������ ������ ��ʶ���� �ַ���
		constantsAndSymbol2SeqNum = new HashMap<>();
		
		//���� ���
		int seqNumCnt = 0;
		
		//��Ԫ��� ��ÿ�м�¼
		symbolList = new ArrayList<>();
		String line = null;
		
		//��¼����
		int lineCnt = 0;
		
		outer: while((line = bufferedReader.readLine()) != null)
		{
			//����+1
			++lineCnt;
			
			//���㴦�� �Ӻ�׺�ո�
			line += " ";
			
			//����հ׷�"\t"
			line = line.replaceAll("\t", " ");
			
			ArrayList<Symbol> thisLineSymbol = new ArrayList<>();
			symbolList.add(thisLineSymbol);
			
			//�ַ�����
			StringBuilder stringBuilder = new StringBuilder();
			
			//��ǰ�������ַ�λ�� �� �г���
			int ind = 0; int len = line.length();
			
			//�����ַ�
			char ch;
			
			//��ʼ��״̬
			int state = State.START;
			
			while(ind < len)
			{

				ch = line.charAt(ind);

				

				if(ch >= transMatrix[0].length)
				{
					ch = 0;
				}
				//״̬ת��
				state = transMatrix[state][ch];					


				
				
				
				//debug�����
				//System.out.println("state:" + state);
				
				
				if(state == State.ERROR)
				{
					//����ת�� or �Ƿ��ַ�
					error(lineCnt, ind, "illegal character: " + "\'" + ch +"\', " + (int)(ch) + " in value");
					break outer;
				}
				else if(State.FIN_STATES[state])
				{
					//�ս�״̬
					String str = stringBuilder.toString();
					
					//debug ��� ʶ��ķ���
					//System.out.println(str);
					
					
					if(state == State.FIN_BUILDING)
					{
						//��ʶ�����߱�����
						
						Integer kindCode = keyWord2kindCode.get(str);
						if(kindCode != null)
						{
							//������
							Symbol symbol = new Symbol(kindCode, -1);
							thisLineSymbol.add(symbol);
						}
						else
						{
							//��ʶ��
							Integer val = constantsAndSymbol2SeqNum.get(str);
							if(val == null)
							{
								//�µı�ʶ��
								
								Symbol symbol = new Symbol(IDENTIFIER, ++seqNumCnt);
								thisLineSymbol.add(symbol);
								constantsAndSymbol2SeqNum.put(str, seqNumCnt);
								
							}
							else
							{
								//�ɱ�ʶ��
								
								Symbol symbol = new Symbol(IDENTIFIER, val);
								thisLineSymbol.add(symbol);
							}
						}
					}
					else if(state == State.FIN_NUMBER)
					{
						//����
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
						//�ַ�����
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
					//�����ս�״̬
					else
					{
						if(str.length() == 1)
						{
							//���ָ���
							Integer kindCode = singleDelimiter2kindCode.get(str.charAt(0));
							Symbol symbol = new Symbol(kindCode, -1);
							thisLineSymbol.add(symbol);
						}
						else
						{
							//˫�ָ��
							Integer kindCode = getDoubleKindCode(str);
							if(state == State.FIN_RIGHT_ANNO)
							{
								//����ע��״̬
								stringBuilder.setLength(0);
								state = State.START;
								continue;
							}
							
							Symbol symbol = new Symbol(kindCode, -1);
							thisLineSymbol.add(symbol);
						}
					}
					//��ջ���
					stringBuilder.setLength(0);
					//�ص����״̬
					state = State.START;
				}
				else
				{
					//�м�״̬
					
					if(state != State.START)
					{
						//���ո� �����ַ�����
						stringBuilder.append(ch);
					}
					//��һ�ַ�
					++ind;
				}
				
			}
			if(state == State.LEFT_QUOT)
			{
				//ȱ���ҵ�����
				error(lineCnt, ind, "expect right single quote");
				break outer;
			}
			else if(state == State.LEFT_ANNO)
			{
				//ȱ����ע�ͺ�
				error(lineCnt, ind, "expect right annotation");
				break outer;
			}
			
		}
		
		//��մ������ java err��out�������˳��������
		System.err.flush();
		
		//��ӡ
		//printByLine(symbolList);
		printByInterval(symbolList, 5);
		System.out.println(constantsAndSymbol2SeqNum.toString());
	}

	/*
	public static void main(String[] args) throws IOException
	{

		//����������� �༶ ѧ��
		System.out.println("author: Huang Zhenbang");
		System.out.println("class: 16 computer science 2");
		System.out.println("id: 201630598848");
		
		Scanner scanner = new Scanner(System.in);

		System.out.println("input the file path");
		
		
		//�����ļ�·��
		String filePath = scanner.nextLine();
		
		scanner.close();
		
		BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
		
		LexAnalysis lexAnalysis = new LexAnalysis();
		
		//����
		lexAnalysis.tokenAnalysis(bufferedReader);
		
		
		bufferedReader.close();
	}
	*/

}