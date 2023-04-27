package nt.istqbtt.nt_istqbtt;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class ConvertImageToText {
    public static String convertImageFileToText(File imageFileToConvert) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("src/main/resources/nt/istqbtt/nt_istqbtt/tessdata");
        tesseract.setLanguage("eng");
        tesseract.setPageSegMode(1);
        tesseract.setOcrEngineMode(1);
        return tesseract.doOCR(imageFileToConvert);
    }
}
