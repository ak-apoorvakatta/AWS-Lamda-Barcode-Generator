package PassKit.BarcodeGenerator;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.LinkedHashMap;

import com.amazonaws.services.lambda.runtime.Context;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

@SuppressWarnings("serial")
class InvalidInputException extends Exception {
	InvalidInputException(String s) {
        super(s);
    }
}

public class BarcodeGenerator {
		
	public String generateBarcode (LinkedHashMap<String,String> input, Context context) throws InvalidInputException, Exception {
		try {
			String message = input.get("message");
			String barcodeFormat = input.get("format");
			Integer height = null;
			Integer width = null;
			String encoding = null;
			try { height = input.get("height") == "" ? null:Integer.parseInt(input.get("height")); } catch (Exception e) {}
			try { width = input.get("width") == "" ? null:Integer.parseInt(input.get("width")); } catch (Exception e) {}
			try { encoding = input.get("encoding") == "" ? "UTF-8":input.get("encoding"); } catch (Exception e) {}
			return getBarcode(message, barcodeFormat, height, width, encoding);
		} catch (InvalidInputException ie) {
			throw ie;
		} catch (com.google.zxing.WriterException we) {
			throw we;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String getBarcode(String message, String barcodeFormat, Integer height, Integer width, String encoding) throws com.google.zxing.WriterException, InvalidInputException, Exception {
		try {

			if (message == "") {
				throw new InvalidInputException ("Invalid Input Error - Invalid Message Error. Message Input = ["+message+"].");
			}

			// Image Writing Objects
			MultiFormatWriter w = new MultiFormatWriter();
			BitMatrix bm = null;
			
			// Setting Encoding Format
			LinkedHashMap<EncodeHintType,Object> encodingFormat = new LinkedHashMap<EncodeHintType,Object>();
			encodingFormat.put(EncodeHintType.CHARACTER_SET, encoding);
			
			// Barcode Format List
			LinkedHashMap<String,Integer> barcodeFormats = new LinkedHashMap<String,Integer>();
			barcodeFormats.put("pdf417", 1);
			barcodeFormats.put("code128", 2);
			barcodeFormats.put("qrcode", 3);
			barcodeFormats.put("aztec", 4);
			
			// Identifying Target Barcode
			int targetBarcodeFormat = 0;
			try { targetBarcodeFormat = barcodeFormats.get(barcodeFormat); }
			catch (Exception e) { throw new InvalidInputException("Invalid Input Error - Invalid Barcode Format Error. BarcodeFormat Format Input = ["+barcodeFormat+"]. Supported Barcode Formats are qrcode, pdf417, code128 and aztec."); }
			switch (targetBarcodeFormat) {
				case 1: //pdf417
					if (height == null) { height = 53; }
					if (width == null) { width = 210; }
					bm = new BitMatrix(height, height);
					bm = w.encode( message, BarcodeFormat.PDF_417, width, height, encodingFormat);
					break;
				case 2: //code128
					if (height == null) { height = 53; }
					if (width == null) { width = 210; }
					bm = new BitMatrix(height, height);
					bm = w.encode( message, BarcodeFormat.CODE_128, width, height, encodingFormat);
					break;
				case 3: //qrcode
					if (height == null) { height = 105; }
					if (width == null) { width = 105; }
					bm = new BitMatrix(height, height);
					bm = w.encode( message, BarcodeFormat.QR_CODE, height, height, encodingFormat);
					break;
				case 4: //aztec
					if (height == null) { height = 105; }
					if (width == null) { width = 105; }
					bm = new BitMatrix(height, height);
					bm = w.encode( message, BarcodeFormat.AZTEC, height, height, encodingFormat);
					break;
				default:
					throw new InvalidInputException("Invalid Input Error - Invalid Barcode Format Error. BarcodeFormat Format Input = ["+barcodeFormat+"]. Supported Barcode Formats are qrcode, pdf417, code128 and aztec.");
			}
			
			// Writing, Encoding and Returning Image
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MatrixToImageWriter.writeToStream(bm, "png", baos);
			return Base64.getEncoder().encodeToString(baos.toByteArray());

		} catch (InvalidInputException ie) {
			throw ie;
		} catch (com.google.zxing.WriterException we) {
			throw new InvalidInputException("Invalid Input Error - Invalid Encoding Format Error. Encoding Format Input = ["+encoding+"].");
		} catch (Exception e) {
			throw new Exception("Invalid Input Error: Please Check Your Input.");
		}
	}

	public static void main(String[] args) {
		try {
			(new BarcodeGenerator()).getBarcode("message", "qrcode", null, null, "fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
