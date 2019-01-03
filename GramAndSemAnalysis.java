package compilers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Stack;



//����״̬��
class ChainState
{
	//�����ͼ���
	ArrayList<Integer> trueChain;
	ArrayList<Integer> falseChain;
	
	//��ʼ���±�
	int codeBegin;
	
	
	public ChainState()
	{
		trueChain = new ArrayList<>();
		falseChain = new ArrayList<>();
	}
	
	//����������
	void appendToTrueChain(ChainState chainState)
	{
		trueChain.addAll(chainState.trueChain);
	}
	
	//
	void appendToFalseChain(ChainState chainState)
	{
		falseChain.addAll(chainState.falseChain);
	}
	
	//���������
	void swapChain()
	{
		ArrayList<Integer> temp = trueChain;
		trueChain = falseChain;
		falseChain = temp;
	}


	
	
}

//��Ԫʽ��
class TAC
{
	
	Token opToken; //������
	Token argToken1; //��һ��������
	Token argToken2; //�ڶ���������
	Token resultToken; //�����
	
	
	public TAC(Token opToken, Token valueToken1, Token valueToken2, Token resultToken)
	{
		super();
		this.opToken = opToken;
		this.argToken1 = valueToken1;
		this.argToken2 = valueToken2;
		this.resultToken = resultToken;
	}
	
	public String toTAC_String()
	{
		StringBuilder stringBuilder = new StringBuilder();
		String string = stringBuilder.append("(")
		.append(opToken.symbol.content).append(",\t")
		.append(argToken1.symbol.content).append(",\t")
		.append(argToken2.symbol.content).append(",\t")
		.append(resultToken.symbol.content)
		.append(")")
		.toString();
		return string;
	}
	


	/*
	public TAC(TAC element)
	{
		this(element.opToken, element.valueToken1, element.valueToken2, element.resultToken);
	}
	*/
	
	
}

//��������
enum VariableType
{
	INTEGER,
	BOOL,
	CHAR,;

	@Override
	public String toString()
	{
		return super.toString().toLowerCase();
	}
	
	
}

//����
class Token
{
	
	//����Token
	final static Token TOKEN_J = new Token("j");
	final static Token TOKEN_NULL = new Token("-");
	
	
	Symbol symbol;
	int lineInd; //�к�
	int posInd; //�ڸ��е�λ��
	
	VariableType variableType;
	
	
	public Token(String content, int lineInd, int posInd)
	{
		
		this.lineInd = lineInd;
		this.posInd = posInd;
		this.symbol = new Symbol(-1, -1, content);
	}
	
	public Token(String content)
	{

		this.lineInd = -1;
		this.posInd = -1;
		this.symbol = new Symbol(-1, -1, content);
	}
	
	public Token(Symbol symbol, String content, int lineInd, int posInd)
	{
		this.symbol = symbol;
		this.lineInd = lineInd;
		this.posInd = posInd;
		this.symbol.content = content;
	}
	
}


//����������
//��������ȡ�������﷨���������
class GramHelper
{
	
	int tokenInd; //��ǰtoken�±�
	List<Token> tokenList; //token��
	 //�ɱ�Ż�ȡ��ʶ���������ַ���������������
	//�ַ�������''��Χ����ʶ������ĸ��ͷ��ʣ�µ�����������
	//
	
	
	
