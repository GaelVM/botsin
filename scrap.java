import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ScrapingExample {

    public static void main(String[] args) {
        String url = "https://moonani.com/PokeList/";

        try {
            // Realizar la petición HTTP y obtener el HTML de la página
            Document doc = Jsoup.connect(url).get();

            // Ejemplo: Obtener el título de la página
            String title = doc.title();
            System.out.println("Título de la página: " + title);

            // Ejemplo: Obtener todos los enlaces de la página
            Elements links = doc.select("a[href]");
            System.out.println("Enlaces encontrados:");
            for (Element link : links) {
                System.out.println(link.attr("href"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
