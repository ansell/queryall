/**
 * 
 */
package org.queryall.mapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class XMLBioMart2CSVResults 
{

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		InputStream inputStream = XMLBioMart2CSVResults.class.getResourceAsStream("/biomart-tsv-hgnc-5183.tsv");
		
		if(inputStream == null)
		{
			System.err.println("Could not find the resource");
			System.exit(1);
		}
		
		CSVReader reader = new CSVReader(new InputStreamReader(inputStream), '\t');
		
		
	    String[] nextLine = null;
	    String[] headers = null;
	    
	    boolean firstLine = true;
	    
	    try {
			while ((nextLine = reader.readNext()) != null) 
			{
				if(firstLine)
				{
					headers = nextLine;
					firstLine = false;
				}
				else
				{
					for(int i = 0; i < nextLine.length; i++)
					{
					    // nextLine[] is an array of values from the line
					    System.out.print(headers[i]+"="+nextLine[i]+"\t");
					}
					
				    System.out.println("");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