	public GramHelper(ArrayList<ArrayList<Symbol>> symbolList, HashMap<String, Integer> constantsAndSymbol2seqNum)
	{
		
//		//��������
//		this.seqNum2ConstantsAndSymbol = new HashMap<>();
//		for(Entry<String, Integer> entry : constantsAndSymbol2seqNum.entrySet())
//		{
//			this.seqNum2ConstantsAndSymbol.put(entry.getValue(), entry.getKey());
//		}
		
		//ƽ�� ���㴦��
		this.tokenList = new ArrayList<>();
		for(int lineInd = 0; lineInd < symbolList.size(); ++lineInd)
		{
			ArrayList<Symbol> symbolListInOneLine = symbolList.get(lineInd);
			for(int posInd = 0; posInd < symbolListInOneLine.size(); ++posInd)
			{
				Symbol symbol = symbolListInOneLine.get(posInd);
				String content = symbol.content; //seqNum2ConstantsAndSymbol.get(symbol.seqNum);
				
				Token token = new Token(symbol, content, lineInd, posInd);
				this.tokenList.add(token);
			}
				
		}
		
		//��ʼ���±�
		tokenInd = 0;
		

		
	}


	
//	private String getCotent(Symbol symbol)
//	{
//		
//		if(symbol.kindCode == LexAnalysis.CONSTANT || symbol.kindCode == LexAnalysis.IDENTIFIER || symbol.kindCode == LexAnalysis.CONST_CHARS)
//		{
//			return seqNum2ConstantsAndSymbol.get(symbol.kindCode);
//		}/*
//		else if(LexAnalysis.kindCode2Double(symbol.kindCode) != null)
//		{
//			return LexAnalysis.kindCode2Double(symbol.kindCode);
//		}
//		else if(LexAnalysis.kindCode2singleDelimiter.get(symbol.kindCode) != null)
//		{
//			return LexAnalysis.kindCode2singleDelimiter.get(symbol.kindCode).toString();
//		}*/
//		
//		else
//		{
//			return null;
//		}
//		
//	}



	//��ȡ��һ��token
	int nextToken()
	{
		if(tokenInd < tokenList.size())
		{
			return tokenInd++;			
		}
		return -1;
	}
	
	//��ȡ��ǰ��symbol
	Token getCurToken()
	{
		//
		return tokenList.get(tokenInd);
		
		
	}
	
	
	//��ȡ��ǰTokenʵ����symbol��contentֵ
	String getCurTokenContent()
	{
		return getCurToken().symbol.content;
		//return seqNum2ConstantsAndSymbol.get(getCurToken().symbol.seqNum);
	}
}

public class GramAndSemAnalysis
{
	
	
	
	//��Ա����
	
	//������
	GramHelper gramHelper;
	
	//��Ԫʽ��
	ArrayList<TAC> tacList;
	
	//������
	ArrayList<Token> variableList;
	
	//�����±�map
	HashMap<String, Integer> constantMap;
	
	//��ַ�±�
	int addressInd;
	
	//��ʱ�����±�
	int tempVarInd;
	
	
	//�����ʽ�������õ�ջ
	Stack<Token> calcStack;
	
	
	
	
	
	
	//������
	private void error(String errorMessage, Token token)
	{
		System.err.println("error: " + errorMessage + " in line " + token.lineInd + " token position " + token.posInd);
		System.err.flush();
		System.exit(0);
	}
	//������
	private void error(String errorMessage, int lineInd)
	{
		System.err.println("error: " + errorMessage + " in line " + lineInd);
		System.err.flush();
		System.exit(0);
	}
	//������
	private void error(String errorMessage)
	{
		System.err.println("error: " + errorMessage);
		System.err.flush();
		System.exit(0);
	}
	
	
	
	//���캯�������ôʷ������Ľ����ʼ��
	public GramAndSemAnalysis(ArrayList<ArrayList<Symbol>> symbolList, HashMap<String, Integer> constantsAndSymbol2seqNum)
	{
		//�������ʼ��
		this.gramHelper = new GramHelper(symbolList, constantsAndSymbol2seqNum);
		
		//���ݽṹ��ʼ��
		tacList = new ArrayList<>();
		variableList = new ArrayList<>();
		constantMap = new HashMap<>();
		calcStack = new Stack<>();
		
		
		//��ʱ�����±��ʼ�� T1 .. T2 .. T3
		tempVarInd = 1;
	}
	
	//<��> -> <��> * <����>��<��> / <����>��<����>
	private ChainState expression_term()
	{
		
		ChainState chainState_temp = new ChainState();
		
		chainState_temp.codeBegin = addressInd;
		chainState_temp = expression_factor();
		
		gramHelper.nextToken();
		
		{
			int curKindCode = gramHelper.getCurToken().symbol.kindCode;
			

			if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('*') || curKindCode == LexAnalysis.singleDelimiter2kindCode.get('/'))
			{
				//<��> * <����>|<��> / <����>
				Token op = gramHelper.getCurToken();
				chainState_temp = expression_term();
				Token u1 = calcStack.pop();
				Token u2 = calcStack.pop();
				Token temp_var = generateTempVariable();
				
				TAC tac = new TAC(op, u2, u1, temp_var);
				addTac(tac);
				
				calcStack.push(temp_var);
				
			}
		}
		
