package co.edu.uniquindio.cinemauq.app;

import co.edu.uniquindio.cinemauq.fachada.CinemaFacade;
import co.edu.uniquindio.cinemauq.repositorio.DatosIniciales;
import co.edu.uniquindio.cinemauq.repositorio.RepositorioArchivo;
import co.edu.uniquindio.cinemauq.servicio.eventos.BitacoraArchivo;
import co.edu.uniquindio.cinemauq.servicio.tiempo.ProveedorTiempo;
import co.edu.uniquindio.cinemauq.servicio.tiempo.TiempoSistema;
import co.edu.uniquindio.cinemauq.vista.Navegador;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.List;


public class CinemaUQApp extends Application {

    @Override
    public void start(Stage stage) {
        RepositorioArchivo repositorio = RepositorioArchivo.getInstance();
        ProveedorTiempo tiempo = new TiempoSistema();
        if (!repositorio.cargar()) {
            DatosIniciales.cargar(repositorio, tiempo.ahora());
        }
        CinemaFacade fachada = new CinemaFacade(repositorio, tiempo,
                List.of(new BitacoraArchivo(Paths.get("datos", "bitacora.log"))));

        Navegador navegador = new Navegador(stage, fachada, repositorio);
        stage.setTitle("CinemaUQ");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        navegador.mostrarLogin();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
