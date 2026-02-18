package cmu;

import cordelmaisum.analysis.DepthFirstAdapter;
import cordelmaisum.node.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class GeradorC extends DepthFirstAdapter {
    private StringBuilder codigo = new StringBuilder();

    public String getCodigo() {
        return codigo.toString();
    }

    private String traduzirTipo(PTipo tipo) {
        if (tipo instanceof ANumeroTipo) return "double";
        if (tipo instanceof ARespostaTipo) return "int";
        if (tipo instanceof ANadaTipo) return "void";
        return "void";
    }

    // --- 1. Raiz do Programa ---
    @Override
    public void caseAPrograma(APrograma node) {
        codigo.append("#include <stdio.h>\n");
        codigo.append("#include <stdlib.h>\n");
        codigo.append("#include <math.h>\n\n");
        codigo.append("#define sim 1\n");
        codigo.append("#define nao 0\n\n");

        ADecFuncao funcaoPrincipal = null;

        // Gera Protótipos
        for (PDecFuncao f : node.getDecFuncao()) {
            ADecFuncao dec = (ADecFuncao) f;
            gerarPrototipo(dec);
            if (dec.getMarcador() != null) {
                funcaoPrincipal = dec;
            }
        }
        codigo.append("\n");

        // Gera o corpo das funções
        for (PDecFuncao f : node.getDecFuncao()) {
            f.apply(this);
        }

        // --- GERAÇÃO DA MAIN DO C ---
        if (funcaoPrincipal != null) {
            codigo.append("\nint main() {\n");

            List<String> variaveisMain = new ArrayList<>();
            int contador = 0;

            for (PParamGeral p : funcaoPrincipal.getParams()) {
                if (p instanceof ADeclaracaoParamGeral) {
                    ADeclaracaoParamGeral decl = (ADeclaracaoParamGeral) p;
                    String tipoC = traduzirTipo(decl.getTipo());

                    // --- CORREÇÃO AQUI: Se for void, ignora e não cria variável ---
                    if (tipoC.equals("void")) {
                        continue;
                    }
                    // -------------------------------------------------------------

                    String nomeVar = "input_" + contador++;
                    codigo.append("    ").append(tipoC).append(" ").append(nomeVar).append(";\n");

                    if (tipoC.equals("double")) {
                        codigo.append("    printf(\"Digite um numero para o parametro " + nomeVar + ": \");\n");
                        codigo.append("    scanf(\"%lf\", &").append(nomeVar).append(");\n");
                    } else if (tipoC.equals("int")) {
                        codigo.append("    printf(\"Digite 0 ou 1 para o parametro " + nomeVar + ": \");\n");
                        codigo.append("    scanf(\"%d\", &").append(nomeVar).append(");\n");
                    }
                    variaveisMain.add(nomeVar);
                }
            }
            codigo.append("\n");

            String tipoRetorno = traduzirTipo(funcaoPrincipal.getTipoRetorno());

            codigo.append("    ");
            if (tipoRetorno.equals("double")) {
                codigo.append("printf(\"Resultado: %.2f\\n\", ");
            } else if (tipoRetorno.equals("int")) {
                codigo.append("printf(\"Resultado: %d\\n\", ");
            }

            codigo.append(funcaoPrincipal.getNome().getText()).append("(");

            Iterator<String> it = variaveisMain.iterator();
            while(it.hasNext()) {
                codigo.append(it.next());
                if(it.hasNext()) codigo.append(", ");
            }
            codigo.append(")");

            if (!tipoRetorno.equals("void")) {
                codigo.append(")");
            }
            codigo.append(";\n");

            codigo.append("    return 0;\n");
            codigo.append("}\n");
        }
    }

    private void gerarPrototipo(ADecFuncao node) {
        codigo.append(traduzirTipo(node.getTipoRetorno()));
        codigo.append(" ").append(node.getNome().getText()).append("(");

        Iterator<PParamGeral> it = node.getParams().iterator();
        while(it.hasNext()) {
            processarParametro(it.next());
            if(it.hasNext()) codigo.append(", ");
        }
        codigo.append(");\n");
    }

    @Override
    public void caseADecFuncao(ADecFuncao node) {
        codigo.append(traduzirTipo(node.getTipoRetorno()));
        codigo.append(" ").append(node.getNome().getText()).append("(");

        Iterator<PParamGeral> it = node.getParams().iterator();
        while(it.hasNext()) {
            processarParametro(it.next());
            if(it.hasNext()) codigo.append(", ");
        }
        codigo.append(") {\n");

        for (PDecConstante c : node.getConstantes()) {
            c.apply(this);
        }

        codigo.append("    return ");
        node.getRetorno().apply(this);
        codigo.append(";\n");
        codigo.append("}\n\n");
    }

    private void processarParametro(PParamGeral param) {
        if (param instanceof ADeclaracaoParamGeral) {
            ADeclaracaoParamGeral p = (ADeclaracaoParamGeral) param;
            codigo.append(traduzirTipo(p.getTipo()));
            if (p.getNome() != null) {
                codigo.append(" ").append(p.getNome().getText());
            }
        } else if (param instanceof AAssinaturaParamGeral) {
            AAssinaturaParamGeral p = (AAssinaturaParamGeral) param;
            codigo.append(traduzirTipo(p.getTipoRetorno()));
            codigo.append(" (*").append(p.getNome().getText()).append(")(");
            Iterator<PParamGeral> subIt = p.getParams().iterator();
            while(subIt.hasNext()) {
                processarParametro(subIt.next());
                if(subIt.hasNext()) codigo.append(", ");
            }
            codigo.append(")");
        }
    }

    @Override
    public void caseADecConstante(ADecConstante node) {
        codigo.append("    ");
        codigo.append(traduzirTipo(node.getTipo()));
        codigo.append(" ").append(node.getNome().getText()).append(" = ");
        node.getValor().apply(this);
        codigo.append(";\n");
    }

    @Override
    public void caseATernarioExp(ATernarioExp node) {
        codigo.append("(");
        node.getCond().apply(this);
        codigo.append(" ? ");
        node.getVerd().apply(this);
        codigo.append(" : ");
        node.getFalso().apply(this);
        codigo.append(")");
    }

    private void visitBinaria(PExp esq, String op, PExp dir) {
        codigo.append("(");
        esq.apply(this);
        codigo.append(" ").append(op).append(" ");
        dir.apply(this);
        codigo.append(")");
    }

    @Override public void caseASomaExp(ASomaExp node) { visitBinaria(node.getEsq(), "+", node.getDir()); }
    @Override public void caseASubExp(ASubExp node) { visitBinaria(node.getEsq(), "-", node.getDir()); }
    @Override public void caseAMultExp(AMultExp node) { visitBinaria(node.getEsq(), "*", node.getDir()); }
    @Override public void caseADivExp(ADivExp node) { visitBinaria(node.getEsq(), "/", node.getDir()); }

    @Override public void caseAModExp(AModExp node) {
        codigo.append("((int)");
        node.getEsq().apply(this);
        codigo.append(" % (int)");
        node.getDir().apply(this);
        codigo.append(")");
    }

    @Override public void caseAOuExp(AOuExp node) { visitBinaria(node.getEsq(), "||", node.getDir()); }
    @Override public void caseAEExp(AEExp node) { visitBinaria(node.getEsq(), "&&", node.getDir()); }
    @Override public void caseAIgualExp(AIgualExp node) { visitBinaria(node.getEsq(), "==", node.getDir()); }
    @Override public void caseAMaiorExp(AMaiorExp node) { visitBinaria(node.getEsq(), ">", node.getDir()); }
    @Override public void caseAMenorExp(AMenorExp node) { visitBinaria(node.getEsq(), "<", node.getDir()); }
    @Override public void caseAMaiorIgualExp(AMaiorIgualExp node) { visitBinaria(node.getEsq(), ">=", node.getDir()); }
    @Override public void caseAMenorIgualExp(AMenorIgualExp node) { visitBinaria(node.getEsq(), "<=", node.getDir()); }

    @Override public void caseAMenosExp(AMenosExp node) { codigo.append("-"); node.getExp().apply(this); }
    @Override public void caseANaoExp(ANaoExp node) { codigo.append("!"); node.getExp().apply(this); }

    @Override public void caseAIntExp(AIntExp node) { codigo.append(node.getNumInt().getText()); }
    @Override public void caseARealExp(ARealExp node) { codigo.append(node.getNumReal().getText().replace(',', '.')); }
    @Override public void caseAIdExp(AIdExp node) { codigo.append(node.getId().getText()); }
    @Override public void caseASimExp(ASimExp node) { codigo.append("1"); }
    @Override public void caseANaoValExp(ANaoValExp node) { codigo.append("0"); }

    @Override public void caseABoolExp(ABoolExp node) {
        if(node.getBoolVal() instanceof ASimBoolVal) codigo.append("1");
        else codigo.append("0");
    }

    @Override
    public void caseAChamadaExp(AChamadaExp node) {
        String nome = node.getNome().getText();
        if (nome.equals("seno")) nome = "sin";
        else if (nome.equals("cosseno")) nome = "cos";
        else if (nome.equals("tangente")) nome = "tan";
        else if (nome.equals("logaritmo")) nome = "log10";
        else if (nome.equals("potencia")) nome = "pow";

        codigo.append(nome).append("(");
        Iterator<PExp> it = node.getArgs().iterator();
        while(it.hasNext()) {
            it.next().apply(this);
            if(it.hasNext()) codigo.append(", ");
        }
        codigo.append(")");
    }
}