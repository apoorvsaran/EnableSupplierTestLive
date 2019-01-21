package com.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class EnableSupplierQuery {

	public static void main(String[] args) throws IOException, FileNotFoundException {
		
		final String supplierFilePath = "C:\\Users\\Apoorv\\Documents\\Automate\\suppliers.txt";
		final String mappingFilePath = "C:\\Users\\Apoorv\\Documents\\Automate\\mapping.txt";
		final String queryFilePath = "C:\\Users\\Apoorv\\Documents\\Automate\\Query.txt";
		final String depotId = "5012068059086";
		int countSupplier = 0;
		int countMissing = 0;
		int countSuccessful = 0;
		
		System.out.println("\nStarted the process...Please wait!");
		
		File supplierFile = new File(supplierFilePath);
		File mappingFile = new File(mappingFilePath);
		
		if(supplierFile==null || mappingFile==null)
		{
			System.out.println("Error while getting input files: Check \"suppliers\" and \"mapping\" file name...");
			return;
		}
		
		Scanner suppFileObj = new Scanner(supplierFile);
		Scanner mapFileObj = new Scanner(mappingFile);
		
		ArrayList<Integer> supplierList = new ArrayList<Integer>();
		ArrayList<Integer> missingGLNCodeSupplierList = new ArrayList<Integer>();
		HashMap<Integer, ArrayList<String>> hm = new HashMap<Integer, ArrayList<String>>();
		
		//Mapping Suppliers with their respective GLN Code(s).
		while(mapFileObj.hasNext())
		{
			int suppId = mapFileObj.nextInt();
			String glnCode = mapFileObj.next();
			
			if(hm.containsKey(suppId))
			{
				hm.get(suppId).add(glnCode);
			}
			else
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(glnCode);
				hm.put(suppId, list);
			}
		}
		
		//Making an ArrayList of suppliers provided in the file.
		while(suppFileObj.hasNext())
		{
			countSupplier++;
			supplierList.add(suppFileObj.nextInt());
		}
		System.out.println(countSupplier+" suppliers in the input file");
		
		//Depot Query
		String depotFirst = "insert into scnrd.depot_allocation_status(supplier_id,depot_id, test_allocation_effective_start_date,test_allocation_effective_end_date)  values('";
		String depotLast = "','F0"+depotId+"','1/11/2019  00:00:00','1/11/2020  00:00:00');";
		String depotQuery = "";
		
		//GLN Code Query
		String glnFirst = "insert into scnrd.depot_allocation_status(supplier_id,depot_id, test_allocation_effective_start_date,test_allocation_effective_end_date)  values('";
		String glnLast = "','"+depotId+"','1/11/2019  00:00:00','1/11/2020  00:00:00');";
		String glnQuery = "";
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(queryFilePath));
		for(Integer i: supplierList)
		{
			if(hm.containsKey(i))
			{
				depotQuery = depotFirst + i + depotLast;
				writer.write(depotQuery);
				writer.append('\n');
				
				for(String s: hm.get(i))
				{
					glnQuery = glnFirst + s + glnLast;
					writer.write(glnQuery);
					writer.append('\n');		
				}
				writer.append('\n');
				writer.append('\n');
				countSuccessful++;
			}
			else
			{
				missingGLNCodeSupplierList.add(i);
				countMissing++;
			}
		}
		writer.close();
		System.out.println("Query file created at "+queryFilePath);
		
		//Printing the suppliers not having GLN Code.
		if(!missingGLNCodeSupplierList.isEmpty())
		{
			for(Integer i: missingGLNCodeSupplierList)
			System.out.println(i);
		}
		System.out.println(countMissing+" suppliers in total not having GLN Code");
		System.out.println(countSuccessful+" suppliers having GLN Code(s)");

	}

}
