package compilers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import java.util.Stack;



//回填状态类
class ChainState
{
	//真链和假链
	ArrayList<Integer> trueChain;
	ArrayList<Integer> falseChain;
	
	//开始的下标
	int codeBegin;
	
	
	public ChainState()
	{
		trueChain = new ArrayList<>();
		falseChain = new ArrayList<>();
	}
	
	//将参数链的
	void appendToTrueChain(ChainState chainState)
	{
		trueChain.addAll(chainState.trueChain);
	}
	
	//
	void appendToFalseChain(ChainState chainState)
	{
		falseChain.addAll(chainState.falseChain);
	}
	
	//真假链交换
	void swapChain()
	{
		ArrayList<Integer> temp = trueChain;
		trueChain = falseChain;
		falseChain = temp;
	}


	
	
}

//四元式类
class TAC
{
	
	Token opToken; //操作符
	Token argToken1; //第一个参数符
	Token argToken2; //第二个参数符
	Token resultToken; //结果符
	
	
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

//变量类型
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

//符号
class Token
{
	
	//常量Token
	final static Token TOKEN_J = new Token("j");
	final static Token TOKEN_NULL = new Token("-");
	
	
	Symbol symbol;
	int lineInd; //行号
	int posInd; //在该行的位置
	
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


//分析帮助类
//用来方便取符号作语法和语义分析
class GramHelper
{
	
	int tokenInd; //当前token下标
	List<Token> tokenList; //token表
	 //由标号获取标识符或常量：字符常量，整数常量
	//字符常量以''包围，标识符以字母开头，剩下的是整数常量
	//
	
	
	
	public GramHelper(ArrayList<ArrayList<Symbol>> symbolList, HashMap<String, Integer> constantsAndSymbol2seqNum)
	{
		
//		//反向索引
//		this.seqNum2ConstantsAndSymbol = new HashMap<>();
//		for(Entry<String, Integer> entry : constantsAndSymbol2seqNum.entrySet())
//		{
//			this.seqNum2ConstantsAndSymbol.put(entry.getValue(), entry.getKey());
//		}
		
		//平铺 方便处理
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
		
		//初始化下标
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



	//获取下一个token
	int nextToken()
	{
		if(tokenInd < tokenList.size())
		{
			return tokenInd++;			
		}
		return -1;
	}
	
	//获取当前的symbol
	Token getCurToken()
	{
		//
		return tokenList.get(tokenInd);
		
		
	}
	
	
	//获取当前Token实例的symbol的content值
	String getCurTokenContent()
	{
		return getCurToken().symbol.content;
		//return seqNum2ConstantsAndSymbol.get(getCurToken().symbol.seqNum);
	}
}

public class GramAndSemAnalysis
{
	
	
	
	//成员变量
	
	//辅助类
	GramHelper gramHelper;
	
	//四元式表
	ArrayList<TAC> tacList;
	
	//变量表
	ArrayList<Token> variableList;
	
	//常量下标map
	HashMap<String, Integer> constantMap;
	
	//地址下标
	int addressInd;
	
	//临时变量下标
	int tempVarInd;
	
	
	//作表达式计算所用的栈
	Stack<Token> calcStack;
	
	
	
	
	
	
	//出错处理
	private void error(String errorMessage, Token token)
	{
		System.err.println("error: " + errorMessage + " in line " + token.lineInd + " token position " + token.posInd);
		System.err.flush();
		System.exit(0);
	}
	//出错处理
	private void error(String errorMessage, int lineInd)
	{
		System.err.println("error: " + errorMessage + " in line " + lineInd);
		System.err.flush();
		System.exit(0);
	}
	//出错处理
	private void error(String errorMessage)
	{
		System.err.println("error: " + errorMessage);
		System.err.flush();
		System.exit(0);
	}
	
	
	
	//构造函数，利用词法分析的结果初始化
	public GramAndSemAnalysis(ArrayList<ArrayList<Symbol>> symbolList, HashMap<String, Integer> constantsAndSymbol2seqNum)
	{
		//帮助类初始化
		this.gramHelper = new GramHelper(symbolList, constantsAndSymbol2seqNum);
		
		//数据结构初始化
		tacList = new ArrayList<>();
		variableList = new ArrayList<>();
		constantMap = new HashMap<>();
		calcStack = new Stack<>();
		
		
		//临时变量下标初始化 T1 .. T2 .. T3
		tempVarInd = 1;
	}
	
