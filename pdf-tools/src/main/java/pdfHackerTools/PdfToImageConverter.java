package pdfHackerTools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PdfToImageConverter {
	static String ExaminedFolder;
	static String newFileName;

	public static void main(String args[]) {
		try {
			ExaminedFolder = PdfUtilities.chooseFolder();
			if (ExaminedFolder != null) {
				ArrayList<File> files = PdfUtilities.getPaths(new File(ExaminedFolder),
						new ArrayList<File>());
				if (files != null) {
					for (int i = 0; i < files.size(); i++) {
						if (!files.get(i).isDirectory() && files.get(i) != null) {
							try {
								System.out.println(files.get(i)
										.getCanonicalPath());
								if (PdfUtilities.testPdfOk(files.get(i))) {
									PDDocument testfile = PDDocument.load(files
											.get(i));							
									newFileName = deletePdfExtension(files.get(i).toString());									
									convertToJpegPages(testfile);
								}
							} catch (IOException e) {
								System.out.println(e); //TODO: Which exception could happen here?
							}
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e); //TODO: Which exception could happen here?
		}
	}
	
	/**
	 * updates the FileName, as the ".pdf"-extension should be deleted
	 * 
	 * @param: input is a String which is the orginal name of the file which has to be converted into xx jpeg pages
	 * @return: String newFileName which is the original file name minues the ".pdf"-extension
	 * 
	 */
	
	private static String deletePdfExtension(String NameFilePath) {
		int lenOld = NameFilePath.length();
		int lenNew = lenOld - 4;		
		newFileName = (NameFilePath
				.substring(0, lenNew));
		return newFileName;
	}
	
	/**
	 * Converts a PDF file into as many JPEG Pages as the orginal PDF has pages. The JPEG pages are numbered.
	 * 
	 * @param: input is the PDF file which has to be converted
	 * @return: It is void and does not return anything to the main thread, but of course it puts jpegs into the folder....
	 * 
	 */

	private static void convertToJpegPages(PDDocument testfile)
			throws IOException {
		int j = 0;
		List<PDPage> pages = testfile.getDocumentCatalog().getAllPages();
		for (PDPage page : pages) {
			BufferedImage img = page.convertToImage(BufferedImage.TYPE_INT_RGB,
					72);
			ImageIO.write(img, "jpg", new File(newFileName + "_page_" + j
					+ ".jpg"));
			j++;
		}

	}
}