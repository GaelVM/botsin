import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PokeListScraper extends TelegramLongPollingBot {

    private static final String TOKEN = "6581042871:AAFKlCHlOXUkGrd14WSyBD4oy01N65XiKcE"; // Reemplaza con el token de tu bot
    private static final String GROUP_CHAT_ID = "@pruebapectm"; // Reemplaza con el ID de tu grupo de Telegram

    public static void main(String[] args) {
        ApiContextInitializer.init(); // Inicializa el contexto de la API de Telegram

        // Crear un bot
        PokeListScraper bot = new PokeListScraper();

        // Programar la tarea de scraping y envío de mensajes
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(bot::scrapeAndSendData, 0, 8, TimeUnit.MINUTES); // Ejecutar cada 8 minutos

        // Mantener el programa en ejecución
        while (true) {
            try {
                Thread.sleep(1000); // Esperar 1 segundo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
        // Método necesario para la clase TelegramLongPollingBot, pero no será utilizado directamente en este ejemplo
    }

    @Override
    public String getBotUsername() {
        return "PokeListScraperBot"; // Nombre de usuario del bot
    }

    @Override
    public String getBotToken() {
        return TOKEN; // Token de acceso del bot
    }

    private void scrapeAndSendData() {
        String url = "https://moonani.com/PokeList/index.php";

        try {
            // Realizar la solicitud HTTP y obtener el HTML de la página
            Document doc = Jsoup.connect(url).get();

            // Encontrar la tabla con id 'customers'
            Element table = doc.getElementById("customers");

            if (table != null) {
                // Lista para almacenar los datos de los Pokémon
                List<PokemonData> data_list = new ArrayList<>();

                // Obtener todas las filas de la tabla, excepto la primera (encabezados)
                Elements rows = table.select("tr:gt(0)");

                // Iterar sobre las filas y extraer los datos relevantes
                for (Element row : rows) {
                    Elements cells = row.select("td");

                    // Verificar que haya suficientes celdas y que el ID del Pokémon sea 519
                    if (cells.size() > 0 && Integer.parseInt(cells.get(2).text().trim()) == 519) {
                        PokemonData data = new PokemonData(
                                cells.get(1).text().trim(), // Nombre
                                cells.get(4).text().trim(), // CP
                                cells.get(5).text().trim(), // Level
                                cells.get(11).text().trim(), // Shiny
                                cells.get(13).text().trim(), // End Time
                                cells.get(3).text().trim() // Coords
                        );
                        data_list.add(data);
                    }
                }

                // Ordenar los datos por "End Time" en orden descendente
                data_list.sort(Comparator.comparing(PokemonData::getEndTime).reversed());

                // Limitar a 20 elementos
                data_list = data_list.subList(0, Math.min(data_list.size(), 20));

                // Enviar los datos formateados a Telegram
                for (PokemonData data : data_list) {
                    sendToTelegram(data);
                    Thread.sleep(10000); // Esperar 10 segundos entre cada mensaje
                }
            } else {
                System.out.println("No se encontró la tabla con id 'customers'.");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendToTelegram(PokemonData data) {
        LocalDateTime endDateTime = LocalDateTime.parse(data.getEndTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String shinyIcon = data.getShiny().equalsIgnoreCase("yes") ? "✨" : "";

        String message = String.format(
                "🔰  %s %s 🅘🅥 💯\n" +
                        "🔰🅛🅥: %s  🅒🅟: %s\n" +
                        "🛩𝕊𝕟𝕚𝕡𝕖𝕣 𝕊𝕖𝕝𝕝𝕖𝕔𝕥𝕖𝕕 𝟙𝟘𝟘🚀\n" +
                        "❛ Únete a nuestro discord ❜\n" +
                        "CC: %s",
                data.getName(), shinyIcon, data.getLevel(), data.getCP(), data.getCoords()
        );

        SendMessage sendMessage = new SendMessage()
                .setChatId(GROUP_CHAT_ID)
                .setText(message);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Clase para almacenar los datos de un Pokémon
    private static class PokemonData {
        private String name;
        private String CP;
        private String level;
        private String shiny;
        private String endTime;
        private String coords;

        public PokemonData(String name, String CP, String level, String shiny, String endTime, String coords) {
            this.name = name;
            this.CP = CP;
            this.level = level;
            this.shiny = shiny;
            this.endTime = endTime;
            this.coords = coords;
        }

        public String getName() {
            return name;
        }

        public String getCP() {
            return CP;
        }

        public String getLevel() {
            return level;
        }

        public String getShiny() {
            return shiny;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getCoords() {
            return coords;
        }
    }
}
