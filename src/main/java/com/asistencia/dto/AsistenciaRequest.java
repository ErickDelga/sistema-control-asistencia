package com.asistencia.dto;

import com.asistencia.model.EstadoAsistencia;
import lombok.Data;
@Data
public class AsistenciaRequest {
    private Long estudianteId;
    private EstadoAsistencia estado;

}
