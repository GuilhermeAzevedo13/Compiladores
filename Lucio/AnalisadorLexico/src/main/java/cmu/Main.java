package cmu;

import cmu.lexer.Lexer;
import cmu.parser.Parser;
import cmu.node.*;
import cmu.analysis.DepthFirstAdapter;
import java.io.FileReader;
import java.io.PushbackReader;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        String arquivo = args.length > 0 ? args[0] : "triangulo_existencia.cmu";

        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        File f = new File(arquivo);
        if (!f.exists()) {
            System.err.println("Arquivo nao encontrado: " + f.getAbsolutePath());
            return;
        }

        System.out.println(">>> Iniciando Analise do arquivo: " + arquivo);

        try {
            Lexer lexer = new Lexer(new PushbackReader(new FileReader(arquivo), 1024));
            Parser parser = new Parser(lexer);
            Start tree = parser.parse();

            System.out.println("\n=== SUCESSO: O codigo compilou! ===");
            System.out.println("Imprimindo Arvore Sintatica...\n");

            tree.apply(new ImpressoraArvore());

        } catch (Exception e) {
            System.err.println("=== ERRO: Falha na analise ===");
            e.printStackTrace();
        }
    }

    static class ImpressoraArvore extends DepthFirstAdapter {
        private int nivel = 0;

        private void print(String texto) {
            for (int i = 0; i < nivel; i++)
                System.out.print("  |");
            System.out.println("- " + texto);
        }

        @Override
        public void defaultIn(Node node) {
            String nomeNo = node.getClass().getSimpleName();
            nomeNo = nomeNo.replaceFirst("^(A|P|T)", "");
            print(nomeNo);
            nivel++;
        }

        @Override
        public void defaultOut(Node node) {
            nivel--;
        }

        @Override
        public void defaultCase(Node node) {
            if (node instanceof Token) {
                String valor = ((Token) node).getText();
                valor = valor.replace("\n", "\\n").replace("\r", "");
                print("'" + valor + "'");
            }
        }
    }
}