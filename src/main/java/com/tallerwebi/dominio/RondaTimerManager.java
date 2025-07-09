package com.tallerwebi.dominio;
import com.tallerwebi.dominio.DTO.MensajeAvanzarRondaDTO;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class RondaTimerManager {

    private final ScheduledExecutorService scheduler;
    private final Map<Long, ScheduledFuture<?>> tareasPorPartida = new ConcurrentHashMap<>();

    @Lazy //Resuelve el autowired una vez que el hilo llama a partidaService lo que asegura que se cargue correctaemente sino rompe porque no lo detecta
    @Autowired
    private PartidaService partidaService;


    public RondaTimerManager() {
        this.scheduler = Executors.newScheduledThreadPool(
                Runtime.getRuntime().availableProcessors() * 2
        );
    }

    public void agendarFinalizacionRonda(Long partidaId, long delaySegundos) {
        cancelarTarea(partidaId); // evita duplicados

        ScheduledFuture<?> tarea = scheduler.schedule(() -> {
            try {
                MensajeAvanzarRondaDTO dto = new MensajeAvanzarRondaDTO(partidaId, 0L, true);
                partidaService.avanzarRonda(dto); // se invoca vía proxy (¡importante!)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySegundos, TimeUnit.SECONDS);

        tareasPorPartida.put(partidaId, tarea);
    }

    public void cancelarTarea(Long partidaId) {
        ScheduledFuture<?> tarea = tareasPorPartida.remove(partidaId);
        if (tarea != null && !tarea.isDone()) {
            tarea.cancel(true);

        }
    }
    //Sugerido para cerrar y que funcionen bien los test.
    @PreDestroy
    public void shutdown() {
        System.out.println("🔻 Apagando scheduler de rondas...");
        scheduler.shutdownNow();
    }
}