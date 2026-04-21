import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class EstoqueApp extends JFrame {
    private final JTextField campoNome = new JTextField();
    private final JTextField campoQuantidade = new JTextField();
    private final JTextField campoPreco = new JTextField();

    private final DefaultTableModel modeloTabela = new DefaultTableModel(
            new Object[]{"ID", "Produto", "Quantidade", "Preço (R$)", "Total (R$)"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabela = new JTable(modeloTabela);

    // Painel administrativo
    private final JLabel lblTotalProdutos = new JLabel("0");
    private final JLabel lblUnidadesEstoque = new JLabel("0");
    private final JLabel lblValorTotal = new JLabel("R$ 0,00");
    private final JLabel lblEstoqueBaixo = new JLabel("0");
    private final JSpinner spinnerMinimoBaixo = new JSpinner(new SpinnerNumberModel(5, 0, 100000, 1));
    private final JSpinner spinnerAjusteQuantidade = new JSpinner(new SpinnerNumberModel(1, -100000, 100000, 1));
    private final JSpinner spinnerReajustePercentual = new JSpinner(new SpinnerNumberModel(5.0, -100.0, 500.0, 0.5));

    private int proximoId = 1;

    public EstoqueApp() {
        super("Sistema de Estoque - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 600);
        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Estoque", criarPainelEstoque());
        abas.addTab("Painel administrativo", criarPainelAdministrativo());

        add(abas);
        atualizarIndicadoresAdmin();
    }

    private JPanel criarPainelEstoque() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));

        JPanel painelFormulario = criarPainelFormulario();
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel painelRodape = criarPainelRodape();

        painel.add(painelFormulario, BorderLayout.NORTH);
        painel.add(scrollTabela, BorderLayout.CENTER);
        painel.add(painelRodape, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(new EmptyBorder(10, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Produto:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painel.add(new JLabel("Quantidade:"), gbc);

        gbc.gridx = 1;
        painel.add(campoQuantidade, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(new JLabel("Preço unitário (R$):"), gbc);

        gbc.gridx = 1;
        painel.add(campoPreco, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.BOTH;

        JButton botaoAdicionar = new JButton("Adicionar");
        botaoAdicionar.addActionListener(this::adicionarProduto);
        painel.add(botaoAdicionar, gbc);

        return painel;
    }

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painel.setBorder(new EmptyBorder(0, 10, 10, 10));

        JButton botaoRemoverSelecionado = new JButton("Remover selecionado");
        botaoRemoverSelecionado.addActionListener(e -> removerSelecionado());

        JButton botaoLimpar = new JButton("Limpar estoque");
        botaoLimpar.addActionListener(e -> limparEstoque());

        JButton botaoValorTotal = new JButton("Mostrar valor total");
        botaoValorTotal.addActionListener(e -> mostrarValorTotal());

        painel.add(botaoRemoverSelecionado);
        painel.add(botaoLimpar);
        painel.add(botaoValorTotal);

        return painel;
    }

    private JPanel criarPainelAdministrativo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel painelIndicadores = new JPanel(new GridLayout(2, 2, 10, 10));
        painelIndicadores.add(criarCardIndicador("Produtos cadastrados", lblTotalProdutos));
        painelIndicadores.add(criarCardIndicador("Unidades em estoque", lblUnidadesEstoque));
        painelIndicadores.add(criarCardIndicador("Valor total", lblValorTotal));
        painelIndicadores.add(criarCardIndicador("Produtos com estoque baixo", lblEstoqueBaixo));

        JPanel painelAcoes = new JPanel(new GridBagLayout());
        painelAcoes.setBorder(BorderFactory.createTitledBorder("Ações administrativas"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        painelAcoes.add(new JLabel("Limite estoque baixo:"), gbc);

        gbc.gridx = 1;
        painelAcoes.add(spinnerMinimoBaixo, gbc);

        JButton botaoAtualizar = new JButton("Atualizar indicadores");
        botaoAtualizar.addActionListener(e -> atualizarIndicadoresAdmin());
        gbc.gridx = 2;
        painelAcoes.add(botaoAtualizar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        painelAcoes.add(new JLabel("Ajustar quantidade (linha selecionada):"), gbc);

        gbc.gridx = 1;
        painelAcoes.add(spinnerAjusteQuantidade, gbc);

        JButton botaoAjustarQuantidade = new JButton("Aplicar ajuste");
        botaoAjustarQuantidade.addActionListener(e -> ajustarQuantidadeSelecionada());
        gbc.gridx = 2;
        painelAcoes.add(botaoAjustarQuantidade, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        painelAcoes.add(new JLabel("Reajuste de preço (%):"), gbc);

        gbc.gridx = 1;
        painelAcoes.add(spinnerReajustePercentual, gbc);

        JButton botaoReajustar = new JButton("Reajustar preços");
        botaoReajustar.addActionListener(e -> reajustarPrecos());
        gbc.gridx = 2;
        painelAcoes.add(botaoReajustar, gbc);

        painel.add(painelIndicadores, BorderLayout.NORTH);
        painel.add(painelAcoes, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarCardIndicador(String titulo, JLabel valor) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(Font.PLAIN, 13f));

        valor.setFont(valor.getFont().deriveFont(Font.BOLD, 22f));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        return card;
    }

    private void adicionarProduto(ActionEvent event) {
        String nome = campoNome.getText().trim();
        String quantidadeTexto = campoQuantidade.getText().trim();
        String precoTexto = campoPreco.getText().trim().replace(",", ".");

        if (nome.isEmpty() || quantidadeTexto.isEmpty() || precoTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int quantidade = Integer.parseInt(quantidadeTexto);
            double preco = Double.parseDouble(precoTexto);

            if (quantidade < 0 || preco < 0) {
                JOptionPane.showMessageDialog(this, "Quantidade e preço devem ser positivos.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = quantidade * preco;
            modeloTabela.addRow(new Object[]{
                    proximoId++,
                    nome,
                    quantidade,
                    formatarMoeda(preco),
                    formatarMoeda(total)
            });

            limparFormulario();
            atualizarIndicadoresAdmin();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade e preço devem ser numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabela.removeRow(linha);
        atualizarIndicadoresAdmin();
    }

    private void limparEstoque() {
        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente limpar todo o estoque?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmar == JOptionPane.YES_OPTION) {
            modeloTabela.setRowCount(0);
            proximoId = 1;
            atualizarIndicadoresAdmin();
        }
    }

    private void mostrarValorTotal() {
        JOptionPane.showMessageDialog(
                this,
                String.format("Valor total em estoque: %s", formatarMoeda(calcularValorTotal())),
                "Resumo do estoque",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void ajustarQuantidadeSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item na tabela para ajustar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ajuste = (Integer) spinnerAjusteQuantidade.getValue();
        int quantidadeAtual = (Integer) modeloTabela.getValueAt(linha, 2);
        int novaQuantidade = quantidadeAtual + ajuste;

        if (novaQuantidade < 0) {
            JOptionPane.showMessageDialog(this, "Ajuste inválido: quantidade ficaria negativa.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabela.setValueAt(novaQuantidade, linha, 2);
        atualizarTotalDaLinha(linha);
        atualizarIndicadoresAdmin();
    }

    private void reajustarPrecos() {
        double percentual = (Double) spinnerReajustePercentual.getValue();
        double fator = 1 + (percentual / 100.0);

        if (fator < 0) {
            JOptionPane.showMessageDialog(this, "Percentual inválido: preço não pode ficar negativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            double precoAtual = valorMonetarioDaColuna(i, 3);
            double novoPreco = precoAtual * fator;
            modeloTabela.setValueAt(formatarMoeda(novoPreco), i, 3);
            atualizarTotalDaLinha(i);
        }

        atualizarIndicadoresAdmin();
    }

    private void atualizarTotalDaLinha(int linha) {
        int quantidade = (Integer) modeloTabela.getValueAt(linha, 2);
        double preco = valorMonetarioDaColuna(linha, 3);
        modeloTabela.setValueAt(formatarMoeda(quantidade * preco), linha, 4);
    }

    private void atualizarIndicadoresAdmin() {
        int totalProdutos = modeloTabela.getRowCount();
        int totalUnidades = 0;
        int estoqueBaixo = 0;
        int minimoBaixo = (Integer) spinnerMinimoBaixo.getValue();

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            int quantidade = (Integer) modeloTabela.getValueAt(i, 2);
            totalUnidades += quantidade;
            if (quantidade <= minimoBaixo) {
                estoqueBaixo++;
            }
        }

        lblTotalProdutos.setText(String.valueOf(totalProdutos));
        lblUnidadesEstoque.setText(String.valueOf(totalUnidades));
        lblValorTotal.setText(formatarMoeda(calcularValorTotal()));
        lblEstoqueBaixo.setText(String.valueOf(estoqueBaixo));
    }

    private double calcularValorTotal() {
        double soma = 0.0;

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            soma += valorMonetarioDaColuna(i, 4);
        }

        return soma;
    }

    private double valorMonetarioDaColuna(int linha, int coluna) {
        try {
            String texto = modeloTabela.getValueAt(linha, coluna).toString();
            return java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR")).parse(texto).doubleValue();
        } catch (java.text.ParseException e) {
            return 0.0;
        }
    }

    private String formatarMoeda(double valor) {
        return String.format("R$ %,.2f", valor)
                .replace(",", "X")
                .replace(".", ",")
                .replace("X", ".");
    }

    private void limparFormulario() {
        campoNome.setText("");
        campoQuantidade.setText("");
        campoPreco.setText("");
        campoNome.requestFocus();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Se não conseguir aplicar o tema do sistema, segue com o padrão.
            }
            new EstoqueApp().setVisible(true);
        });
    }
}
