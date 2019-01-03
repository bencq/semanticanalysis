package compilers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Stack;



//����״̬��
class ChainState
{
	ArrayList<Integer> trueChain;
	ArrayList<Integer> falseChain;
	
	int codeBegin;
	boolean accept;
	
	public ChainState()
	{
		trueChain = new ArrayList<>();
		falseChain = new ArrayList<>();
		accept = true;
	}
	
	void addTrueChain(ChainState chainState) //addToTrueChain
	{
		trueChain.addAll(chainState.trueChain);
	}
	
	void addFalseChain(ChainState chainState) //addToFalseChain
	{
		falseChain.addAll(chainState.falseChain);
	}
	
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
	
	Token opToken;
	Token valueToken1;
	Token valueToken2;
	Token resultToken;
	public TAC(Token opToken, Token valueToken1, Token valueToken2, Token resultToken)
	{
		super();
		this.opToken = opToken;
		this.valueToken1 = valueToken1;
		this.valueToken2 = valueToken2;
		this.resultToken = resultToken;
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
		// TODO Auto-generated method stub
		return super.toString().toLowerCase();
	}
	
	
}

//����
class Token
{
	Symbol symbol;
	int lineInd; //�к�
	int posInd; //�ڸ��е�λ��
	
	String content;
	
	VariableType variableType;
	
	
	
	public Token(String content, int lineInd, int posInd)
	{
		this.content = content;
		this.lineInd = lineInd;
		this.posInd = posInd;
	}
	
	public Token(String content)
	{
		this.content = content;
		this.lineInd = 0;
		this.posInd = 0;
	}
	
	public Token(Symbol symbol, String content, int lineInd, int posInd)
	{
		this.symbol = symbol;
		this.lineInd = lineInd;
		this.posInd = posInd;
		this.content = content;
	}
	
}


//������
//��������ȡ�������﷨���������
class GramHelper
{
	
	int tokenInd; //��ǰtoken�±�
	List<Token> tokenList; //token��
	 //�ɱ�Ż�ȡ��ʶ���������ַ���������������
	//�ַ�������''��Χ����ʶ������ĸ��ͷ��ʣ�µ�����������
	HashMap<Integer, String> seqNum2ConstantsAndSymbol;
	
	
	
	public GramHelper(ArrayList<ArrayList<Symbol>> symbolList, HashMap<String, Integer> constantsAndSymbol2seqNum)
	{
		
		//��������
		this.seqNum2ConstantsAndSymbol = new HashMap<>();
		for(Entry<String, Integer> entry : constantsAndSymbol2seqNum.entrySet())
		{
			this.seqNum2ConstantsAndSymbol.put(entry.getValue(), entry.getKey());
		}
		
		//ƽ�� ���㴦��
		this.tokenList = new ArrayList<>();
		for(int lineInd = 0; lineInd < symbolList.size(); ++lineInd)
		{
			ArrayList<Symbol> symbolListInOneLine = symbolList.get(lineInd);
			for(int posInd = 0; posInd < symbolListInOneLine.size(); ++posInd)
			{
				Symbol symbol = symbolListInOneLine.get(posInd);
				String content = /*getCotent(symbol);*/
				seqNum2ConstantsAndSymbol.get(symbol.seqNum);
				Token token = new Token(symbol, content, lineInd, posInd);
				this.tokenList.add(token);
			}
				
		}
		
		//��ʼ���±�
		tokenInd = 0;
		

		
	}



	private String getCotent(Symbol symbol)
	{
		
		if(symbol.kindCode == LexAnalysis.CONSTANT || symbol.kindCode == LexAnalysis.IDENTIFIER || symbol.kindCode == LexAnalysis.CONST_CHARS)
		{
			return seqNum2ConstantsAndSymbol.get(symbol.kindCode);
		}/*
		else if(LexAnalysis.kindCode2Double(symbol.kindCode) != null)
		{
			return LexAnalysis.kindCode2Double(symbol.kindCode);
		}
		else if(LexAnalysis.kindCode2singleDelimiter.get(symbol.kindCode) != null)
		{
			return LexAnalysis.kindCode2singleDelimiter.get(symbol.kindCode).toString();
		}*/
		
		else
		{
			return null;
		}
		
	}