		return chainState_temp;
		
		
	}
	
	//<����> -> <������>��- <����>
	private ChainState expression_factor()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		gramHelper.nextToken();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('-'))
		{
			Token op = gramHelper.getCurToken();
			expression_factor();
			Token u1 = calcStack.pop();
			Token temp_var = generateTempVariable();
			
			//ȡ����Ż�ջ��
			calcStack.push(temp_var);
			
			//������Ԫʽ
			TAC tac = new TAC(op, u1, op, temp_var);
			addTac(tac);
			
		}// <������> -> <����>��<��ʶ��>���� <�������ʽ> ��
		else if(curKindCode == LexAnalysis.CONSTANT)
		{
			//��������
			calcStack.push(gramHelper.getCurToken());
		}
		else if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			//��ʶ��
			if (!judgeIsDefined()){
				error("undefined identifier " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
			}
			else if (getCurrUnitType() != VariableType.INTEGER)
			{
				error("expect variable type integer, but " + gramHelper.getCurToken().variableType + " is found", gramHelper.getCurToken());
			}
			calcStack.push(gramHelper.getCurToken());
		}
		else if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('('))
		{
			expression_arithmetic();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(')'))
			{
				error("expect \')\'", gramHelper.getCurToken().lineInd);
			}
		}
		else
		{
			error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		
		return chainState_temp;
	}
	
	//������ʱ���������Ҵ���һ����
	private Token generateTempVariable()
	{
		String content = "T" + String.valueOf(tempVarInd++);
		Token temp = new Token(content, LexAnalysis.IDENTIFIER, constantMap.size());
		constantMap.put(content, constantMap.size());
		return temp;
	}
	
	//<�������ʽ> -> <�������ʽ> + <��>��<�������ʽ> - <��>��<��>
	private ChainState expression_arithmetic()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		chainState_temp = expression_term();
		
		while(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.singleDelimiter2kindCode.get('+') || gramHelper.getCurToken().symbol.kindCode == LexAnalysis.singleDelimiter2kindCode.get('-')) 
		{
			Token op = gramHelper.getCurToken();
			chainState_temp = expression_term();
			if(calcStack.size() >= 2)
			{
				Token u1 = calcStack.pop();
				Token u2 = calcStack.pop();
				Token temp_var = generateTempVariable();
				
				TAC tac = new TAC(op, u2, u1, temp_var);
				addTac(tac);
				calcStack.push(temp_var);
				
			}
		}
		
		return chainState_temp;
		
	}
	
	
	//�ж��Ƿ����ж���
	private boolean judgeIsDefined()
	{
		for(Token token : variableList)
		{
			if(token.symbol.seqNum == gramHelper.getCurToken().symbol.seqNum)
			{
				return true;
			}
		}
		return false;
	}
	
	
	
	private Token rec_variableTypeDefine()
	{
		Token token_temp = new Token("-");

		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.keyWord2kindCode.get("integer"))
		{
			token_temp.variableType = VariableType.INTEGER;
			gramHelper.nextToken();
			
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("bool"))
		{
			token_temp.variableType = VariableType.BOOL;
			gramHelper.nextToken();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("char"))
		{
			token_temp.variableType = VariableType.CHAR;
			gramHelper.nextToken();
		}
		else
		{
			error("syntax error " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}

		return token_temp;
		
	}
	
	
	private Token multi_VariableDefine()
	{
		Token token_temp = gramHelper.getCurToken();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode; 
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get(','))
		{
			gramHelper.nextToken();
			token_temp.variableType = production_variableDefine().variableType;
		}
		else if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get(':'))
		{
			gramHelper.nextToken();
			token_temp.variableType = rec_variableTypeDefine().variableType;
		}
		else
		{
			error("syntax error " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		return token_temp;
		
	}
	
	
	//<��������> -> <��ʶ����> : <����> ;<��������>��<��ʶ����>  : <����> ;
	private Token production_variableDefine()
	{
		Token token_temp = gramHelper.getCurToken();
		
		if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.IDENTIFIER)
		{
			error("expect identifier", gramHelper.getCurToken().lineInd);
		}
		
		gramHelper.nextToken();
		
		token_temp.variableType = multi_VariableDefine().variableType; //��ʶ����������, �������������
		
		constantMap.put(token_temp.symbol.content, variableList.size()); //����ָ����±�
		
		variableList.add(token_temp);
		
		return token_temp;
		
		
		
		
	}
	
	private ChainState multi_VariableDeclare()
	{
		ChainState chainState_temp = new ChainState();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.IDENTIFIER)
		{
			production_variableDefine();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
			{
				error("expect \';\' after program", gramHelper.getCurToken().lineInd);
			}
		}
		return chainState_temp;
	}
	
	
	//<����˵��> -> var <��������>����
	private ChainState production_variableDeclare()
	{
		ChainState chainState_temp = new ChainState();
		//�жϱ����� var
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("var"))
		{
			gramHelper.nextToken();
			
			// <��������> չ��
			production_variableDefine();
			
			//�жϷֺ�
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
			{
				error("expect \';\' after program", gramHelper.getCurToken().lineInd);
			}
			
			
			gramHelper.nextToken();
			
			//��ʶ��
			while(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.IDENTIFIER)
			{
				chainState_temp = multi_VariableDeclare();
				
				gramHelper.nextToken();
			}
			
			//����һ��token
			--gramHelper.tokenInd;

		}
		return chainState_temp;
	}
	
	//<��ֵ��> -> <��ʶ��> := <�������ʽ>
	private ChainState statement_assign()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		if(!judgeIsDefined())
		{
			error("undefined identifier " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		
		if(variableList.get(constantMap.get(gramHelper.getCurTokenContent())).variableType != VariableType.INTEGER)
		{
			error("expect variable type integer, but " + gramHelper.getCurToken().variableType + " is found", gramHelper.getCurToken());
		}
		
		calcStack.push(gramHelper.getCurToken());
		
		gramHelper.nextToken();
		
		if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.getDoubleKindCode(":="))
		{
			error("expect assign token \":=\" ", gramHelper.getCurToken().lineInd);
		}
		
		Token assign_op = gramHelper.getCurToken();
		
		chainState_temp = expression_arithmetic();
		
		//��ֵ��Ԫʽ
		if(calcStack.size() >= 2)
		{
			Token u1 = calcStack.pop();
			Token u2 = calcStack.pop();
			Token null_unit = new Token("-");
			TAC tac = new TAC(assign_op, u1, null_unit, u2);
			addTac(tac);
		}
		
		return chainState_temp;

	}
	
	//<���Ͼ�> -> begin <����> end
	private ChainState statement_complexSentence()
	{
		ChainState chainState_temp = new ChainState();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("begin"))
		{
			gramHelper.nextToken();
			chainState_temp = production_sentenceList();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.keyWord2kindCode.get("end"))
			{
				error("expect keyword \"end\"", gramHelper.getCurToken().lineInd);
			}
			
			gramHelper.nextToken();
		}
		return chainState_temp;
	}
	
	

	
	
	//<����> -> <���>;<����>��<���>
	private ChainState production_sentenceList()
	{
		//<>
		ChainState chainState_temp = statement_sentence();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.singleDelimiter2kindCode.get(';'))
		{
			gramHelper.nextToken();
			//<>
			production_sentenceList();
		}
		return chainState_temp;
	}
	
	
	//<while��>  ->  while <�������ʽ> do <���>
	private ChainState statement_while()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		gramHelper.nextToken();
		chainState_temp = expression_bool();
		
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.keyWord2kindCode.get("do"))
		{
			gramHelper.nextToken();
			statement_sentence();
		}
		else
		{
			error("expect keyword \"do\"", gramHelper.getCurToken().lineInd);
		}
		
		TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token(String.valueOf(chainState_temp.codeBegin)));
		addTac(tac);
		
		for (int i = 0; i < chainState_temp.falseChain.size() - 1; ++i)
		{
			tacList.get(chainState_temp.falseChain.get(i)).resultToken.symbol.content = String.valueOf(addressInd);
		}
		int ind = chainState_temp.falseChain.get(chainState_temp.falseChain.size() - 1);
		tacList.get(ind).resultToken.symbol.content = String.valueOf(addressInd);
		return chainState_temp;
		
	}
	
	//<�������ʽ> -> <�������ʽ> or <������>��<������>
	private ChainState expression_bool()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		chainState_temp = expression_boolTerm();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("or"))
		{
			//Token op = gramHelper.getCurToken();
			gramHelper.nextToken();
			ChainState temp_state = expression_bool();
			chainState_temp.appendToTrueChain(temp_state);//or������ֱ�ӱ���ȴ�����
			//����һ��bool_Term����Ҫ����ļ�����������bool_Exp��code_begin
			for (int i = 0; i < chainState_temp.falseChain.size(); ++i)
			{
				tacList.get(chainState_temp.falseChain.get(i)).resultToken.symbol.content = String.valueOf(temp_state.codeBegin);
			}
			//����ǰ��bool_Exp�е�����Ǹ���Ҫ����ļ���������State��
			chainState_temp.appendToFalseChain(temp_state);
			if (chainState_temp.falseChain.size() >= 1)
			{
				chainState_temp.falseChain.set(0, chainState_temp.falseChain.get(chainState_temp.falseChain.size() - 1));
				
				//resize(1)
				int first = chainState_temp.falseChain.get(0);
				chainState_temp.falseChain.clear();
				chainState_temp.falseChain.add(first);
			}
		}
		//�������ʽ������������������ǰ���µ�ַ
		for (int i = 0; i < chainState_temp.trueChain.size(); ++i)
		{
			tacList.get(chainState_temp.trueChain.get(i)).resultToken.symbol.content = String.valueOf(addressInd);
		}
		return chainState_temp;
		
	}
	
	
	//<������> -> <������> and <������>��<������>
	private ChainState expression_boolTerm()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		
		chainState_temp = expression_boolFactor();
		
		
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("and"))
		{
			//Token op = gramHelper.getCurToken();
			gramHelper.nextToken();
			ChainState temp_state = expression_boolTerm();
			chainState_temp.appendToFalseChain(temp_state);//����ֱ�ӱ���ȴ�����
			for (int i = 0; i < chainState_temp.trueChain.size(); ++i)
			{
				//����һ��bool_factor������������ڵ�bool_Term��code_begin
				tacList.get(chainState_temp.trueChain.get(i)).resultToken.symbol.content = String.valueOf(temp_state.codeBegin);
			}
			//����ǰ��bool_Term�е�����Ǹ���Ҫ���������������State��
			chainState_temp.appendToTrueChain(temp_state);
			if (chainState_temp.trueChain.size() >= 1)
			{
				chainState_temp.trueChain.set(0, chainState_temp.trueChain.get(chainState_temp.trueChain.size() - 1));
				
				//resize(1)
				int first = chainState_temp.trueChain.get(0);
				chainState_temp.trueChain.clear();
				chainState_temp.trueChain.add(first);

			}
		}
		return chainState_temp;
		
	}
	
	//<������> -> <������>��not <������>
	private ChainState expression_boolFactor()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('-') || curKindCode == LexAnalysis.CONSTANT)
		{
			//����
			--gramHelper.tokenInd;
			
			//���ʽ����
			chainState_temp = expression_arithmetic();
			
			Token u1 = calcStack.pop();
			
			int type = gramHelper.getCurToken().symbol.kindCode;
			
			Token op = gramHelper.getCurToken();
			
			
			if(type >= 53 && type <= 58)
			{
				//ƥ���ϵ�� < <= <> = > >=
				chainState_temp = expression_arithmetic();
				 //��ȡ������
				Token u2 = calcStack.pop();
				
				//������Ԫʽ
				{
					TAC tac = new TAC(new Token("j" + op.symbol.content), u1, u2, new Token("-"));
					chainState_temp.trueChain.add(addTac(tac));
				}
				{
					TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token("-"));
					chainState_temp.falseChain.add(addTac(tac));
				}	
			}
			else
			{
				error("incomplete expression", gramHelper.getCurToken().lineInd);
			}
		}
		else if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			if(!judgeIsDefined())
			{
				error("undefined identifier " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());				
			}
			if(getCurrUnitType() == VariableType.INTEGER)
			{
				//����
				--gramHelper.tokenInd;
				chainState_temp = expression_arithmetic();
				
				Token u1 = calcStack.pop();
				int type = gramHelper.getCurToken().symbol.kindCode;
				Token op = gramHelper.getCurToken();
				
				if (type >= 53 && type <= 58)//ƥ���ϵ��
				{
					chainState_temp = expression_arithmetic();
					
					Token u2 = calcStack.pop();//��ȡ���ļ�����
					//����Ҫ����a>b�����
					{
						TAC tac = new TAC(new Token("j" + op.symbol.content), u1, u2, new Token("-"));
						chainState_temp.trueChain.add(addTac(tac));						
					}
					{
						TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token("-"));
						chainState_temp.falseChain.add(addTac(tac));
					}
				}
				else
				{
					error("incomplete expression", gramHelper.getCurToken().lineInd);
				}
			}
			else if(getCurrUnitType() == VariableType.BOOL)
			{
				{
					TAC tac = new TAC(new Token("jnz"), gramHelper.getCurToken(), Token.TOKEN_NULL, new Token("-"));
					chainState_temp.trueChain.add(addTac(tac));
				}
				{
					TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token("-"));
					chainState_temp.falseChain.add(addTac(tac));
				}
				gramHelper.nextToken();
				return chainState_temp;
			}
			else
			{
				error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
			}
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("not"))
		{
			//Token op = gramHelper.getCurToken();
			gramHelper.nextToken();
			chainState_temp = expression_boolFactor();
			chainState_temp.swapChain();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("true"))
		{
			TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token("-"));
			chainState_temp.trueChain.add(addTac(tac));
			calcStack.push(gramHelper.getCurToken());
			gramHelper.nextToken();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("false"))
		{
			TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token("-"));
			chainState_temp.falseChain.add(addTac(tac));
			calcStack.push(gramHelper.getCurToken());
			gramHelper.nextToken();
		}
		else if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('('))
		{
			gramHelper.nextToken();
			expression_bool();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(')'))
			{
				error("expect \')\'", gramHelper.getCurToken().lineInd);
			}
			gramHelper.nextToken();
		}
		else
		{
			error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		
		return chainState_temp;
		
		
	}
	private VariableType getCurrUnitType()
	{
		int ind = constantMap.get(gramHelper.getCurTokenContent());
		return variableList.get(ind).variableType;
	}
	private int addTac(TAC tac)
	{
		tacList.add(tac);
		return addressInd++;
	}
	
	//<if��>  -> if <�������ʽ> then <���>��if <�������ʽ> then <���> else <���>
	private ChainState statement_if()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		gramHelper.nextToken();
		chainState_temp = expression_bool();
		if (gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("then"))
		{
			gramHelper.nextToken();
			statement_sentence();
			TAC tac = new TAC(Token.TOKEN_J, Token.TOKEN_NULL, Token.TOKEN_NULL, new Token("0"));
			chainState_temp.trueChain.add(addTac(tac));
			int ind = chainState_temp.falseChain.get(chainState_temp.falseChain.size() - 1);
			tacList.get(ind).resultToken.symbol.content = String.valueOf(addressInd);
		}
		else
		{
			error("expect keyword \"then\"", gramHelper.getCurToken().lineInd);
		}
		if (gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("else"))
		{
			//else
			gramHelper.nextToken();
			for (Integer i : chainState_temp.falseChain)
			{
				tacList.get(i).resultToken.symbol.content = String.valueOf(addressInd);
			}
			statement_sentence();
			int ind = chainState_temp.trueChain.get(chainState_temp.trueChain.size() - 1);
			tacList.get(ind).resultToken.symbol.content = String.valueOf(addressInd);
		}
		return chainState_temp;
	}
	
	// <repeat��>   -> repeat <���>  until <�������ʽ>
	private ChainState statement_repeat()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		gramHelper.nextToken();
		statement_sentence();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if (curKindCode == LexAnalysis.keyWord2kindCode.get("until"))
		{
			gramHelper.nextToken();
			ChainState temp_state = expression_bool();
			int ind = temp_state.falseChain.get(temp_state.falseChain.size() - 1);
			tacList.get(ind).resultToken.symbol.content = String.valueOf(chainState_temp.codeBegin);
		}
		else
		{
			error("expect keyword \"then\"", gramHelper.getCurToken().lineInd);
		}
		return chainState_temp;
	}
	
	
	//<���> -> <��ֵ��>��<if��>��<while��>��<repeat��>��<���Ͼ�>
	private ChainState statement_sentence()
	{
		ChainState chainState_temp = new ChainState();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			// <��ֵ���ʽ> չ��
			chainState_temp = statement_assign();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("begin"))
		{
			// <���Ͼ�> չ��
			chainState_temp = statement_complexSentence();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("while"))
		{
			// <while��>	 չ��
			chainState_temp = statement_while();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("if"))
		{
			// <if��> չ��
			chainState_temp = statement_if();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("repeat"))
		{
			// <repeat��> չ��
			chainState_temp = statement_repeat();
		}
		else
		{
			error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		
		return chainState_temp;
		
	}


	
	//<����> -> program <��ʶ��> ; <����˵��> <�������> .
	private ChainState production_program()
	{
		ChainState chainState_temp = new ChainState();
		//gramHelper.getNextToken();
		switch (gramHelper.getCurToken().symbol.kindCode)
		{
			//program ������
			case 23:
			{
				//��ȡ��ǰ��token
				Token token_temp = gramHelper.getCurToken();
				
				//program
				gramHelper.nextToken();
				
				//����ʶ��
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.IDENTIFIER)
				{
					error("expect program name", gramHelper.getCurToken());
				}
				
				//������Ԫʽ	��������
				{
					TAC tac = new TAC(token_temp, gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken());
					addTac(tac);
				}
				
				//��һ����
				gramHelper.nextToken();
				
				//�жϷֺ�
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
				{
					error("expect \';\' after program", gramHelper.getCurToken().lineInd);
				}
				
				//��һ����
				gramHelper.nextToken();
				
				// <����˵��> չ��
				chainState_temp = production_variableDeclare();
				
				//��һ����
				gramHelper.nextToken();
				
				//<�������> չ��
				statement_complexSentence();
				
				//���������� .
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get('.'))
				{
					error("expect \'.\' for program ending");
				}
				
				//������Ԫʽ	�������
				{
					TAC tac = new TAC(gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken());
					addTac(tac);
				}
			}
			break;

			default:
			{
				error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
			}
			break;
		}
		return chainState_temp;
	}
	public void parse()
	{
		// ��ʼ�ݹ��½���������ʼ��Ϊ<����>
		production_program();
		
		unconditionalJumpCompress();
	}
	public void printTAC()
	{

		for (int k = 0; k < tacList.size(); ++k)
		{
			TAC tac = tacList.get(k);
			
			//��Ԫʽ���
			String tacInd = "(" + k + ")";
			
			if (tac.opToken.symbol.kindCode == 23)
			{
				//program
				System.out.println(tacInd + "(" + tac.opToken.symbol.content + ",\t" + tac.argToken1.symbol.content + ",\t-,\t-)");
			}
			else if (tac.opToken.symbol.kindCode == 46)
			{
				//end.
				System.out.println(tacInd + "(sys,\t-,\t-,\t-)");
			}
			else
			{
				// �������
				System.out.println(tacInd + tac.toTAC_String());
				//System.out.println(tacInd + "(" + tac.opToken.symbol.content + "," + tac.argToken1.symbol.content + "," + tac.argToken2.symbol.content + "," + tac.resultToken.symbol.content + ")");
			}
			
		}
	}
	
	//��������������jump�������ѹ������������ת
	private void unconditionalJumpCompress()
	{
		for (TAC curJump : tacList){
			int ind = 0;
			TAC nextJump = curJump;
			while (nextJump.opToken.symbol.content.equals("j") )
			{
				ind = Integer.parseInt(nextJump.resultToken.symbol.content);
				nextJump = tacList.get(ind);
			}
			if (curJump.opToken.symbol.content.equals("j"))
			{
				curJump.resultToken.symbol.content = String.valueOf(ind);
			}
		}
		
	}
}
