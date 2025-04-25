package com.example.taskmanager.controller;

import com.example.taskmanager.model.Tarefa;
import com.example.taskmanager.model.dao.TarefaDAO;
import com.example.taskmanager.util.AlertUtil;
import com.example.taskmanager.util.DateUtil;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TarefaController implements Initializable {

    @FXML
    private TableView<Tarefa> tableTarefas;

    @FXML
    private TableColumn<Tarefa, Integer> columnId;

    @FXML
    private TableColumn<Tarefa, String> columnTitulo;

    @FXML
    private TableColumn<Tarefa, String> columnDescricao;

    @FXML
    private TableColumn<Tarefa, LocalDate> columnData;

    @FXML
    private TableColumn<Tarefa, String> columnStatus;

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextArea txtDescricao;

    @FXML
    private DatePicker dpDataEntrega;

    @FXML
    private ComboBox<String> cbStatus;

    @FXML
    private Button btnNovo;

    @FXML
    private Button btnSalvar;

    @FXML
    private Button btnExcluir;

    @FXML
    private Button btnAtualizar;

    @FXML
    private Label labelDateTime;

    private final TarefaDAO tarefaDAO = new TarefaDAO();
    private Tarefa tarefaSelecionada;
    private ObservableList<Tarefa> tarefas;
    private Thread clockThread;

    /**
     * Inicializa o controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxStatus();
        configurarColunas();
        atualizarTabela();
        limparFormulario();

        iniciarRelogio();
    }

    /**
     * Configura o ComboBox de status.
     */
    private void configurarComboBoxStatus() {
        List<String> statusOpcoes = Arrays.asList("A Fazer", "Em Andamento", "Concluído");
        cbStatus.setItems(FXCollections.observableArrayList(statusOpcoes));
        cbStatus.setValue("A Fazer");
    }

    /**
     * Configura as colunas da tabela.
     */
    private void configurarColunas() {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        columnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        columnData.setCellValueFactory(new PropertyValueFactory<>("dataEntrega"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    /**
     * Atualiza a tabela com os dados do banco.
     */
    private void atualizarTabela() {
        try {
            List<Tarefa> listaTarefas = tarefaDAO.listarTodos();
            tarefas = FXCollections.observableArrayList(listaTarefas);
            tableTarefas.setItems(tarefas);
        } catch (SQLException e) {
            AlertUtil.showError("Erro", "Erro ao carregar tarefas",
                    "Ocorreu um erro ao carregar as tarefas: " + e.getMessage());
        }
    }

    /**
     * Limpa os campos do formulário.
     */
    private void limparFormulario() {
        txtTitulo.clear();
        txtDescricao.clear();
        dpDataEntrega.setValue(null);
        cbStatus.setValue("A Fazer");

        tarefaSelecionada = null;

        btnExcluir.setDisable(true);
    }

    /**
     * Inicializa o relógio que exibe data e hora em tempo real.
     */
    private void iniciarRelogio() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        clockThread = new Thread(() -> {
            while (true) {
                try {
                    // Atualiza o relógio a cada segundo
                    String timeString = LocalDateTime.now().format(formatter);

                    // Atualização da UI deve ser feita na thread principal
                    Platform.runLater(() -> labelDateTime.setText(timeString));

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        clockThread.setDaemon(true);
        clockThread.start();
    }

    //Preenche o formulário com os dados da tarefa selecionada.
    private void preencherFormulario(Tarefa tarefa) {
        if (tarefa != null) {
            txtTitulo.setText(tarefa.getTitulo());
            txtDescricao.setText(tarefa.getDescricao());
            dpDataEntrega.setValue(tarefa.getDataEntrega());
            cbStatus.setValue(tarefa.getStatus());

            btnExcluir.setDisable(false);
        }
    }

    //Valida o formulário para garantir que os campos obrigatórios estão preenchidos.
    private boolean validarFormulario() {
        String mensagemErro = "";

        if (txtTitulo.getText() == null || txtTitulo.getText().trim().isEmpty()) {
            mensagemErro += "- O título é obrigatório.\n";
        }

        if (!mensagemErro.isEmpty()) {
            AlertUtil.showError("Validação", "Erro de validação",
                    "Corrija os seguintes erros:\n" + mensagemErro);
            return false;
        }

        return true;
    }
    @FXML
    private void handleNovo() {
        limparFormulario();
    }
    @FXML
    private void handleSalvar() {
        if (!validarFormulario()) {
            return;
        }

        try {
            if (tarefaSelecionada == null) {
                // Criar nova tarefa
                Tarefa novaTarefa = new Tarefa();
                novaTarefa.setTitulo(txtTitulo.getText());
                novaTarefa.setDescricao(txtDescricao.getText());
                novaTarefa.setDataEntrega(dpDataEntrega.getValue());
                novaTarefa.setStatus(cbStatus.getValue());

                tarefaDAO.salvar(novaTarefa);

                AlertUtil.showInfo("Sucesso", null, "Tarefa cadastrada com sucesso!");
            } else {
                // Atualizar tarefa existente
                tarefaSelecionada.setTitulo(txtTitulo.getText());
                tarefaSelecionada.setDescricao(txtDescricao.getText());
                tarefaSelecionada.setDataEntrega(dpDataEntrega.getValue());
                tarefaSelecionada.setStatus(cbStatus.getValue());

                tarefaDAO.atualizar(tarefaSelecionada);

                AlertUtil.showInfo("Sucesso", null, "Tarefa atualizada com sucesso!");
            }

            limparFormulario();
            atualizarTabela();
        } catch (SQLException e) {
            AlertUtil.showError("Erro", "Erro ao salvar tarefa",
                    "Ocorreu um erro ao salvar a tarefa: " + e.getMessage());
        }
    }

    //Manipula o evento de clique no botão Excluir.
    @FXML
    private void handleExcluir() {
        if (tarefaSelecionada == null) {
            AlertUtil.showError("Erro", "Nenhuma tarefa selecionada",
                    "Selecione uma tarefa para excluir.");
            return;
        }

        boolean confirmacao = AlertUtil.showConfirmation("Confirmação", "Excluir Tarefa",
                "Tem certeza que deseja excluir esta tarefa?");

        if (confirmacao) {
            try {
                tarefaDAO.excluir(tarefaSelecionada.getId());

                AlertUtil.showInfo("Sucesso", null, "Tarefa excluída com sucesso!");

                limparFormulario();
                atualizarTabela();
            } catch (SQLException e) {
                AlertUtil.showError("Erro", "Erro ao excluir tarefa",
                        "Ocorreu um erro ao excluir a tarefa: " + e.getMessage());
            }
        }
    }

    //Manipula o evento de clique no botão Atualizar.
    @FXML
    private void handleAtualizar() {
        atualizarTabela();
    }

    //Mmanipula o evento de seleção de tarefa na tabela.
    @FXML
    private void handleTarefaSelect() {
        Tarefa tarefa = tableTarefas.getSelectionModel().getSelectedItem();

        if (tarefa != null) {
            tarefaSelecionada = tarefa;
            preencherFormulario(tarefa);
        }
    }

    //metodo chamado quando o controlador é finalizado.
    public void finalise() {
        if (clockThread != null && clockThread.isAlive()) {
            clockThread.interrupt();
        }
    }

}