import Mecanismo.*;

public class App {
    public static void main(String[] args) throws Exception {
        //ExemploHashMap.executar();
        // MapearIdades mapa = new MapearIdades();
        // mapa.executar();

        Executor exec = new Executor();
        // motor.CarregarArquivo();
        exec.CarregarArquivo("LexPascal/src/Saudacao.pas");
        exec.ProcessarBufferPrimario();
        exec.ImprimirBufferPrimario();
        exec.ProcessarBufferSecundario();
        exec.ImprimirBufferSecundario();
        exec.AnalisarMontandoTabelaSimbolos();
        exec.ImprimirTabelaSimbolosPrograma();        
    }
}
