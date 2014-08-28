package pdfHackerTools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PdfTwinTest {

	static String OrgPdf;
	static String MigPdf;
	static long filesizeOrg;
	static long filesizeMig;

	static String ExaminedFolder;

	static PrintWriter outputfile;

	public static void main(String args[]) throws IOException {

		System.out
				.println("Please select the folder for outputfile 'PdfTwinTest.txt'");

		ExaminedFolder = PdfUtilities.chooseFolder();

		if (ExaminedFolder != null) {

			outputfile = new PrintWriter(new FileWriter(ExaminedFolder
					+ "\\PdfTwinTester.txt"));
			outputfile.println("Pdf Twin Test");

			OrgPdf = PdfUtilities.chooseFile();
			System.out.println(OrgPdf);

			MigPdf = PdfUtilities.chooseFile();
			System.out.println(MigPdf);

			outputfile.println("Original File: " + OrgPdf);
			outputfile.println("Migrated File: " + MigPdf);

			if (OrgPdf != null && MigPdf != null) {

				if (PdfUtilities.testPdfOk(OrgPdf) == true
						&& PdfUtilities.testPdfOk(MigPdf) == true) {

					String[] LinesOrg = PdfUtilities.extractsPdfLines(OrgPdf);
					String[] LinesMig = PdfUtilities.extractsPdfLines(MigPdf);

					int differences = 0;

					int lenOrg = LinesOrg.length;
					int lenMig = LinesMig.length;

					outputfile.println(OrgPdf + " has " + lenOrg + " lines.");
					outputfile.println(OrgPdf + " has " + lenMig + " lines.");

					if (lenOrg != lenMig) {
						if (lenOrg > lenMig) {
							outputfile.println("The migrated Pdf has"
									+ (lenOrg - lenMig) + " lines less.");
						} else {
							outputfile.println("The migrated PDf has"
									+ (lenMig - lenOrg) + " lines more.");
						}
						outputfile.println();
					}

					//TODO: Create a method for the actual comparison?
					
					if ((lenOrg > lenMig || lenOrg == lenMig)) {

						for (int j = 0; j < lenMig; j++) {

							if (!(LinesOrg[j]).equals(LinesMig[j])) {
								outputfile.println();
								outputfile.println("Differs in line: "
										+ (j + 1));
								outputfile.println();
								outputfile.println("Original : " + LinesOrg[j]);
								outputfile.println("Migration: " + LinesMig[j]);
								outputfile.println();
								differences++;
							}
						}
						if (differences == 0) {
							outputfile.println("Both PDF-Files are alike.");
						}

						else {
							outputfile.println(differences
									+ " lines have differences.");
						}
					}

					else /* (lenMig > lenOrg) */{
						for (int j = 0; j < lenOrg; j++) {

					

							if (!(LinesOrg[j]).equals(LinesMig[j])) {
								outputfile.println();
								outputfile.println("Differs in line: "
										+ (j + 1));
								outputfile.println();
								outputfile.println("Original : " + LinesOrg[j]);
								outputfile.println("Migration: " + LinesMig[j]);
								outputfile.println();
								differences++;
							}
						}
						if (differences == 0) {
							outputfile.println("Both PDF-Files are alike.");
						}
						else {
							outputfile.println(differences
									+ " lines have differences.");
						}
					}
				} else {
					System.out.println("Program closed.");
				}
			} else {
				System.out.println("Please choose two files.");
			}

			outputfile.close();
		}
	}
}
