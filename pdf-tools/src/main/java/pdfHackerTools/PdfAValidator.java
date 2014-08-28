package pdfHackerTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import javax.activation.FileDataSource;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

public class PdfAValidator {

	static String examinedFolder;
	static PrintWriter outputfile;
	static PrintWriter ShortSummary;

	// TODO: are these Constants and should be put in CAPITAL LETTERS?
	static String valid = " is a valid PDF/A-1b file";
	static String invalid = " is not valid, error(s) :";

	public static void main(String args[]) throws IOException {

		try {

			examinedFolder = PdfUtilities.chooseFolder();

			// Generating two Outputfiles in the folder that is examined

			outputfile = new PrintWriter(new FileWriter(examinedFolder + "//"
					+ "PdfAValidation.txt"));

			ShortSummary = new PrintWriter(new FileWriter(examinedFolder + "//"
					+ "PdfAValidationShortSummary.txt"));

			if (examinedFolder != null) {

				ArrayList<File> files = PdfUtilities.getPaths(new File(
						examinedFolder), new ArrayList<File>());

				for (int i = 0; i < files.size(); i++) {
					if (!files.get(i).isDirectory() && files.get(i) != null) {

						System.out.println(i + 1);
						outputfile.println(i + 1);
						ShortSummary.println(i + 1);

						try {
							System.out.println(files.get(i).getCanonicalPath());
							outputfile.println(files.get(i).getCanonicalPath());
							ShortSummary.println(files.get(i)
									.getCanonicalPath());

							if (PdfUtilities.testPdfOk(files.get(i)))
							/*
							 * Test if the Pdf File is OK to be examined.
							 * Otherwise gives error in Console
							 */
							{
								String PdfType = PdfUtilities.checkIfPdfA(files
										.get(i));
								if (PdfType.contains("PDF/A")) {
									/*
									 * the actual PdfAValidation starts here
									 */
									ValidationResult result = null;
									FileDataSource fd = new FileDataSource(
											files.get(i).toString());
									PreflightParser parser = new PreflightParser(
											fd);
									try {
										parser.parse();
										PreflightDocument document = parser
												.getPreflightDocument();
										try {
											document.validate();

											result = document.getResult();
											document.close();

										} catch (NullPointerException e) {
											/*
											 * TODO: Why can this generate a
											 * NullPointerException ?
											 */
											outputfile.println(e);
											ShortSummary.println(e);
										}
									} catch (SyntaxValidationException e) {
										/*
										 * the parse method throws a
										 * SyntaxValidationException if the PDF
										 * file can't be parsed.
										 */
										result = e.getResult();
									}
									if (result != null) {
										if (result.isValid()) {
											outputfile
													.println("The file "
															+ files.get(i)
															+ " is a valid PDF/A-1b file");

											ShortSummary.println("The file "
													+ files.get(i) + valid);
										} else {
											outputfile.println("The file "
													+ files.get(i) + valid);

											ShortSummary.println("The file "
													+ files.get(i) + invalid);
											for (ValidationError error : result
													.getErrorsList()) {
												outputfile.println(error
														.getErrorCode()
														+ " : "
														+ error.getDetails());
											}
										}
									}
								} else {
									ShortSummary.println("No PDF/A file");
									outputfile.println("No PDF/A file");
								}
							}
						} catch (IOException e) {
							outputfile.print(e);
						}
					}
				}
			}

			outputfile.close();

			// TODO: Analyze Results / Statistics

			/*
			 * Analyzing the just-generated outputfile with all the findings of
			 * the PDF/A-Validation-Test
			 */

			FileInputStream inputStream = new FileInputStream(examinedFolder
					+ "//" + "PdfAValidation.txt");

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));

			String line = null;

			int countValidPdf = 0;
			int countInvalidPdf = 0;

			ArrayList<String> errorlist = new ArrayList<String>();
			ArrayList<String> lines = new ArrayList<String>();

			StringBuilder responseData = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				responseData.append(line);
				lines.add(line);
			}

			int len = lines.size();

			for (int i = 0; i < len; i++) {
				if (lines.get(i).contains(valid)) {
					countValidPdf++;
				} else if (lines.get(i).contains(invalid)) {
					countInvalidPdf++;
				} else if (lines.get(i).contains("ErrorMessage")) {
					errorlist.add(lines.get(i));
				}
			}

			System.out.println("valid PDF/A files: " + countValidPdf);
			ShortSummary.println("valid PDF/A files: " + countValidPdf);
			System.out.println("invalid PDF files: " + countInvalidPdf);
			ShortSummary.println("invalid PDF files: " + countInvalidPdf);
			reader.close();
			
			Collections.sort(errorlist);
			
			//Errors will be sorted, counted and written in ShortSummary
			
			
			
			

			ShortSummary.close();

		} catch (FileNotFoundException e) {
		}
	}
}