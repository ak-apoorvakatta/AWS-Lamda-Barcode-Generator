# AWS-Lamda-Barcode-Generator

##Steps to Set-Up the required Lambda function
###1. Create Lambda function
Note: Set Lambda Function Handler to `PassKit.BarcodeGenerator.BarcodeGenerator::generateBarcode`
###2. Upload JAR file to the Lambda function
Note: File location `BarcodeGenerator\target\BarcodeGenerator-1.0.0.jar`
###3. Save
###4. Test
Note: Set Actions -> Configure test event as follow
```json
{
	"message":"testMEssage",
	"format":"qrcode",
	"encoding":"UTF-8",
	"height":"150",
	"width":"150
}