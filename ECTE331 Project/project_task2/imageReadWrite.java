package project2;  

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class imageReadWrite{

	public static void readJpgImage(String filePath, colourImage imgData) {
		 try {
	            // Read the image file
	            File imageFile = new File(filePath);
	            BufferedImage bufferedImg = ImageIO.read(imageFile);
	            
	            System.out.println("file: "+imageFile.getCanonicalPath());
	            
	            // Check if the image is in sRGB color space
	            if (!bufferedImg.getColorModel().getColorSpace().isCS_sRGB()) {
	                System.out.println("Image is not in sRGB color space");
	                return;
	            }
	            
	            // Get the width and height of the image
	            int imgWidth = bufferedImg.getWidth();
	            int imgHeight = bufferedImg.getHeight();
	            imgData.imgWidth = imgWidth;
	            imgData.imgHeight = imgHeight;
	            imgData.rgbData = new short[imgHeight][imgWidth][3];

	           // Loop over each pixel of the image and store its RGB color components in the array
	            for (int row = 0; row < imgHeight; row++) {
	                for (int col = 0; col < imgWidth; col++) {
	                    // Get the color of the current pixel
	                    int pixelValue = bufferedImg.getRGB(col, row);
	                    Color pixelColor = new Color(pixelValue, true);

	                    // Store the red, green, and blue color components of the pixel in the array
	                    imgData.rgbData[row][col][0] = (short) pixelColor.getRed();
	                    imgData.rgbData[row][col][1] = (short) pixelColor.getGreen();
	                    imgData.rgbData[row][col][2] = (short) pixelColor.getBlue();
	                }
	            }            
	                       

	        } catch (IOException e) {
	            System.out.println("Error reading image file: " + e.getMessage());
	        }  	
	}

	public static void writeJpgImage(colourImage imgData, String filePath) {
		 try {
	    	 int imgWidth = imgData.imgWidth;
	         int imgHeight = imgData.imgHeight;
	         BufferedImage bufferedImg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);

	         // Set the RGB color values of the BufferedImage using the pixel array
	         for (int row = 0; row < imgHeight; row++) {
	             for (int col = 0; col < imgWidth; col++) {
	                 int rgbValue = new Color(imgData.rgbData[row][col][0], imgData.rgbData[row][col][1], imgData.rgbData[row][col][2]).getRGB();
	                 bufferedImg.setRGB(col, row, rgbValue);
	             }
	         }

	         // Write the BufferedImage to a JPEG file
	         File outputFile = new File(filePath);
	         ImageIO.write(bufferedImg, "jpg", outputFile);

	     } catch (IOException e) {
	         System.out.println("Error writing image file: " + e.getMessage());
	     }       
	
       }//

}
