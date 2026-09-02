package com.una.reservas.ai;

import com.una.reservas.model.Categoria;

import java.util.List;

/**
 * Contrato para extraer los datos de una reserva a partir de una frase en
 * lenguaje natural escrita por el usuario.
 *
 * Implementaciones esperadas: GeminiExtractorReserva (llama a la API de Gemini)
 * y ReglasExtractorReserva (respaldo por reglas simples de texto, sin red).
 *
 * El servicio que orquesta el intento con Gemini y la caida a reglas si falla
 * (patron Strategy) tambien vive en este paquete. Ver docs/05_ai_extraction.md.
 */
public interface ExtractorReserva {

    /**
     * Intenta extraer los datos de la reserva a partir de la frase dada.
     *
     * @param frase                texto en lenguaje natural escrito por el usuario
     * @param categoriasDisponibles categorias entre las cuales el extractor puede identificar coincidencias
     * @return datos extraidos, con los campos que no se pudieron identificar en null o vacios
     */
    DatosReservaExtraidos extraer(String frase, List<Categoria> categoriasDisponibles);
}
