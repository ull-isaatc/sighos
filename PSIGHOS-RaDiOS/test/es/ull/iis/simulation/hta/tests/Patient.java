package es.ull.iis.simulation.hta.tests;

public class Patient {
	private Double weight;
	private Double age;

	public Patient(Double weight, Double age) {
		this.weight = weight;
		this.age = age;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getAge() {
		return age;
	}

	public void setAge(Double age) {
		this.age = age;
	}

}
