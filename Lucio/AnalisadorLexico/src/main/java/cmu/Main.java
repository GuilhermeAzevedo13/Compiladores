package cmu;

import cordelmaisum.lexer.Lexer;
import cordelmaisum.parser.Parser;
import cordelmaisum.node.*;
import cordelmaisum.analysis.DepthFirstAdapter;
import cordelmaisum.node.Token;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : "fatorial.cmu";

        File f = new File(arquivo);
        if (!f.exists()) {
            System.err.println("Arquivo nao encontrado: " + f.getAbsolutePath());
            return;
        }

        try {
            Lexer lexer = new Lexer(new PushbackReader(new FileReader(arquivo), 1024));
            Parser parser = new Parser(lexer);
            Start tree = parser.parse();

            tree.apply(new ImpressoraArvore());

            System.out.println("\n>>> GERANDO CODIGO C AGORA..."); // Mensagem de debug

            GeradorC gerador = new GeradorC();
            tree.apply(gerador);

            String nomeSaida = arquivo.replace(".cmu", ".c");
            if (!nomeSaida.endsWith(".c")) nomeSaida += ".c";

            PrintWriter writer = new PrintWriter(nomeSaida);
            writer.print(gerador.getCodigo());
            writer.close(); // Importante fechar para salvar!

            System.out.println("SUCESSO! Arquivo gerado: " + new File(nomeSaida).getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class ImpressoraArvore extends DepthFirstAdapter {
        private int nivel = 0;
        private void print(String texto) {
            for (int i = 0; i < nivel; i++) System.out.print("  |");
            System.out.println("- " + texto);
        }
        public void defaultIn(Node node) {
            String nome = node.getClass().getSimpleName().replaceFirst("^(A|P|T)", "");
            print(nome); nivel++;
        }
        public void defaultOut(Node node) { nivel--; }
        public void defaultCase(Node node) {
            if (node instanceof Token) print("'" + ((Token) node).getText().replace("\n", "\\n") + "'");
        }
    }
}