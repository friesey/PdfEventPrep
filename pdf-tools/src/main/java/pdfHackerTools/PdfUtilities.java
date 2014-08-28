package pdfHackerTools;

// TODO: next time, the package name should start with a small character, this is the convention

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class PdfUtilities {

	/*******************************************************
	 * Variables and objects used within the whole package
	 ********************************************************/
	// set 16MB as maximum file length
	// TODO: There is one file with 16.058.818 Bytes which is too big as well,
	// so this value should be changed to a smaller one.
	private static final long DEFAULT_MAX_FILE_LENGTH = 1024 * 1024 * 16;

	static BufferedReader PdfHeaderTest;

	/*********************************************************
	 * Methods used within the whole package
	 *
	 ********************************************************/

	/****************************************************************************
	 * Checks if a PDF is ok to work with %PDF Header, Broken PDF & Encryption
	 * 
	 * @param file
	 *            or String
	 * @return: boolean true or false
	 * @throws IOException
	 */

	static boolean testPdfOk(String file) throws IOException {
		if (testFileHeader(file)) {
			if (!checkPdfSize(file)) {
				PDDocument testfile = PDDocument.load(file);
				if (!testfile.isEncrypted()) {
					if (!checkBrokenPdf(file)) {
						return true;
					} else {
						System.out.println("Broken Pdf");
						return false;
					}
				} else {
					System.out.println("Is encrypted");
					return false;
				}
			} else {
				System.out.println("Pdf too big to be examined");
				return false;
			}
		} else {
			System.out.println("No PDF Header");
			return false;
		}
	}

	static boolean testPdfOk(File file) throws IOException {

		if (testFileHeader(file)) {
			if (!checkPdfSize(file)) {
				PDDocument testfile = PDDocument.load(file);
				if (!testfile.isEncrypted()) {
					if (!checkBrokenPdf(file.toString())) {
						return true;
					} else {
						System.out.println("Broken Pdf");
						return false;
					}
				} else {
					System.out.println("Is encrypted");
					return false;
				}
			} else {
				System.out.println("Pdf too big to be examined");
				return false;
			}
		} else {
			System.out.println("No PDF Header");
			return false;
		}
	}

	/**
	 * lists all files and directories in given directory
	 * 
	 * @param
	 * @return: ArrayList<File> of all found files and subfolders
	 * 
	 */
	static ArrayList<File> getPaths(File file, ArrayList<File> list) {
		if (file == null || list == null || !file.isDirectory())
			return null;
		File[] fileArr = file.listFiles();
		for (File f : fileArr) {
			// TODO If a folder is chosen that cannot be searched/read, e. g.
			// C:/, the tool runs into issues
			if (f.isDirectory()) {
				getPaths(f, list);
			}
			list.add(f);
		}
		return list;
	}

	/**
	 * Tests if the first line of the file contains the proper PDF-Header "%PDF"
	 * for Datatype file
	 * 
	 * @param Creates
	 *            a PdfHeaderTest-Pdf-Reader and reads the first line of the
	 *            PDF-file like an editor would do.
	 * @return: boolean false = no PDF-Header; true = first line contains
	 *          PDF-Header
	 */
	static boolean testFileHeader(File file) throws IOException {
		PdfHeaderTest = new BufferedReader(new FileReader(file));
		String FileHeader = PdfHeaderTest.readLine();
		// System.out.println(FileHeader);
		if (FileHeader != null) {
			if (FileHeader.contains("%PDF")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// TODO: Is there a better way to overload a method?

	/**
	 * Tests if the first line of the file contains the proper PDF-Header "%PDF"
	 * For Strings
	 * 
	 * @param Creates
	 *            a PdfHeaderTest-Pdf-Reader and reads the first line of the
	 *            PDF-file like an editor would do. Overloaded with the data
	 *            type "String"
	 * @return: boolean false = no PDF-Header; true = first line contains
	 *          PDF-Header
	 */
	static boolean testFileHeader(String file) throws IOException {
		PdfHeaderTest = new BufferedReader(new FileReader(file));
		String FileHeader = PdfHeaderTest.readLine();
		// System.out.println(FileHeader);
		if (FileHeader != null) {

			if (FileHeader.contains("%PDF")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Chooses the folder which is examined via a simple folder browser Dialog
	 * 
	 * @param Does
	 *            not need any to begin with.
	 * @return: string for folder path
	 */

	public static String chooseFolder() throws FileNotFoundException {
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		j.showOpenDialog(j);
		if (j.getSelectedFile() == null) {
			System.out.println("No folder was chosen");
		} else {
			String folder = j.getSelectedFile().getPath();
			return folder;
		}
		return null;
	}

	/**
	 * Chooses the file which is examined via a simple folder browser Dialog
	 * 
	 * @param Does
	 *            not need any to begin with.
	 * @return: string for file path
	 */

	public static String chooseFile() throws FileNotFoundException {
		JFileChooser j = new JFileChooser();
		j.setFileSelectionMode(JFileChooser.FILES_ONLY);
		j.showOpenDialog(j);
		if (j.getSelectedFile() == null) {
			System.out.println("No file was chosen");
		} else {
			String file = j.getSelectedFile().getPath();
			return file;
		}
		return null;

	}

	/**
	 * Determines which PDF version it is. Can also detect PDF/A.
	 * 
	 * @param File
	 *            (should be PDF)
	 * @return: String PDF Version TODO: occasionally throws WARN about log4j
	 *          that I cannot understand or get rid of.
	 * @throws IOException
	 */

	public static String checkIfPdfA(File file) throws IOException {
		String pdfType = "No XMP Metadata";
		String XmpMetadata;
		PdfReader reader;
		try {
			try {
				reader = new PdfReader(file.toString());
				// There is no PDF/A compliance before PDF 1.4
				if (reader.getPdfVersion() > 3) {
					if (reader.getMetadata() != null) {
						XmpMetadata = new String(reader.getMetadata()); // nullpointerException
						reader.close();
						if (XmpMetadata.contains("pdfaid:conformance")) {
							pdfType = "PDF/A";
						} else {
							pdfType = "PDF 1.4 or higher";
						}
					}
				} else {
					pdfType = "PDF 1.0 - 1.3";
				}
				return pdfType;
			} catch (OutOfMemoryError e) {
				System.out.println(e);
				pdfType = "Too big";
				return pdfType;
			}
		} catch (java.lang.NullPointerException e) {
			System.out.println(e);
			pdfType = "PDF cannot be read by PdfReader";
			return pdfType;
		}
	}

	/**
	 * Checks if a Pdf is too broken to be examined.
	 * 
	 * @param File
	 *            (should be PDF)
	 * @return: boolean
	 * @throws IOException
	 */

	// TODO: This function does not work, e. g. for encrypted files and should
	// not be used until it is fixed.
	public static boolean checkBrokenPdf(String file) throws IOException {

		boolean brokenPdf;
		try {
			PdfReader reader = new PdfReader(file);
			reader.getMetadata();
			// TODO: One day this function could test more and be more clever.
			brokenPdf = false;
			return brokenPdf;
		} catch (Exception e) {
			System.out.println("Broken: " + file);
			brokenPdf = true;
			return brokenPdf;
		}
	}

	/**
	 * Simple Encryption Test without reader, because encryption causes lots of
	 * exceptions.
	 * 
	 * @param PDDocument
	 *            (should be PDF)
	 * @return: boolean
	 * @throws IOException
	 */

	public static boolean testsEncryption(PDDocument file) throws IOException {
		// PDDocumentInformation info =
		// PDDocument.load(file).getDocumentInformation();
		if (file.isEncrypted() == true) {
			System.out.println(file + " is encrypted");
			return true;
		} else {
			return false;
		}
	}

	public static String[] extractsPdfLines(String PdfFile) throws IOException {
		StringBuffer buff = new StringBuffer();
		String ExtractedText = null;
		PdfReader reader = new PdfReader(PdfFile);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		TextExtractionStrategy strategy;
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
			strategy = parser.processContent(i,
					new SimpleTextExtractionStrategy());
			ExtractedText = strategy.getResultantText().toString();
			buff.append(ExtractedText + "\n");
		}

		String[] LinesArray;

		LinesArray = buff.toString().split("\n");
		reader.close();
		return LinesArray;
	}

	/**
	 * Checks the size of the Pdf-file, because some big Pdf Files might cause
	 * exceptions. *
	 * 
	 * @param file
	 *            (should be Pdf)
	 * @return: boolean
	 * @throws
	 */

	public static boolean checkPdfSize(File file) {
		boolean toobig = isFileTooLong(file, DEFAULT_MAX_FILE_LENGTH);
		if (toobig) {
			System.out
					.println("File is bigger than 16 MB and therefore cannot be measured");
		}
		return toobig;
	}

	public static boolean checkPdfSize(String filePath) {
		File toCheck = new File(filePath);
		return checkPdfSize(toCheck);
	}

	public static boolean isFileTooLong(File toCheck, long maxLength) {
		return (toCheck.length() > maxLength);
	}
}
