package br.com.southsystem.analitycs.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Venda implements Comparable<Venda> {

	private Integer id;
	private List<Item> itens = new ArrayList<Item>();
	private Vendedor vendedor;
	private BigDecimal valorTotal;

	public Venda() {
	}

	public Venda(Integer id, List<Item> itens, Vendedor vendedor) {
		this.id = id;
		this.itens = itens;
		this.vendedor = vendedor;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<Item> getItens() {
		return itens;
	}

	public void setItens(List<Item> itens) {
		this.itens = itens;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public int compareTo(Venda b) {
		if (this.valorTotal.compareTo(b.getValorTotal()) == 0) {
			return this.id.compareTo(b.getId());
		} else {
			return this.valorTotal.compareTo(b.getValorTotal());
		}
	}
}