	int getNextToken()
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
		Token t = tokenList.get(tokenInd);
		if(t.variableType != null) System.out.println("not null");
		return t;
	}
	
	String getCurTokenContent()
	{
		return seqNum2ConstantsAndSymbol.get(getCurToken().symbol.seqNum);
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
	int addressNum;
	
	//��ʱ�����±�
	int sys_tempVar;
	
	Stack<Token> cal_stack;
	
	
	
	
	
	
	//������
	void error(String errorMessage, Token token)
	{
		System.err.println("error: " + errorMessage + " in line " + token.lineInd + " token position " + token.posInd);
		System.err.flush();
		System.exit(0);
	}
	//������
	void error(String errorMessage, int lineInd)
	{
		System.err.println("error: " + errorMessage + " in line " + lineInd);
		System.err.flush();
		System.exit(0);
	}
	//������
	void error(String errorMessage)
	{
		System.err.println("error: " + errorMessage);
		System.err.flush();
		System.exit(0);
	}
	
	
	
	public GramAndSemAnalysis(ArrayList<ArrayList<Symbol>> symbolList, HashMap<String, Integer> constantsAndSymbol2seqNum)
	{
		this.gramHelper = new GramHelper(symbolList, constantsAndSymbol2seqNum);
		
		
		tacList = new ArrayList<>();
		
		variableList = new ArrayList<>();
		
		constantMap = new HashMap<>();
		
		cal_stack = new Stack<>();
	}
	
	
	private ChainState Term()
	{
		
		ChainState chainState_temp = new ChainState();
		
		chainState_temp.codeBegin = addressNum;
		chainState_temp = factor();
		
		gramHelper.getNextToken();
		
		{
			int curKindCode = gramHelper.getCurToken().symbol.kindCode;
			if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('*') || curKindCode == LexAnalysis.singleDelimiter2kindCode.get('/'))
			{
				Token op = gramHelper.getCurToken();
				chainState_temp = Term();
				Token u1 = cal_stack.pop();
				Token u2 = cal_stack.pop();
				Token temp_var = generateTempVar();
				
				TAC tac = new TAC(op, u2, u1, temp_var);
				pushTac(tac);
				
				cal_stack.push(temp_var);
				
			}
		}
		
		return chainState_temp;
		
		
	}
	
	private ChainState factor()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		gramHelper.getNextToken();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('-'))
		{
			Token op = gramHelper.getCurToken();
			factor();
			Token u1 = cal_stack.pop();
			Token temp_var = generateTempVar();
			
			//ȡ����Ż�ջ��
			cal_stack.push(temp_var);
			
			//������Ԫʽ
			TAC tac = new TAC(op, u1, op, temp_var);
			pushTac(tac);
			
		}
		else if(curKindCode == LexAnalysis.CONSTANT)
		{
			//��������
			cal_stack.push(gramHelper.getCurToken());
		}
		else if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			//��ʶ��
			if (!checkDefine()){
				error("undefined identifier " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
			}
			else if (getCurrUnitType() != VariableType.INTEGER)
			{
				error("expect variable type integer, but " + gramHelper.getCurToken().variableType + " is found", gramHelper.getCurToken());
			}
			cal_stack.push(gramHelper.getCurToken());
		}
		else if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('('))
		{
			cacl_exp();
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
	private Token generateTempVar()
	{
		String content = "T" + String.valueOf(sys_tempVar++);
		Token temp = new Token(content, LexAnalysis.IDENTIFIER, constantMap.size());
		constantMap.put(content, constantMap.size());
		return temp;
	}
	private ChainState cacl_exp()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		chainState_temp = Term();
		
		while(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.singleDelimiter2kindCode.get('+') || gramHelper.getCurToken().symbol.kindCode == LexAnalysis.singleDelimiter2kindCode.get('-')) 
		{
			Token op = gramHelper.getCurToken();
			chainState_temp = Term();
			if(cal_stack.size() >= 2)
			{
				Token u1 = cal_stack.pop();
				Token u2 = cal_stack.pop();
				Token temp_var = generateTempVar();
				
				TAC tac = new TAC(op, u2, u1, temp_var);
				pushTac(tac);
				
			}
		}
		
		return chainState_temp;
		
	}
	
	
	
	boolean checkDefine()
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
	
	
	
	Token typeDefine()
	{
		Token token_temp = new Token("-");

		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.keyWord2kindCode.get("integer"))
		{
			token_temp.variableType = VariableType.INTEGER;
			gramHelper.getNextToken();
			
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("bool"))
		{
			token_temp.variableType = VariableType.BOOL;
			gramHelper.getNextToken();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("char"))
		{
			token_temp.variableType = VariableType.CHAR;
			gramHelper.getNextToken();
		}
		else
		{
			error("syntax error " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}

		return token_temp;
		
	}
	
	
	Token remainVariableDefine()
	{
		Token token_temp = gramHelper.getCurToken();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode; 
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get(','))
		{
			gramHelper.getNextToken();
			token_temp.variableType = valueDefine().variableType;
		}
		else if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get(':'))
		{
			gramHelper.getNextToken();
			token_temp.variableType = typeDefine().variableType;
		}
		else
		{
			error("syntax error " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		return token_temp;
		
	}
	
	Token valueDefine()
	{
		Token token_temp = gramHelper.getCurToken();
		
		if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.IDENTIFIER)
		{
			error("expect identifier", gramHelper.getCurToken().lineInd);
		}
		
		gramHelper.getNextToken();
		
		token_temp.variableType = remainVariableDefine().variableType; //��ʶ����������, �������������
		
		constantMap.put(token_temp.content, variableList.size()); //����ָ����±�
		
		variableList.add(token_temp);
		
		return token_temp;
		
		
		
		
	}
	
	ChainState production_multiVariableDeclare()
	{
		ChainState chainState_temp = new ChainState();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.IDENTIFIER)
		{
			valueDefine();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
			{
				error("expect \';\' after program", gramHelper.getCurToken().lineInd);
			}
		}
		return chainState_temp;
	}
	
	
	//<����˵��> -> var <��������>����
	ChainState production_variableDeclare()
	{
		ChainState chainState_temp = new ChainState();
		//�жϱ����� var
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("var"))
		{
			gramHelper.getNextToken();
			
			// <��������> չ��
			valueDefine();
			
			//�жϷֺ�
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
			{
				error("expect \';\' after program", gramHelper.getCurToken().lineInd);
			}
			
			
			gramHelper.getNextToken();
			
			//��ʶ��
			while(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.IDENTIFIER)
			{
				chainState_temp = production_multiVariableDeclare();
				
				gramHelper.getNextToken();
			}
			
			//����һ��token
			--gramHelper.tokenInd;

		}
		return chainState_temp;
	}
	
	
	ChainState production_assignExpression()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		if(!checkDefine())
		{
			error("undefined identifier", gramHelper.getCurToken());
		}
		
		if(variableList.get(constantMap.get(gramHelper.getCurTokenContent())).variableType != VariableType.INTEGER)
		{
			error("expect variable type integer, but " + gramHelper.getCurToken().variableType + " is found", gramHelper.getCurToken());
		}
		
		cal_stack.push(gramHelper.getCurToken());
		
		gramHelper.getNextToken();
		
		if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.getDoubleKindCode(":="))
		{
			error("expect assign token \":=\" ", gramHelper.getCurToken().lineInd);
		}
		
		Token assign_op = gramHelper.getCurToken();
		
		chainState_temp = cacl_exp();
		
		if(cal_stack.size() >= 2)
		{
			Token u1 = cal_stack.pop();
			Token u2 = cal_stack.pop();
			Token null_unit = new Token("-");
			TAC tac = new TAC(assign_op, u1, null_unit, u2);
			pushTac(tac);
		}
		
		return chainState_temp;

	}
	
	
	ChainState production_complexSentence()
	{
		ChainState chainState_temp = new ChainState();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("begin"))
		{
			gramHelper.getNextToken();
			chainState_temp = production_sentenceList();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.keyWord2kindCode.get("end"))
			{
				error("expect keyword \"end\"", gramHelper.getCurToken().lineInd);
			}
			
			gramHelper.getNextToken();
		}
		return chainState_temp;
	}
	
	

	
	
	
	ChainState production_sentenceList()
	{
		//<>
		ChainState chainState_temp = production_sentence();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.singleDelimiter2kindCode.get(';'))
		{
			gramHelper.getNextToken();
			//<>
			production_sentenceList();
		}
		return chainState_temp;
	}
	
	ChainState production_whileExpression()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		gramHelper.getNextToken();
		chainState_temp = bool_Exp();
		
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.keyWord2kindCode.get("do"))
		{
			gramHelper.getNextToken();
			production_sentence();
		}
		else
		{
			error("expect keyword \"do\"", gramHelper.getCurToken().lineInd);
		}
		
		TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token(String.valueOf(chainState_temp.codeBegin)));
		pushTac(tac);
		
		for (int i = 0; i < chainState_temp.falseChain.size() - 1; ++i)
		{
			tacList.get(chainState_temp.falseChain.get(i)).resultToken.content = String.valueOf(addressNum);
		}
		int ind = chainState_temp.falseChain.get(chainState_temp.falseChain.size() - 1);
		tacList.get(ind).resultToken.content = String.valueOf(addressNum);
		return chainState_temp;
		
	}
	
	private ChainState bool_Exp()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		chainState_temp = bool_Term();
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("or"))
		{
			Token op = gramHelper.getCurToken();
			gramHelper.getNextToken();
			ChainState temp_state = bool_Exp();
			chainState_temp.addTrueChain(temp_state);//or������ֱ�ӱ���ȴ�����
			//����һ��bool_Term����Ҫ����ļ�����������bool_Exp��code_begin
			for (int i = 0; i < chainState_temp.falseChain.size(); ++i)
			{
				tacList.get(chainState_temp.falseChain.get(i)).resultToken.content = String.valueOf(temp_state.codeBegin);
			}
			//����ǰ��bool_Exp�е�����Ǹ���Ҫ����ļ���������State��
			chainState_temp.addFalseChain(temp_state);
			if (chainState_temp.falseChain.size() >= 1)
			{
				chainState_temp.falseChain.set(0, chainState_temp.falseChain.get(chainState_temp.falseChain.size() - 1));
				
				//resize(1)
				int first = chainState_temp.falseChain.get(0);
				chainState_temp.falseChain.clear();
				chainState_temp.falseChain.add(first);
				
				
			}
		}
		return chainState_temp;
		
	}
	
	private ChainState bool_Term()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		
		chainState_temp = bool_factor();
		
		
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("and"))
		{
			Token op = gramHelper.getCurToken();
			gramHelper.getNextToken();
			ChainState temp_state = bool_Term();
			chainState_temp.addFalseChain(temp_state);//����ֱ�ӱ���ȴ�����
			for (int i = 0; i < chainState_temp.trueChain.size(); ++i)
			{
				//����һ��bool_factor������������ڵ�bool_Term��code_begin
				tacList.get(chainState_temp.trueChain.get(i)).resultToken.content = String.valueOf(temp_state.codeBegin);
			}
			//����ǰ��bool_Term�е�����Ǹ���Ҫ���������������State��
			chainState_temp.addTrueChain(temp_state);
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
	private ChainState bool_factor()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('-') || curKindCode == LexAnalysis.CONSTANT)
		{
			//����
			--gramHelper.tokenInd;
			
			//���ʽ����
			chainState_temp = cacl_exp();
			
			Token u1 = cal_stack.pop();
			
			int type = gramHelper.getCurToken().symbol.kindCode;
			
			Token op = gramHelper.getCurToken();
			
			
			if(type >= 53 && type <= 58)
			{
				//ƥ���ϵ�� < <= <> = > >=
				chainState_temp = cacl_exp();
				 //��ȡ������
				Token u2 = cal_stack.pop();
				
				//������Ԫʽ
				{
					TAC tac = new TAC(new Token("j" + op.content), u1, u2, new Token("-"));
					chainState_temp.trueChain.add(pushTac(tac));
				}
				{
					TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token("-"));
					chainState_temp.falseChain.add(pushTac(tac));
				}	
			}
			else
			{
				error("incomplete expression", gramHelper.getCurToken().lineInd);
			}
		}
		else if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			if(!checkDefine())
			{
				error("undefined identifier " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());				
			}
			if(getCurrUnitType() == VariableType.INTEGER)
			{
				//����
				--gramHelper.tokenInd;
				chainState_temp = cacl_exp();
				
				Token u1 = cal_stack.pop();
				int type = gramHelper.getCurToken().symbol.kindCode;
				Token op = gramHelper.getCurToken();
				
				if (type >= 53 && type <= 58)//ƥ���ϵ��
				{
					chainState_temp = cacl_exp();
					if (!chainState_temp.accept)
					{
						return chainState_temp;
					}
					Token u2 = cal_stack.pop();//��ȡ���ļ�����
					//����Ҫ����a>b�����
					{
						TAC tac = new TAC(new Token("j" + op.content), u1, u2, new Token("-"));
						chainState_temp.trueChain.add(pushTac(tac));						
					}
					{
						TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token("-"));
						chainState_temp.falseChain.add(pushTac(tac));
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
					TAC tac = new TAC(new Token("jnz"), gramHelper.getCurToken(), new Token("-"), new Token("-"));
					chainState_temp.trueChain.add(pushTac(tac));
				}
				{
					TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token("-"));
					chainState_temp.falseChain.add(pushTac(tac));
				}
				gramHelper.getNextToken();
				return chainState_temp;
			}
			else
			{
				error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
			}
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("not"))
		{
			Token op = gramHelper.getCurToken();
			gramHelper.getNextToken();
			chainState_temp = bool_factor();
			chainState_temp.swapChain();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("true"))
		{
			TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token("-"));
			chainState_temp.trueChain.add(pushTac(tac));
			cal_stack.push(gramHelper.getCurToken());
			gramHelper.getNextToken();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("false"))
		{
			TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token("-"));
			chainState_temp.falseChain.add(pushTac(tac));
			cal_stack.push(gramHelper.getCurToken());
			gramHelper.getNextToken();
		}
		else if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('('))
		{
			gramHelper.getNextToken();
			bool_Exp();
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(')'))
			{
				error("expect \')\'", gramHelper.getCurToken().lineInd);
			}
			gramHelper.getNextToken();
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
	private int pushTac(TAC tac)
	{
		tacList.add(tac);
		return addressNum++;
	}
	ChainState production_ifExpression()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		gramHelper.getNextToken();
		chainState_temp = bool_Exp();
		//int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if (gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("then"))
		{
			gramHelper.getNextToken();
			production_sentence();
			TAC tac = new TAC(new Token("jump"), new Token("-"), new Token("-"), new Token("0"));
			chainState_temp.trueChain.add(pushTac(tac));
			int ind = chainState_temp.falseChain.get(chainState_temp.falseChain.size() - 1);
			tacList.get(ind).resultToken.content = String.valueOf(addressNum);
		}
		else
		{
			error("expect keyword \"then\"", gramHelper.getCurToken().lineInd);
		}
		if (gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("else"))
		{
			//else
			gramHelper.getNextToken();
			for (Integer i : chainState_temp.falseChain)
			{
				tacList.get(i).resultToken.content = String.valueOf(addressNum);
			}
			production_sentence();
			int ind = chainState_temp.trueChain.get(chainState_temp.trueChain.size() - 1);
			tacList.get(ind).resultToken.content = String.valueOf(addressNum);
		}
		return chainState_temp;
	}
	
	ChainState production_repeatExpression()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressNum;
		gramHelper.getNextToken();
		production_sentence();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if (curKindCode == LexAnalysis.keyWord2kindCode.get("until"))
		{
			gramHelper.getNextToken();
			ChainState temp_state = bool_Exp();
			int ind = temp_state.falseChain.get(temp_state.falseChain.size() - 1);
			tacList.get(ind).resultToken.content = String.valueOf(chainState_temp.codeBegin);
		}
		else
		{
			error("expect keyword \"then\"", gramHelper.getCurToken().lineInd);
		}
		return chainState_temp;
	}
	
	ChainState production_sentence()
	{
		ChainState chainState_temp = new ChainState();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			// <��ֵ���ʽ> չ��
			chainState_temp = production_assignExpression();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("begin"))
		{
			// <���Ͼ�> չ��
			chainState_temp = production_complexSentence();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("while"))
		{
			// <while��>	 չ��
			chainState_temp = production_whileExpression();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("if"))
		{
			// <if��> չ��
			chainState_temp = production_ifExpression();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("repeat"))
		{
			// <repeat��> չ��
			chainState_temp = production_repeatExpression();
		}
		else
		{
			error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		
		return chainState_temp;
		
	}


	
	//<����> -> program <��ʶ��> ; <����˵��> <�������> .
	ChainState production_program()
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
				gramHelper.getNextToken();
				
				//����ʶ��
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.IDENTIFIER)
				{
					error("expect program name", gramHelper.getCurToken());
				}
				
				//������Ԫʽ	��������
				{
					TAC tac = new TAC(token_temp, gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken());
					pushTac(tac);
				}
				
				//��һ����
				gramHelper.getNextToken();
				
				//�жϷֺ�
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
				{
					error("expect \';\' after program", gramHelper.getCurToken().lineInd);
				}
				
				//��һ����
				gramHelper.getNextToken();
				
				// <����˵��> չ��
				chainState_temp = production_variableDeclare();
				
				//��һ����
				gramHelper.getNextToken();
				
				//<�������> չ��
				production_complexSentence();
				
				//���������� .
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get('.'))
				{
					error("expect \'.\' for program ending");
				}
				
				//������Ԫʽ	�������
				{
					TAC tac = new TAC(gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken());
					pushTac(tac);
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
		gramHelper.tokenInd = 0;
		//gramHelper.getNextToken();
		production_program();
		
	}
	public void printTAC()
	{
		compressJump();
		int i = 0;
		for (TAC element : tacList)
		{
			String lineNum = "(" + i + ")";
			if (element.opToken.symbol.kindCode == 23)
			{//program
				System.out.println(lineNum + "(" + element.opToken.content + "," + element.valueToken1.content + ",-,-)");
			}
			else if (element.opToken.symbol.kindCode == 46)
			{//���������.
				System.out.println(lineNum + "(sys,-,-,-)");
			}
			else
			{// ��Ԫ���*
				System.out.println(lineNum + "(" + element.opToken.content + "," + element.valueToken1.content + "," + element.valueToken2.content + "," + element.resultToken.content + ")");
			}
			++i;
		}
	}
	private void compressJump()
	{
		for (TAC element : tacList){
			int ind = 0;
			TAC nextJump = element;
			while (/*nextJump.opToken.content != null && */nextJump.opToken.content.equals("jump") )
			{
				ind = Integer.parseInt(nextJump.resultToken.content);
				nextJump = tacList.get(ind);
			}
			if (/*element.opToken.content != null && */element.opToken.content.equals("jump"))
			{
				element.resultToken.content = String.valueOf(ind);
			}
		}
		
	}
}
