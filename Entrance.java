package compilers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Entrance
{
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
		
		GramAndSemAnalysis gramAndSemAnalysis = new GramAndSemAnalysis(lexAnalysis.symbolList, lexAnalysis.constantsAndSymbol2SeqNum);
		gramAndSemAnalysis.parse();
		gramAndSemAnalysis.printTAC();
		
		
		bufferedReader.close();
	}
}
