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

    private int proximoId = 1;

    public EstoqueApp() {
        super("Sistema de Estoque - Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel painelFormulario = criarPainelFormulario();
        JTable tabela = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabela);
        scrollTabela.setBorder(new EmptyBorder(0, 10, 10, 10));

        JPanel painelRodape = criarPainelRodape();

        add(painelFormulario, BorderLayout.NORTH);
        add(scrollTabela, BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);
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
                    String.format("%.2f", preco),
                    String.format("%.2f", total)
            });

            limparFormulario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade e preço devem ser numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerSelecionado() {
        JTable tabela = obterTabela();
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        modeloTabela.removeRow(linha);
    }

    private JTable obterTabela() {
        JScrollPane scroll = (JScrollPane) getContentPane().getComponent(1);
        return (JTable) scroll.getViewport().getView();
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
        }
    }

    private void mostrarValorTotal() {
        double soma = 0.0;

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            String totalTexto = modeloTabela.getValueAt(i, 4).toString().replace(",", ".");
            soma += Double.parseDouble(totalTexto);
        }

        JOptionPane.showMessageDialog(
                this,
                String.format("Valor total em estoque: R$ %.2f", soma),
                "Resumo do estoque",
                JOptionPane.INFORMATION_MESSAGE
        );
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
