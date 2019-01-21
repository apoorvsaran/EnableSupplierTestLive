package com.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class EnableSupplierLiveParallel {

	public static void main(String[] args) throws IOException, FileNotFoundException {

		// "Automate" is the folder already created, which has the supplier and
		// mapping txt file, in this folder itself the "Query.txt" file will be
		// generated.
		// change this "commonPath" as per your working directory.
		final String commonPath = "D:\\Users\\1418690\\Documents\\AutoTestLive\\";
		final String supplierFilePath = commonPath + "suppliers.txt";
		final String mappingFilePath = commonPath + "mapping.txt";
		final String queryFilePath = commonPath + "QueryParallel.txt";

		// Depot ID for which you want to enable suppliers.
		final String depotId = "5012068053800";

		// Effective start date and effective end date.
		final String startDate = "2019-01-22 02:00:00.000"; // yyyy-mm-dd
															// hh:mm:ss format
		final String endDate = "2022-01-22 02:00:00.000"; // 1 year difference
															// from startDate

		int countSupplier = 0;
		int countMissing = 0;
		int countSuccessful = 0;

		System.out.println("\nStarted the process...Please wait!");

		// Getting the files as File object...
		File supplierFile = new File(supplierFilePath);
		File mappingFile = new File(mappingFilePath);

		if (!supplierFile.exists()) {
			System.out.println("Supplier file missing");
			return;
		}
		if (!mappingFile.exists()) {
			System.out.println("Supplier-Mapping file missing");
			return;
		}

		Scanner suppFileObj = new Scanner(supplierFile);
		Scanner mapFileObj = new Scanner(mappingFile);

		if (suppFileObj.hasNext() == false || mapFileObj.hasNext() == false) {
			System.out.println("Suppliers or Supplier-mapping missing. Please check the entry in files.\nAborted...");
			return;
		}

		// Making an ArrayList of Suppliers in suppliers.txt file
		ArrayList<Integer> supplierList = new ArrayList<Integer>();

		// ArrayList for Suppliers missing GLN Code(s) in the mapping file.
		ArrayList<Integer> missingGLNCodeSupplierList = new ArrayList<Integer>();

		// Making a HashMap of Suppliers vs their multiple GLN Codes.
		HashMap<Integer, ArrayList<String>> hm = new HashMap<Integer, ArrayList<String>>();

		// Mapping Suppliers with their respective GLN Code(s).
		while (mapFileObj.hasNext()) {
			int suppId = mapFileObj.nextInt();
			String glnCode = mapFileObj.next();

			if (hm.containsKey(suppId)) {
				hm.get(suppId).add(glnCode);
			} else {
				ArrayList<String> list = new ArrayList<String>();
				list.add(glnCode);
				hm.put(suppId, list);
			}
		}

		// Making an ArrayList of suppliers provided in the file.
		while (suppFileObj.hasNext()) {
			countSupplier++;
			supplierList.add(suppFileObj.nextInt());
		}
		System.out.println(countSupplier + " suppliers in the input file");

		// Depot Query
		String first = "update scnrd.depot_allocation_status set live_allocation_effective_start_date = '" + startDate
				+ "', live_allocation_effective_end_date = '" + endDate
				+ "',test_allocation_effective_start_date = null, test_allocation_effective_end_date = null where supplier_id = '";
		String last = "' and depot_id= '" + depotId + "';";
		String query = "";

		BufferedWriter writer = new BufferedWriter(new FileWriter(queryFilePath));
		for (Integer i : supplierList) {
			if (hm.containsKey(i)) {
				if (i.toString().length() == 3)
					query = first + "F00" + i + last;
				else if (i.toString().length() == 4)
					query = first + "F0" + i + last;
				else if (i.toString().length() == 5)
					query = first + "F" + i + last;
				writer.write(query);
				writer.append('\n');

				for (String s : hm.get(i)) {
					query = first + s + last;
					writer.write(query);
					writer.append('\n');
				}
				writer.append("\n\n");
				countSuccessful++;
			} else {
				missingGLNCodeSupplierList.add(i);
				countMissing++;
			}
		}
		writer.close();
		System.out.println("Query file created at " + queryFilePath);

		// Printing the suppliers not having GLN Code.
		if (!missingGLNCodeSupplierList.isEmpty()) {
			System.out.println("\nSupplier(s) missing GLN Code");
			for (Integer i : missingGLNCodeSupplierList)
				System.out.println(i);
		}
		if (countMissing != 0)
			System.out.println(countMissing + " suppliers in total not having GLN Code");
		if (countSuccessful != 0)
			System.out.println(countSuccessful + " suppliers having GLN Code(s)");

	}

}