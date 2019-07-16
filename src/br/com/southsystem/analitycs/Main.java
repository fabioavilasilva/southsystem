package br.com.southsystem.analitycs;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import br.com.southsystem.analitycs.service.FileService;

public class Main {
	
	public static void main(String[] args) {
		//Carrega as configurações. Se não existir o arquivo de properties, ou se a configuração não estiver lá, carrega os valores default
		Map<String, String> config = new HashMap<String, String>(0);
		
		ResourceBundle configResource = ResourceBundle.getBundle("br.com.southsystem.analitycs.resources.config");
		
		if (configResource != null) {
			if (configResource.containsKey("path.in")) {
				config.put("inPath", configResource.getString("path.in"));
			} else {
				config.put("inPath", "/data/in");
			}
			if (configResource.containsKey("path.out")) {
				config.put("outPath", configResource.getString("path.out"));
			} else {
				config.put("outPath", "/data/out");
			}
			if (configResource.containsKey("extension")) {
				config.put("extension", configResource.getString("extension"));
			} else {
				config.put("extension", ".dat");
			}
			if (configResource.containsKey("done")) {
				config.put("done", configResource.getString("done"));
			} else {
				config.put("done", "done");
			}
			if (configResource.containsKey("repeated_file_extension")) {
				config.put("repeatedFileExtension", configResource.getString("repeated_file_extension"));
			} else {
				config.put("repeatedFileExtension", "(new)");
			}
		} else {
			config.put("inPath", "/data/in");
			config.put("outPath", "/data/out");
			config.put("extension", ".dat");
			config.put("done", "done");
			config.put("repeatedFileExtension", "(new)");
		}

		// Executa o processamento dos arquivos
		FileService fileService = new FileService();
		fileService.init(config);
	}

}
