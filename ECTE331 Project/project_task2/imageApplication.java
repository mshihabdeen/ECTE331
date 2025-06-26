package project2;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class imageApplication {
    
    public static void main(String[] args) {
        String inputFileName = "C:\Users\Muaadh\Desktop\ECTE331 Project\Rain_Tree.jpg";  // Update path as needed
        String outputSingleThread = "C:\Users\Muaadh\Desktop\ECTE331 Project\output_single.jpg";
        String outputMultiShared = "C:\Users\Muaadh\Desktop\ECTE331 Project\output_multi_shared.jpg";
        String outputMultiSub = "C:\Users\Muaadh\Desktop\ECTE331 Project\output_multi_sub.jpg";
        
        // Test different numbers of threads
        int[] threadCounts = {2, 4, 8, 16};
        int numAverages = 3; // For fair comparison
        
        System.out.println("Histogram Equalization Performance Comparison\n");
        
        // Load the input image
        colourImage inputImage = new colourImage();
        imageReadWrite.readJpgImage(inputFileName, inputImage);
        System.out.println("Input image loaded: " + inputImage.width + "x" + inputImage.height + " pixels\n");
        
        // Test single-threaded implementation
        System.out.println("Testing Single-Threaded Implementation:");
        long singleThreadTime = testSingleThreaded(inputImage, outputSingleThread, numAverages);
        System.out.println("Average execution time: " + singleThreadTime + " ms\n");
        
        // Test multi-threaded implementations
        System.out.println("Testing Multi-Threaded Implementations:");
        System.out.println("Threads\tShared Histogram\tSub-Histograms");
        
        for (int numThreads : threadCounts) {
            long sharedTime = testMultiThreadedShared(inputImage, outputMultiShared, numThreads, numAverages);
            long subTime = testMultiThreadedSub(inputImage, outputMultiSub, numThreads, numAverages);
            
            System.out.println(numThreads + "\t" + sharedTime + " ms\t\t" + subTime + " ms");
        }
        
        System.out.println("\n=== Performance Analysis ===");
        System.out.println("Single-threaded baseline: " + singleThreadTime + " ms");
        
        for (int numThreads : threadCounts) {
            long sharedTime = testMultiThreadedShared(inputImage, null, numThreads, 1);
            long subTime = testMultiThreadedSub(inputImage, null, numThreads, 1);
            
            double sharedSpeedup = (double) singleThreadTime / sharedTime;
            double subSpeedup = (double) singleThreadTime / subTime;
            
            System.out.printf("%d threads - Shared: %.2fx speedup, Sub-histograms: %.2fx speedup%n", 
                            numThreads, sharedSpeedup, subSpeedup);
        }
    }
    
    // Test single-threaded implementation
    private static long testSingleThreaded(colourImage inputImage, String outputFile, int numAverages) {
        long totalTime = 0;
        
        for (int i = 0; i < numAverages; i++) {
            colourImage outputImage = new colourImage();
            copyImageStructure(inputImage, outputImage);
            
            long startTime = System.currentTimeMillis();
            histogramEqualizationSingleThread(inputImage, outputImage);
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
            
            // Save output only on first iteration
            if (i == 0 && outputFile != null) {
                imageReadWrite.writeJpgImage(outputImage, outputFile);
            }
        }
        
        return totalTime / numAverages;
    }
    
    // Test multi-threaded shared histogram implementation
    private static long testMultiThreadedShared(colourImage inputImage, String outputFile, int numThreads, int numAverages) {
        long totalTime = 0;
        
        for (int i = 0; i < numAverages; i++) {
            colourImage outputImage = new colourImage();
            copyImageStructure(inputImage, outputImage);
            
            long startTime = System.currentTimeMillis();
            histogramEqualizationMultiThreadShared(inputImage, outputImage, numThreads);
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
            
            // Save output only on first iteration
            if (i == 0 && outputFile != null) {
                imageReadWrite.writeJpgImage(outputImage, outputFile);
            }
        }
        
        return totalTime / numAverages;
    }
    
    // Test multi-threaded sub-histograms implementation
    private static long testMultiThreadedSub(colourImage inputImage, String outputFile, int numThreads, int numAverages) {
        long totalTime = 0;
        
        for (int i = 0; i < numAverages; i++) {
            colourImage outputImage = new colourImage();
            copyImageStructure(inputImage, outputImage);
            
            long startTime = System.currentTimeMillis();
            histogramEqualizationMultiThreadSub(inputImage, outputImage, numThreads);
            long endTime = System.currentTimeMillis();
            
            totalTime += (endTime - startTime);
            
            // Save output only on first iteration
            if (i == 0 && outputFile != null) {
                imageReadWrite.writeJpgImage(outputImage, outputFile);
            }
        }
        
        return totalTime / numAverages;
    }
    
    // Single-threaded histogram equalization
    private static void histogramEqualizationSingleThread(colourImage inputImage, colourImage outputImage) {
        int width = inputImage.width;
        int height = inputImage.height;
        int size = width * height;
        int L = 255; // For 8-bit images
        
        // Process each color channel separately
        for (int channel = 0; channel < 3; channel++) {
            // Step 1: Compute histogram
            int[] histogram = new int[256];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int intensity = inputImage.pixels[y][x][channel] & 0xFF;
                    histogram[intensity]++;
                }
            }
            
            // Step 2: Calculate cumulative histogram with optimization
            int[] cumulativeHist = new int[256];
            cumulativeHist[0] = histogram[0];
            for (int i = 1; i <= L; i++) {
                cumulativeHist[i] = cumulativeHist[i-1] + histogram[i];
            }
            
            // Optimize: pre-calculate the mapping
            for (int i = 0; i <= L; i++) {
                cumulativeHist[i] = (cumulativeHist[i] * L) / size;
            }
            
            // Step 3: Apply mapping
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int intensity = inputImage.pixels[y][x][channel] & 0xFF;
                    outputImage.pixels[y][x][channel] = (short) cumulativeHist[intensity];
                }
            }
        }
    }
    
    // Multi-threaded histogram equalization with shared histogram
    private static void histogramEqualizationMultiThreadShared(colourImage inputImage, colourImage outputImage, int numThreads) {
        int width = inputImage.width;
        int height = inputImage.height;
        int size = width * height;
        int L = 255;
        
        // Process each color channel separately
        for (int channel = 0; channel < 3; channel++) {
            // Step 1: Compute histogram using shared atomic array
            AtomicIntegerArray sharedHistogram = new AtomicIntegerArray(256);
            
            Thread[] threads = new Thread[numThreads];
            int rowsPerThread = height / numThreads;
            
            // Create threads for histogram computation
            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                final int startRow = t * rowsPerThread;
                final int endRow = (t == numThreads - 1) ? height : (t + 1) * rowsPerThread;
                final int currentChannel = channel;
                
                threads[t] = new Thread(() -> {
                    for (int y = startRow; y < endRow; y++) {
                        for (int x = 0; x < width; x++) {
                            int intensity = inputImage.pixels[y][x][currentChannel] & 0xFF;
                            sharedHistogram.incrementAndGet(intensity);
                        }
                    }
                });
            }
            
            // Start and wait for histogram computation threads
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Step 2: Calculate cumulative histogram (single-threaded for simplicity)
            int[] cumulativeHist = new int[256];
            cumulativeHist[0] = sharedHistogram.get(0);
            for (int i = 1; i <= L; i++) {
                cumulativeHist[i] = cumulativeHist[i-1] + sharedHistogram.get(i);
            }
            
            // Optimize: pre-calculate the mapping
            for (int i = 0; i <= L; i++) {
                cumulativeHist[i] = (cumulativeHist[i] * L) / size;
            }
            
            // Step 3: Apply mapping using multiple threads
            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                final int startRow = t * rowsPerThread;
                final int endRow = (t == numThreads - 1) ? height : (t + 1) * rowsPerThread;
                final int currentChannel = channel;
                
                threads[t] = new Thread(() -> {
                    for (int y = startRow; y < endRow; y++) {
                        for (int x = 0; x < width; x++) {
                            int intensity = inputImage.pixels[y][x][currentChannel] & 0xFF;
                            outputImage.pixels[y][x][currentChannel] = (short) cumulativeHist[intensity];
                        }
                    }
                });
            }
            
            // Start and wait for mapping threads
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Multi-threaded histogram equalization with sub-histograms
    private static void histogramEqualizationMultiThreadSub(colourImage inputImage, colourImage outputImage, int numThreads) {
        int width = inputImage.width;
        int height = inputImage.height;
        int size = width * height;
        int L = 255;
        
        // Process each color channel separately
        for (int channel = 0; channel < 3; channel++) {
            // Step 1: Compute sub-histograms
            int[][] subHistograms = new int[numThreads][256];
            Thread[] threads = new Thread[numThreads];
            int rowsPerThread = height / numThreads;
            
            // Create threads for sub-histogram computation
            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                final int startRow = t * rowsPerThread;
                final int endRow = (t == numThreads - 1) ? height : (t + 1) * rowsPerThread;
                final int currentChannel = channel;
                
                threads[t] = new Thread(() -> {
                    for (int y = startRow; y < endRow; y++) {
                        for (int x = 0; x < width; x++) {
                            int intensity = inputImage.pixels[y][x][currentChannel] & 0xFF;
                            subHistograms[threadId][intensity]++;
                        }
                    }
                });
            }
            
            // Start and wait for sub-histogram computation threads
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            // Step 2: Combine sub-histograms
            int[] finalHistogram = new int[256];
            for (int i = 0; i < 256; i++) {
                for (int t = 0; t < numThreads; t++) {
                    finalHistogram[i] += subHistograms[t][i];
                }
            }
            
            // Step 3: Calculate cumulative histogram
            int[] cumulativeHist = new int[256];
            cumulativeHist[0] = finalHistogram[0];
            for (int i = 1; i <= L; i++) {
                cumulativeHist[i] = cumulativeHist[i-1] + finalHistogram[i];
            }
            
            // Optimize: pre-calculate the mapping
            for (int i = 0; i <= L; i++) {
                cumulativeHist[i] = (cumulativeHist[i] * L) / size;
            }
            
            // Step 4: Apply mapping using multiple threads
            for (int t = 0; t < numThreads; t++) {
                final int threadId = t;
                final int startRow = t * rowsPerThread;
                final int endRow = (t == numThreads - 1) ? height : (t + 1) * rowsPerThread;
                final int currentChannel = channel;
                
                threads[t] = new Thread(() -> {
                    for (int y = startRow; y < endRow; y++) {
                        for (int x = 0; x < width; x++) {
                            int intensity = inputImage.pixels[y][x][currentChannel] & 0xFF;
                            outputImage.pixels[y][x][currentChannel] = (short) cumulativeHist[intensity];
                        }
                    }
                });
            }
            
            // Start and wait for mapping threads
            for (Thread thread : threads) {
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Helper method to copy image structure
    private static void copyImageStructure(colourImage source, colourImage destination) {
        destination.width = source.width;
        destination.height = source.height;
        destination.pixels = new short[source.height][source.width][3];
    }
}
