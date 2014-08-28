package pdfHackerTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

public class PdfChecker {

	static String ExaminedFolder;

	static int NoPdfHeader;
	static int PdfA;
	static int PdfStandard;
	static int i;

	static PrintWriter outputfile;

	public static void main(String args[]) throws IOException {

		try {

			ExaminedFolder = PdfUtilities.chooseFolder();

			// TODO: Create an XML Writer

			if (ExaminedFolder != null) {

				ArrayList<File> files = PdfUtilities.getPaths(new File(ExaminedFolder),
						new ArrayList<File>());
				if (files == null)
					return;
		
				NoPdfHeader = 0;
				PdfA = 0;								
				PdfStandard = 0;

				for (i = 0; i < files.size(); i++) {
					if (!files.get(i).isDirectory() && files.get(i) != null) {
						System.out.println(i + 1);

						try {
							System.out.println(files.get(i).getCanonicalPath());

							// is this necessary as the %PDF Test follows
							// anyway?
							String extension = FilenameUtils.getExtension(files
									.get(i).getCanonicalPath());

							if (extension.equals("pdf")) {

								if (PdfUtilities.testPdfOk(files.get(i))) {

									String PdfType = PdfUtilities
											.checkIfPdfA(files.get(i));

									System.out.println("Pdf Type: " + PdfType);

									if (PdfType.contains("PDF/A")) {
										PdfA++;
									} else {
										PdfStandard++; // this included
														// files with
														// "%PDF-header that have no XMP Metadata"
									}
									PdfUtilities.PdfHeaderTest.close();
								}
							}

							else {
								System.out.println("Extension is not .pdf");
								NoPdfHeader++;
							}
						}
						catch (IOException e) {
							System.out.print(e);
						}
					}
				}

				System.out.println();
				System.out.println();

				// System.out.println("Files examined: 	" + i); // does not
				// always
				// work
				System.out.println("Non-PDF-Files: 	" + NoPdfHeader);		
				System.out.println("PDF/A-files:		" + PdfA);
				System.out.println("PDF Standard files: 	" + PdfStandard);
			}
		} catch (FileNotFoundException e) {

		}
	}
}
