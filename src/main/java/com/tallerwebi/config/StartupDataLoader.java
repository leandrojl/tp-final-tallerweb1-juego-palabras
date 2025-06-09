package com.tallerwebi.config;

import com.tallerwebi.dominio.PalabraImportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupDataLoader implements CommandLineRunner {

    private final PalabraImportService importService;

    public StartupDataLoader(PalabraImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(String... args) {
        importService.importarPalabraDesdeAPI("Castellano");
        importService.importarPalabraDesdeAPI("Ingles");
    }
}
