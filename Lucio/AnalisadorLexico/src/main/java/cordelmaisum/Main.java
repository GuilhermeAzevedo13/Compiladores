
import cordelmaisum.lexer.Lexer;
import cordelmaisum.parser.Parser;
import cordelmaisum.node.*;
import cordelmaisum.analysis.DepthFirstAdapter;
import java.io.FileReader;
import java.io.PushbackReader;

public class Main {

    public static void main(String[] args) {
        // Testando com o arquivo do triângulo (verifique se o nome está correto)
        String arquivo = "triangulo_existencia.cmu";

        System.out.println(">>> Iniciando Análise do arquivo: " + arquivo);

        try {
            // 1. Cria o Lexer
            Lexer lexer = new Lexer(new PushbackReader(new FileReader(arquivo), 1024));

            // 2. Cria o Parser
            Parser parser = new Parser(lexer);

            // 3. Gera a Árvore Sintática (AST)
            Start tree = parser.parse();

            System.out.println("\n=== SUCESSO: O código compilou! ===");
            System.out.println("Imprimindo Árvore Sintática...\n");

            // 4. Imprime a árvore formatada
            tree.apply(new ImpressoraArvore());

        } catch (Exception e) {
            System.err.println("=== ERRO: Falha na análise ===");
            e.printStackTrace();
        }
    }

    // Classe auxiliar para imprimir a árvore com recuos (identação)
    static class ImpressoraArvore extends DepthFirstAdapter {
        private int nivel = 0;

        private void print(String texto) {
            // Cria o recuo baseado no nível da árvore
            for (int i = 0; i < nivel; i++) System.out.print("  |");
            System.out.println("- " + texto);
        }

        @Override
        public void defaultIn(Node node) {
            // Quando entra num nó, imprime o nome dele e aumenta o nível
            String nomeNo = node.getClass().getSimpleName();
            // Remove os prefixos 'A', 'P', 'T' do SableCC para ficar limpo
            nomeNo = nomeNo.replaceFirst("^(A|P|T)", "");
            print(nomeNo);
            nivel++;
        }

        @Override
        public void defaultOut(Node node) {
            // Quando sai do nó, diminui o nível
            nivel--;
        }

        @Override
        public void defaultCase(Node node) {
            // Se for um token final (folha), imprime o valor
            if (node instanceof Token) {
                String valor = ((Token) node).getText();
                // Limpa quebras de linha para não bagunçar o console
                valor = valor.replace("\n", "\\n").replace("\r", "");
                print("'" + valor + "'");
            }
        }
    }
}