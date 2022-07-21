package com.alura.service;

import static java.awt.Transparency.TRANSLUCENT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import javax.imageio.ImageIO;

@SuppressWarnings("squid:S106")
public interface WebService {

  default String jsonFromGet(String url) throws IOException, InterruptedException {
    return HttpClient.newHttpClient()
        .send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
            BodyHandlers.ofString())
        .body();
  }

  default void printField(String format, String field) {
    System.out.println(format + field + "\033[0m");
  }

  default void createSticker(InputStream inputImage, String title, String output) throws IOException {

    final Integer heightPlus = 200;
    final String text = "IMERSÃO JAVA";
    final String format = "png";

    BufferedImage movieImage = ImageIO.read(inputImage);
    BufferedImage sticker = new BufferedImage(movieImage.getWidth(), movieImage.getHeight() + heightPlus,
        TRANSLUCENT);

    Graphics2D graphic = (Graphics2D) sticker.getGraphics();
    graphic.drawImage(movieImage, 0, 0, null);

    var font = new Font("Impact", Font.PLAIN, 128);
    Shape textShape = new TextLayout(text, font, graphic.getFontRenderContext()).getOutline(null);

    FontMetrics metrics = graphic.getFontMetrics(font);
    graphic.translate((sticker.getWidth() - textShape.getBounds().width) / 2,
        ((heightPlus - textShape.getBounds().height) / 2) + metrics.getAscent() + movieImage.getHeight());
    graphic.setColor(Color.BLACK);
    graphic.fill(textShape);
    graphic.setStroke(new BasicStroke(5));
    graphic.setColor(Color.YELLOW);
    graphic.draw(textShape);

    ImageIO.write(sticker, format, new File(output + title + "." + format));
  }

}
