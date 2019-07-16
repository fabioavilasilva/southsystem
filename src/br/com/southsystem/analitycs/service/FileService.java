package br.com.southsystem.analitycs.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.southsystem.analitycs.model.Cliente;
import br.com.southsystem.analitycs.model.Item;
import br.com.southsystem.analitycs.model.Venda;
import br.com.southsystem.analitycs.model.Vendedor;

import java.util.Optional;
import java.util.Scanner;
import java.util.TreeSet;

public class FileService {

	private List<Cliente> clientes = new ArrayList<Cliente>();
	private List<Vendedor> vendedores = new ArrayList<Vendedor>();
	private TreeSet<Venda> vendas = new TreeSet<Venda>();
	private String nomeArquivo;
	private String extension;
	private String done;
	private String repeatedFileExtension;
	Map<String, String> config = new HashMap<String, String>();
	
	public void init(Map<String, String> config) {
		this.config = config;
		
		this.extension = config.get("extension");
		this.done = config.get("done");
		this.repeatedFileExtension = config.get("repeatedFileExtension");
		
		File inPath = new File(config.get("inPath"));
		Path dir = inPath.toPath();

		//Processa todos os arquivos que já estão no diretório de entrada
		try {
			Files.list(dir).forEach(e -> {
				processFile(dir, e.getFileName());
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Ativa o listener para aguardar que novos arquivos sejam criados
		listen(dir);
	}

	@SuppressWarnings("unchecked")
	public void listen(Path dir) {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();

			for (;;) {

				WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path filename = ev.context();
					nomeArquivo = filename.toString();

					processFile(dir, filename);
				}

				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processFile(Path dir, Path filename) {
		try {
			nomeArquivo = filename.toString();

			Path child = dir.resolve(filename);
			if (Files.isReadable(child) && filename.toString().endsWith(this.extension)) {
				readFile(child);
				analyzeResults();
				clientes = new ArrayList<Cliente>();
				vendedores = new ArrayList<Vendedor>();
				vendas = new TreeSet<Venda>();
				Files.delete(child);
			}
		} catch (IOException x) {
			System.err.println(x);
			return;
		}
	}

	public void readFile(Path filename) {
		try {
			Scanner fileScanner = new Scanner(filename.toFile());

			while (fileScanner.hasNextLine()) {
				Scanner lineScanner = new Scanner(fileScanner.nextLine());
				lineScanner.useDelimiter("ç");
				
				if (lineScanner.hasNext()) {
					int type = lineScanner.nextInt(); 
					
					switch (type) {
						case 1: 
							processSalesman(lineScanner);
							break;
						case 2: 
							processClient(lineScanner);
							break;
						case 3: 
							processSales(lineScanner);
							break;
					}
				}
			}
			
			fileScanner.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void processSalesman(Scanner lineScanner) {
		Vendedor vendedor = new Vendedor();
		
		for (int i = 0; lineScanner.hasNext(); i++) {
			switch (i) {
				case 1: 
					vendedor.setCpf(lineScanner.next());
					break;
				case 2: 
					vendedor.setName(lineScanner.next());
					break;
				case 3: 
					vendedor.setSalary(new BigDecimal(lineScanner.next()));
					break;
			}
		}
		
		vendedores.add(vendedor);
	}

	private void processClient(Scanner lineScanner) {
		Cliente cliente = new Cliente();
		
		for (int i = 0; lineScanner.hasNext(); i++) {
			switch (i) {
				case 1: 
					cliente.setCnpj(lineScanner.next());
					break;
				case 2: 
					cliente.setName(lineScanner.next());
					break;
				case 3: 
					cliente.setBusinessArea(lineScanner.next());
					break;
			}
		}
		
		clientes.add(cliente);
	}

	private void processSales(Scanner lineScanner) {
		Venda venda = new Venda();
		
		for (int i = 0; lineScanner.hasNext(); i++) {
			switch (i) {
				case 1: 
					venda.setId(new Integer(lineScanner.next()));
					break;
				case 2: 
					String itens = lineScanner.next();
					venda.setItens(processItens(itens.substring(1, itens.length()-2)));
					break;
				case 3: 
					venda.setVendedor(findVendedorByName(lineScanner.next()));
					break;
			}
		}
		
		venda.setValorTotal(sumItens(venda.getItens()));
		vendas.add(venda);
	}

	private List<Item> processItens(String itensList) {
		List<Item> itens = new ArrayList<Item>();
		
		Scanner itensScanner = new Scanner(itensList);
		itensScanner.useDelimiter(",");
		while (itensScanner.hasNext()) {
			Scanner itemScanner = new Scanner(itensScanner.next());
			itemScanner.useDelimiter("-");
			itens.add(processItem(itemScanner));
		}
		itensScanner.close();
		
		return itens;
	}

	private Item processItem(Scanner itemScanner) {
		Item item = new Item();
		
		for (int i = 0; itemScanner.hasNext(); i++) {
			switch (i) {
				case 1: 
					item.setId(new Integer(itemScanner.next()));
					break;
				case 2: 
					item.setQuantity(new Integer(itemScanner.next()));
					break;
				case 3: 
					item.setPrice(new BigDecimal(itemScanner.next()));
					break;
			}
		}
		
		return item;
	}

	private BigDecimal sumItens(List<Item> itens) {
		BigDecimal total = new BigDecimal("0.00");
		
		for (Item item: itens) {
			total = total.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
		}
		
		return total;
	}

	private Vendedor findVendedorByName(String name) {
		for (Vendedor vendedor:vendedores) {
			if (vendedor.getName().equals(name)) {
				return vendedor;
			}
		}
		return null;
	}

	private void analyzeResults() throws IOException {
		Integer totalClientes = clientes.size();
		Integer totalVendedores = vendedores.size();
		Vendedor piorVendedor = getPiorVendedor();
		Venda vendaMaisCara = vendas.pollLast();
		String doneExtension = "."+this.done+this.extension;

		String filename = config.get("outPath") + "/" + nomeArquivo.replaceAll(this.extension, doneExtension);
		File outFile = new File(filename);
		if (!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		
		while (outFile.exists()) {
			outFile = new File(outFile.getAbsolutePath().replaceAll(doneExtension, this.repeatedFileExtension+doneExtension));
		}
		
        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(outFile));
        
        buffWrite.append("Quantidade de clientes no arquivo de entrada: " + totalClientes + "\n");
        buffWrite.append("Quantidade de vendedor no arquivo de entrada: " + totalVendedores + "\n");
        buffWrite.append("ID da venda mais cara: " + vendaMaisCara.getId() + "\n");
        buffWrite.append("O pior vendedor: " + piorVendedor.getName() + "\n");
        
        buffWrite.close();
	}

	private Vendedor getPiorVendedor() {
		Map<String, BigDecimal> totalPorVendedor = new HashMap<String, BigDecimal>();
		
		for (Venda venda : vendas) {
			if (!totalPorVendedor.containsKey(venda.getVendedor().getName())) {
				totalPorVendedor.put(venda.getVendedor().getName(), venda.getValorTotal());
			} else {
				totalPorVendedor.replace(venda.getVendedor().getName(), totalPorVendedor.get(venda.getVendedor().getName()).add(venda.getValorTotal()));
			}
		}

		Optional<String> piorVendedor = Optional.empty();
		BigDecimal totalPiorVendedor = new BigDecimal("0.00");
		
		for (Entry<String, BigDecimal> entry : totalPorVendedor.entrySet()) {
			if (!piorVendedor.isPresent() || entry.getValue().compareTo(totalPiorVendedor) < 0) {
				totalPiorVendedor = entry.getValue();
				piorVendedor = Optional.of(entry.getKey());
			}
		}
		
		return findVendedorByName(piorVendedor.get());
	}

}
