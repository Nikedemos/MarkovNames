package namegen;

import namegen.generators.*;

public class Main {
	public static final MarkovRoman ROMAN = new MarkovRoman();
	
	public static void main(String[] args){
		Form mainWindow = new Form();
		mainWindow.setVisible(true);
		
		for (int i=0; i<32; i++)
		{
		String threePartRomanName = ROMAN.fetch();
		System.out.println(threePartRomanName);
		}
	}
}
