package cr.ac.una.reservas.presentation.model;

import cr.ac.una.reservas.model.Funcionario;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioModel extends AbstractModel {

    public static final String PROP_FUNCIONARIOS = "funcionarios";
    public static final String PROP_FUNCIONARIO_SELECCIONADO = "funcionarioSeleccionado";

    private List<Funcionario> funcionarios = new ArrayList<>();
    private Funcionario funcionarioSeleccionado;

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> nuevaLista) {
        List<Funcionario> anterior = this.funcionarios;
        this.funcionarios = nuevaLista;
        notificarCambioForzado(PROP_FUNCIONARIOS, anterior, nuevaLista);
    }

    public Funcionario getFuncionarioSeleccionado() {
        return funcionarioSeleccionado;
    }

    public void setFuncionarioSeleccionado(Funcionario nuevo) {
        Funcionario anterior = this.funcionarioSeleccionado;
        this.funcionarioSeleccionado = nuevo;
        notificarCambio(PROP_FUNCIONARIO_SELECCIONADO, anterior, nuevo);
    }
}
