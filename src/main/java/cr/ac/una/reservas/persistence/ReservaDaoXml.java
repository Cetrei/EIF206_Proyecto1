package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.util.PersistenciaException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservaDaoXml implements ReservaDao {
    private final String pathReserva;
    private final List<Reserva> reservas = new ArrayList<>();

    public ReservaDaoXml(String path) {
        this.pathReserva = path;
        cargarDesdeXml();
    }

    private void cargarDesdeXml() {
        File file = new File(pathReserva);
        if (!file.exists()) {
            return;
        }
        try {
            reservas.clear();
            reservas.addAll(obtenerReservas());
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo cargar reservas desde " + pathReserva, e);
        }
    }

    public void guardarReservas() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ReservasWrapper.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            File archivo = new File(pathReserva);
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }
            try (FileOutputStream fileOut = new FileOutputStream(archivo)) {
                jaxbMarshaller.marshal(new ReservasWrapper(reservas), fileOut);
            }
        } catch (Exception e) {
            throw new PersistenciaException("No se pudo guardar reservas en " + pathReserva, e);
        }
    }

    public List<Reserva> obtenerReservas() throws Exception {
        File file = new File(pathReserva);
        JAXBContext jaxbContext = JAXBContext.newInstance(ReservasWrapper.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        ReservasWrapper wrapper = (ReservasWrapper) jaxbUnmarshaller.unmarshal(file);
        return wrapper.getReservas();
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        List<Reserva> res = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getIdFuncionario() != null && reserva.getIdFuncionario().equals(idFuncionario)) {
                res.add(reserva);
            }
        }
        return res;
    }

    @Override
    public Optional<Reserva> buscarPorId(String s) {
        for (Reserva reserva : reservas) {
            if (reserva.getId().equals(s)) {
                return Optional.of(reserva);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Reserva> listarTodos() {
        return reservas;
    }

    @Override
    public void guardar(Reserva entidad) {
        if (buscarPorId(entidad.getId()).isEmpty()) {
            reservas.add(entidad);
        } else {
            for (int i = 0; i < reservas.size(); i++) {
                if (reservas.get(i).getId().equals(entidad.getId())) {
                    reservas.set(i, entidad);
                }
            }
        }
        guardarReservas();
    }

    @Override
    public void eliminar(String s) {
        for (int i = 0; i < reservas.size(); i++) {
            if (reservas.get(i).getId().equals(s)) {
                reservas.remove(i);
                break;
            }
        }
        guardarReservas();
    }

    @XmlRootElement(name = "reservas")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class ReservasWrapper {
        @XmlElement(name = "reserva")
        private List<Reserva> reservas = new ArrayList<>();

        public ReservasWrapper() {
        }
        public ReservasWrapper(List<Reserva> reservas) {
            this.reservas = reservas;
        }

        public List<Reserva> getReservas() {
            return reservas;
        }
    }
}
