package lsh.querying;

import java.util.Collections;
import java.util.List;

public class Perturbation implements Comparable<Perturbation> {
	
	private List<Integer> perturbedVector;
	private double perturbationScore;
	
	public Perturbation(List<Integer> perturbedVector, double perturbationScore) {
		this.perturbedVector = perturbedVector;
		this.perturbationScore = perturbationScore;
	}

	@Override
	public int compareTo(Perturbation other) {
		
		if (other == null) {
			throw new NullPointerException();
		}
		
		Perturbation otherPerturbation = (Perturbation) other;
		return Double.compare(Double.valueOf(this.perturbationScore), Double.valueOf(otherPerturbation.perturbationScore));

	}

	public List<Integer> getPerturbedVector() {
		return Collections.unmodifiableList(this.perturbedVector);
	}
	public double getPerturbationScore() { return this.perturbationScore; }
}
