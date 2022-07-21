package com.alura.service;

import static java.awt.Transparency.TRANSLUCENT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
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
import java.util.Optional;

import javax.imageio.ImageIO;

import com.alura.model.Sticker;

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

  default void createSticker(InputStream inputImage, String title, Double rating, Sticker param) throws IOException {

    BufferedImage movieImage = ImageIO.read(inputImage);
    int width = movieImage.getWidth();
    int height = movieImage.getHeight();

    if (width > 1000 || height > 1500) {
      Double scale = Math.min(
          width > 1000 ? 1000 / Double.valueOf(width) : 1,
          height > 1500 ? 1500 / Double.valueOf(height) : 1);
      width = (int) (movieImage.getWidth() * scale);
      height = (int) (movieImage.getHeight() * scale);
    }

    BufferedImage sticker = new BufferedImage(width, height + param.getHeightPlus(),
        TRANSLUCENT);

    Graphics2D graphic = (Graphics2D) sticker.getGraphics();
    graphic.drawImage(movieImage, 0, 0, width, height, null);

    Optional.ofNullable(rating).ifPresent(value -> {
      if (param.getRangeText().floorEntry((int) Math.round(value)).equals(param.getRangeText().lastEntry())) {
        try {
          BufferedImage joinha = ImageIO.read(new File("data/image/sticker/joinha.png"));
          graphic.drawImage(
              joinha, sticker.getWidth() - joinha.getWidth() + 35,
              sticker.getHeight() - joinha.getHeight(),
              null);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

    var font = new Font("Impact", Font.PLAIN, 128);
    Shape textShape = new TextLayout(
        param.getRangeText().floorEntry((int) Math.round(Optional.ofNullable(rating).isPresent() ? rating : 0.0))
            .getValue(),
        font, graphic.getFontRenderContext()).getOutline(null);

    graphic.translate((sticker.getWidth() - textShape.getBounds().width) / 2,
        ((param.getHeightPlus() - textShape.getBounds().height) / 2) + textShape.getBounds().height
            + height);
    graphic.setColor(Color.BLACK);
    graphic.fill(textShape);
    graphic.setStroke(new BasicStroke(5));
    graphic.setColor(Color.YELLOW);
    graphic.draw(textShape);

    ImageIO.write(sticker, param.getFormat(), new File(param.getOutput() + title + "." + param.getFormat()));
  }

}
