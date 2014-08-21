package fr.njiv.plugin;

/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

import java.awt.image.ConvolveOp;

import java.awt.image.*;

/**
 * A filter which applies Gaussian blur to an image. This is a subclass of ConvolveFilter
 * which simply creates a kernel with a Gaussian distribution for blurring.
 * @author Jerry Huxtable
 */
 class Blur {

	   public static ConvolveOp getGaussianBlurFilter(int radius,
	            boolean horizontal) {
	        if (radius < 1) {
	            throw new IllegalArgumentException("Radius must be >= 1");
	        }
	        
	        int size = radius * 2 + 1;
	        float[] data = new float[size];
	        
	        float sigma = radius / 3.0f;
	        float twoSigmaSquare = 2.0f * sigma * sigma;
	        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
	        float total = 0.0f;
	        
	        for (int i = -radius; i <= radius; i++) {
	            float distance = i * i;
	            int index = i + radius;
	            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
	            total += data[index];
	        }
	        
	        for (int i = 0; i < data.length; i++) {
	            data[i] /= total;
	        }        
	        
	        Kernel kernel = null;
	        if (horizontal) {
	            kernel = new Kernel(size, 1, data);
	        } else {
	            kernel = new Kernel(1, size, data);
	        }
	        return new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
	    }
	   
}