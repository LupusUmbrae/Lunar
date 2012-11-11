package org.moss.lunar.postprocess;

/**
 * This class deals with using Bi-Linear Interpolation to fill in the gaps
 * inside the data set
 * 
 * @author Robin
 * 
 */
public class PostProcessor {

	private Float latStep;
	private Float lonStep;

	public PostProcessor(Float latStep, Float lonStep) {
		this.latStep = latStep;
		this.lonStep = lonStep;

	}

	public void postProcess() {

	}
}
