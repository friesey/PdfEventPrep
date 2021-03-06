package pdfHackerTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.activation.FileDataSource;

import org.apache.pdfbox.preflight.PreflightDocument;
import org.apache.pdfbox.preflight.ValidationResult;
import org.apache.pdfbox.preflight.ValidationResult.ValidationError;
import org.apache.pdfbox.preflight.exception.SyntaxValidationException;
import org.apache.pdfbox.preflight.parser.PreflightParser;

public class PdfAValidator {

	static String t;
	static PrintWriter outputfile;
	static PrintWriter ShortSummary;

	public static void main(String args[]) throws IOException {

		try {

			t = PdfUtilities.chooseFolder();

			// Generating two Outputfiles in the folder that is examined

			outputfile = new PrintWriter(new FileWriter(t + "//"
					+ "PdfAValidation.txt"));

			ShortSummary = new PrintWriter(new FileWriter(t + "//"
					+ "PdfAValidationShortSummary.txt"));

			if (t != null) {

				ArrayList<File> files = PdfUtilities.getPaths(new File(t),
						new ArrayList<File>());

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
							 * Test if the Pdf File is ok to be examined.
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

											ShortSummary
													.println("The file "
															+ files.get(i)
															+ " is a valid PDF/A-1b file");
										} else {
											outputfile
													.println("The file "
															+ files.get(i)
															+ " is not valid, error(s) :");

											ShortSummary
													.println("The file "
															+ files.get(i)
															+ " is not valid, error(s) :");
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
			ShortSummary.close();
			outputfile.close();
		} catch (FileNotFoundException e) {
		}
	}
}