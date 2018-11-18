package lsh.querying;

import java.util.Collections;
import java.util.List;

public class Perturbation2 implements Comparable<Perturbation2> {
	
	private List<Integer> perturbedVector;
	private double perturbationScore;
	
	public Perturbation2(List<Integer> perturbedVector, double perturbationScore) {
		this.perturbedVector = perturbedVector;
		this.perturbationScore = perturbationScore;
	}

	@Override
	public int compareTo(Perturbation2 other) {
		
		if (other == null) {
			throw new NullPointerException();
		}
		
		Perturbation2 otherPerturbation = (Perturbation2) other;
		return Double.compare(Double.valueOf(this.perturbationScore), Double.valueOf(otherPerturbation.perturbationScore));

	}

	public List<Integer> getPerturbedVector() {
		return Collections.unmodifiableList(this.perturbedVector);
	}
	public double getPerturbationScore() { return this.perturbationScore; }
}