	//<项> -> <项> * <因子>│<项> / <因子>│<因子>
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
				//<项> * <因子>|<项> / <因子>
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
	
	//<因子> -> <算术量>│- <因子>
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
			
			//取负后放回栈内
			calcStack.push(temp_var);
			
			//生成四元式
			TAC tac = new TAC(op, u1, op, temp_var);
			addTac(tac);
			
		}// <算术量> -> <整数>│<标识符>│（ <算术表达式> ）
		else if(curKindCode == LexAnalysis.CONSTANT)
		{
			//整数常量
			calcStack.push(gramHelper.getCurToken());
		}
		else if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			//标识符
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
	
	//生成临时变量，并且处理一致性
	private Token generateTempVariable()
	{
		String content = "T" + String.valueOf(tempVarInd++);
		Token temp = new Token(content, LexAnalysis.IDENTIFIER, constantMap.size());
		constantMap.put(content, constantMap.size());
		return temp;
	}
	
	//<算术表达式> -> <算术表达式> + <项>│<算术表达式> - <项>│<项>
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
	
	
	//判断是否已有定义
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
	
	
	//<变量定义> -> <标识符表> : <类型> ;<变量定义>│<标识符表>  : <类型> ;
	private Token production_variableDefine()
	{
		Token token_temp = gramHelper.getCurToken();
		
		if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.IDENTIFIER)
		{
			error("expect identifier", gramHelper.getCurToken().lineInd);
		}
		
		gramHelper.nextToken();
		
		token_temp.variableType = multi_VariableDefine().variableType; //标识符后续声明, 并保存变量类型
		
		constantMap.put(token_temp.symbol.content, variableList.size()); //更新指向的下标
		
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
	
	
	//<变量说明> -> var <变量定义>│ε
	private ChainState production_variableDeclare()
	{
		ChainState chainState_temp = new ChainState();
		//判断保留字 var
		if(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.keyWord2kindCode.get("var"))
		{
			gramHelper.nextToken();
			
			// <变量定义> 展开
			production_variableDefine();
			
			//判断分号
			if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
			{
				error("expect \';\' after program", gramHelper.getCurToken().lineInd);
			}
			
			
			gramHelper.nextToken();
			
			//标识符
			while(gramHelper.getCurToken().symbol.kindCode == LexAnalysis.IDENTIFIER)
			{
				chainState_temp = multi_VariableDeclare();
				
				gramHelper.nextToken();
			}
			
			//回溯一个token
			--gramHelper.tokenInd;

		}
		return chainState_temp;
	}
	
	//<赋值句> -> <标识符> := <算术表达式>
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
		
		//赋值四元式
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
	
	//<复合句> -> begin <语句表> end
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
	
	

	
	
	//<语句表> -> <语句>;<语句表>│<语句>
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
	
	
	//<while句>  ->  while <布尔表达式> do <语句>
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
	
	//<布尔表达式> -> <布尔表达式> or <布尔项>│<布尔项>
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
			chainState_temp.appendToTrueChain(temp_state);//or的真链直接保存等待回填
			//将上一个bool_Term中需要回填的假链填上现在bool_Exp的code_begin
			for (int i = 0; i < chainState_temp.falseChain.size(); ++i)
			{
				tacList.get(chainState_temp.falseChain.get(i)).resultToken.symbol.content = String.valueOf(temp_state.codeBegin);
			}
			//将当前的bool_Exp中的最后那个需要回填的假链保存在State中
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
		//布尔表达式结束，回填真链到当前最新地址
		for (int i = 0; i < chainState_temp.trueChain.size(); ++i)
		{
			tacList.get(chainState_temp.trueChain.get(i)).resultToken.symbol.content = String.valueOf(addressInd);
		}
		return chainState_temp;
		
	}
	
	
	//<布尔项> -> <布尔项> and <布因子>│<布因子>
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
			chainState_temp.appendToFalseChain(temp_state);//假链直接保存等待回填
			for (int i = 0; i < chainState_temp.trueChain.size(); ++i)
			{
				//将上一个bool_factor的真链回填到现在的bool_Term的code_begin
				tacList.get(chainState_temp.trueChain.get(i)).resultToken.symbol.content = String.valueOf(temp_state.codeBegin);
			}
			//将当前的bool_Term中的最后那个需要回填的真链保存在State中
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
	
