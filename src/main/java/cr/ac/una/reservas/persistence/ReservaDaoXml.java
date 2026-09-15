package cr.ac.una.reservas.persistence;

import cr.ac.una.reservas.model.Reserva;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
    }

    public void guardarFuncionarios() throws Exception{
        JAXBContext jaxbContext = JAXBContext.newInstance(Reserva.class);
        FileOutputStream fileOut = new FileOutputStream(pathReserva);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.marshal(reservas, fileOut);
        fileOut.flush();
        fileOut.close();
    }

    public List<Reserva> obtenerFuncionarios() throws Exception{
        File file = new File(pathReserva);
        JAXBContext jaxbContext = JAXBContext.newInstance(Reserva.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (List<Reserva>) jaxbUnmarshaller.unmarshal(file);
    }

    @Override
    public List<Reserva> listarPorFuncionario(String idFuncionario) {
        List<Reserva> res = new ArrayList<>();
        for (Reserva reserva : reservas) {
            if (reserva.getFuncionario().getId().equals(idFuncionario)) {
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
        }else{
            for  (int i = 0; i< reservas.size(); i++) {
                if(reservas.get(i).getId().equals(entidad.getId())) {
                    reservas.set(i, entidad);
                }
            }
        }
    }

    @Override
    public void eliminar(String s) {
        for  (int i = 0; i< reservas.size(); i++) {
            if(reservas.get(i).getId().equals(s)) {
                reservas.remove(i);
                break;
            }
        }
    }
}
