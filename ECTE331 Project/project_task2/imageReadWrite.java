package project2;  

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class imageReadWrite{

	public static void readJpgImage(String fileName, colourImage ImgStruct) {
		 try {
	            // Read the image file
	            File file = new File(fileName);
	            BufferedImage image = ImageIO.read(file);
	            
	            System.out.println("file: "+file.getCanonicalPath());
	            
	            // Check if the image is in sRGB color space
	            if (!image.getColorModel().getColorSpace().isCS_sRGB()) {
	                System.out.println("Image is not in sRGB color space");
	                return;
	            }
	            
	            // Get the width and height of the image
	            int width = image.getWidth();
	            int height = image.getHeight();
	            ImgStruct.width=width;
	            ImgStruct.height=height;
	            ImgStruct.pixels=new short[height][width][3];

	           // Loop over each pixel of the image and store its RGB color components in the array
	            for (int y = 0; y < height; y++) {
	                for (int x = 0; x < width; x++) {
	                    // Get the color of the current pixel
	                    int pixel = image.getRGB(x, y);
	                    Color color = new Color(pixel, true);

	                    // Store the red, green, and blue color components of the pixel in the array
	                    ImgStruct.pixels[y][x][0] = (short) color.getRed();
	                    ImgStruct.pixels[y][x][1] = (short) color.getGreen();
	                    ImgStruct.pixels[y][x][2] = (short) color.getBlue();
	                }
	            }            
	                       

	        } catch (IOException e) {
	            System.out.println("Error reading image file: " + e.getMessage());
	        }  	
	}

	public static void writeJpgImage(colourImage ImgStruct, String fileName) {
		 try {
	    	 int width = ImgStruct.width;
	         int height = ImgStruct.height;
	         BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

	         // Set the RGB color values of the BufferedImage using the pixel array
	         for (int y = 0; y < height; y++) {
	             for (int x = 0; x < width; x++) {
	                 int rgb = new Color(ImgStruct.pixels[y][x][0], ImgStruct.pixels[y][x][1], ImgStruct.pixels[y][x][2]).getRGB();
	                 image.setRGB(x, y, rgb);
	             }
	         }

	         // Write the BufferedImage to a JPEG file
	         File outputFile = new File(fileName);
	         ImageIO.write(image, "jpg", outputFile);

	     } catch (IOException e) {
	         System.out.println("Error writing image file: " + e.getMessage());
	     }       
	
       }//

}
