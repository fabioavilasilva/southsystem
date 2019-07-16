package br.com.southsystem.analitycs.model;

import java.math.BigDecimal;

public class Vendedor {

	private String cpf;
	private String name;
	private BigDecimal salary;
	
	public Vendedor(String cpf, String name, BigDecimal salary) {
		this.cpf = cpf;
		this.name = name;
		this.salary = salary;
	}

	public Vendedor() {
	}

	public String getCpf() {
		return cpf;
	}
	
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public BigDecimal getSalary() {
		return salary;
	}
	
	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

}
