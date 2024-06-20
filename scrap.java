// Importa las clases necesarias de Jsoup
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class scrap {
    public static void main(String[] args) {
        String url = "https://moonani.com/PokeList/";

        try {
            // Establece la configuración para encontrar la ubicación del archivo JAR de Jsoup
            System.setProperty("java.class.path", "lib/jsoup-<version>.jar");

            // Realiza la petición HTTP y obtén el HTML de la página
            Document doc = Jsoup.connect(url).get();

            // Ejemplo: Obtén el título de la página
            String title = doc.title();
            System.out.println("Título de la página: " + title);

            // Ejemplo: Obtén todos los enlaces de la página
            Elements links = doc.select("a[href]");
            System.out.println("Enlaces encontrados:");
            for (Element link : links) {
                System.out.println(link.attr("href"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
