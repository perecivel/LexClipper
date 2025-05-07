package Mecanismo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Executor {

    private final String captureComment = "\\/\\/.*|\\(\\*(?:.|\\s)*\\*\\)";
    private final String captureNumbers = "(?<!\\w)(?:-?\\d+(?:\\.\\d+)?)(?!\\w)";
    private final String captureLiteral = "'(?:[^']|'')*'";
    private final String captureWords   = "\\w+";
    private final String captureCharacters = "(?::=|>=|<=|<>|>|<|=|\\+|\\-|\\*|\\|[:;,.()\\[\\]{}])";
    private final String captureIdentifier = "^[A-Za-z][A-Za-z0-9_]*$";
    private final String[] pascalKeywords = {"program", "begin", "end", "var", "const", "type", "procedure", "function", "if", "then", "else", "while", "do", "for", "to", "downto", "repeat", "until", "case", "of", "write", "writeln", "read", "readln"};
    private String capture;

    private BufferedReader reader;
    private ArrayList<String> bufferPrimario;
    private ArrayList<String> bufferSecundario;

    private HashMap<String, Token> tabelaSimbolosPrograma;

    private boolean IsNumber(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureNumbers);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsLiteral(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureLiteral);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsCharacter(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureCharacters);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsIdentifier(String valor)
    {
        Pattern pattern = Pattern.compile(this.captureIdentifier);
        Matcher matcher = pattern.matcher(valor);
        if (matcher.find() == true){
            return true;
        }
        return false;
    }

    private boolean IsKeyword(String valor) {  // Nesse caso, nao da pra usar pattern e matcher, pois ao inves de um Regex, esta sendo utilizado um vetor,
        for (String keyword : pascalKeywords) {//sendo assim, o .compile nao funciona, e consequentemente o matcher tambem nao.
            if (keyword.equals(valor)) {       //Acredito que nao seja a melhor forma para checar se é uma KeyWord ou nao, porem como a lista de palavras reservadas usadas foram poucas, nao há problema
                return true;
            }
        }
        return false;
    }

    public void CarregarArquivo(){
        System.out.println("----------------------------------------");
        System.out.println("##### Carregar Arquivo Pascal #####");
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o diretório do arquivo: ");
        String diretorio = scanner.next();
        System.out.print("Digite o nome do arquivo (com extensão .pas): ");
        String nomeArquivo = scanner.next();
        String caminhoCompleto = diretorio + "/" + nomeArquivo;
        this.CarregarBufferPrimario(caminhoCompleto);
        scanner.close();        
    }

    public void CarregarArquivo(String caminhoCompleto){
        System.out.println("----------------------------------------");
        System.out.println("##### Carregar Arquivo Pascal #####");
        this.CarregarBufferPrimario(caminhoCompleto);
    }

    private void CarregarBufferPrimario(String caminhoCompleto){
        this.reader = null;
        try {
            this.reader = new BufferedReader(new FileReader(caminhoCompleto));
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
    }

    public void ProcessarBufferPrimario(){
        this.bufferPrimario = new ArrayList<>();
        try {
            String linha;
            while ((linha = this.reader.readLine()) != null) {
                bufferPrimario.add(linha);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Erro ao fechar o arquivo: " + e.getMessage());
                }
            }
        }
    }

    public void ImprimirBufferPrimario(){
        System.out.println("----------------------------------------");
        System.out.println("##### Conteúdo do Buffer primário: #####");
        for (String texto : this.bufferPrimario) {
            System.out.println(texto);
        }
        System.out.println("----------------------------------------");        
    }

    public void ProcessarBufferSecundario()
    {        
        this.capture = captureComment.concat("|")
            .concat(captureNumbers).concat("|")
            .concat(captureLiteral).concat("|")
            .concat(captureWords).concat("|")
            .concat(captureCharacters);

        this.bufferSecundario = new ArrayList<>();

        Pattern pattern = Pattern.compile(this.capture);

        for (String texto : bufferPrimario) {
            Matcher matcher = pattern.matcher(texto);
            while(matcher.find()){
                String lexema = matcher.group();
                if (this.bufferSecundario.contains(lexema) == false){
                    this.bufferSecundario.add(lexema);
                }
            }
        }
        bufferSecundario.removeIf(value -> value.startsWith("//") || value.startsWith("(*"));
    } 
    
    public void ImprimirBufferSecundario(){
        System.out.println("----------------------------------------");
        System.out.println("##### Conteúdo do Buffer secundário: #####");
        for (String texto : this.bufferSecundario) {
            System.out.println(texto);
        }
        System.out.println("----------------------------------------");        
    }

    public void AnalisarMontandoTabelaSimbolos(){
        //AQUI É QUE ENTRA O TRABALHO DE VCS.
        //1 - Precisa da Tabela de Simbolos do programa.
        //2 - Precisa da Tabela de Simbolos da linguagem.
        //3 - Precisa varrer o buffer secundário, para localizar os tokens, definindo o que é cada um dos lexemas.
        this.tabelaSimbolosPrograma = new HashMap<>();
        int endereço = 0;
        
        for (String texto : this.bufferSecundario) {
            Token token = new Token();
            if (IsKeyword(texto)) { //Adicionado a funcao IsKeyword, pois as palavras reservadas estavam sendo classificadas como Literal e as Caracteres
                String tipo = "Palavra Reservada";
                token.setToken(texto);
                token.setLexema(texto);
                token.setTipo(tipo);
                token.setDescricao(tipo);
                token.setEndereco(endereço);
                endereço++;

                this.tabelaSimbolosPrograma.put(texto, token);
            }

            
            else if (IsCharacter(texto)) {
                String tipo = "Caractere";
                token.setToken(texto);
                token.setLexema(texto);
                token.setTipo(tipo);
                token.setDescricao(tipo);
                token.setEndereco(endereço);
                endereço++;

                this.tabelaSimbolosPrograma.put(texto, token);   
            }
            else if (IsLiteral(texto)) {
                String tipo = "Literal";
                token.setToken(texto);
                token.setLexema(texto);
                token.setTipo(tipo);
                token.setDescricao(tipo);
                token.setEndereco(endereço);
                endereço++;

                this.tabelaSimbolosPrograma.put(texto, token);
            }

            else if (IsIdentifier(texto)) {
                String tipo = "Identificador";
                token.setToken(texto);
                token.setLexema(texto);
                token.setTipo(tipo);
                token.setDescricao(tipo);
                token.setEndereco(endereço);
                endereço++;

                this.tabelaSimbolosPrograma.put(texto, token);
            }

            else if (IsNumber(texto)) {
                String tipo = "Numero";
                token.setToken(texto);
                token.setLexema(texto);
                token.setTipo(tipo);
                token.setDescricao(tipo);
                token.setEndereco(endereço);
                endereço++;

                this.tabelaSimbolosPrograma.put(texto, token);
            }
        }
    }

    public void ImprimirTabelaSimbolosPrograma(){
        //A parte final, na qual vc imprime todas as entradas da Tabela de Simbolos do programa, após o processamento.
        System.out.println("----------------------------------------");
        System.out.println("##### Conteúdo da Tabela de Simbolos: #####");
        for (String chave : this.tabelaSimbolosPrograma.keySet()) {
            Token token = this.tabelaSimbolosPrograma.get(chave);
            System.out.println(token.toString());
            System.out.println('\n');
        }
        System.out.println("----------------------------------------");
    }

}