	//<布因子> -> <布尔量>│not <布因子>
	private ChainState expression_boolFactor()
	{
		ChainState chainState_temp = new ChainState();
		chainState_temp.codeBegin = addressInd;
		
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.singleDelimiter2kindCode.get('-') || curKindCode == LexAnalysis.CONSTANT)
		{
			//回退
			--gramHelper.tokenInd;
			
			//表达式计算
			chainState_temp = expression_arithmetic();
			
			Token u1 = calcStack.pop();
			
			int type = gramHelper.getCurToken().symbol.kindCode;
			
			Token op = gramHelper.getCurToken();
			
			
			if(type >= 53 && type <= 58)
			{
				//匹配关系符 < <= <> = > >=
				chainState_temp = expression_arithmetic();
				 //获取计算结果
				Token u2 = calcStack.pop();
				
				//推入四元式
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
				//回退
				--gramHelper.tokenInd;
				chainState_temp = expression_arithmetic();
				
				Token u1 = calcStack.pop();
				int type = gramHelper.getCurToken().symbol.kindCode;
				Token op = gramHelper.getCurToken();
				
				if (type >= 53 && type <= 58)//匹配关系符
				{
					chainState_temp = expression_arithmetic();
					
					Token u2 = calcStack.pop();//获取最后的计算结果
					//这里要翻译a>b的语句
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
	
	//<if句>  -> if <布尔表达式> then <语句>│if <布尔表达式> then <语句> else <语句>
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
	
	// <repeat句>   -> repeat <语句>  until <布尔表达式>
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
	
	
	//<语句> -> <赋值句>│<if句>│<while句>│<repeat句>│<复合句>
	private ChainState statement_sentence()
	{
		ChainState chainState_temp = new ChainState();
		int curKindCode = gramHelper.getCurToken().symbol.kindCode;
		if(curKindCode == LexAnalysis.IDENTIFIER)
		{
			// <赋值表达式> 展开
			chainState_temp = statement_assign();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("begin"))
		{
			// <复合句> 展开
			chainState_temp = statement_complexSentence();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("while"))
		{
			// <while句>	 展开
			chainState_temp = statement_while();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("if"))
		{
			// <if句> 展开
			chainState_temp = statement_if();
		}
		else if(curKindCode == LexAnalysis.keyWord2kindCode.get("repeat"))
		{
			// <repeat句> 展开
			chainState_temp = statement_repeat();
		}
		else
		{
			error("unexpected token " + gramHelper.getCurTokenContent(), gramHelper.getCurToken());
		}
		
		return chainState_temp;
		
	}


	
	//<程序> -> program <标识符> ; <变量说明> <复合语句> .
	private ChainState production_program()
	{
		ChainState chainState_temp = new ChainState();
		//gramHelper.getNextToken();
		switch (gramHelper.getCurToken().symbol.kindCode)
		{
			//program 保留字
			case 23:
			{
				//获取当前的token
				Token token_temp = gramHelper.getCurToken();
				
				//program
				gramHelper.nextToken();
				
				//检查标识符
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.IDENTIFIER)
				{
					error("expect program name", gramHelper.getCurToken());
				}
				
				//产生四元式	程序声明
				{
					TAC tac = new TAC(token_temp, gramHelper.getCurToken(), gramHelper.getCurToken(), gramHelper.getCurToken());
					addTac(tac);
				}
				
				//下一符号
				gramHelper.nextToken();
				
				//判断分号
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get(';'))
				{
					error("expect \';\' after program", gramHelper.getCurToken().lineInd);
				}
				
				//下一符号
				gramHelper.nextToken();
				
				// <变量说明> 展开
				chainState_temp = production_variableDeclare();
				
				//下一符号
				gramHelper.nextToken();
				
				//<复合语句> 展开
				statement_complexSentence();
				
				//检查结束符号 .
				if(gramHelper.getCurToken().symbol.kindCode != LexAnalysis.singleDelimiter2kindCode.get('.'))
				{
					error("expect \'.\' for program ending");
				}
				
				//产生四元式	程序结束
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
		// 开始递归下降分析，初始符为<程序>
		production_program();
		
		unconditionalJumpCompress();
	}
	public void printTAC()
	{

		for (int k = 0; k < tacList.size(); ++k)
		{
			TAC tac = tacList.get(k);
			
			//四元式序号
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
				// 其他语句
				System.out.println(tacInd + tac.toTAC_String());
				//System.out.println(tacInd + "(" + tac.opToken.symbol.content + "," + tac.argToken1.symbol.content + "," + tac.argToken2.symbol.content + "," + tac.resultToken.symbol.content + ")");
			}
			
		}
	}
	
	//将连续的无条件jump语句连串压缩，简化无用跳转
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
